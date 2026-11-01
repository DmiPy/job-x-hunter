package dev.dmitriy.job_x_hunter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dmitriy.job_x_hunter.entity.Resume;
import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.enums.Role;
import dev.dmitriy.job_x_hunter.repository.ResumeRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.repository.UserRepository;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JobXHunterApplicationTests {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Test
    @Order(1)
    void appliesSchemaAndReferenceDataButNotLocalSeedDataOnFreshTestDatabase() {
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'job_vacancy'", Integer.class));
        assertEquals(5, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM skill", Integer.class));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM databasechangelog WHERE id = '3'", Integer.class));
    }

    @Test
    @Order(2)
    void doesNotReapplyPreviouslyExecutedLocalSeedChangeSetWhenSwitchingToProdContext() throws Exception {
        updateLiquibase("db/changelog/db.changelog-master.yaml");
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM databasechangelog WHERE id = '3'", Integer.class));
        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class));

        updateLiquibase("db/changelog/db.changelog-prod.yaml");
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM databasechangelog WHERE id = '3'", Integer.class));
        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class));
    }

    @Test
    @Order(3)
    void rejectsNullVacancyTitleAtDatabaseLevel() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO job_vacancy (title, url, source, source_job_id) VALUES (NULL, ?, ?, ?)",
                "https://example.test/job", "TEST", UUID.randomUUID().toString()));
    }

    @Test
    @Order(4)
    void returnsConflictForDuplicateExternalVacancyIdentity() throws Exception {
        String sourceJobId = "test-" + UUID.randomUUID();
        String payload = objectMapper.writeValueAsString(new VacancyRequest(sourceJobId));

        mockMvc.perform(post("/job-vacancies")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/job-vacancies")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @Order(5)
    void rejectsSkillProficiencyOutsideTheConfiguredPercentageRange() throws Exception {
        mockMvc.perform(post("/user-profile-skill")
                        .contentType("application/json")
                        .content("{\"userProfileId\":1,\"skillName\":\"Validation test\",\"proficiency\":101}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @Order(6)
    void preservesOmittedResumeFieldsAndRejectsExplicitNullForMandatoryFields() throws Exception {
        Resume resume = createResume();

        mockMvc.perform(patch("/resumes")
                        .contentType("application/json")
                        .content("{\"resumeId\":" + resume.getResumeId() + ",\"description\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original title"))
                .andExpect(jsonPath("$.url").value("/original.pdf"))
                .andExpect(jsonPath("$.description").value("Updated"));

        mockMvc.perform(patch("/resumes")
                        .contentType("application/json")
                        .content("{\"resumeId\":" + resume.getResumeId() + ",\"title\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private void updateLiquibase(String changelog) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    changelog,
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update(new Contexts());
            database.close();
        }
    }

    private Resume createResume() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("resume-" + UUID.randomUUID() + "@example.test");
        user.setUserRole(Role.USER);
        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFirstname("Resume");
        profile.setSurname("Tester");
        profile = userProfileRepository.save(profile);

        Resume resume = new Resume();
        resume.setUserProfile(profile);
        resume.setTitle("Original title");
        resume.setUrl("/original.pdf");
        resume.setDescription("Original");
        return resumeRepository.save(resume);
    }

    private record VacancyRequest(
            String sourceJobId,
            String title,
            String url,
            String source
    ) {
        private VacancyRequest(String sourceJobId) {
            this(sourceJobId, "Backend developer", "https://example.test/jobs/" + sourceJobId, "TEST");
        }
    }
}
