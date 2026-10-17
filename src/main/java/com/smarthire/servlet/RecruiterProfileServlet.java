package com.smarthire.servlet;

import com.smarthire.dao.RecruiterProfileDAO;
import com.smarthire.model.RecruiterProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/recruiter/profile")
public class RecruiterProfileServlet extends HttpServlet {

    private final RecruiterProfileDAO profileDAO = new RecruiterProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            RecruiterProfile profile = profileDAO.findByUserId(userId);
            req.setAttribute("profile", profile);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load profile.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/recruiter/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        String companyName = req.getParameter("companyName");
        String companyDescription = req.getParameter("companyDescription");

        if (companyName == null || companyName.trim().isEmpty()) {
            req.setAttribute("error", "Company name is required.");
            doGet(req, resp);
            return;
        }

        try {
            RecruiterProfile profile = new RecruiterProfile();
            profile.setUserId(userId);
            profile.setCompanyName(companyName.trim());
            profile.setCompanyDescription(companyDescription != null ? companyDescription.trim() : "");
            profileDAO.insertOrUpdate(profile);

            req.setAttribute("success", "Profile updated successfully!");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not update profile.");
        }

        doGet(req, resp);
    }
}
