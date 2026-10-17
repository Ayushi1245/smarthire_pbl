package com.smarthire.model;

import java.sql.Timestamp;

public class Job {
    private long id;
    private long recruiterId;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private Double minCgpa;
    private boolean active;
    private Timestamp createdAt;

    // Transient fields for display
    private String recruiterName;
    private String companyName;

    public Job() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getRecruiterId() { return recruiterId; }
    public void setRecruiterId(long recruiterId) { this.recruiterId = recruiterId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public Double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(Double minCgpa) { this.minCgpa = minCgpa; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getRecruiterName() { return recruiterName; }
    public void setRecruiterName(String recruiterName) { this.recruiterName = recruiterName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
