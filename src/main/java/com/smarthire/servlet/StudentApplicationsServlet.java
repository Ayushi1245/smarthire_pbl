package com.smarthire.servlet;

import com.smarthire.dao.ApplicationDAO;
import com.smarthire.model.Application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/student/applications")
public class StudentApplicationsServlet extends HttpServlet {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        if ("true".equals(req.getParameter("applied"))) {
            req.setAttribute("success", "Application submitted successfully!");
        }

        try {
            List<Application> applications = applicationDAO.findByStudentId(userId);
            req.setAttribute("applications", applications);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load applications.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/student/applications.jsp").forward(req, resp);
    }
}
