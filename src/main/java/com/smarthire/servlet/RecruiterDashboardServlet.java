package com.smarthire.servlet;

import com.smarthire.dao.JobDAO;
import com.smarthire.dao.ApplicationDAO;
import com.smarthire.dao.RecruiterProfileDAO;
import com.smarthire.model.Job;
import com.smarthire.model.Application;
import com.smarthire.model.RecruiterProfile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/recruiter/dashboard")
public class RecruiterDashboardServlet extends HttpServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final RecruiterProfileDAO profileDAO = new RecruiterProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            RecruiterProfile profile = profileDAO.findByUserId(userId);
            List<Job> jobs = jobDAO.findByRecruiterId(userId);

            // Count applications per job
            Map<Long, Integer> appCounts = new HashMap<>();
            int totalApplications = 0;
            for (Job job : jobs) {
                List<Application> apps = applicationDAO.findByJobId(job.getId());
                appCounts.put(job.getId(), apps.size());
                totalApplications += apps.size();
            }

            req.setAttribute("profile", profile);
            req.setAttribute("jobs", jobs);
            req.setAttribute("appCounts", appCounts);
            req.setAttribute("totalApplications", totalApplications);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dashboard data.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/recruiter/dashboard.jsp").forward(req, resp);
    }
}
