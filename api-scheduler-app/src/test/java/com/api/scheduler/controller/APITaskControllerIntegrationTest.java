package com.api.scheduler.controller;

import com.api.scheduler.entity.ApiTask;
import com.api.scheduler.exception.TaskNotFoundException;
import com.api.scheduler.service.ApiClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class APITaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiClassService apiClassService;

    private ApiTask task;

    @BeforeEach
    public void setUp() {
        task = new ApiTask();
        task.setId(1L);
        task.setUrl("http://example.com");
        task.setHttpMethod("GET");
        task.setScheduleExpression("0 */2 * * * *");
        task.setNextExecution(LocalDateTime.now().plusMinutes(2)); 
    }

    @Test
    public void testCreateTask() throws Exception {
        when(apiClassService.createTask(any(ApiTask.class))).thenReturn(task);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"name\": \"Sample Task\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value("http://example.com"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        when(apiClassService.updateTask(anyLong(), any(ApiTask.class))).thenReturn(task);

        mockMvc.perform(put("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"name\": \"Updated Task\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value("http://example.com"));
    }

    @Test
    public void testDeleteTask() throws Exception {
        when(apiClassService.deleteTask(anyLong())).thenReturn("Task deleted successfully");

        mockMvc.perform(delete("/api/tasks/{id}", 1))
                .andExpect(status().isOk());
    }
    
    
    @Test
    public void testDeleteTaskException() throws Exception {
        when(apiClassService.deleteTask(anyLong())).thenThrow(new TaskNotFoundException());

        mockMvc.perform(delete("/api/tasks/{id}", 1))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testGetAllTasks() throws Exception {
        when(apiClassService.findAllTask()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].url").value("http://example.com"));
    }
}

