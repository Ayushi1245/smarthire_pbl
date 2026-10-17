package com.smarthire.servlet;

import com.smarthire.dao.ResumeDAO;
import com.smarthire.model.Resume;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Handles resume upload and management.
 * Stores files outside the web root in a configurable directory.
 */
@WebServlet(urlPatterns = {"/student/resume", "/resume/download"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 5 * 1024 * 1024,       // 5 MB
    maxRequestSize = 10 * 1024 * 1024    // 10 MB
)
public class ResumeServlet extends HttpServlet {

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private String uploadDir;

    @Override
    public void init() throws ServletException {
        // Store uploads outside web root
        uploadDir = System.getenv().getOrDefault("SMARTHIRE_UPLOAD_DIR",
                System.getProperty("user.home") + File.separator + "smarthire-uploads");
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        // Handle download
        String downloadId = req.getParameter("download");
        if (downloadId != null) {
            handleDownload(req, resp, userId, Long.parseLong(downloadId));
            return;
        }

        // Handle delete
        String deleteId = req.getParameter("delete");
        if (deleteId != null) {
            handleDelete(req, resp, userId, Long.parseLong(deleteId));
            return;
        }

        // List resumes
        try {
            ResumeDAO resumeDAO = new ResumeDAO();
            List<Resume> resumes = resumeDAO.findByStudentId(userId);
            req.setAttribute("resumes", resumes);
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load resumes.");
        }
        req.getRequestDispatcher("/WEB-INF/jsp/student/resume.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (long) session.getAttribute("userId");

        Part filePart = req.getPart("resumeFile");
        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("error", "Please select a file to upload.");
            doGet(req, resp);
            return;
        }

        // Validate file type
        String contentType = filePart.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            req.setAttribute("error", "Only PDF and Word documents are allowed.");
            doGet(req, resp);
            return;
        }

        // Validate file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            req.setAttribute("error", "File size must be under 5 MB.");
            doGet(req, resp);
            return;
        }

        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String storedName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir, storedName);

        try (InputStream is = filePart.getInputStream()) {
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            Resume resume = new Resume();
            resume.setStudentId(userId);
            resume.setOriginalName(originalName);
            resume.setStoredName(storedName);
            resume.setFilePath(filePath.toString());
            new ResumeDAO().insert(resume);

            req.setAttribute("success", "Resume uploaded successfully!");
        } catch (SQLException e) {
            // Clean up file on DB failure
            Files.deleteIfExists(filePath);
            req.setAttribute("error", "Could not save resume record.");
        }

        doGet(req, resp);
    }

    private void handleDownload(HttpServletRequest req, HttpServletResponse resp, long userId, long resumeId) throws IOException {
        HttpSession session = req.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;
        try {
            Resume resume = new ResumeDAO().findById(resumeId);
            if (resume == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            // Allow owner student or any recruiter
            if (resume.getStudentId() != userId && !"RECRUITER".equals(role)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Path path = Paths.get(resume.getFilePath());
            if (!Files.exists(path)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + resume.getOriginalName() + "\"");
            Files.copy(path, resp.getOutputStream());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, long userId, long resumeId) throws IOException {
        try {
            Resume resume = new ResumeDAO().findById(resumeId);
            if (resume != null && resume.getStudentId() == userId) {
                Files.deleteIfExists(Paths.get(resume.getFilePath()));
                new ResumeDAO().delete(resumeId, userId);
            }
        } catch (SQLException e) {
            // silently fail
        }
        resp.sendRedirect(req.getContextPath() + "/student/resume");
    }
}
