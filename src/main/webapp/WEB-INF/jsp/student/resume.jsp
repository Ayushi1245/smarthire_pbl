<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Resume, java.util.List, java.text.SimpleDateFormat" %>
<%
    @SuppressWarnings("unchecked")
    List<Resume> resumes = (List<Resume>) request.getAttribute("resumes");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Manage Resume" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Resume Management</h1>
            <p class="page-subtitle">Upload and maintain your curriculum vitae for prospective employers</p>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 28px;">
        <!-- Upload New File Card -->
        <div class="card">
            <h2 class="card-title">Upload New Resume</h2>
            <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 20px;">
                Accepted formats: PDF or Word (.doc, .docx). Maximum file size is 5MB. Files are stored securely on the server.
            </p>

            <form action="<%= request.getContextPath() %>/student/resume" method="POST" enctype="multipart/form-data">
                <div class="form-group">
                    <label class="form-label" for="resumeFile">Select Document</label>
                    <input type="file" id="resumeFile" name="resumeFile" class="form-control" accept=".pdf,.doc,.docx" required>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Upload Document</button>
            </form>
        </div>

        <!-- Current Resumes List -->
        <div class="card">
            <h2 class="card-title">Uploaded Resumes</h2>

            <% if (resumes == null || resumes.isEmpty()) { %>
                <p style="color: var(--text-muted); font-size: 0.95rem;">No resume uploaded yet. Recruiters prefer reviewing applications that have an attached resume.</p>
            <% } else { %>
                <div style="display: grid; gap: 12px;">
                    <% for (Resume r : resumes) { %>
                        <div style="display: flex; justify-content: space-between; align-items: center; padding: 14px 16px; border: 1px solid var(--border); border-radius: var(--radius-sm); background: #f8fafc;">
                            <div>
                                <div style="font-weight: 600; font-size: 0.95rem; color: var(--text-main);"><%= r.getOriginalName() %></div>
                                <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 2px;">
                                    Uploaded: <%= r.getUploadedAt() != null ? sdf.format(r.getUploadedAt()) : "-" %>
                                </div>
                            </div>
                            <div style="display: flex; gap: 8px;">
                                <a href="<%= request.getContextPath() %>/student/resume?download=<%= r.getId() %>" class="btn btn-secondary btn-sm">Download</a>
                                <a href="<%= request.getContextPath() %>/student/resume?delete=<%= r.getId() %>" onclick="return confirm('Are you sure you want to remove this resume?')" class="btn btn-danger btn-sm">Delete</a>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } %>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
