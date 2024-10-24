package com.api.scheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.scheduler.Dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionhandlerController {

	
	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<ErrorResponse> taskNotFoundExceptionHandler(TaskNotFoundException exception){
		
		ErrorResponse errorResponse =new ErrorResponse();
		errorResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		errorResponse.setMessage(exception.getMessage());
		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
}
