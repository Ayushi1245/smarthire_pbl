package com.smarthire.servlet;

import com.smarthire.dao.JobDAO;
import com.smarthire.dao.SkillDAO;
import com.smarthire.dao.ApplicationDAO;
import com.smarthire.model.Job;
import com.smarthire.model.Skill;
import com.smarthire.service.MatchService;

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

@WebServlet("/student/jobs")
public class StudentJobsServlet extends HttpServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final MatchService matchService = new MatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            String search = req.getParameter("search");
            List<Job> jobs;
            if (search != null && !search.trim().isEmpty()) {
                jobs = jobDAO.searchActive(search.trim());
                req.setAttribute("search", search.trim());
            } else {
                jobs = jobDAO.findAllActive();
            }

            // Get student skills for match scoring
            List<Skill> studentSkills = skillDAO.findByStudentId(userId);
            List<String> studentSkillNames = studentSkills.stream().map(Skill::getName).collect(Collectors.toList());

            // Calculate match scores for each job
            List<Double> matchScores = new ArrayList<>();
            List<List<Skill>> jobSkillsList = new ArrayList<>();
            for (Job job : jobs) {
                List<Skill> jobSkills = skillDAO.findByJobId(job.getId());
                jobSkillsList.add(jobSkills);
                List<String> jobSkillNames = jobSkills.stream().map(Skill::getName).collect(Collectors.toList());
                MatchService.MatchResult result = matchService.calculateMatch(studentSkillNames, jobSkillNames);
                matchScores.add(result.getScore());
            }

            req.setAttribute("jobs", jobs);
            req.setAttribute("matchScores", matchScores);
            req.setAttribute("jobSkillsList", jobSkillsList);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load job listings.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/student/jobs.jsp").forward(req, resp);
    }
}
