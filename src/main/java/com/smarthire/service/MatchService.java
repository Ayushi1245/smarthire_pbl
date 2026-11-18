package com.smarthire.service;

import com.smarthire.model.JobSkill;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates a match score between a student's skills and a job's required skills.
 *
 * <h3>Scoring Formula (weighted)</h3>
 * <pre>
 *   skillComponent = (weightedMatched / totalWeight) * 70
 *   finalScore     = skillComponent   (CGPA component reserved for future use)
 * </pre>
 *
 * <ul>
 *   <li>Each job skill contributes its {@code weight} to {@code totalWeight}.</li>
 *   <li>When a student has a skill, that skill's weight is added to
 *       {@code weightedMatched}.</li>
 *   <li>When a <em>mandatory</em> skill is missing, a penalty of
 *       {@code 0.5 * weight} is <em>subtracted</em> from {@code weightedMatched}.</li>
 *   <li>The weighted matched value is clamped to &ge; 0 before scoring.</li>
 *   <li>Skill names are normalized to lowercase and trimmed before comparison.</li>
 *   <li>Duplicate skills within either set are removed before scoring.</li>
 *   <li>If the job has no required skills, the score is 70.0 (full skill portion).</li>
 *   <li>The score is rounded to two decimal places.</li>
 * </ul>
 *
 * <h3>Backward Compatibility</h3>
 * <p>The original {@code calculateMatch(List&lt;String&gt;, List&lt;String&gt;)} signature
 * is retained; it wraps every job skill as weight&nbsp;1.0, non-mandatory.</p>
 */
public class MatchService {

    /** Fraction of the total score allocated to the skill component (0-1). */
    private static final double SKILL_WEIGHT = 0.70;

    /** Penalty factor applied to mandatory missing skills (fraction of their weight). */
    private static final double MANDATORY_PENALTY = 0.5;

    // -------------------------------------------------------------------------
    // Immutable result
    // -------------------------------------------------------------------------

    /**
     * Immutable result of a match calculation.
     */
    public static class MatchResult {
        private final double score;
        private final Set<String> matchedSkills;
        private final Set<String> missingSkills;

        public MatchResult(double score, Set<String> matchedSkills, Set<String> missingSkills) {
            this.score = score;
            this.matchedSkills = Collections.unmodifiableSet(matchedSkills);
            this.missingSkills = Collections.unmodifiableSet(missingSkills);
        }

        /** Final score from 0 to 100, rounded to two decimal places. */
        public double getScore() { return score; }

        /** Skill names (normalized) that the student has and the job requires. */
        public Set<String> getMatchedSkills() { return matchedSkills; }

        /** Skill names (normalized) that the job requires but the student lacks. */
        public Set<String> getMissingSkills() { return missingSkills; }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Legacy API retained for backward compatibility with existing callers
     * and tests that pass plain skill-name lists.
     *
     * <p>Every job skill is treated as weight 1.0 and non-mandatory.</p>
     *
     * @param studentSkills list of the student's skill names
     * @param jobSkills     list of the job's required skill names
     * @return a {@link MatchResult} with score, matched, and missing skills
     */
    public MatchResult calculateMatch(List<String> studentSkills,
                                      List<String> jobSkills) {
        List<JobSkill> wrapped = (jobSkills == null)
                ? Collections.emptyList()
                : jobSkills.stream()
                           .map(s -> new JobSkill(s, 1.0, false))
                           .collect(Collectors.toList());
        return calculateWeightedMatchInternal(studentSkills, wrapped);
    }

    /**
     * Weighted-match API that uses {@code weight} and {@code is_mandatory}
     * from the {@code job_skills} table.
     *
     * <p>Use this overload when you have full {@link JobSkill} objects from the DB.</p>
     *
     * @param studentSkills list of the student's skill names
     * @param jobSkills     list of {@link JobSkill} objects from the DB
     * @return a {@link MatchResult} with score, matched, and missing skills
     */
    public MatchResult calculateWeightedMatch(List<String> studentSkills,
                                              List<JobSkill> jobSkills) {
        return calculateWeightedMatchInternal(studentSkills, jobSkills);
    }

    // -------------------------------------------------------------------------
    // Core implementation
    // -------------------------------------------------------------------------

    private MatchResult calculateWeightedMatchInternal(List<String> studentSkills,
                                               List<JobSkill> jobSkills) {
        Set<String> normalizedStudent = normalize(studentSkills);

        if (jobSkills == null) {
            jobSkills = Collections.emptyList();
        }

        // No required skills - full skill component granted.
        if (jobSkills.isEmpty()) {
            return new MatchResult(
                    round(100.0 * SKILL_WEIGHT),
                    Collections.emptySet(),
                    Collections.emptySet());
        }

        Set<String> matched = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();

        double totalWeight     = 0.0;
        double weightedMatched = 0.0;

        for (JobSkill js : jobSkills) {
            totalWeight += js.getWeight();

            if (normalizedStudent.contains(js.getName())) {
                matched.add(js.getName());
                weightedMatched += js.getWeight();
            } else {
                missing.add(js.getName());
                // Apply penalty for missing mandatory skills.
                if (js.isMandatory()) {
                    weightedMatched -= MANDATORY_PENALTY * js.getWeight();
                }
            }
        }

        // Clamp to avoid negative scores after penalties.
        if (weightedMatched < 0) {
            weightedMatched = 0;
        }

        // Skill component is SKILL_WEIGHT (70%) of the final score.
        double skillComponent = (weightedMatched / totalWeight) * 100.0 * SKILL_WEIGHT;

        return new MatchResult(round(skillComponent), matched, missing);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Normalize a list of skill names: trim, lowercase, remove nulls/blanks,
     * deduplicate while preserving insertion order.
     */
    private Set<String> normalize(List<String> skills) {
        if (skills == null) return Collections.emptySet();
        return skills.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Round to two decimal places. */
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
