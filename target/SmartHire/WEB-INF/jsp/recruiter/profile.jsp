<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.RecruiterProfile" %>
<%
    RecruiterProfile profile = (RecruiterProfile) request.getAttribute("profile");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Company Profile" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Company Profile</h1>
            <p class="page-subtitle">Update your organisation branding and company overview</p>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <div class="card" style="max-width: 720px;">
        <form action="<%= request.getContextPath() %>/recruiter/profile" method="POST">
            <div class="form-group">
                <label class="form-label" for="companyName">Company Name *</label>
                <input type="text" id="companyName" name="companyName" class="form-control" required placeholder="e.g. Acme Tech Solutions" value="<%= (profile != null && profile.getCompanyName() != null) ? profile.getCompanyName() : "" %>">
            </div>

            <div class="form-group">
                <label class="form-label" for="companyDescription">About Company / Organization</label>
                <textarea id="companyDescription" name="companyDescription" class="form-control" rows="5" placeholder="Share what makes your company culture, technology stack, and mission stand out..."><%= (profile != null && profile.getCompanyDescription() != null) ? profile.getCompanyDescription() : "" %></textarea>
            </div>

            <button type="submit" class="btn btn-primary" style="padding: 10px 24px;">Save Company Profile</button>
        </form>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
