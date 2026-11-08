package dev.dmitriy.job_x_hunter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dmitriy.job_x_hunter.entity.Resume;
import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.enums.Role;
import dev.dmitriy.job_x_hunter.repository.ResumeRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.repository.UserRepository;
import dev.dmitriy.job_x_hunter.security.CurrentUserService;
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
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private CurrentUserService currentUserService;

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
    @Order(1)
    void requiresAuthenticationForPrivateEndpointsAndLeavesVacancyBrowsePublic() throws Exception {
        mockMvc.perform(get("/profiles/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/job-vacancies")).andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void deniesUserRoleOnAdministrativeVacancyCreation() throws Exception {
        mockMvc.perform(post("/job-vacancies")
                        .with(jwt().jwt(token -> token.claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER")))))
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void rejectsMissingVerifiedIdentityAndApplicationRole() throws Exception {
        mockMvc.perform(get("/users/me").with(jwt().jwt(token -> token
                        .issuer("https://issuer.test/realm").subject("missing-email")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))))))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/users/me").with(jwt().jwt(token -> token
                        .issuer("https://issuer.test/realm").subject("unverified")
                        .claim("email", "unverified@example.test").claim("email_verified", false)
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))))))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/users/me").with(jwt().jwt(token -> token
                        .issuer("https://issuer.test/realm").subject("no-role")
                        .claim("email", "no-role@example.test").claim("email_verified", true))))
                .andExpect(status().isForbidden());
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
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/job-vacancies")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @Order(5)
    void rejectsSkillProficiencyOutsideTheConfiguredPercentageRange() throws Exception {
        mockMvc.perform(post("/user-profile-skill")
                        .with(jwt().jwt(token -> token.claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER")))))
                        .contentType("application/json")
                        .content("{\"userProfileId\":1,\"skillName\":\"Validation test\",\"proficiency\":101}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @Order(6)
    void preservesOmittedResumeFieldsAndRejectsExplicitNullForMandatoryFields() throws Exception {
        Resume resume = createResume("resume-test-subject");
        var ownerJwt = jwt().jwt(token -> token.issuer("https://issuer.test/realm")
                .subject("resume-test-subject").claim("email", "resume-test@example.test")
                .claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))));

        mockMvc.perform(patch("/resumes")
                        .with(ownerJwt)
                        .contentType("application/json")
                        .content("{\"resumeId\":" + resume.getResumeId() + ",\"description\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original title"))
                .andExpect(jsonPath("$.url").value("/original.pdf"))
                .andExpect(jsonPath("$.description").value("Updated"));

        mockMvc.perform(patch("/resumes")
                        .with(ownerJwt)
                        .contentType("application/json")
                        .content("{\"resumeId\":" + resume.getResumeId() + ",\"title\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @Order(7)
    void secondUserCannotAccessOrChangeFirstUsersPrivateResources() throws Exception {
        Resume resume = createResume("idor-owner-subject");
        Long resumeId = resume.getResumeId();
        Long profileId = resume.getUserProfile().getUserProfileId();
        UUID ownerUserId = resume.getUserProfile().getUser().getUserId();
        var owner = jwt().jwt(token -> token.issuer("https://issuer.test/realm")
                .subject("idor-owner-subject").claim("email", "owner@example.test")
                .claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))));
        var other = jwt().jwt(token -> token.issuer("https://issuer.test/realm")
                .subject("other-subject").claim("email", "other@example.test")
                .claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))));

        mockMvc.perform(get("/resumes/{id}", resumeId).with(owner)).andExpect(status().isOk());
        mockMvc.perform(get("/profiles/{id}", profileId).with(owner)).andExpect(status().isOk());
        mockMvc.perform(get("/profiles/user/{id}", ownerUserId).with(owner)).andExpect(status().isOk());
        mockMvc.perform(get("/resumes/{id}", resumeId).with(other)).andExpect(status().isNotFound());
        mockMvc.perform(patch("/resumes").with(other).contentType("application/json")
                .content("{\"resumeId\":" + resumeId + ",\"title\":\"stolen\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/resumes/{id}", resumeId).with(other)).andExpect(status().isNotFound());
        mockMvc.perform(get("/profiles/{id}", profileId).with(other)).andExpect(status().isNotFound());
        mockMvc.perform(get("/profiles/user/{id}", ownerUserId).with(other)).andExpect(status().isNotFound());
        mockMvc.perform(post("/user-profile-skill").with(other).contentType("application/json")
                .content("{\"userProfileId\":" + profileId + ",\"skillName\":\"Java\",\"proficiency\":50}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/user-profile-skill").with(owner).contentType("application/json")
                .content("{\"userProfileId\":" + profileId + ",\"skillName\":\"Java\",\"proficiency\":50}"))
                .andExpect(status().isCreated());
        Long skillId = jdbcTemplate.queryForObject("SELECT skill_id FROM skill WHERE skill_name = 'Java'", Long.class);
        mockMvc.perform(get("/user-profile-skill/profile/{id}", profileId).with(other))
                .andExpect(status().isNotFound());
        mockMvc.perform(patch("/user-profile-skill").with(other).contentType("application/json")
                .content("{\"profileId\":" + profileId + ",\"skillId\":" + skillId + ",\"proficiency\":99}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/user-profile-skill").with(other).contentType("application/json")
                .content("{\"userProfileId\":" + profileId + ",\"skillId\":" + skillId + "}"))
                .andExpect(status().isNotFound());

        assertEquals("Original title", resumeRepository.findById(resumeId).orElseThrow().getTitle());
        assertEquals("Resume", userProfileRepository.findById(profileId).orElseThrow().getFirstname());
        assertEquals(50, jdbcTemplate.queryForObject(
                "SELECT proficiency FROM user_profile_skill WHERE user_profile_id = ? AND skill_id = ?",
                Integer.class, profileId, skillId));
    }

    @Test
    @Order(8)
    void concurrentFirstLoginCreatesExactlyOneInternalUser() throws Exception {
        String subject = "concurrent-" + UUID.randomUUID();
        String issuer = "https://issuer.test/realm";
        Jwt firstToken = Jwt.withTokenValue("test-one").header("alg", "none")
                .issuer(issuer).subject(subject)
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300))
                .claim("email", subject + "-one@example.test")
                .claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER")))
                .build();
        Jwt secondToken = Jwt.withTokenValue("test-two").header("alg", "none")
                .issuer(issuer).subject(subject)
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300))
                .claim("email", subject + "-two@example.test")
                .claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER")))
                .build();
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> { start.await(); return currentUserService.getOrProvision(firstToken).getUserId(); });
            var second = executor.submit(() -> { start.await(); return currentUserService.getOrProvision(secondToken).getUserId(); });
            start.countDown();
            assertEquals(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS));
        }
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE auth_issuer = ? AND auth_subject = ?",
                Integer.class, issuer, subject));
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO users (user_id, email, user_role, auth_issuer, auth_subject) VALUES (?, ?, 'USER', ?, ?)",
                UUID.randomUUID(), subject + "-third@example.test", issuer, subject));
    }

    @Test
    @Order(8)
    void authenticatedUserCreatesOwnProfileAndResumeThroughApi() throws Exception {
        String subject = "new-owner-" + UUID.randomUUID();
        String email = subject + "@example.test";
        var owner = jwt().jwt(token -> token.issuer("https://issuer.test/realm")
                .subject(subject)
                .claim("email", email)
                .claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))));
        var profileResponse = mockMvc.perform(post("/profiles").with(owner)
                        .contentType("application/json")
                        .content("{\"firstname\":\"Alice\",\"surname\":\"Tester\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        Long profileId = objectMapper.readTree(profileResponse.getContentAsString()).get("userProfileId").asLong();
        var resumeResponse = mockMvc.perform(post("/resumes").with(owner)
                        .contentType("application/json")
                        .content("{\"title\":\"My resume\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        Long resumeId = objectMapper.readTree(resumeResponse.getContentAsString()).get("resumeId").asLong();
        mockMvc.perform(get("/profiles/{id}", profileId).with(owner))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstname").value("Alice"));
        mockMvc.perform(get("/resumes/{id}", resumeId).with(owner))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("My resume"));
    }

    @Test
    @Order(9)
    @EnabledIfEnvironmentVariable(named = "KEYCLOAK_USER_TEST_TOKEN", matches = ".+")
    void realKeycloakTokensAreValidatedAndRolesAreEnforced() throws Exception {
        String userToken = System.getenv("KEYCLOAK_USER_TEST_TOKEN");
        String adminToken = System.getenv("KEYCLOAK_ADMIN_TEST_TOKEN");
        assertEquals(200, mockMvc.perform(get("/users/me").header("Authorization", "Bearer " + userToken))
                .andReturn().getResponse().getStatus());
        assertEquals(403, mockMvc.perform(get("/users").header("Authorization", "Bearer " + userToken))
                .andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(get("/users").header("Authorization", "Bearer " + adminToken))
                .andReturn().getResponse().getStatus());
        assertEquals(401, mockMvc.perform(get("/users/me").header("Authorization", "Bearer invalid-token"))
                .andReturn().getResponse().getStatus());
        String wrongAudienceToken = System.getenv("KEYCLOAK_WRONG_AUDIENCE_TEST_TOKEN");
        if (wrongAudienceToken != null) {
            assertEquals(401, mockMvc.perform(get("/users/me").header("Authorization", "Bearer " + wrongAudienceToken))
                    .andReturn().getResponse().getStatus());
        }
    }

    @Test
    @Order(10)
    void searchFiltersArePrivateValidatedAndPersisted() throws Exception {
        var a = testJwt("filter-a-" + UUID.randomUUID());
        var b = testJwt("filter-b-" + UUID.randomUUID());
        var noProfile = testJwt("filter-no-profile-" + UUID.randomUUID());
        String path = "/api/search-filters/me";
        mockMvc.perform(get(path)).andExpect(status().isUnauthorized());
        mockMvc.perform(get(path).with(noProfile)).andExpect(status().isNotFound());
        mockMvc.perform(patch(path).with(noProfile).contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(path).with(noProfile)).andExpect(status().isNotFound());
        mockMvc.perform(post("/profiles").with(a).contentType("application/json")
                .content("{\"firstname\":\"A\",\"surname\":\"Tester\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/profiles").with(b).contentType("application/json")
                .content("{\"firstname\":\"B\",\"surname\":\"Tester\"}"))
                .andExpect(status().isCreated());
        Long skill1 = jdbcTemplate.queryForObject("SELECT MIN(skill_id) FROM skill", Long.class);
        Long skill2 = jdbcTemplate.queryForObject("SELECT MIN(skill_id) FROM skill WHERE skill_id > ?", Long.class, skill1);
        String create = "{\"preferredLocation\":\"Berlin\",\"radiusKm\":30,\"minimumSalary\":60000,"
                + "\"employmentTypes\":[\"FULL_TIME\",\"PART_TIME\"],\"workplaceTypes\":[\"REMOTE\",\"HYBRID\"],"
                + "\"preferredSkillIds\":[" + skill1 + "],\"excludedSkillIds\":[" + skill2 + "]}";
        mockMvc.perform(post(path).with(a).contentType("application/json").content(create))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.salaryCurrency").value("EUR"))
                .andExpect(jsonPath("$.salaryPeriod").value("ANNUAL_GROSS"));
        mockMvc.perform(post(path).with(a).contentType("application/json").content("{}"))
                .andExpect(status().isConflict());
        mockMvc.perform(get(path).with(b)).andExpect(status().isNotFound());
        mockMvc.perform(patch(path).with(b).contentType("application/json").content("{\"keywords\":\"stolen\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(path).with(b)).andExpect(status().isNotFound());
        mockMvc.perform(get(path).with(a)).andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredLocation").value("Berlin"))
                .andExpect(jsonPath("$.preferredSkillIds[0]").value(skill1))
                .andExpect(jsonPath("$.excludedSkillIds[0]").value(skill2))
                .andExpect(jsonPath("$.employmentTypes.length()").value(2))
                .andExpect(jsonPath("$.workplaceTypes.length()").value(2));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM search_filter_preferred_skill WHERE skill_id = ?", Integer.class, skill1));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM search_filter_prohibited_skill WHERE skill_id = ?", Integer.class, skill2));
        mockMvc.perform(patch(path).with(a).contentType("application/json")
                .content("{\"keywords\":\"Java\",\"excludedSkillIds\":null,\"employmentTypes\":[]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.minimumSalary").value(60000))
                .andExpect(jsonPath("$.preferredLocation").value("Berlin"))
                .andExpect(jsonPath("$.employmentTypes").isEmpty())
                .andExpect(jsonPath("$.excludedSkillIds").isEmpty());
        mockMvc.perform(patch(path).with(a).contentType("application/json")
                .content("{\"minimumSalary\":null,\"preferredLocation\":null}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.minimumSalary").doesNotExist());
        mockMvc.perform(patch(path).with(a).contentType("application/json")
                .content("{\"radiusKm\":-1}")) .andExpect(status().isBadRequest());
        mockMvc.perform(patch(path).with(a).contentType("application/json")
                .content("{\"minimumSalary\":-1}")) .andExpect(status().isBadRequest());
        mockMvc.perform(patch(path).with(a).contentType("application/json")
                .content("{\"excludedSkillIds\":[" + skill1 + "]}")) .andExpect(status().isBadRequest());
        mockMvc.perform(get(path).with(a)).andExpect(jsonPath("$.preferredSkillIds[0]").value(skill1));
        Long profileId = jdbcTemplate.queryForObject("SELECT user_profile_id FROM search_filter", Long.class);
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO search_filter (user_profile_id) VALUES (?)", profileId));
        mockMvc.perform(post(path).with(b).contentType("application/json").content("{}"))
                .andExpect(status().isCreated());
        mockMvc.perform(delete(path).with(a)).andExpect(status().isNoContent());
        mockMvc.perform(get(path).with(a)).andExpect(status().isNotFound());
        mockMvc.perform(get(path).with(b)).andExpect(status().isOk());
    }

    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor testJwt(String subject) {
        return jwt().jwt(token -> token.issuer("https://issuer.test/realm").subject(subject)
                .claim("email", subject + "@example.test").claim("email_verified", true)
                .claim("realm_access", java.util.Map.of("roles", java.util.List.of("USER"))));
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

    private Resume createResume(String subject) {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("resume-" + UUID.randomUUID() + "@example.test");
        user.setUserRole(Role.USER);
        user.setAuthIssuer("https://issuer.test/realm");
        user.setAuthSubject(subject);
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
