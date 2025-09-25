package io.spring.demo.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository repository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void creates_customer() throws Exception {
        // given
        var payload = Map.of(
            "name", "Alice",
            "email", "alice@example.com"
        );

        // when / then
        mvc.perform(post("/customers")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void returns_400_when_email_is_invalid() throws Exception {
        // given
        var payload = Map.of(
            "name", "Bob",
            "email", "not-an-email"
        );

        // when / then
        mvc.perform(post("/customers")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("email is invalid"));
    }

    @Test
    void returns_400_when_email_is_missing() throws Exception {
        // given
        var payload = Map.of(
            "name", "Charlie"
        );

        // when / then
        mvc.perform(post("/customers")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
                .value("email must not be empty"));
    }


    @Test
    void returns_400_when_name_is_blank() throws Exception {
        // given
        var payload = Map.of(
            "name", "   ",
            "email", "bob@example.com"
        );

        // when / then
        mvc.perform(post("/customers")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
                .value("name must not be empty"));
    }


    @Test
    void returns_customer_by_id() throws Exception {
        // given
        var customer = new Customer();
        customer.setName("Bob");
        customer.setEmail("bob@example.com");
        var saved = repository.saveAndFlush(customer);

        // when / then
        mvc.perform(get("/customers/{id}", saved.getId())
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()))
            .andExpect(jsonPath("$.name").value("Bob"))
            .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void returns_404_when_customer_is_not_present() throws Exception {
        // given
        var nonExistentId  = RandomUtils.secure().randomLong();

        // when / then
        mvc.perform(get("/customers/{id}", nonExistentId)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error")
                .value("Could not find customer with id=%s".formatted(nonExistentId)));
    }
}