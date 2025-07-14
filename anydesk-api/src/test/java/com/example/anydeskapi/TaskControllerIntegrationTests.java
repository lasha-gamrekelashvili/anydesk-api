package com.example.anydeskapi;

import com.example.anydeskapi.data.repositories.TaskRepository;
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
class TaskControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_ShouldReturn201_WhenValidInput() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Task A",
                        "description": "Do this"
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Task A"))
            .andExpect(jsonPath("$.description").value("Do this"));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "title": "Task 1", "description": "Desc 1" }
            """));

        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                { "title": "Task 2", "description": "Desc 2" }
            """));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "Task A", "description": "Do this" }
                """))
            .andReturn().getResponse().getContentAsString();

        long id = extractIdFromJson(response);

        mockMvc.perform(get("/api/tasks/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Task A"));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask_WhenValid() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "Old", "description": "Old desc" }
                """))
            .andReturn().getResponse().getContentAsString();

        long id = extractIdFromJson(response);

        mockMvc.perform(put("/api/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "Updated", "description": "Updated Desc" }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated"))
            .andExpect(jsonPath("$.description").value("Updated Desc"));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "ToDelete", "description": "To delete" }
                """))
            .andReturn().getResponse().getContentAsString();

        long id = extractIdFromJson(response);

        mockMvc.perform(delete("/api/tasks/" + id))
            .andExpect(status().isNoContent());
    }

    @Test
    void createTask_ShouldReturn400_WhenTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "", "description": "Valid" }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Task title cannot be empty."));
    }

    @Test
    void createTask_ShouldReturn400_WhenDescriptionIsBlank() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "Valid", "description": "" }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Task description cannot be empty."));
    }

    @Test
    void createTask_ShouldReturn400_WhenTitleAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    { "title": "Duplicate", "description": "desc" }
                """));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "title": "Duplicate", "description": "something else" }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Task with this title already exists."));
    }

    public static long extractIdFromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        return node.get("id").asLong();
    }
}
