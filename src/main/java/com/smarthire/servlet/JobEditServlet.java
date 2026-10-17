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
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/recruiter/job/edit", "/recruiter/jobs/edit"})
public class JobEditServlet extends HttpServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/recruiter/dashboard");
            return;
        }

        try {
            long jobId = Long.parseLong(idStr);
            Job job = jobDAO.findById(jobId);
            if (job == null || job.getRecruiterId() != userId) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            List<Skill> allSkills = skillDAO.findAll();
            List<Skill> jobSkills = skillDAO.findByJobId(jobId);
            List<Long> selectedSkillIds = jobSkills.stream().map(Skill::getId).collect(Collectors.toList());

            req.setAttribute("job", job);
            req.setAttribute("allSkills", allSkills);
            req.setAttribute("selectedSkillIds", selectedSkillIds);
            req.getRequestDispatcher("/WEB-INF/jsp/recruiter/job_form.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/recruiter/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        String idStr = req.getParameter("id");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String location = req.getParameter("location");
        String employmentType = req.getParameter("employmentType");
        String minCgpaStr = req.getParameter("minCgpa");
        String isActiveStr = req.getParameter("isActive");
        String[] skillIds = req.getParameterValues("skills");

        if (idStr == null || title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            location == null || location.trim().isEmpty()) {
            req.setAttribute("error", "All fields are required.");
            doGet(req, resp);
            return;
        }

        try {
            long jobId = Long.parseLong(idStr);
            Job existing = jobDAO.findById(jobId);
            if (existing == null || existing.getRecruiterId() != userId) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Double minCgpa = null;
            if (minCgpaStr != null && !minCgpaStr.trim().isEmpty()) {
                minCgpa = Double.parseDouble(minCgpaStr.trim());
            }

            Job job = new Job();
            job.setId(jobId);
            job.setRecruiterId(userId);
            job.setTitle(title.trim());
            job.setDescription(description.trim());
            job.setLocation(location.trim());
            job.setEmploymentType(employmentType != null ? employmentType.trim() : "Full-time");
            job.setMinCgpa(minCgpa);
            job.setActive("true".equalsIgnoreCase(isActiveStr) || "1".equals(isActiveStr) || "on".equalsIgnoreCase(isActiveStr));

            jobDAO.update(job);

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

            resp.sendRedirect(req.getContextPath() + "/recruiter/dashboard");
        } catch (SQLException e) {
            req.setAttribute("error", "Database error updating job: " + e.getMessage());
            doGet(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Invalid form input: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
