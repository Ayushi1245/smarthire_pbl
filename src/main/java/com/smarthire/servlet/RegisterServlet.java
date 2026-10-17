package com.smarthire.servlet;

import com.smarthire.dao.UserDAO;
import com.smarthire.dao.StudentProfileDAO;
import com.smarthire.dao.RecruiterProfileDAO;
import com.smarthire.model.User;
import com.smarthire.model.StudentProfile;
import com.smarthire.model.RecruiterProfile;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final StudentProfileDAO studentProfileDAO = new StudentProfileDAO();
    private final RecruiterProfileDAO recruiterProfileDAO = new RecruiterProfileDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = trim(req.getParameter("fullName"));
        String email = trim(req.getParameter("email"));
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String role = trim(req.getParameter("role"));

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password == null || password.isEmpty() || role == null) {
            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            req.setAttribute("error", "Invalid email format.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }
        if (password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }
        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }
        if (!"STUDENT".equals(role) && !"RECRUITER".equals(role)) {
            req.setAttribute("error", "Invalid role selected.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                req.setAttribute("error", "An account with this email already exists.");
                req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
                return;
            }

            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(fullName, email, hash, role);
            long userId = userDAO.insert(user);

            if (userId > 0) {
                // Create initial empty profile
                if ("STUDENT".equals(role)) {
                    StudentProfile sp = new StudentProfile();
                    sp.setUserId(userId);
                    studentProfileDAO.insertOrUpdate(sp);
                } else {
                    // For recruiter, get company info from form
                    String companyName = trim(req.getParameter("companyName"));
                    if (companyName.isEmpty()) companyName = "My Company";
                    RecruiterProfile rp = new RecruiterProfile();
                    rp.setUserId(userId);
                    rp.setCompanyName(companyName);
                    recruiterProfileDAO.insertOrUpdate(rp);
                }
                resp.sendRedirect(req.getContextPath() + "/login?registered=true");
            } else {
                req.setAttribute("error", "Registration failed. Please try again.");
                req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("error", "A system error occurred. Please try again later.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
        }
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
}
