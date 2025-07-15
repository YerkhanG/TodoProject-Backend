package com.example.prac1.integration;

import com.example.prac1.model.Task;
import com.example.prac1.model.User;
import com.example.prac1.repo.TaskRepository;
import com.example.prac1.repo.UserRepository;
import com.example.prac1.security.JwtUtil;
import com.example.prac1.service.CustomUserDetailsService;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.transaction.annotation.Transactional;


import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class TaskControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity()) // <--- ADD THIS LINE!
                .build();
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesTaskController() {
        ServletContext servletContext = webApplicationContext.getServletContext();
        assertNotNull(servletContext);
        assertNotNull(webApplicationContext.getBean("taskController"));
    }

    @Test
    public void givenMalformedJson_whenCreateTask_thenReturnsBadRequest() throws Exception {
        String malformedJson = "{invalid Json}";
        String currentUserToken = generateValidToken();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + currentUserToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn403WhenDeleteOthersTask() throws Exception {

        Task othersTask = createTaskForUser();

        String currentUserToken = generateValidToken();

        mockMvc.perform(delete("/api/tasks/" + othersTask.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + currentUserToken))
                .andExpect(status().isForbidden());
    }

    private Task createTaskForUser(){

        userRepository.findByName("otherUser").ifPresent(userRepository::delete);

        User newUser = new User();
        newUser.setName("otherUser");
        newUser.setPassword("password");
        User savedUser = userRepository.save(newUser);

        Task task = new Task();
        task.setTitle("Task for otherUser");
        task.setDescription("Description for otherUser's task");
        task.setUser(savedUser);
        return taskRepository.save(task);
    }

    @Test
    void shouldReturn401WhenTokenExpired() throws Exception {
        String expiredToken = generateExpiredToken();

        mockMvc.perform(get("/api/tasks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                        .andExpect(status().isUnauthorized());
    }

    private String generateValidToken() {

        User currentUser = userRepository.findByName("currentUser")
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName("currentUser");
                    newUser.setPassword("encodedPassword");
                    return userRepository.save(newUser);
                });
        UserDetails userDetails = userDetailsService.loadUserByUsername(currentUser.getName());
        return jwtUtil.generateToken(userDetails);
    }
    @Value("${jwt.secret}")
    String TEST_JWT_SECRET ;
    public String generateExpiredToken() {
        User expiredUser = userRepository.findByName("expiredUser")
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName("expiredUser");
                    newUser.setPassword("encodedPassword");
                    return userRepository.save(newUser);
                });
        return jwtUtil.createExpiredToken(expiredUser.getName());
    }
}