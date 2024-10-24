package com.api.scheduler.Dto;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {

	private String message;
	private HttpStatus httpStatus;
}
