package com.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class ApiController {

	
	@GetMapping("/call")
	public ResponseEntity<String> getResult() {
		//TODO: process POST request
		
		return new ResponseEntity<String>("Hello..api is getting called", HttpStatus.OK);
	}
	
}
