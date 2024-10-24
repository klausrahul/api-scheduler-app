package com.api.scheduler.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.scheduler.entity.Audit;

public interface AuditRepo extends JpaRepository<Audit, Long> {

}
