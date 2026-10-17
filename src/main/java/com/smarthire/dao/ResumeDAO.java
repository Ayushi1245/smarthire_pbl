package com.smarthire.dao;

import com.smarthire.model.Resume;
import com.smarthire.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResumeDAO {

    public long insert(Resume r) throws SQLException {
        String sql = "INSERT INTO resumes (student_id, original_name, stored_name, file_path) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getStudentId());
            ps.setString(2, r.getOriginalName());
            ps.setString(3, r.getStoredName());
            ps.setString(4, r.getFilePath());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    public List<Resume> findByStudentId(long studentId) throws SQLException {
        List<Resume> resumes = new ArrayList<>();
        String sql = "SELECT * FROM resumes WHERE student_id = ? ORDER BY uploaded_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resumes.add(mapRow(rs));
            }
        }
        return resumes;
    }

    public Resume findById(long id) throws SQLException {
        String sql = "SELECT * FROM resumes WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void delete(long id, long studentId) throws SQLException {
        String sql = "DELETE FROM resumes WHERE id = ? AND student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, studentId);
            ps.executeUpdate();
        }
    }

    private Resume mapRow(ResultSet rs) throws SQLException {
        Resume r = new Resume();
        r.setId(rs.getLong("id"));
        r.setStudentId(rs.getLong("student_id"));
        r.setOriginalName(rs.getString("original_name"));
        r.setStoredName(rs.getString("stored_name"));
        r.setFilePath(rs.getString("file_path"));
        r.setUploadedAt(rs.getTimestamp("uploaded_at"));
        return r;
    }
}
