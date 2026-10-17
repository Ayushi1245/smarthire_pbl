<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Welcome to SmartHire" />
</jsp:include>

<main class="container">
    <div style="text-align: center; max-width: 800px; margin: 48px auto 64px auto;">
        <span class="badge-tag" style="background: var(--primary-light); color: var(--primary); font-size: 0.85rem; padding: 6px 14px; border-radius: 9999px; font-weight: 600; text-transform: uppercase;">Next-Gen Campus & Tech Hiring</span>
        <h1 style="font-size: 3rem; font-weight: 800; letter-spacing: -0.03em; color: var(--text-main); margin: 20px 0 16px 0; line-height: 1.15;">
            Intelligent Rule-Based Recruitment Platform
        </h1>
        <p style="font-size: 1.2rem; color: var(--text-muted); line-height: 1.6; margin-bottom: 32px;">
            SmartHire connects talented students with top recruiters through automated skill overlap analysis, eligibility enforcement, and ranked candidate matching.
        </p>

        <% if (session.getAttribute("userId") == null) { %>
            <div style="display: flex; gap: 16px; justify-content: center; flex-wrap: wrap;">
                <a href="<%= request.getContextPath() %>/register" class="btn btn-primary" style="padding: 14px 28px; font-size: 1rem;">Get Started Free &rarr;</a>
                <a href="<%= request.getContextPath() %>/login" class="btn btn-secondary" style="padding: 14px 28px; font-size: 1rem;">Sign In</a>
            </div>
        <% } else { %>
            <div style="display: flex; gap: 16px; justify-content: center; flex-wrap: wrap;">
                <% if ("STUDENT".equals(session.getAttribute("role"))) { %>
                    <a href="<%= request.getContextPath() %>/student/dashboard" class="btn btn-primary" style="padding: 14px 28px; font-size: 1rem;">Go to Student Dashboard &rarr;</a>
                    <a href="<%= request.getContextPath() %>/student/jobs" class="btn btn-secondary" style="padding: 14px 28px; font-size: 1rem;">Explore Openings</a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/recruiter/dashboard" class="btn btn-primary" style="padding: 14px 28px; font-size: 1rem;">Go to Recruiter Dashboard &rarr;</a>
                    <a href="<%= request.getContextPath() %>/recruiter/job/create" class="btn btn-secondary" style="padding: 14px 28px; font-size: 1rem;">Post a New Job</a>
                <% } %>
            </div>
        <% } %>
    </div>

    <!-- Feature Highlights Grid -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 24px; margin-bottom: 48px;">
        <div class="card" style="padding: 32px 24px;">
            <div style="width: 44px; height: 44px; border-radius: 10px; background: #eff6ff; display: flex; align-items: center; justify-content: center; color: var(--primary); font-size: 1.4rem; font-weight: 700; margin-bottom: 16px;">
                🎯
            </div>
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">Automated Skill Matching</h3>
            <p style="color: var(--text-muted); font-size: 0.925rem; line-height: 1.6;">
                Our rule engine normalizes skill sets and calculates candidate-job alignment in real time, showing students matched and missing skills.
            </p>
        </div>

        <div class="card" style="padding: 32px 24px;">
            <div style="width: 44px; height: 44px; border-radius: 10px; background: #ecfdf5; display: flex; align-items: center; justify-content: center; color: #10b981; font-size: 1.4rem; font-weight: 700; margin-bottom: 16px;">
                📋
            </div>
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">Eligibility Gatekeeping</h3>
            <p style="color: var(--text-muted); font-size: 0.925rem; line-height: 1.6;">
                Recruiters specify academic thresholds such as minimum CGPA. Unqualified candidates receive transparent feedback rather than silent rejections.
            </p>
        </div>

        <div class="card" style="padding: 32px 24px;">
            <div style="width: 44px; height: 44px; border-radius: 10px; background: #faf5ff; display: flex; align-items: center; justify-content: center; color: #8b5cf6; font-size: 1.4rem; font-weight: 700; margin-bottom: 16px;">
                ⚡
            </div>
            <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 8px;">End-to-End Application Tracking</h3>
            <p style="color: var(--text-muted); font-size: 0.925rem; line-height: 1.6;">
                Recruiters review ranked applicants, download stored CVs, and transition candidates through Under Review, Shortlisted, and Accepted stages.
            </p>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
