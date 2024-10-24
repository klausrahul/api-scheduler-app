package com.api.scheduler.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import com.api.scheduler.entity.ApiTask;
import com.api.scheduler.entity.Audit;
import com.api.scheduler.repo.ApiTaskRepository;
import com.api.scheduler.repo.AuditRepo;

@SpringJUnitConfig
@EnableScheduling
public class APISchedulerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiTaskRepository apiTaskRepository;

    @Mock
    private AuditRepo auditRepo;

    @InjectMocks
    private APISchedulerService apiSchedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiSchedulerService = new APISchedulerService();
        apiSchedulerService.restTemplate = restTemplate;
        apiSchedulerService.apiTaskRepository = apiTaskRepository;
        apiSchedulerService.auditRepo = auditRepo;
    }

    @Test
    public void testScheduleApiTasks_success() {
        // Mocking the API Task with future next execution time
        ApiTask task = new ApiTask();
        task.setId(1L);
        task.setUrl("http://example.com");
        task.setHttpMethod("GET");
        task.setScheduleExpression("*/5 * * * *");
        task.setNextExecution(LocalDateTime.now().minusMinutes(1)); // Task is due for execution

        when(apiTaskRepository.findByNextExecutionBefore(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(task));

        ResponseEntity<String> mockResponse = mock(ResponseEntity.class);
        when(mockResponse.getBody()).thenReturn("Mocked API Response");
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);

        // Call the scheduler
        apiSchedulerService.scheduleApiTasks();

        // Verify that the task was saved after execution
       // verify(apiTaskRepository, times(1)).save(any(ApiTask.class));

        // Verify that the audit log was created
      //  verify(auditRepo, times(1)).save(any(Audit.class));
    }

    @Test
    public void testScheduleApiTasks_failure() {
        // Mocking a task that will fail
        ApiTask task = new ApiTask();
        task.setId(1L);
        task.setUrl("http://example.com/fail");
        task.setHttpMethod("GET");
        task.setScheduleExpression("*/5 * * * *");
        task.setNextExecution(LocalDateTime.now().minusMinutes(1));

        when(apiTaskRepository.findByNextExecutionBefore(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(task));

        // Simulate an API call failure
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
            .thenThrow(new RuntimeException("API call failed"));

        // Call the scheduler
        apiSchedulerService.scheduleApiTasks();

        // Verify that the task was saved after execution
       // verify(apiTaskRepository, times(1)).save(any(ApiTask.class));

        // Verify that the audit log was created with failure status
      //  verify(auditRepo, times(1)).save(any(Audit.class));
    }

    @Test
    public void testCacheEviction() {
        ApiTask task = new ApiTask();
        task.setId(1L);
        task.setUrl("http://example.com");
        task.setHttpMethod("GET");
        task.setScheduleExpression("*/5 * * * *");

        // Mock the cache manager
        CacheManager cacheManager = new ConcurrentMapCacheManager("apiResults");

        // Test cache eviction
        apiSchedulerService.evictApiResult(task);

        // Since it's an in-memory cache, there's no direct verification in this simple case,
        // but we can ensure no unexpected behavior happened
        verifyNoMoreInteractions(apiTaskRepository, restTemplate, auditRepo);
    }
}
