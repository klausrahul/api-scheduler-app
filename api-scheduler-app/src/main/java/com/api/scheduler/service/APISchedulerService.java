package com.api.scheduler.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.scheduler.entity.ApiTask;
import com.api.scheduler.entity.Audit;
import com.api.scheduler.repo.ApiTaskRepository;
import com.api.scheduler.repo.AuditRepo;

@Service
public class APISchedulerService {
	public static final Logger log=LoggerFactory.getLogger(APISchedulerService.class);
	@Autowired
	 RestTemplate restTemplate;
	
	@Autowired
	 ApiTaskRepository apiTaskRepository;
	
	@Autowired
	 AuditRepo auditRepo;
	
	
	
	 @Cacheable(value = "apiResults", key = "#task.id")
	    public String getCachedApiResult(ApiTask task) {
	        // Fetch result from cache (if not present, this method will be called and the result will be cached)
	        return null; // Return null to simulate no cache hit
	    }

	    @CachePut(value = "apiResults", key = "#task.id")
	    public String cacheApiResult(ApiTask task, String result) {
	        // Cache the new result after execution
	        return result;
	    }

	    @CacheEvict(value = "apiResults", key = "#task.id")
	    public void evictApiResult(ApiTask task) {
	        // Evict the cached result for a task
	    }
	
	    @Scheduled(fixedRate = 60000)
	    public void scheduleApiTasks() {
	        List<ApiTask> tasks = apiTaskRepository.findByNextExecutionBefore(LocalDateTime.now());
	        for (ApiTask task : tasks) {
	            String cachedResult = getCachedApiResult(task);
	            if (cachedResult != null) {
	                System.out.println("Using cached result for task " + task.getId());
	                continue;
	            }
	            executeTask(task);
	        }
	    }
	 
	 private void executeTask(ApiTask task) {
	        try {
	           String result =makeApiCall(task);
	            cacheApiResult(task, result);
	            task.setLastExecuted(LocalDateTime.now());
	            task.setNextExecution(nextExecutionTime(task.getScheduleExpression()));
	            apiTaskRepository.save(task);

	            logAudit(task, "SUCCESS", null);
	        } catch (Exception e) {
	            logAudit(task, "FAILED", e.getMessage());
	        }
	    }

	    private String makeApiCall(ApiTask task) throws Exception {
	        System.out.println("Calling API: " + task.getUrl());
	       try {
	    	   if(task.getHttpMethod().equalsIgnoreCase("get")) {
		        	 ResponseEntity<String> resultString=restTemplate.getForEntity(task.getUrl(), String.class);
		 	        log.info("Result is " + resultString.getBody());
		 	        return "API Response from "  + task.getUrl() + "= " +resultString.getBody();
		        }
	       }catch (Exception e) {
	    	   throw new Exception("API call failed");
		}
		return null;
	    }

	    private LocalDateTime nextExecutionTime(String scheduleExpression) {
	    	CronExpression cron = CronExpression.parse(scheduleExpression);
	    	LocalDateTime now = LocalDateTime.now();
	        LocalDateTime nextExecution = cron.next(now);

	    	
	        return nextExecution;
	    }

	    
	    private void logAudit(ApiTask task, String status, String errorMessage) {
	       Audit log = new Audit(task.getId(), LocalDateTime.now(), status, errorMessage);
	        auditRepo.save(log);
	    }
	
}
