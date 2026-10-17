package com.smarthire.servlet;

import com.smarthire.dao.JobDAO;
import com.smarthire.dao.SkillDAO;
import com.smarthire.model.Job;
import com.smarthire.model.Skill;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/recruiter/job/create", "/recruiter/jobs/create"})
public class JobCreateServlet extends HttpServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Skill> allSkills = skillDAO.findAll();
            req.setAttribute("allSkills", allSkills);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load available skills.");
        }
        req.getRequestDispatcher("/WEB-INF/jsp/recruiter/job_form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String location = req.getParameter("location");
        String employmentType = req.getParameter("employmentType");
        String minCgpaStr = req.getParameter("minCgpa");
        String[] skillIds = req.getParameterValues("skills");

        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            location == null || location.trim().isEmpty()) {
            req.setAttribute("error", "Title, description, and location are required.");
            doGet(req, resp);
            return;
        }

        Double minCgpa = null;
        if (minCgpaStr != null && !minCgpaStr.trim().isEmpty()) {
            try {
                minCgpa = Double.parseDouble(minCgpaStr.trim());
                if (minCgpa < 0 || minCgpa > 10.0) {
                    req.setAttribute("error", "Minimum CGPA must be between 0.0 and 10.0.");
                    doGet(req, resp);
                    return;
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "Invalid CGPA format.");
                doGet(req, resp);
                return;
            }
        }

        try {
            Job job = new Job();
            job.setRecruiterId(userId);
            job.setTitle(title.trim());
            job.setDescription(description.trim());
            job.setLocation(location.trim());
            job.setEmploymentType(employmentType != null ? employmentType.trim() : "Full-time");
            job.setMinCgpa(minCgpa);
            job.setActive(true);

            long jobId = jobDAO.insert(job);
            if (jobId != -1) {
                List<Long> sIds = new ArrayList<>();
                if (skillIds != null) {
                    for (String sid : skillIds) {
                        try {
                            sIds.add(Long.parseLong(sid));
                        } catch (NumberFormatException ignored) {}
                    }
                }
                // Handle custom skills entered by recruiter
                String customSkills = req.getParameter("customSkills");
                if (customSkills != null && !customSkills.trim().isEmpty()) {
                    for (String skillName : customSkills.split(",")) {
                        String name = skillName.trim();
                        if (!name.isEmpty()) {
                            long newId = skillDAO.insertIfNotExists(name);
                            if (!sIds.contains(newId)) {
                                sIds.add(newId);
                            }
                        }
                    }
                }
                skillDAO.updateJobSkills(jobId, sIds);
            }

            resp.sendRedirect(req.getContextPath() + "/recruiter/dashboard");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not create job posting: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
