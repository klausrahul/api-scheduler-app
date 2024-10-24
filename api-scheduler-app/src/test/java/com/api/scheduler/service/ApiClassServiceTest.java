package com.api.scheduler.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.api.scheduler.entity.ApiTask;
import com.api.scheduler.exception.TaskNotFoundException;
import com.api.scheduler.repo.ApiTaskRepository;

@SpringBootTest
public class ApiClassServiceTest {

    //@InjectMocks
	@Autowired
    private ApiClassService apiClassService;

    @MockBean
    private ApiTaskRepository apiTaskRepository;

    
    @MockBean
    private APISchedulerService apiSchedulerService;
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
    public void testCreateTask() {
        task.setUrl("http://example.com");
        when(apiTaskRepository.save(any(ApiTask.class))).thenReturn(task);

        ApiTask createdTask = apiClassService.createTask(task);

        verify(apiTaskRepository).save(task);
        assertEquals(task.getUrl(), createdTask.getUrl());
    }

    
    
    @Test
    public void testUpdateTaskSuccess() {
        when(apiTaskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(apiTaskRepository.save(any(ApiTask.class))).thenReturn(task);

        ApiTask updatedTask = new ApiTask();
        updatedTask.setUrl("http://newurl.com");
        updatedTask.setHttpMethod("POST");
        updatedTask.setScheduleExpression("0/10 * * * * ?");

        ApiTask result = apiClassService.updateTask(1L, updatedTask);

        verify(apiTaskRepository).findById(1L);
        verify(apiSchedulerService).evictApiResult(task);
        verify(apiTaskRepository).save(any(ApiTask.class));

        assertEquals("http://newurl.com", result.getUrl());
        assertEquals("POST", result.getHttpMethod());
    }

    @Test
    public void testUpdateTaskNotFound() {
        when(apiTaskRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiTask updatedTask = new ApiTask();
        updatedTask.setUrl("http://newurl.com");

        assertThrows(TaskNotFoundException.class, () -> apiClassService.updateTask(1L, updatedTask));
        verify(apiTaskRepository).findById(1L);
    }

    @Test
    public void testDeleteTaskSuccess() {
        when(apiTaskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        String result = apiClassService.deleteTask(1L);

        verify(apiTaskRepository).findById(1L);
        verify(apiSchedulerService).evictApiResult(task);
        verify(apiTaskRepository).deleteById(1L);

        assertEquals("success", result);
    }

    @Test
    public void testDeleteTaskNotFound() {
        when(apiTaskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> apiClassService.deleteTask(1L));
        verify(apiTaskRepository).findById(1L);
    }
    
    
    @Test
    public void testFindAllTask() {
        when(apiTaskRepository.findAll()).thenReturn(Collections.singletonList(task));

        List<ApiTask> tasks = apiClassService.findAllTask();

        verify(apiTaskRepository).findAll();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }
}
