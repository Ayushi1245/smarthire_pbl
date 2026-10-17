<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Create Account" />
</jsp:include>

<main class="container">
    <div class="auth-wrapper" style="max-width: 520px;">
        <div class="auth-card">
            <div class="auth-header">
                <h1 class="auth-title">Create Account</h1>
                <p class="auth-subtitle">Join SmartHire to discover opportunities or hire talent</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/register" method="POST">
                <div class="form-group">
                    <label class="form-label" for="fullName">Full Name</label>
                    <input type="text" id="fullName" name="fullName" class="form-control" placeholder="Alex Taylor" required value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>">
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" placeholder="alex@domain.com" required value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" placeholder="Minimum 6 characters" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Re-enter your password" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="role">I want to:</label>
                    <select id="role" name="role" class="form-control" required onchange="toggleRoleFields(this.value)">
                        <option value="STUDENT" <%= "STUDENT".equals(request.getAttribute("role")) ? "selected" : "" %>>Apply for Jobs (Student)</option>
                        <option value="RECRUITER" <%= "RECRUITER".equals(request.getAttribute("role")) ? "selected" : "" %>>Post Jobs & Hire (Recruiter)</option>
                    </select>
                </div>

                <div id="studentFields" style="display: <%= "RECRUITER".equals(request.getAttribute("role")) ? "none" : "block" %>;">
                    <div class="form-group">
                        <label class="form-label" for="college">College / University</label>
                        <input type="text" id="college" name="college" class="form-control" placeholder="e.g. Stanford University">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="branch">Major / Branch</label>
                        <input type="text" id="branch" name="branch" class="form-control" placeholder="e.g. Computer Science">
                    </div>
                </div>

                <div id="recruiterFields" style="display: <%= "RECRUITER".equals(request.getAttribute("role")) ? "block" : "none" %>;">
                    <div class="form-group">
                        <label class="form-label" for="companyName">Company Name</label>
                        <input type="text" id="companyName" name="companyName" class="form-control" placeholder="e.g. Acme Innovations">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Register</button>
            </form>

            <div class="auth-footer">
                Already have an account? <a href="<%= request.getContextPath() %>/login">Sign in</a>
            </div>
        </div>
    </div>
</main>

<script>
function toggleRoleFields(role) {
    document.getElementById('studentFields').style.display = (role === 'STUDENT') ? 'block' : 'none';
    document.getElementById('recruiterFields').style.display = (role === 'RECRUITER') ? 'block' : 'none';
}
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
