package com.smarthire.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MatchService Algorithm Tests")
class MatchServiceTest {

    private MatchService matchService;

    @BeforeEach
    void setUp() {
        matchService = new MatchService();
    }

    @Test
    @DisplayName("Should return 100% when all required job skills are present in student skills")
    void testExactAndCompleteMatch() {
        List<String> studentSkills = Arrays.asList("Java", "MySQL", "Spring", "Git");
        List<String> jobSkills = Arrays.asList("Java", "MySQL", "Git");

        MatchService.MatchResult result = matchService.calculateMatch(studentSkills, jobSkills);

        assertNotNull(result);
        assertEquals(100.0, result.getScore(), 0.001);
        assertEquals(3, result.getMatchedSkills().size());
        assertTrue(result.getMissingSkills().isEmpty());
    }

    @Test
    @DisplayName("Should normalize casing and whitespace (e.g. ' java ' == 'Java')")
    void testCaseAndWhitespaceNormalization() {
        List<String> studentSkills = Arrays.asList("  java  ", "docker", "PYTHON");
        List<String> jobSkills = Arrays.asList("JAVA", "Python", "Docker");

        MatchService.MatchResult result = matchService.calculateMatch(studentSkills, jobSkills);

        assertEquals(100.0, result.getScore(), 0.001);
        assertEquals(3, result.getMatchedSkills().size());
        assertTrue(result.getMissingSkills().isEmpty());
    }

    @Test
    @DisplayName("Should accurately calculate partial match percentage")
    void testPartialMatch() {
        List<String> studentSkills = Arrays.asList("Java", "HTML");
        List<String> jobSkills = Arrays.asList("Java", "HTML", "CSS", "JavaScript");

        MatchService.MatchResult result = matchService.calculateMatch(studentSkills, jobSkills);

        // 2 out of 4 skills matched = 50.0%
        assertEquals(50.0, result.getScore(), 0.001);
        assertEquals(2, result.getMatchedSkills().size());
        assertEquals(2, result.getMissingSkills().size());
        assertTrue(result.getMissingSkills().contains("css"));
        assertTrue(result.getMissingSkills().contains("javascript"));
    }

    @Test
    @DisplayName("Should return 0% when there is no skill overlap")
    void testZeroMatch() {
        List<String> studentSkills = Arrays.asList("PHP", "Laravel");
        List<String> jobSkills = Arrays.asList("Python", "Django", "PostgreSQL");

        MatchService.MatchResult result = matchService.calculateMatch(studentSkills, jobSkills);

        assertEquals(0.0, result.getScore(), 0.001);
        assertTrue(result.getMatchedSkills().isEmpty());
        assertEquals(3, result.getMissingSkills().size());
    }

    @Test
    @DisplayName("Should handle empty or null student skills gracefully without throwing exceptions")
    void testEmptyStudentSkills() {
        List<String> jobSkills = Arrays.asList("Java", "AWS");

        MatchService.MatchResult resultWithEmpty = matchService.calculateMatch(Collections.emptyList(), jobSkills);
        assertEquals(0.0, resultWithEmpty.getScore(), 0.001);
        assertEquals(2, resultWithEmpty.getMissingSkills().size());

        MatchService.MatchResult resultWithNull = matchService.calculateMatch(null, jobSkills);
        assertEquals(0.0, resultWithNull.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should return 100% when job has no required skills")
    void testEmptyJobRequirements() {
        List<String> studentSkills = Arrays.asList("Java", "SQL");

        MatchService.MatchResult resultWithEmpty = matchService.calculateMatch(studentSkills, Collections.emptyList());
        assertEquals(100.0, resultWithEmpty.getScore(), 0.001);

        MatchService.MatchResult resultWithNull = matchService.calculateMatch(studentSkills, null);
        assertEquals(100.0, resultWithNull.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should deduplicate repeated skills correctly")
    void testDuplicateSkillsHandling() {
        List<String> studentSkills = Arrays.asList("Java", "java", "JAVA", "Spring");
        List<String> jobSkills = Arrays.asList("Java", "Spring", "SPRING");

        MatchService.MatchResult result = matchService.calculateMatch(studentSkills, jobSkills);

        // Required unique: java, spring (2). Student has both (2).
        assertEquals(100.0, result.getScore(), 0.001);
        assertEquals(2, result.getMatchedSkills().size());
        assertEquals(0, result.getMissingSkills().size());
    }
}
