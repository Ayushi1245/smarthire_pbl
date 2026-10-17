package com.smarthire.servlet;

import com.smarthire.dao.*;
import com.smarthire.model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private final StudentProfileDAO profileDAO = new StudentProfileDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final ResumeDAO resumeDAO = new ResumeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            StudentProfile profile = profileDAO.findByUserId(userId);
            List<Skill> studentSkills = skillDAO.findByStudentId(userId);
            List<Application> applications = applicationDAO.findByStudentId(userId);
            List<Resume> resumes = resumeDAO.findByStudentId(userId);

            req.setAttribute("profile", profile);
            req.setAttribute("studentSkills", studentSkills);
            req.setAttribute("applications", applications);
            req.setAttribute("resumes", resumes);
            req.setAttribute("applicationCount", applications.size());
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dashboard data.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/student/dashboard.jsp").forward(req, resp);
    }
}
