<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.StudentProfile, com.smarthire.model.Skill, java.util.List, java.util.Set, java.util.HashSet" %>
<%
    StudentProfile profile = (StudentProfile) request.getAttribute("profile");
    @SuppressWarnings("unchecked")
    List<Skill> allSkills = (List<Skill>) request.getAttribute("allSkills");
    @SuppressWarnings("unchecked")
    List<Skill> studentSkills = (List<Skill>) request.getAttribute("studentSkills");

    Set<Long> studentSkillIds = new HashSet<>();
    if (studentSkills != null) {
        for (Skill s : studentSkills) {
            studentSkillIds.add(s.getId());
        }
    }
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="Profile & Skills" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title">Profile & Skills</h1>
            <p class="page-subtitle">Update your academic information and select your skills for intelligent job matching</p>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="alert alert-success"><%= request.getAttribute("success") %></div>
    <% } %>

    <form action="<%= request.getContextPath() %>/student/profile" method="POST">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
            <!-- Academic Information -->
            <div class="card">
                <h2 class="card-title">Academic & Background Details</h2>
                
                <div class="form-group">
                    <label class="form-label" for="education">Education / Degree & College</label>
                    <input type="text" id="education" name="education" class="form-control" placeholder="e.g. B.Tech in Computer Science, Stanford" value="<%= (profile != null && profile.getEducation() != null) ? profile.getEducation() : "" %>">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                    <div class="form-group">
                        <label class="form-label" for="graduationYear">Graduation Year</label>
                        <input type="number" min="1990" max="2040" id="graduationYear" name="graduationYear" class="form-control" placeholder="e.g. 2026" value="<%= (profile != null && profile.getGraduationYear() != null) ? profile.getGraduationYear() : "" %>">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="cgpa">CGPA (Scale 0.0 - 10.0)</label>
                        <input type="number" step="0.01" min="0" max="10" id="cgpa" name="cgpa" class="form-control" placeholder="e.g. 8.75" value="<%= (profile != null && profile.getCgpa() != null) ? profile.getCgpa() : "" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="summary">Professional Summary</label>
                    <textarea id="summary" name="summary" class="form-control" rows="3" placeholder="Brief overview of your interests and strengths..."><%= (profile != null && profile.getSummary() != null) ? profile.getSummary() : "" %></textarea>
                </div>

                <div class="form-group">
                    <label class="form-label" for="experience">Projects & Experience</label>
                    <textarea id="experience" name="experience" class="form-control" rows="3" placeholder="Key projects, internships, hackathons..."><%= (profile != null && profile.getExperience() != null) ? profile.getExperience() : "" %></textarea>
                </div>
            </div>

            <!-- Skills Selection -->
            <div class="card">
                <h2 class="card-title">Select Your Technical Skills</h2>
                <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 16px;">Check technologies you are proficient in, or type your own below.</p>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; max-height: 320px; overflow-y: auto; padding: 4px;">
                    <% if (allSkills != null) { 
                        for (Skill sk : allSkills) { 
                            boolean checked = studentSkillIds.contains(sk.getId());
                    %>
                        <label style="display: flex; align-items: center; gap: 8px; font-size: 0.9rem; cursor: pointer; padding: 8px 10px; border-radius: 6px; background: <%= checked ? "#eff6ff" : "#f8fafc" %>; border: 1px solid <%= checked ? "#bfdbfe" : "#e2e8f0" %>;">
                            <input type="checkbox" name="skills" value="<%= sk.getId() %>" <%= checked ? "checked" : "" %>>
                            <span><%= sk.getName() %></span>
                        </label>
                    <%   } 
                       } %>
                </div>

                <!-- Custom Skills Tag Input -->
                <div style="margin-top: 16px; border-top: 1px solid #e2e8f0; padding-top: 16px;">
                    <label class="form-label" style="font-size: 0.85rem; margin-bottom: 8px; display: block;">Add Custom Skills</label>
                    <div id="studentSkillTags" style="display: flex; flex-wrap: wrap; gap: 6px; padding: 8px 10px; min-height: 42px; border: 1px solid #e2e8f0; border-radius: 6px; background: #fff; align-items: center; cursor: text;" onclick="document.getElementById('studentSkillInput').focus()">
                        <input type="text" id="studentSkillInput" placeholder="Type a skill and press Enter..."
                               style="border: none; outline: none; font-size: 0.875rem; flex: 1; min-width: 140px; background: transparent; padding: 2px 0;"
                               autocomplete="off">
                    </div>
                    <input type="hidden" id="studentCustomSkillsHidden" name="customSkills" value="">
                    <small style="color: var(--text-muted); font-size: 0.78rem; display: block; margin-top: 4px;">Press Enter or comma to add each skill. Click the &times; to remove.</small>
                </div>
            </div>
        </div>

        <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
            <button type="submit" class="btn btn-primary" style="padding: 12px 28px;">Save Profile & Skills</button>
        </div>
    </form>

<script>
(function() {
    var tags = [];
    var input = document.getElementById('studentSkillInput');
    var container = document.getElementById('studentSkillTags');
    var hidden = document.getElementById('studentCustomSkillsHidden');

    function updateHidden() {
        hidden.value = tags.join(',');
    }

    function addTag(text) {
        text = text.trim().replace(/,/g, '');
        if (!text || tags.indexOf(text.toLowerCase()) !== -1) return;
        tags.push(text.toLowerCase());

        var tag = document.createElement('span');
        tag.style.cssText = 'display:inline-flex;align-items:center;gap:4px;padding:4px 10px;background:#e0e7ff;color:#3730a3;border-radius:20px;font-size:0.8rem;font-weight:600;';
        tag.textContent = text;

        var btn = document.createElement('span');
        btn.innerHTML = '&times;';
        btn.style.cssText = 'cursor:pointer;font-size:1rem;line-height:1;margin-left:2px;color:#6366f1;';
        btn.onclick = function(e) {
            e.stopPropagation();
            var idx = tags.indexOf(text.toLowerCase());
            if (idx > -1) tags.splice(idx, 1);
            tag.remove();
            updateHidden();
        };
        tag.appendChild(btn);
        container.insertBefore(tag, input);
        updateHidden();
    }

    input.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' || e.key === ',') {
            e.preventDefault();
            addTag(input.value);
            input.value = '';
        }
        if (e.key === 'Backspace' && input.value === '' && tags.length > 0) {
            var lastTag = container.querySelectorAll('span')[container.querySelectorAll('span').length - 1];
            if (lastTag) {
                tags.pop();
                lastTag.remove();
                updateHidden();
            }
        }
    });

    input.addEventListener('blur', function() {
        if (input.value.trim()) {
            addTag(input.value);
            input.value = '';
        }
    });
})();
</script>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
