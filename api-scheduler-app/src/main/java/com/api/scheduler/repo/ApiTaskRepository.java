package com.api.scheduler.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.scheduler.entity.ApiTask;

@Repository
public interface ApiTaskRepository extends JpaRepository<ApiTask, Long>{
	 List<ApiTask> findByNextExecutionBefore(LocalDateTime now);
}
