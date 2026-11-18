package com.smarthire.service;

import com.smarthire.model.JobSkill;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the weighted matching algorithm in {@link MatchService}.
 *
 * <p>These tests cover {@code calculateWeightedMatch(List&lt;String&gt;, List&lt;JobSkill&gt;)}
 * which respects {@code weight} and {@code is_mandatory} from the DB.</p>
 */
@DisplayName("Weighted MatchService Algorithm Tests")
class WeightedMatchServiceTest {

    private MatchService service;

    @BeforeEach
    void setUp() {
        service = new MatchService();
    }

    // -------------------------------------------------------------------------
    // Weighted scoring
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("All skills matched - full skill component (70)")
    void testAllSkillsMatched() {
        List<String> student = Arrays.asList("java", "sql", "docker");
        List<JobSkill> job = Arrays.asList(
                new JobSkill("java",   2.0, false),
                new JobSkill("sql",    3.0, false),
                new JobSkill("docker", 1.0, false)
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(student, job);

        // weightedMatched=6, totalWeight=6 -> (6/6)*70 = 70.0
        assertEquals(70.0, result.getScore(), 0.01);
        assertEquals(3, result.getMatchedSkills().size());
        assertTrue(result.getMissingSkills().isEmpty());
    }

    @Test
    @DisplayName("Higher-weight skills missing reduces score more than lower-weight")
    void testWeightImpact() {
        List<String> student = Arrays.asList("docker");  // only the low-weight skill
        List<JobSkill> job = Arrays.asList(
                new JobSkill("java",   5.0, false),  // heavy, missing
                new JobSkill("docker", 1.0, false)   // light, present
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(student, job);

        // weightedMatched=1, totalWeight=6 -> (1/6)*70 = 11.67
        assertEquals(11.67, result.getScore(), 0.01);
    }

    // -------------------------------------------------------------------------
    // Mandatory-skill penalty
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Mandatory skill penalty reduces score when mandatory skill missing")
    void testMandatoryPenalty() {
        List<String> student = Arrays.asList("java", "sql");
        List<JobSkill> job = Arrays.asList(
                new JobSkill("java",   2.0, true),   // mandatory - present
                new JobSkill("python", 3.0, true),   // mandatory - MISSING  -> penalty = 0.5*3 = 1.5
                new JobSkill("sql",    1.0, false)   // optional  - present
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(student, job);

        // weightedMatched = 2 + 1 - 1.5 = 1.5; totalWeight = 6
        // skillComponent = (1.5/6)*70 = 17.5
        assertEquals(17.5, result.getScore(), 0.01);
        assertTrue(result.getMissingSkills().contains("python"));
        assertFalse(result.getMissingSkills().contains("java"));
    }

    @Test
    @DisplayName("Score never goes below 0 even with many mandatory misses")
    void testScoreNeverNegative() {
        List<String> student = Collections.emptyList();
        List<JobSkill> job = Arrays.asList(
                new JobSkill("java",   5.0, true),
                new JobSkill("python", 5.0, true),
                new JobSkill("aws",    5.0, true)
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(student, job);

        assertTrue(result.getScore() >= 0.0, "Score must not be negative");
        assertEquals(0.0, result.getScore(), 0.001);
    }

    @Test
    @DisplayName("Optional missing skill incurs no extra penalty")
    void testOptionalMissingNoExtraPenalty() {
        List<String> student = Arrays.asList("java");
        List<JobSkill> job = Arrays.asList(
                new JobSkill("java",   1.0, false),  // present
                new JobSkill("docker", 1.0, false)   // optional - missing, no penalty
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(student, job);

        // weightedMatched=1, totalWeight=2 -> (1/2)*70 = 35.0
        assertEquals(35.0, result.getScore(), 0.01);
    }

    // -------------------------------------------------------------------------
    // Edge cases
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("No job skills returns full skill component (70)")
    void testNoJobSkills() {
        List<String> student = Arrays.asList("java", "sql");

        MatchService.MatchResult resultEmpty = service.calculateWeightedMatch(student, Collections.emptyList());
        assertEquals(70.0, resultEmpty.getScore(), 0.001);

        MatchService.MatchResult resultNull = service.calculateWeightedMatch(student, (List<JobSkill>) null);
        assertEquals(70.0, resultNull.getScore(), 0.001);
    }

    @Test
    @DisplayName("No student skills - all job skills missing")
    void testNoStudentSkills() {
        List<JobSkill> job = Arrays.asList(
                new JobSkill("java",   1.0, false),
                new JobSkill("python", 1.0, false)
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(Collections.emptyList(), job);

        assertEquals(0.0, result.getScore(), 0.001);
        assertEquals(2, result.getMissingSkills().size());
    }

    @Test
    @DisplayName("Skill name normalization works in weighted mode")
    void testNormalizationInWeightedMode() {
        List<String> student = Arrays.asList("  JAVA  ", "sql");
        List<JobSkill> job = Arrays.asList(
                new JobSkill("Java", 2.0, true),
                new JobSkill("SQL",  2.0, true)
        );

        MatchService.MatchResult result = service.calculateWeightedMatch(student, job);

        // Both matched after normalization -> full 70.0
        assertEquals(70.0, result.getScore(), 0.001);
        assertTrue(result.getMissingSkills().isEmpty());
    }

    // -------------------------------------------------------------------------
    // Legacy API backward-compatibility
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Legacy String-list API still works; each skill has weight 1.0 non-mandatory")
    void testLegacyApiCompatibility() {
        List<String> student = Arrays.asList("java", "sql");
        List<String> job     = Arrays.asList("java", "sql", "python");

        MatchService.MatchResult result = service.calculateMatch(student, job);

        // 2/3 matched, no mandatory penalty -> (2/3)*70 = 46.67
        assertEquals(46.67, result.getScore(), 0.01);
        assertTrue(result.getMissingSkills().contains("python"));
    }

    @Test
    @DisplayName("Legacy API: empty job skills returns 70.0")
    void testLegacyEmptyJobSkills() {
        MatchService.MatchResult result = service.calculateMatch(
                Arrays.asList("java"), Collections.emptyList());
        assertEquals(70.0, result.getScore(), 0.001);
    }

    @Test
    @DisplayName("Legacy API: null job skills returns 70.0")
    void testLegacyNullJobSkills() {
        MatchService.MatchResult result = service.calculateMatch(
                Arrays.asList("java"), (List<String>) null);
        assertEquals(70.0, result.getScore(), 0.001);
    }
}
