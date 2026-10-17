<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Job, com.smarthire.model.Skill, com.smarthire.model.StudentProfile, com.smarthire.model.Resume, com.smarthire.service.MatchService.MatchResult, java.util.List, java.util.Set" %>
<%
    Job job = (Job) request.getAttribute("job");
    @SuppressWarnings("unchecked")
    List<Skill> jobSkills = (List<Skill>) request.getAttribute("jobSkills");
    @SuppressWarnings("unchecked")
    List<Skill> studentSkills = (List<Skill>) request.getAttribute("studentSkills");
    MatchResult matchResult = (MatchResult) request.getAttribute("matchResult");
    StudentProfile profile = (StudentProfile) request.getAttribute("profile");
    Boolean hasApplied = (Boolean) request.getAttribute("hasApplied");
    String eligibilityError = (String) request.getAttribute("eligibilityError");
    @SuppressWarnings("unchecked")
    List<Resume> resumes = (List<Resume>) request.getAttribute("resumes");

    double score = matchResult != null ? matchResult.getScore() : 0.0;
    Set<String> matched = matchResult != null ? matchResult.getMatchedSkills() : Set.of();
    Set<String> missing = matchResult != null ? matchResult.getMissingSkills() : Set.of();
    String scoreBadge = score >= 75 ? "match-high" : (score >= 40 ? "match-medium" : "match-low");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="<%= job != null ? job.getTitle() : \"Job Details\" %>" />
</jsp:include>

<main class="container">
    <div style="margin-bottom: 20px;">
        <a href="<%= request.getContextPath() %>/student/jobs" style="color: var(--text-muted); text-decoration: none; font-size: 0.9rem;">&larr; Back to all job openings</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <% if (job != null) { %>
        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 28px;">
            <!-- Main Job Details -->
            <div>
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 16px;">
                        <div>
                            <h1 style="font-size: 1.85rem; font-weight: 700; color: var(--text-main);"><%= job.getTitle() %></h1>
                            <div style="font-size: 1rem; color: var(--text-muted); margin-top: 6px; display: flex; gap: 16px; flex-wrap: wrap;">
                                <span style="font-weight: 600; color: var(--primary);"><%= job.getCompanyName() != null ? job.getCompanyName() : "Confidential Recruiter" %></span>
                                <span>&bull;</span>
                                <span><%= job.getLocation() %></span>
                                <span>&bull;</span>
                                <span><%= job.getEmploymentType() %></span>
                            </div>
                        </div>
                        <span class="match-badge <%= scoreBadge %>" style="font-size: 1rem; padding: 8px 16px;">
                            <%= (int)Math.round(score) %>% Match
                        </span>
                    </div>

                    <div style="border-top: 1px solid var(--border); padding-top: 20px; margin-top: 20px;">
                        <h2 style="font-size: 1.15rem; font-weight: 600; margin-bottom: 12px;">Job Description</h2>
                        <div style="white-space: pre-wrap; line-height: 1.6; color: #334155;"><%= job.getDescription() %></div>
                    </div>

                    <!-- Skills Assessment Breakdown -->
                    <div style="border-top: 1px solid var(--border); padding-top: 20px; margin-top: 24px;">
                        <h2 style="font-size: 1.15rem; font-weight: 600; margin-bottom: 12px;">Skill Alignment Analysis</h2>
                        <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 14px;">Here is how your current profile skills compare against this position:</p>

                        <% if (jobSkills != null && !jobSkills.isEmpty()) { %>
                            <div style="display: grid; gap: 10px;">
                                <div>
                                    <span style="font-size: 0.85rem; font-weight: 600; color: #166534;">Matched Skills (<%= matched.size() %>):</span>
                                    <div class="skill-tags">
                                        <% if (matched.isEmpty()) { %>
                                            <span style="font-size: 0.825rem; color: var(--text-muted);">None of your current profile skills match this role.</span>
                                        <% } else { 
                                            for (String s : matched) { %>
                                                <span class="skill-tag matched">&check; <%= s %></span>
                                        <%  } 
                                           } %>
                                    </div>
                                </div>

                                <div>
                                    <span style="font-size: 0.85rem; font-weight: 600; color: #991b1b;">Missing Required Skills (<%= missing.size() %>):</span>
                                    <div class="skill-tags">
                                        <% if (missing.isEmpty()) { %>
                                            <span style="font-size: 0.825rem; color: #166534; font-weight: 600;">You have all the required skills!</span>
                                        <% } else { 
                                            for (String s : missing) { %>
                                                <span class="skill-tag" style="background: #fef2f2; color: #991b1b; border-color: #fecaca;">&times; <%= s %></span>
                                        <%  } 
                                           } %>
                                    </div>
                                </div>
                            </div>
                        <% } else { %>
                            <p style="color: var(--text-muted); font-size: 0.9rem;">No specific skills were listed by the recruiter for this position.</p>
                        <% } %>
                    </div>
                </div>
            </div>

            <!-- Application Sidebar Card -->
            <div>
                <div class="card">
                    <h2 class="card-title">Application Status</h2>

                    <div style="margin-bottom: 16px;">
                        <span style="font-size: 0.85rem; color: var(--text-muted); display: block;">Eligibility Criteria:</span>
                        <% if (job.getMinCgpa() != null && job.getMinCgpa() > 0) { %>
                            <p style="font-size: 0.925rem; margin-top: 4px;">
                                Required CGPA: <strong><%= String.format("%.2f", job.getMinCgpa()) %></strong><br>
                                Your CGPA: <strong><%= (profile != null && profile.getCgpa() != null) ? String.format("%.2f", profile.getCgpa()) : "Not Provided" %></strong>
                            </p>
                        <% } else { %>
                            <p style="font-size: 0.925rem; margin-top: 4px; color: #166534;">No minimum CGPA required.</p>
                        <% } %>
                    </div>

                    <% if (Boolean.TRUE.equals(hasApplied)) { %>
                        <div class="alert alert-success" style="margin-bottom: 0;">
                            <strong>Applied!</strong> You have already submitted an application for this position.
                        </div>
                        <div style="margin-top: 14px;">
                            <a href="<%= request.getContextPath() %>/student/applications" class="btn btn-secondary" style="width: 100%;">View All Applications</a>
                        </div>
                    <% } else if (eligibilityError != null) { %>
                        <div class="alert alert-error">
                            <strong>Not Eligible:</strong> <%= eligibilityError %>
                        </div>
                        <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 8px;">
                            Update your profile or acquire new skills to qualify for this role.
                        </p>
                        <a href="<%= request.getContextPath() %>/student/profile" class="btn btn-secondary btn-sm" style="margin-top: 10px; width: 100%;">Update Profile</a>
                    <% } else { %>
                        <form action="<%= request.getContextPath() %>/student/apply" method="POST">
                            <input type="hidden" name="jobId" value="<%= job.getId() %>">

                            <div class="form-group" style="margin-top: 16px;">
                                <label class="form-label">Resume on File:</label>
                                <% if (resumes == null || resumes.isEmpty()) { %>
                                    <p style="font-size: 0.85rem; color: #b45309; margin-bottom: 10px;">
                                        &bull; You haven't uploaded a resume yet. We recommend uploading one before applying.
                                    </p>
                                    <a href="<%= request.getContextPath() %>/student/resume" class="btn btn-secondary btn-sm" style="width: 100%; margin-bottom: 12px;">+ Upload Resume First</a>
                                <% } else { %>
                                    <p style="font-size: 0.85rem; color: #166534; font-weight: 500; margin-bottom: 12px;">
                                        &check; Active resume: <strong><%= resumes.get(0).getOriginalName() %></strong>
                                    </p>
                                <% } %>
                            </div>

                            <button type="submit" class="btn btn-primary" style="width: 100%;">Submit Application</button>
                        </form>
                    <% } %>
                </div>
            </div>
        </div>
    <% } %>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
