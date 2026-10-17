package com.smarthire.service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates a match score between a student's skills and a job's required skills.
 *
 * <h3>Scoring Formula</h3>
 * <pre>
 *   score = (number of matched skills / number of unique job-required skills) * 100
 * </pre>
 * <ul>
 *   <li>Skill names are normalized to lowercase and trimmed before comparison.</li>
 *   <li>Duplicate skills within either set are removed before scoring.</li>
 *   <li>If the job has no required skills, the score is 100 (no skill barrier).</li>
 *   <li>If the student has no skills, the score is 0 (unless the job also has none).</li>
 * </ul>
 *
 * The result is returned as a {@link MatchResult} containing the numeric score,
 * the set of matched skill names, and the set of missing skill names.
 */
public class MatchService {

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

        /** Score from 0 to 100, rounded to two decimal places. */
        public double getScore() { return score; }

        /** Skill names (normalized) that the student has and the job requires. */
        public Set<String> getMatchedSkills() { return matchedSkills; }

        /** Skill names (normalized) that the job requires but the student lacks. */
        public Set<String> getMissingSkills() { return missingSkills; }
    }

    /**
     * Calculate the match score.
     *
     * @param studentSkills list of the student's skill names (may contain nulls, blanks, or duplicates)
     * @param jobSkills     list of the job's required skill names (may contain nulls, blanks, or duplicates)
     * @return a MatchResult with score, matched, and missing skills
     */
    public MatchResult calculateMatch(List<String> studentSkills, List<String> jobSkills) {
        Set<String> normalizedStudent = normalize(studentSkills);
        Set<String> normalizedJob = normalize(jobSkills);

        // If the job has no required skills, the student is a perfect match.
        if (normalizedJob.isEmpty()) {
            return new MatchResult(100.0, Collections.emptySet(), Collections.emptySet());
        }

        Set<String> matched = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();

        for (String skill : normalizedJob) {
            if (normalizedStudent.contains(skill)) {
                matched.add(skill);
            } else {
                missing.add(skill);
            }
        }

        double score = Math.round(((double) matched.size() / normalizedJob.size()) * 100.0 * 100.0) / 100.0;

        return new MatchResult(score, matched, missing);
    }

    /**
     * Normalize a list of skill names: trim, lowercase, remove nulls/blanks, deduplicate.
     */
    private Set<String> normalize(List<String> skills) {
        if (skills == null) return Collections.emptySet();
        return skills.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
