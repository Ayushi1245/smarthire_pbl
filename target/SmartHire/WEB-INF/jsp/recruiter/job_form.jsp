<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.smarthire.model.Job, com.smarthire.model.Skill, java.util.List" %>
<%
    Job job = (Job) request.getAttribute("job");
    boolean isEdit = (job != null && job.getId() > 0);
    @SuppressWarnings("unchecked")
    List<Skill> allSkills = (List<Skill>) request.getAttribute("allSkills");
    @SuppressWarnings("unchecked")
    List<Long> selectedSkillIds = (List<Long>) request.getAttribute("selectedSkillIds");
    String formAction = isEdit ? (request.getContextPath() + "/recruiter/job/edit") : (request.getContextPath() + "/recruiter/job/create");
%>
<jsp:include page="/WEB-INF/jsp/common/header.jsp">
    <jsp:param name="title" value="<%= isEdit ? \"Edit Job Posting\" : \"Create New Job\" %>" />
</jsp:include>

<main class="container">
    <div class="page-header">
        <div>
            <h1 class="page-title"><%= isEdit ? "Edit Job Posting" : "Post a New Opening" %></h1>
            <p class="page-subtitle"><%= isEdit ? "Modify job parameters, criteria, and skill requirements" : "Specify requirements and skills so our matching engine can rank candidates" %></p>
        </div>
        <a href="<%= request.getContextPath() %>/recruiter/dashboard" class="btn btn-secondary">&larr; Back to Dashboard</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form action="<%= formAction %>" method="POST">
        <% if (isEdit) { %>
            <input type="hidden" name="id" value="<%= job.getId() %>">
        <% } %>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px;">
            <!-- Job Details Section -->
            <div class="card">
                <h2 class="card-title">Job Details</h2>

                <div class="form-group">
                    <label class="form-label" for="title">Job Title *</label>
                    <input type="text" id="title" name="title" class="form-control" required placeholder="e.g. Junior Java Developer" value="<%= (isEdit && job.getTitle() != null) ? job.getTitle() : "" %>">
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                    <div class="form-group">
                        <label class="form-label" for="location">Location *</label>
                        <input type="text" id="location" name="location" class="form-control" required placeholder="e.g. Remote, San Francisco, CA" value="<%= (isEdit && job.getLocation() != null) ? job.getLocation() : "" %>">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="employmentType">Employment Type</label>
                        <select id="employmentType" name="employmentType" class="form-control">
                            <% 
                                String empType = (isEdit && job.getEmploymentType() != null) ? job.getEmploymentType() : "Full-time"; 
                                String[] types = {"Full-time", "Part-time", "Internship", "Contract"};
                                for (String t : types) {
                            %>
                                <option value="<%= t %>" <%= t.equalsIgnoreCase(empType) ? "selected" : "" %>><%= t %></option>
                            <% } %>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="minCgpa">Minimum CGPA Requirement (0.0 - 10.0)</label>
                    <input type="number" step="0.01" min="0" max="10" id="minCgpa" name="minCgpa" class="form-control" placeholder="Optional minimum threshold, e.g. 7.50" value="<%= (isEdit && job.getMinCgpa() != null) ? job.getMinCgpa() : "" %>">
                    <small style="color: var(--text-muted); font-size: 0.8rem; display: block; margin-top: 4px;">Students below this threshold cannot submit an application.</small>
                </div>

                <div class="form-group">
                    <label class="form-label" for="description">Job Description & Responsibilities *</label>
                    <textarea id="description" name="description" class="form-control" rows="8" required placeholder="Outline key deliverables, team expectations, and role benefits..."><%= (isEdit && job.getDescription() != null) ? job.getDescription() : "" %></textarea>
                </div>

                <% if (isEdit) { %>
                    <div class="form-group">
                        <label style="display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 0.95rem; font-weight: 600;">
                            <input type="checkbox" name="isActive" value="true" <%= job.isActive() ? "checked" : "" %>>
                            <span>Active (Accepting applications)</span>
                        </label>
                    </div>
                <% } %>
            </div>

            <!-- Required Skills Section -->
            <div>
                <div class="card">
                    <h2 class="card-title">Required Skills</h2>
                    <p style="color: var(--text-muted); font-size: 0.85rem; margin-bottom: 14px;">Select the technologies required for this role, or type your own below.</p>

                    <div style="display: grid; gap: 8px; max-height: 300px; overflow-y: auto; padding: 4px;">
                        <% if (allSkills != null) { 
                            for (Skill sk : allSkills) { 
                                boolean isChecked = (selectedSkillIds != null && selectedSkillIds.contains(sk.getId()));
                        %>
                            <label style="display: flex; align-items: center; gap: 8px; font-size: 0.875rem; cursor: pointer; padding: 6px 10px; border-radius: 4px; background: <%= isChecked ? "#eff6ff" : "#f8fafc" %>; border: 1px solid <%= isChecked ? "#bfdbfe" : "#e2e8f0" %>;">
                                <input type="checkbox" name="skills" value="<%= sk.getId() %>" <%= isChecked ? "checked" : "" %>>
                                <span><%= sk.getName() %></span>
                            </label>
                        <%  } 
                           } %>
                    </div>

                    <!-- Custom Skills Tag Input -->
                    <div style="margin-top: 16px; border-top: 1px solid #e2e8f0; padding-top: 16px;">
                        <label class="form-label" style="font-size: 0.85rem; margin-bottom: 8px; display: block;">Add Custom Skills</label>
                        <div id="customSkillTags" style="display: flex; flex-wrap: wrap; gap: 6px; padding: 8px 10px; min-height: 42px; border: 1px solid #e2e8f0; border-radius: 6px; background: #fff; align-items: center; cursor: text;" onclick="document.getElementById('customSkillInput').focus()">
                            <input type="text" id="customSkillInput" placeholder="Type a skill and press Enter..."
                                   style="border: none; outline: none; font-size: 0.875rem; flex: 1; min-width: 140px; background: transparent; padding: 2px 0;"
                                   autocomplete="off">
                        </div>
                        <input type="hidden" id="customSkillsHidden" name="customSkills" value="">
                        <small style="color: var(--text-muted); font-size: 0.78rem; display: block; margin-top: 4px;">Press Enter or comma to add each skill. Click the &times; to remove.</small>
                    </div>
                </div>

                <div style="margin-top: 20px;">
                    <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px;"><%= isEdit ? "Update Job Posting" : "Publish Job Opening" %></button>
                </div>
            </div>

<script>
(function() {
    var tags = [];
    var input = document.getElementById('customSkillInput');
    var container = document.getElementById('customSkillTags');
    var hidden = document.getElementById('customSkillsHidden');

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
        </div>
    </form>
</main>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
