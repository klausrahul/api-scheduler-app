package com.api.scheduler.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.api.scheduler.entity.ApiTask;
import com.api.scheduler.exception.TaskNotFoundException;
import com.api.scheduler.repo.ApiTaskRepository;

@Service
public class ApiClassService {

	public static final Logger log = LoggerFactory.getLogger(ApiClassService.class);

	@Autowired
	private ApiTaskRepository apiTaskRepository;
	@Autowired
	private APISchedulerService apiSchedulerService;

	public ApiTask createTask(ApiTask task) {

		CronExpression cron = CronExpression.parse(task.getScheduleExpression());
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextExecution = cron.next(now);
		task.setNextExecution(nextExecution);
		log.info("Current time is + " + now);
		log.info("nextExecution time is + " + nextExecution);
		return apiTaskRepository.save(task);
	}

	public ApiTask updateTask(Long id, ApiTask task) {
		
		
		ApiTask existingTask = apiTaskRepository.findById(id)
				.orElseThrow(() ->new TaskNotFoundException("Given id " + "id" + "Task is not avaliable"));
		existingTask.setUrl(task.getUrl());
		existingTask.setHttpMethod(task.getHttpMethod());
		existingTask.setScheduleExpression(task.getScheduleExpression());
		// Evict cache as task is updated
		apiSchedulerService.evictApiResult(existingTask);
		return apiTaskRepository.save(existingTask);

	}
	
	public String deleteTask(Long id) {
		ApiTask existingTask = apiTaskRepository.findById(id)
				.orElseThrow(() ->new TaskNotFoundException("Given id " + "id" + "Task is not avaliable"));
	        
	        apiSchedulerService.evictApiResult(existingTask);
	        
	        apiTaskRepository.deleteById(id);
	        return "success";
	}

	public List<ApiTask> findAllTask() {
		return apiTaskRepository.findAll();
	}
}
