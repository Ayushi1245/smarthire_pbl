<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Job, com.smarthire.model.Application, com.smarthire.model.StudentProfile, com.smarthire.model.Skill, com.smarthire.model.Resume, java.util.List, java.util.Map, java.text.SimpleDateFormat" %>
<%
    Job job = (Job) request.getAttribute("job");
    @SuppressWarnings("unchecked")
    List<Application> applicants = (List<Application>) request.getAttribute("applicants");
    @SuppressWarnings("unchecked")
    Map<Long, StudentProfile> profileMap = (Map<Long, StudentProfile>) request.getAttribute("profileMap");
    @SuppressWarnings("unchecked")
    Map<Long, List<Skill>> studentSkillsMap = (Map<Long, List<Skill>>) request.getAttribute("studentSkillsMap");
    @SuppressWarnings("unchecked")
    Map<Long, Resume> resumeMap = (Map<Long, Resume>) request.getAttribute("resumeMap");
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="<%= (job != null ? job.getTitle() : \"Applicants\") + \" - Applicant Review\" %>" />
</jsp:include>

<main class="container">
    <div style="margin-bottom: 20px;">
        <a href="<%= request.getContextPath() %>/recruiter/dashboard" style="color: var(--text-muted); text-decoration: none; font-size: 0.9rem;">&larr; Back to Dashboard</a>
    </div>

    <div class="page-header">
        <div>
            <h1 class="page-title">Applicants for <%= job != null ? job.getTitle() : "Position" %></h1>
            <p class="page-subtitle">Ranked by automated skill match score. Review candidate profiles, resumes, and manage review stages.</p>
        </div>
        <div>
            <% if (job != null) { %>
                <a href="<%= request.getContextPath() %>/recruiter/job/edit?id=<%= job.getId() %>" class="btn btn-secondary">Edit Job Posting</a>
            <% } %>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (applicants == null || applicants.isEmpty()) { %>
        <div class="card" style="text-align: center; padding: 48px 24px;">
            <h3 style="font-size: 1.25rem; font-weight: 600; color: var(--text-muted); margin-bottom: 8px;">No applicants yet</h3>
            <p style="color: var(--text-muted); font-size: 0.95rem;">Candidates who meet your minimum requirements and apply will appear here ranked by their match score.</p>
        </div>
    <% } else { %>
        <div style="display: grid; gap: 16px;">
            <% for (Application app : applicants) { 
                long sId = app.getStudentId();
                StudentProfile sp = profileMap != null ? profileMap.get(sId) : null;
                List<Skill> sSkills = studentSkillsMap != null ? studentSkillsMap.get(sId) : null;
                Resume resume = resumeMap != null ? resumeMap.get(sId) : null;
                double score = app.getMatchScore() != null ? app.getMatchScore() : 0.0;
                String scoreBadge = score >= 75 ? "match-high" : (score >= 40 ? "match-medium" : "match-low");
            %>
                <div class="card" style="margin-bottom: 0;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 12px; flex-wrap: wrap;">
                        <div>
                            <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
                                <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--text-main); margin-bottom: 0;">
                                    <%= app.getStudentName() != null ? app.getStudentName() : "Applicant #" + sId %>
                                </h2>
                                <span class="match-badge <%= scoreBadge %>" style="font-size: 0.9rem; padding: 4px 10px;">
                                    <%= (int)Math.round(score) %>% Match
                                </span>
                                <span class="status-badge status-<%= app.getStatus() %>">
                                    <%= app.getStatus() %>
                                </span>
                            </div>

                            <div style="font-size: 0.9rem; color: var(--text-muted); margin-top: 6px; display: flex; gap: 16px; flex-wrap: wrap;">
                                <span>Email: <a href="mailto:<%= app.getStudentEmail() %>" style="color: var(--primary); text-decoration: none;"><%= app.getStudentEmail() %></a></span>
                                <% if (sp != null && sp.getEducation() != null && !sp.getEducation().isEmpty()) { %>
                                    <span>&bull;</span>
                                    <span><%= sp.getEducation() %></span>
                                <% } %>
                                <% if (sp != null && sp.getCgpa() != null) { %>
                                    <span>&bull;</span>
                                    <span>CGPA: <strong><%= String.format("%.2f", sp.getCgpa()) %></strong></span>
                                <% } %>
                                <span>&bull;</span>
                                <span>Applied: <%= app.getAppliedAt() != null ? sdf.format(app.getAppliedAt()) : "-" %></span>
                            </div>
                        </div>

                        <!-- Resume link -->
                        <div style="flex-shrink: 0;">
                            <% if (resume != null) { %>
                                <a href="<%= request.getContextPath() %>/resume/download?download=<%= resume.getId() %>" class="btn btn-secondary btn-sm" target="_blank">
                                    &darr; Download Resume
                                </a>
                            <% } else { %>
                                <span style="font-size: 0.8rem; color: var(--text-muted); font-style: italic;">No resume uploaded</span>
                            <% } %>
                        </div>
                    </div>

                    <!-- Student's Skills -->
                    <div style="margin: 12px 0;">
                        <span style="font-size: 0.8rem; font-weight: 600; color: var(--text-muted); text-transform: uppercase;">Candidate Skills:</span>
                        <div class="skill-tags" style="margin-top: 4px;">
                            <% if (sSkills != null && !sSkills.isEmpty()) { 
                                for (Skill sk : sSkills) { %>
                                    <span class="skill-tag"><%= sk.getName() %></span>
                            <%  } 
                               } else { %>
                                <span style="font-size: 0.825rem; color: var(--text-muted);">No skills on profile</span>
                            <% } %>
                        </div>
                    </div>

                    <!-- Application Status Change Form -->
                    <div style="border-top: 1px solid var(--border); padding-top: 12px; margin-top: 14px; display: flex; justify-content: flex-end; align-items: center; gap: 12px; flex-wrap: wrap;">
                        <form action="<%= request.getContextPath() %>/recruiter/job/applicants" method="POST" style="display: flex; align-items: center; gap: 10px;">
                            <input type="hidden" name="jobId" value="<%= job != null ? job.getId() : 0 %>">
                            <input type="hidden" name="applicationId" value="<%= app.getId() %>">
                            
                            <label style="font-size: 0.85rem; font-weight: 600; color: var(--text-muted);">Update Status:</label>
                            <select name="status" class="form-control" style="width: auto; padding: 6px 10px; font-size: 0.85rem;">
                                <% 
                                    String[] statuses = {"APPLIED", "UNDER_REVIEW", "SHORTLISTED", "ACCEPTED", "REJECTED"};
                                    for (String st : statuses) {
                                %>
                                    <option value="<%= st %>" <%= st.equals(app.getStatus()) ? "selected" : "" %>><%= st %></option>
                                <% } %>
                            </select>

                            <button type="submit" class="btn btn-primary btn-sm">Save</button>
                        </form>
                    </div>
                </div>
            <% } %>
        </div>
    <% } %>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
