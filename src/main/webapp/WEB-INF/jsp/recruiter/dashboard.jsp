<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Job, com.smarthire.model.RecruiterProfile, java.util.List, java.util.Map, java.text.SimpleDateFormat" %>
<%
    RecruiterProfile profile = (RecruiterProfile) request.getAttribute("profile");
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    @SuppressWarnings("unchecked")
    Map<Long, Integer> appCounts = (Map<Long, Integer>) request.getAttribute("appCounts");
    Integer totalApplications = (Integer) request.getAttribute("totalApplications");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Recruiter Dashboard" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title"><%= (profile != null && profile.getCompanyName() != null) ? profile.getCompanyName() : session.getAttribute("fullName") %> Hiring Portal</h1>
            <p class="page-subtitle">Manage your open job postings and evaluate ranked applicant pools</p>
        </div>
        <div style="display: flex; gap: 10px;">
            <a href="<%= request.getContextPath() %>/recruiter/profile" class="btn btn-secondary">Edit Company Profile</a>
            <a href="<%= request.getContextPath() %>/recruiter/job/create" class="btn btn-primary">+ Post New Opening</a>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <!-- Quick Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Active Job Openings</div>
            <div class="stat-value"><%= jobs != null ? jobs.size() : 0 %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Total Candidate Applications</div>
            <div class="stat-value" style="color: #0284c7;"><%= totalApplications != null ? totalApplications : 0 %></div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Company</div>
            <div class="stat-value" style="font-size: 1.35rem; color: #166534; font-weight: 600;">
                <%= (profile != null && profile.getCompanyName() != null) ? profile.getCompanyName() : "Not Specified" %>
            </div>
        </div>
    </div>

    <!-- Job Postings List -->
    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 class="card-title" style="margin-bottom: 0;">Your Job Postings</h2>
            <a href="<%= request.getContextPath() %>/recruiter/job/create" class="btn btn-primary btn-sm">+ Post Job</a>
        </div>

        <% if (jobs == null || jobs.isEmpty()) { %>
            <div style="text-align: center; padding: 40px 20px;">
                <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 16px;">You haven't posted any jobs yet. Create an opening to start receiving scored applicants.</p>
                <a href="<%= request.getContextPath() %>/recruiter/job/create" class="btn btn-primary">Create Your First Job</a>
            </div>
        <% } else { %>
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Job Title</th>
                            <th>Location</th>
                            <th>Type</th>
                            <th>Applicants</th>
                            <th>Status</th>
                            <th>Created On</th>
                            <th style="text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Job j : jobs) { 
                            int count = (appCounts != null && appCounts.containsKey(j.getId())) ? appCounts.get(j.getId()) : 0;
                        %>
                            <tr>
                                <td style="font-weight: 600;">
                                    <a href="<%= request.getContextPath() %>/recruiter/job/applicants?jobId=<%= j.getId() %>" style="color: var(--primary); text-decoration: none;">
                                        <%= j.getTitle() %>
                                    </a>
                                </td>
                                <td><%= j.getLocation() %></td>
                                <td><%= j.getEmploymentType() %></td>
                                <td>
                                    <%
                                        String countColor = count > 0 ? "#1d4ed8" : "#64748b";
                                    %>
                                    <span style="font-weight: 700; color: <%= countColor %>">
                                        <%= count %> candidate<%= count == 1 ? "" : "s" %>
                                    </span>
                                </td>
                                <td>
                                    <%
                                        String badgeBg = j.isActive() ? "#dcfce7" : "#f1f5f9";
                                        String badgeColor = j.isActive() ? "#15803d" : "#64748b";
                                    %>
                                    <span class="status-badge" style="background: <%= badgeBg %>; color: <%= badgeColor %>">
                                        <%= j.isActive() ? "ACTIVE" : "CLOSED" %>
                                    </span>
                                </td>
                                <td><%= j.getCreatedAt() != null ? sdf.format(j.getCreatedAt()) : "-" %></td>
                                <td style="text-align: right;">
                                    <div style="display: inline-flex; gap: 8px;">
                                        <a href="<%= request.getContextPath() %>/recruiter/job/applicants?jobId=<%= j.getId() %>" class="btn btn-primary btn-sm">Applicants (<%= count %>)</a>
                                        <a href="<%= request.getContextPath() %>/recruiter/job/edit?id=<%= j.getId() %>" class="btn btn-secondary btn-sm">Edit</a>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
