package com.smarthire.dao;

import com.smarthire.model.Application;
import com.smarthire.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    /** Submit an application. Returns generated id, or -1. */
    public long insert(Application app) throws SQLException {
        String sql = "INSERT INTO applications (student_id, job_id, status, match_score) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, app.getStudentId());
            ps.setLong(2, app.getJobId());
            ps.setString(3, app.getStatus() != null ? app.getStatus() : "APPLIED");
            ps.setObject(4, app.getMatchScore(), Types.DOUBLE);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    /** Check if student already applied to a job. */
    public boolean hasApplied(long studentId, long jobId) throws SQLException {
        String sql = "SELECT 1 FROM applications WHERE student_id=? AND job_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Update application status (recruiter action). */
    public void updateStatus(long applicationId, String status, long recruiterId) throws SQLException {
        // Only allow if the recruiter owns the job
        String sql = "UPDATE applications a JOIN jobs j ON a.job_id = j.id " +
                     "SET a.status = ? WHERE a.id = ? AND j.recruiter_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, applicationId);
            ps.setLong(3, recruiterId);
            ps.executeUpdate();
        }
    }

    /** All applications by a student (with job info). */
    public List<Application> findByStudentId(long studentId) throws SQLException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT a.*, j.title AS job_title, rp.company_name " +
                     "FROM applications a " +
                     "JOIN jobs j ON a.job_id = j.id " +
                     "LEFT JOIN recruiter_profiles rp ON j.recruiter_id = rp.user_id " +
                     "WHERE a.student_id = ? ORDER BY a.applied_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) apps.add(mapRow(rs));
            }
        }
        return apps;
    }

    /** All applications for a given job (recruiter view). */
    public List<Application> findByJobId(long jobId) throws SQLException {
        List<Application> apps = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS student_name, u.email AS student_email, j.title AS job_title " +
                     "FROM applications a " +
                     "JOIN users u ON a.student_id = u.id " +
                     "JOIN jobs j ON a.job_id = j.id " +
                     "WHERE a.job_id = ? ORDER BY a.match_score DESC, a.applied_at ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) apps.add(mapRowWithStudent(rs));
            }
        }
        return apps;
    }

    /** Find application by id. */
    public Application findById(long id) throws SQLException {
        String sql = "SELECT a.*, j.title AS job_title, u.full_name AS student_name, u.email AS student_email " +
                     "FROM applications a JOIN jobs j ON a.job_id = j.id JOIN users u ON a.student_id = u.id WHERE a.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowWithStudent(rs);
            }
        }
        return null;
    }

    private Application mapRow(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getLong("id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setJobId(rs.getLong("job_id"));
        a.setStatus(rs.getString("status"));
        a.setMatchScore(rs.getObject("match_score") != null ? rs.getDouble("match_score") : null);
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        a.setJobTitle(rs.getString("job_title"));
        try { a.setCompanyName(rs.getString("company_name")); } catch (SQLException ignored) {}
        return a;
    }

    private Application mapRowWithStudent(ResultSet rs) throws SQLException {
        Application a = mapRow(rs);
        try { a.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        try { a.setStudentEmail(rs.getString("student_email")); } catch (SQLException ignored) {}
        return a;
    }
}
