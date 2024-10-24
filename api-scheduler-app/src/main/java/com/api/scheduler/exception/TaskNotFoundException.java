package com.api.scheduler.exception;

public class TaskNotFoundException extends RuntimeException{

	public TaskNotFoundException() {
		super("Given Id is not avaliable");
	
	}

	

	public TaskNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}


	
	

}
