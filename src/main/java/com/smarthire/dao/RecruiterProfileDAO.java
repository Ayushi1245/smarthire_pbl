package com.smarthire.dao;

import com.smarthire.model.RecruiterProfile;
import com.smarthire.util.DatabaseUtil;

import java.sql.*;

public class RecruiterProfileDAO {

    public void insertOrUpdate(RecruiterProfile p) throws SQLException {
        String update = "UPDATE recruiter_profiles SET company_name=?, company_description=? WHERE user_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setString(1, p.getCompanyName());
            ps.setString(2, p.getCompanyDescription());
            ps.setLong(3, p.getUserId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                insert(p);
            }
        }
    }

    private void insert(RecruiterProfile p) throws SQLException {
        String sql = "INSERT INTO recruiter_profiles (user_id, company_name, company_description) VALUES (?,?,?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, p.getUserId());
            ps.setString(2, p.getCompanyName());
            ps.setString(3, p.getCompanyDescription());
            ps.executeUpdate();
        }
    }

    public RecruiterProfile findByUserId(long userId) throws SQLException {
        String sql = "SELECT id, user_id, company_name, company_description FROM recruiter_profiles WHERE user_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RecruiterProfile rp = new RecruiterProfile();
                    rp.setId(rs.getLong("id"));
                    rp.setUserId(rs.getLong("user_id"));
                    rp.setCompanyName(rs.getString("company_name"));
                    rp.setCompanyDescription(rs.getString("company_description"));
                    return rp;
                }
            }
        }
        return null;
    }
}
