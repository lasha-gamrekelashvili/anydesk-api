package com.example.anydeskapi;

import com.example.anydeskapi.data.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldReturn201_WhenValid() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "Jon", "email": "jon@example.com" }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("Jon"))
            .andExpect(jsonPath("$.email").value("jon@example.com"));
    }

    @Test
    void createUser_ShouldReturn400_WhenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "username": "Jon", "email": "jon@example.com" }
                """));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "Duplicate", "email": "jon@example.com" }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("User with this email already exists."));
    }

    @Test
    void createUser_ShouldReturn400_WhenUsernameIsBlank() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "", "email": "test@example.com" }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.username").value("Username cannot be empty."));
    }

    @Test
    void createUser_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "Test", "email": "invalid" }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").value("Email format is invalid."));
    }

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "username": "Jon", "email": "jon@example.com" }
                """));
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "username": "Jane", "email": "jane@example.com" }
                """));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "Jon", "email": "jon@example.com" }
                """))
            .andReturn().getResponse().getContentAsString();

        long id = extractIdFromJson(response);

        mockMvc.perform(get("/api/users/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("Jon"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "Old", "email": "old@example.com" }
                """))
            .andReturn().getResponse().getContentAsString();

        long id = extractIdFromJson(response);

        mockMvc.perform(put("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "Updated", "email": "updated@example.com" }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("Updated"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "username": "DeleteMe", "email": "delete@example.com" }
                """))
            .andReturn().getResponse().getContentAsString();

        long id = extractIdFromJson(response);

        mockMvc.perform(delete("/api/users/" + id))
            .andExpect(status().isNoContent());
    }

    public static long extractIdFromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        return node.get("id").asLong();
    }
}
