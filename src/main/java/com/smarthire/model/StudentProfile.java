package com.smarthire.model;

public class StudentProfile {
    private long id;
    private long userId;
    private String education;
    private Integer graduationYear;
    private Double cgpa;
    private String summary;
    private String experience;

    public StudentProfile() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
}
