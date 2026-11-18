package com.smarthire.model;

/**
 * Represents a skill required by a job posting, as stored in the
 * {@code job_skills} table.
 *
 * <p>Used by {@link com.smarthire.service.MatchService} to compute a
 * weighted match score between a student's skills and a job's requirements.</p>
 *
 * <p>No ORM annotations are used; the project relies on raw JDBC with
 * {@code PreparedStatement}.</p>
 */
public class JobSkill {

    /** Normalized (lowercase, trimmed) skill name. */
    private final String name;

    /**
     * Importance weight for this skill (must be &gt; 0).
     * Defaults to {@code 1.0} if the stored value is zero or negative.
     */
    private final double weight;

    /**
     * Whether the skill is mandatory.
     * Missing mandatory skills attract a scoring penalty in
     * {@link com.smarthire.service.MatchService}.
     */
    private final boolean mandatory;

    /**
     * Create a JobSkill.
     *
     * @param name      skill name (will be trimmed and lower‑cased)
     * @param weight    importance weight; falls back to 1.0 if &le; 0
     * @param mandatory {@code true} if the skill is strictly required
     */
    public JobSkill(String name, double weight, boolean mandatory) {
        this.name = (name == null) ? "" : name.trim().toLowerCase();
        this.weight = (weight <= 0) ? 1.0 : weight;
        this.mandatory = mandatory;
    }

    /** Normalized (lowercase, trimmed) skill name. */
    public String getName() { return name; }

    /** Importance weight (always &gt; 0). */
    public double getWeight() { return weight; }

    /** {@code true} if this skill is mandatory for the job. */
    public boolean isMandatory() { return mandatory; }

    @Override
    public String toString() {
        return "JobSkill{name='" + name + "', weight=" + weight
                + ", mandatory=" + mandatory + '}';
    }
}

