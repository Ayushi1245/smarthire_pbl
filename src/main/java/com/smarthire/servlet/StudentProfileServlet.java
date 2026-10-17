package com.smarthire.servlet;

import com.smarthire.dao.StudentProfileDAO;
import com.smarthire.dao.SkillDAO;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/student/profile")
public class StudentProfileServlet extends HttpServlet {

    private final StudentProfileDAO profileDAO = new StudentProfileDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            StudentProfile profile = profileDAO.findByUserId(userId);
            List<Skill> allSkills = skillDAO.findAll();
            List<Skill> studentSkills = skillDAO.findByStudentId(userId);

            req.setAttribute("profile", profile);
            req.setAttribute("allSkills", allSkills);
            req.setAttribute("studentSkills", studentSkills);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load profile.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/student/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        try {
            // Update profile
            StudentProfile profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setEducation(req.getParameter("education"));
            String gradYear = req.getParameter("graduationYear");
            if (gradYear != null && !gradYear.trim().isEmpty()) {
                profile.setGraduationYear(Integer.parseInt(gradYear.trim()));
            }
            String cgpa = req.getParameter("cgpa");
            if (cgpa != null && !cgpa.trim().isEmpty()) {
                profile.setCgpa(Double.parseDouble(cgpa.trim()));
            }
            profile.setSummary(req.getParameter("summary"));
            profile.setExperience(req.getParameter("experience"));
            profileDAO.insertOrUpdate(profile);

            // Update skills
            String[] skillIds = req.getParameterValues("skills");
            List<Long> selectedSkills = new ArrayList<>();
            if (skillIds != null) {
                for (String sid : skillIds) {
                    selectedSkills.add(Long.parseLong(sid));
                }
            }
            // Handle custom skills entered by student
            String customSkills = req.getParameter("customSkills");
            if (customSkills != null && !customSkills.trim().isEmpty()) {
                for (String skillName : customSkills.split(",")) {
                    String name = skillName.trim();
                    if (!name.isEmpty()) {
                        long newId = skillDAO.insertIfNotExists(name);
                        if (!selectedSkills.contains(newId)) {
                            selectedSkills.add(newId);
                        }
                    }
                }
            }
            skillDAO.updateStudentSkills(userId, selectedSkills);

            req.setAttribute("success", "Profile updated successfully!");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid number format in profile fields.");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not update profile.");
        }

        doGet(req, resp);
    }
}
