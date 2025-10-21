package com.careapp.repository;

import com.careapp.domain.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends MongoRepository<Worker, String> {
    
    // Find workers by organization
    List<Worker> findByOrganizationId(String organizationId);
    
    // Find workers by status
    List<Worker> findByStatus(String status);
    
    // Find worker by worker ID (W001, W002, etc.)
    Optional<Worker> findByWorkerId(String workerId);
    
    // Find worker by email
    Optional<Worker> findByEmail(String email);
    
    
    // 注意：移除了findByAssignedPatientsContaining方法
    // 因为不再直接分配Patient给Worker
    
    // Find workers by organization and status
    List<Worker> findByOrganizationIdAndStatus(String organizationId, String status);
}
