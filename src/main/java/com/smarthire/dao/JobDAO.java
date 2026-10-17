package com.smarthire.dao;

import com.smarthire.model.Job;
import com.smarthire.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    /** Insert a new job. Returns generated id, or -1. */
    public long insert(Job job) throws SQLException {
        String sql = "INSERT INTO jobs (recruiter_id, title, description, location, employment_type, min_cgpa, is_active) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, job.getRecruiterId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getLocation());
            ps.setString(5, job.getEmploymentType());
            ps.setObject(6, job.getMinCgpa(), Types.DOUBLE);
            ps.setBoolean(7, job.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    /** Update an existing job. */
    public void update(Job job) throws SQLException {
        String sql = "UPDATE jobs SET title=?, description=?, location=?, employment_type=?, min_cgpa=?, is_active=? WHERE id=? AND recruiter_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, job.getTitle());
            ps.setString(2, job.getDescription());
            ps.setString(3, job.getLocation());
            ps.setString(4, job.getEmploymentType());
            ps.setObject(5, job.getMinCgpa(), Types.DOUBLE);
            ps.setBoolean(6, job.isActive());
            ps.setLong(7, job.getId());
            ps.setLong(8, job.getRecruiterId());
            ps.executeUpdate();
        }
    }

    /** Find job by id. */
    public Job findById(long id) throws SQLException {
        String sql = "SELECT j.*, u.full_name AS recruiter_name, rp.company_name " +
                     "FROM jobs j JOIN users u ON j.recruiter_id = u.id " +
                     "LEFT JOIN recruiter_profiles rp ON u.id = rp.user_id " +
                     "WHERE j.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** All active jobs (for students to browse). */
    public List<Job> findAllActive() throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT j.*, u.full_name AS recruiter_name, rp.company_name " +
                     "FROM jobs j JOIN users u ON j.recruiter_id = u.id " +
                     "LEFT JOIN recruiter_profiles rp ON u.id = rp.user_id " +
                     "WHERE j.is_active = 1 ORDER BY j.created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) jobs.add(mapRow(rs));
        }
        return jobs;
    }

    /** Search active jobs by keyword in title or description. */
    public List<Job> searchActive(String keyword) throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT j.*, u.full_name AS recruiter_name, rp.company_name " +
                     "FROM jobs j JOIN users u ON j.recruiter_id = u.id " +
                     "LEFT JOIN recruiter_profiles rp ON u.id = rp.user_id " +
                     "WHERE j.is_active = 1 AND (j.title LIKE ? OR j.description LIKE ?) " +
                     "ORDER BY j.created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) jobs.add(mapRow(rs));
            }
        }
        return jobs;
    }

    /** All jobs posted by a specific recruiter. */
    public List<Job> findByRecruiterId(long recruiterId) throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT j.*, u.full_name AS recruiter_name, rp.company_name " +
                     "FROM jobs j JOIN users u ON j.recruiter_id = u.id " +
                     "LEFT JOIN recruiter_profiles rp ON u.id = rp.user_id " +
                     "WHERE j.recruiter_id = ? ORDER BY j.created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recruiterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) jobs.add(mapRow(rs));
            }
        }
        return jobs;
    }

    private Job mapRow(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getLong("id"));
        j.setRecruiterId(rs.getLong("recruiter_id"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setLocation(rs.getString("location"));
        j.setEmploymentType(rs.getString("employment_type"));
        j.setMinCgpa(rs.getObject("min_cgpa") != null ? rs.getDouble("min_cgpa") : null);
        j.setActive(rs.getBoolean("is_active"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        j.setRecruiterName(rs.getString("recruiter_name"));
        j.setCompanyName(rs.getString("company_name"));
        return j;
    }
}
