package com.api.scheduler.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Audit {

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long apiTaskId;
    private LocalDateTime executionTime;
    private String status;
    private String errorMessage;
	public Audit(Long apiTaskId, LocalDateTime executionTime, String status, String errorMessage) {
		this.apiTaskId = apiTaskId;
		this.executionTime = executionTime;
		this.status = status;
		this.errorMessage = errorMessage;
	}
    
    
}
