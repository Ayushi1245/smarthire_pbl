package com.smarthire.servlet;

import com.smarthire.dao.JobDAO;
import com.smarthire.dao.SkillDAO;
import com.smarthire.dao.ApplicationDAO;
import com.smarthire.model.Job;
import com.smarthire.model.Skill;
import com.smarthire.model.Application;
import com.smarthire.service.MatchService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/student/apply")
public class StudentApplyServlet extends HttpServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final MatchService matchService = new MatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Show job details before applying
        String jobIdStr = req.getParameter("jobId");
        if (jobIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/student/jobs");
            return;
        }

        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            long jobId = Long.parseLong(jobIdStr);
            Job job = jobDAO.findById(jobId);
            if (job == null || !job.isActive()) {
                req.setAttribute("error", "Job not found or no longer active.");
                req.getRequestDispatcher("/WEB-INF/jsp/student/job_detail.jsp").forward(req, resp);
                return;
            }

            List<Skill> jobSkills = skillDAO.findByJobId(jobId);
            List<Skill> studentSkills = skillDAO.findByStudentId(userId);
            boolean alreadyApplied = applicationDAO.hasApplied(userId, jobId);

            // Calculate match
            List<String> jobSkillNames = jobSkills.stream().map(Skill::getName).collect(Collectors.toList());
            List<String> studentSkillNames = studentSkills.stream().map(Skill::getName).collect(Collectors.toList());
            MatchService.MatchResult matchResult = matchService.calculateMatch(studentSkillNames, jobSkillNames);

            req.setAttribute("job", job);
            req.setAttribute("jobSkills", jobSkills);
            req.setAttribute("studentSkills", studentSkills);
            req.setAttribute("matchResult", matchResult);
            req.setAttribute("alreadyApplied", alreadyApplied);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/student/jobs");
            return;
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load job details.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/student/job_detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");
        String jobIdStr = req.getParameter("jobId");

        if (jobIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/student/jobs");
            return;
        }

        try {
            long jobId = Long.parseLong(jobIdStr);

            // Prevent duplicate applications
            if (applicationDAO.hasApplied(userId, jobId)) {
                req.setAttribute("error", "You have already applied to this job.");
                doGet(req, resp);
                return;
            }

            // Check job is active
            Job job = jobDAO.findById(jobId);
            if (job == null || !job.isActive()) {
                req.setAttribute("error", "This job is no longer accepting applications.");
                doGet(req, resp);
                return;
            }

            // Calculate match score
            List<Skill> jobSkills = skillDAO.findByJobId(jobId);
            List<Skill> studentSkills = skillDAO.findByStudentId(userId);
            List<String> jobSkillNames = jobSkills.stream().map(Skill::getName).collect(Collectors.toList());
            List<String> studentSkillNames = studentSkills.stream().map(Skill::getName).collect(Collectors.toList());
            MatchService.MatchResult matchResult = matchService.calculateMatch(studentSkillNames, jobSkillNames);

            Application app = new Application();
            app.setStudentId(userId);
            app.setJobId(jobId);
            app.setStatus("APPLIED");
            app.setMatchScore(matchResult.getScore());
            applicationDAO.insert(app);

            resp.sendRedirect(req.getContextPath() + "/student/applications?applied=true");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/student/jobs");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not submit application. Please try again.");
            doGet(req, resp);
        }
    }
}
