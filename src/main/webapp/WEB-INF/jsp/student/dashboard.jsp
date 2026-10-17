<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.StudentProfile, com.smarthire.model.Skill, com.smarthire.model.Application, java.util.List" %>
<%
    StudentProfile profile = (StudentProfile) request.getAttribute("profile");
    @SuppressWarnings("unchecked")
    List<Skill> skills = (List<Skill>) request.getAttribute("skills");
    Integer applicationCount = (Integer) request.getAttribute("applicationCount");
    @SuppressWarnings("unchecked")
    List<Application> recentApplications = (List<Application>) request.getAttribute("recentApplications");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Student Dashboard" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Welcome back, <%= session.getAttribute("fullName") %>!</h1>
            <p class="page-subtitle">Track your job applications, profile strength, and matching opportunities</p>
        </div>
        <div>
            <a href="<%= request.getContextPath() %>/student/jobs" class="btn btn-primary">Browse Matched Jobs</a>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Quick Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Applications Submitted</div>
            <div class="stat-value"><%= applicationCount != null ? applicationCount : 0 %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Skills Added</div>
            <div class="stat-value"><%= skills != null ? skills.size() : 0 %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Current CGPA</div>
            <div class="stat-value" style="color: #0d9488;"><%= (profile != null && profile.getCgpa() != null) ? String.format("%.2f", profile.getCgpa()) : "Not set" %></div>
        </div>
    </div>

    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
        <!-- Recent Applications -->
        <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h2 class="card-title" style="margin-bottom: 0;">Recent Applications</h2>
                <a href="<%= request.getContextPath() %>/student/applications" style="font-size: 0.875rem; color: var(--primary); text-decoration: none; font-weight: 600;">View All &rarr;</a>
            </div>

            <% if (recentApplications == null || recentApplications.isEmpty()) { %>
                <p style="color: var(--text-muted); font-size: 0.95rem;">You haven't applied to any jobs yet. Check out recommended openings!</p>
                <div style="margin-top: 16px;">
                    <a href="<%= request.getContextPath() %>/student/jobs" class="btn btn-secondary btn-sm">Explore Openings</a>
                </div>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Role</th>
                                <th>Company</th>
                                <th>Match</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Application app : recentApplications) { %>
                                <tr>
                                    <td style="font-weight: 600;"><%= app.getJobTitle() %></td>
                                    <td><%= app.getCompanyName() != null ? app.getCompanyName() : "Confidential" %></td>
                                    <td>
                                        <% 
                                            double score = app.getMatchScore() != null ? app.getMatchScore() : 0;
                                            String badgeClass = score >= 75 ? "match-high" : (score >= 40 ? "match-medium" : "match-low");
                                        %>
                                        <span class="match-badge <%= badgeClass %>"><%= (int)Math.round(score) %>%</span>
                                    </td>
                                    <td>
                                        <span class="status-badge status-<%= app.getStatus() %>"><%= app.getStatus() %></span>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>

        <!-- Profile & Skills Sidebar Card -->
        <div>
            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                    <h2 class="card-title" style="margin-bottom: 0;">My Skills</h2>
                    <a href="<%= request.getContextPath() %>/student/profile" style="font-size: 0.85rem; color: var(--primary); font-weight: 600; text-decoration: none;">Manage</a>
                </div>

                <% if (skills == null || skills.isEmpty()) { %>
                    <p style="color: var(--text-muted); font-size: 0.9rem;">Add your technical skills to get accurate match scores for job openings.</p>
                    <div style="margin-top: 12px;">
                        <a href="<%= request.getContextPath() %>/student/profile" class="btn btn-secondary btn-sm">+ Add Skills</a>
                    </div>
                <% } else { %>
                    <div class="skill-tags">
                        <% for (Skill sk : skills) { %>
                            <span class="skill-tag"><%= sk.getName() %></span>
                        <% } %>
                    </div>
                <% } %>
            </div>

            <div class="card">
                <h2 class="card-title">Resume & Files</h2>
                <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 16px;">Keep your resume updated for recruiter reviews.</p>
                <a href="<%= request.getContextPath() %>/student/resume" class="btn btn-secondary" style="width: 100%;">Upload / View Resume</a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
