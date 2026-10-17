package com.smarthire.dao;

import com.smarthire.model.StudentProfile;
import com.smarthire.util.DatabaseUtil;

import java.sql.*;

public class StudentProfileDAO {

    public void insertOrUpdate(StudentProfile p) throws SQLException {
        // Try update first; if no rows affected, insert.
        String update = "UPDATE student_profiles SET education=?, graduation_year=?, cgpa=?, summary=?, experience=? WHERE user_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setString(1, p.getEducation());
            ps.setObject(2, p.getGraduationYear(), Types.INTEGER);
            ps.setObject(3, p.getCgpa(), Types.DOUBLE);
            ps.setString(4, p.getSummary());
            ps.setString(5, p.getExperience());
            ps.setLong(6, p.getUserId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                insert(p);
            }
        }
    }

    private void insert(StudentProfile p) throws SQLException {
        String sql = "INSERT INTO student_profiles (user_id, education, graduation_year, cgpa, summary, experience) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, p.getUserId());
            ps.setString(2, p.getEducation());
            ps.setObject(3, p.getGraduationYear(), Types.INTEGER);
            ps.setObject(4, p.getCgpa(), Types.DOUBLE);
            ps.setString(5, p.getSummary());
            ps.setString(6, p.getExperience());
            ps.executeUpdate();
        }
    }

    public StudentProfile findByUserId(long userId) throws SQLException {
        String sql = "SELECT id, user_id, education, graduation_year, cgpa, summary, experience FROM student_profiles WHERE user_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StudentProfile sp = new StudentProfile();
                    sp.setId(rs.getLong("id"));
                    sp.setUserId(rs.getLong("user_id"));
                    sp.setEducation(rs.getString("education"));
                    sp.setGraduationYear((Integer) rs.getObject("graduation_year"));
                    sp.setCgpa(rs.getObject("cgpa") != null ? rs.getDouble("cgpa") : null);
                    sp.setSummary(rs.getString("summary"));
                    sp.setExperience(rs.getString("experience"));
                    return sp;
                }
            }
        }
        return null;
    }
}
