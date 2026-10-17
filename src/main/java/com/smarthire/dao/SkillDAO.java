package com.smarthire.dao;

import com.smarthire.model.Skill;
import com.smarthire.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

    /** Get all skills. */
    public List<Skill> findAll() throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT id, name FROM skills ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                skills.add(new Skill(rs.getLong("id"), rs.getString("name")));
            }
        }
        return skills;
    }

    /** Get skills for a specific student. */
    public List<Skill> findByStudentId(long studentId) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT s.id, s.name FROM skills s JOIN student_skills ss ON s.id = ss.skill_id WHERE ss.student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(new Skill(rs.getLong("id"), rs.getString("name")));
                }
            }
        }
        return skills;
    }

    /** Get skills required for a specific job. */
    public List<Skill> findByJobId(long jobId) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT s.id, s.name FROM skills s JOIN job_skills js ON s.id = js.skill_id WHERE js.job_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(new Skill(rs.getLong("id"), rs.getString("name")));
                }
            }
        }
        return skills;
    }

    /** Replace all skills for a student: delete old, insert new. */
    public void updateStudentSkills(long studentId, List<Long> skillIds) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete existing
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM student_skills WHERE student_id = ?")) {
                    del.setLong(1, studentId);
                    del.executeUpdate();
                }
                // Insert new
                if (skillIds != null && !skillIds.isEmpty()) {
                    try (PreparedStatement ins = conn.prepareStatement("INSERT INTO student_skills (student_id, skill_id) VALUES (?, ?)")) {
                        for (Long sid : skillIds) {
                            ins.setLong(1, studentId);
                            ins.setLong(2, sid);
                            ins.addBatch();
                        }
                        ins.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Replace all skills for a job: delete old, insert new. */
    public void updateJobSkills(long jobId, List<Long> skillIds) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM job_skills WHERE job_id = ?")) {
                    del.setLong(1, jobId);
                    del.executeUpdate();
                }
                if (skillIds != null && !skillIds.isEmpty()) {
                    try (PreparedStatement ins = conn.prepareStatement("INSERT INTO job_skills (job_id, skill_id) VALUES (?, ?)")) {
                        for (Long sid : skillIds) {
                            ins.setLong(1, jobId);
                            ins.setLong(2, sid);
                            ins.addBatch();
                        }
                        ins.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Find a skill by name (case-insensitive). */
    public Skill findByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM skills WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Skill(rs.getLong("id"), rs.getString("name"));
            }
        }
        return null;
    }

    /**
     * Find a skill by name (case-insensitive); if it does not exist, insert it.
     * Returns the skill's id in either case.
     */
    public long insertIfNotExists(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be empty");
        }
        String trimmed = name.trim();
        Skill existing = findByName(trimmed);
        if (existing != null) {
            return existing.getId();
        }
        String sql = "INSERT INTO skills (name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, trimmed);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        // Concurrent insert race – try lookup again
        Skill retry = findByName(trimmed);
        if (retry != null) return retry.getId();
        throw new SQLException("Could not insert or find skill: " + trimmed);
    }
}
