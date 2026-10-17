<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Job, com.smarthire.model.Skill, java.util.List" %>
<%
    @SuppressWarnings("unchecked")
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    @SuppressWarnings("unchecked")
    List<Double> matchScores = (List<Double>) request.getAttribute("matchScores");
    @SuppressWarnings("unchecked")
    List<List<Skill>> jobSkillsList = (List<List<Skill>>) request.getAttribute("jobSkillsList");
    String search = (String) request.getAttribute("search");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Browse Openings" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Explore Opportunities</h1>
            <p class="page-subtitle">View open positions scored dynamically according to your profile skills</p>
        </div>
    </div>

    <!-- Search Bar -->
    <div class="card" style="padding: 16px 20px; margin-bottom: 24px;">
        <form action="<%= request.getContextPath() %>/student/jobs" method="GET" style="display: flex; gap: 12px; align-items: center;">
            <input type="text" name="search" class="form-control" placeholder="Search by job title or keywords (e.g. Java, Full Stack, Cloud)..." value="<%= search != null ? search : "" %>">
            <button type="submit" class="btn btn-primary">Search</button>
            <% if (search != null && !search.isEmpty()) { %>
                <a href="<%= request.getContextPath() %>/student/jobs" class="btn btn-secondary">Clear</a>
            <% } %>
        </form>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (jobs == null || jobs.isEmpty()) { %>
        <div class="card" style="text-align: center; padding: 48px 24px;">
            <h3 style="font-size: 1.25rem; font-weight: 600; color: var(--text-muted); margin-bottom: 8px;">No matching jobs found</h3>
            <p style="color: var(--text-muted); font-size: 0.95rem;">Try modifying your search criteria or check back later for new openings.</p>
        </div>
    <% } else { %>
        <div style="display: grid; gap: 16px;">
            <% for (int i = 0; i < jobs.size(); i++) { 
                Job job = jobs.get(i);
                double score = (matchScores != null && i < matchScores.size()) ? matchScores.get(i) : 0;
                List<Skill> reqSkills = (jobSkillsList != null && i < jobSkillsList.size()) ? jobSkillsList.get(i) : null;
                String scoreBadge = score >= 75 ? "match-high" : (score >= 40 ? "match-medium" : "match-low");
            %>
                <div class="card" style="margin-bottom: 0; display: flex; flex-direction: column; justify-content: space-between;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 12px;">
                        <div>
                            <h2 style="font-size: 1.25rem; font-weight: 700; color: var(--text-main); margin-bottom: 4px;">
                                <a href="<%= request.getContextPath() %>/student/apply?id=<%= job.getId() %>" style="color: inherit; text-decoration: none;">
                                    <%= job.getTitle() %>
                                </a>
                            </h2>
                            <div style="font-size: 0.925rem; color: var(--text-muted); display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
                                <span style="font-weight: 600; color: var(--text-main);"><%= job.getCompanyName() != null ? job.getCompanyName() : "Confidential Recruiter" %></span>
                                <span>&bull;</span>
                                <span><%= job.getLocation() %></span>
                                <span>&bull;</span>
                                <span><%= job.getEmploymentType() %></span>
                                <% if (job.getMinCgpa() != null && job.getMinCgpa() > 0) { %>
                                    <span>&bull;</span>
                                    <span>Min CGPA: <strong><%= String.format("%.2f", job.getMinCgpa()) %></strong></span>
                                <% } %>
                            </div>
                        </div>

                        <div style="text-align: right; flex-shrink: 0;">
                            <span class="match-badge <%= scoreBadge %>" style="font-size: 0.9rem; padding: 6px 12px;">
                                Match: <%= (int)Math.round(score) %>%
                            </span>
                        </div>
                    </div>

                    <p style="color: #334155; font-size: 0.95rem; margin-bottom: 16px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                        <%= job.getDescription() %>
                    </p>

                    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; border-top: 1px solid var(--border); padding-top: 12px;">
                        <div class="skill-tags" style="margin: 0;">
                            <% if (reqSkills != null && !reqSkills.isEmpty()) { 
                                for (Skill s : reqSkills) { %>
                                    <span class="skill-tag"><%= s.getName() %></span>
                            <%  } 
                               } else { %>
                                <span style="font-size: 0.8rem; color: var(--text-muted);">No specific skills required</span>
                            <% } %>
                        </div>

                        <div>
                            <a href="<%= request.getContextPath() %>/student/apply?id=<%= job.getId() %>" class="btn btn-primary btn-sm">View Details & Apply</a>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    <% } %>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
