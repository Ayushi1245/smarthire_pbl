package com.smarthire.servlet;

import com.smarthire.dao.ApplicationDAO;
import com.smarthire.dao.JobDAO;
import com.smarthire.dao.ResumeDAO;
import com.smarthire.dao.SkillDAO;
import com.smarthire.dao.StudentProfileDAO;
import com.smarthire.model.Application;
import com.smarthire.model.Job;
import com.smarthire.model.Resume;
import com.smarthire.model.Skill;
import com.smarthire.model.StudentProfile;

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

@WebServlet(urlPatterns = {"/recruiter/job/applicants", "/recruiter/jobs/applicants"})
public class ApplicantReviewServlet extends HttpServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final StudentProfileDAO studentProfileDAO = new StudentProfileDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final ResumeDAO resumeDAO = new ResumeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        String jobIdStr = req.getParameter("jobId");
        if (jobIdStr == null || jobIdStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/recruiter/dashboard");
            return;
        }

        try {
            long jobId = Long.parseLong(jobIdStr);
            Job job = jobDAO.findById(jobId);
            if (job == null || job.getRecruiterId() != userId) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            List<Application> applicants = applicationDAO.findByJobId(jobId);
            Map<Long, StudentProfile> profileMap = new HashMap<>();
            Map<Long, List<Skill>> studentSkillsMap = new HashMap<>();
            Map<Long, Resume> resumeMap = new HashMap<>();

            for (Application app : applicants) {
                long studentId = app.getStudentId();
                if (!profileMap.containsKey(studentId)) {
                    StudentProfile sp = studentProfileDAO.findByUserId(studentId);
                    profileMap.put(studentId, sp);

                    List<Skill> skills = skillDAO.findByStudentId(studentId);
                    studentSkillsMap.put(studentId, skills);

                    List<Resume> resumes = resumeDAO.findByStudentId(studentId);
                    if (!resumes.isEmpty()) {
                        resumeMap.put(studentId, resumes.get(0));
                    }
                }
            }

            req.setAttribute("job", job);
            req.setAttribute("applicants", applicants);
            req.setAttribute("profileMap", profileMap);
            req.setAttribute("studentSkillsMap", studentSkillsMap);
            req.setAttribute("resumeMap", resumeMap);

            req.getRequestDispatcher("/WEB-INF/jsp/recruiter/applicants.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("error", "Error loading applicants: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/recruiter/dashboard.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        String appIdStr = req.getParameter("applicationId");
        String jobIdStr = req.getParameter("jobId");
        String status = req.getParameter("status");

        if (appIdStr != null && jobIdStr != null && status != null) {
            try {
                long appId = Long.parseLong(appIdStr);
                applicationDAO.updateStatus(appId, status.trim(), userId);
            } catch (SQLException ignored) {}
        }

        resp.sendRedirect(req.getContextPath() + "/recruiter/job/applicants?jobId=" + jobIdStr);
    }
}
