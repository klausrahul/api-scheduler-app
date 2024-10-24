
package com.api.scheduler.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.scheduler.entity.ApiTask;
import com.api.scheduler.service.ApiClassService;

@RestController
@RequestMapping("/api/tasks")
public class APITaskController {

	@Autowired
	private ApiClassService apiClassService;

	@PostMapping
	public ResponseEntity<ApiTask> createTask(@RequestBody ApiTask task) {
		return new ResponseEntity<ApiTask>(apiClassService.createTask(task), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiTask> updateTask(@PathVariable Long id, @RequestBody ApiTask task) {
		
		 return new ResponseEntity<ApiTask>(apiClassService.updateTask(id, task), HttpStatus.OK);
	}
	 

	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteTask(@PathVariable Long id) {
		return new ResponseEntity<String>(apiClassService.deleteTask(id), HttpStatus.OK); 
	}

	@GetMapping
	public ResponseEntity<List<ApiTask>> getAllTasks() {
		return new ResponseEntity<List<ApiTask>>(apiClassService.findAllTask(), HttpStatus.OK);
	}
}
