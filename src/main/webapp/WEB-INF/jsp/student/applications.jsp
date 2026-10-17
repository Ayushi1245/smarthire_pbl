<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Application, java.util.List, java.text.SimpleDateFormat" %>
<%
    @SuppressWarnings("unchecked")
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="My Applications" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">My Job Applications</h1>
            <p class="page-subtitle">Track status and match feedback for all your submitted applications</p>
        </div>
        <div>
            <a href="<%= request.getContextPath() %>/student/jobs" class="btn btn-primary">+ Find More Jobs</a>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (applications == null || applications.isEmpty()) { %>
        <div class="card" style="text-align: center; padding: 48px 24px;">
            <h3 style="font-size: 1.25rem; font-weight: 600; color: var(--text-muted); margin-bottom: 8px;">No applications found</h3>
            <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 16px;">You haven't submitted any job applications yet.</p>
            <a href="<%= request.getContextPath() %>/student/jobs" class="btn btn-primary">Browse Available Jobs</a>
        </div>
    <% } else { %>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Job Title</th>
                        <th>Company</th>
                        <th>Match Score</th>
                        <th>Applied On</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Application app : applications) { 
                        double score = app.getMatchScore() != null ? app.getMatchScore() : 0;
                        String badgeClass = score >= 75 ? "match-high" : (score >= 40 ? "match-medium" : "match-low");
                    %>
                        <tr>
                            <td style="font-weight: 600;"><%= app.getJobTitle() %></td>
                            <td><%= app.getCompanyName() != null ? app.getCompanyName() : "Confidential" %></td>
                            <td>
                                <span class="match-badge <%= badgeClass %>"><%= (int)Math.round(score) %>%</span>
                            </td>
                            <td><%= app.getAppliedAt() != null ? sdf.format(app.getAppliedAt()) : "-" %></td>
                            <td>
                                <span class="status-badge status-<%= app.getStatus() %>"><%= app.getStatus() %></span>
                            </td>
                            <td>
                                <a href="<%= request.getContextPath() %>/student/apply?id=<%= app.getJobId() %>" class="btn btn-secondary btn-sm">View Job</a>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
