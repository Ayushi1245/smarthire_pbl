<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String currentRole = (String) session.getAttribute("role");
    String currentUser = (String) session.getAttribute("fullName");
    Long currentUserId = (Long) session.getAttribute("userId");
    String pageTitle = request.getParameter("title");
    if (pageTitle == null || pageTitle.isEmpty()) {
        pageTitle = "SmartHire";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %> - Rule-Based Recruitment</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <header class="navbar">
        <div class="nav-container">
            <a href="<%= request.getContextPath() %>/" class="nav-brand">
                SmartHire
                <% if (currentRole != null) { %>
                    <span class="badge-tag"><%= currentRole %></span>
                <% } %>
            </a>
            
            <nav>
                <ul class="nav-links">
                    <% if ("STUDENT".equals(currentRole)) { %>
                        <li><a href="<%= request.getContextPath() %>/student/dashboard">Dashboard</a></li>
                        <li><a href="<%= request.getContextPath() %>/student/jobs">Browse Jobs</a></li>
                        <li><a href="<%= request.getContextPath() %>/student/applications">My Applications</a></li>
                        <li><a href="<%= request.getContextPath() %>/student/profile">Profile & Skills</a></li>
                        <li><a href="<%= request.getContextPath() %>/student/resume">Resume</a></li>
                    <% } else if ("RECRUITER".equals(currentRole)) { %>
                        <li><a href="<%= request.getContextPath() %>/recruiter/dashboard">Dashboard</a></li>
                        <li><a href="<%= request.getContextPath() %>/recruiter/job/create">+ Post New Job</a></li>
                        <li><a href="<%= request.getContextPath() %>/recruiter/profile">Company Profile</a></li>
                    <% } %>
                </ul>
            </nav>

            <div class="nav-user">
                <% if (currentUserId != null) { %>
                    <span class="user-badge"><%= currentUser != null ? currentUser : "User" %></span>
                    <a href="<%= request.getContextPath() %>/logout" class="btn btn-secondary btn-sm">Log out</a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/login" class="btn btn-secondary btn-sm">Log in</a>
                    <a href="<%= request.getContextPath() %>/register" class="btn btn-primary btn-sm">Sign up</a>
                <% } %>
            </div>
        </div>
    </header>
