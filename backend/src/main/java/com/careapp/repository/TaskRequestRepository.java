package com.careapp.repository;

import com.careapp.domain.TaskRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRequestRepository extends MongoRepository<TaskRequest, String> {
    
    // Find task requests by requester
    List<TaskRequest> findByRequesterId(String requesterId);
    
    // Find task requests by status
    List<TaskRequest> findByStatus(String status);
    
    // Find task requests by request type
    List<TaskRequest> findByRequestType(String requestType);
    
    // Find task requests by organization
    List<TaskRequest> findByOrganizationId(String organizationId);
    
    // Find task requests by patient
    List<TaskRequest> findByPatientId(String patientId);
    
    // Find task requests by reviewer
    List<TaskRequest> findByReviewedBy(String reviewedBy);
    
    // Find pending task requests
    List<TaskRequest> findByStatusOrderBySubmittedDateDesc(String status);
    
    // Find task requests by requester and status
    List<TaskRequest> findByRequesterIdAndStatus(String requesterId, String status);
    
    // Find task requests by organization and status
    List<TaskRequest> findByOrganizationIdAndStatus(String organizationId, String status);
    
    // Find task requests submitted after a certain date
    List<TaskRequest> findBySubmittedDateAfter(LocalDateTime date);
    
    // Find task requests by multiple statuses
    @Query("{'status': {$in: ?0}}")
    List<TaskRequest> findByStatusIn(List<String> statuses);
    
    // Find task requests by requester and multiple statuses
    @Query("{'requesterId': ?0, 'status': {$in: ?1}}")
    List<TaskRequest> findByRequesterIdAndStatusIn(String requesterId, List<String> statuses);
    
    // Find task requests by organization and multiple statuses
    @Query("{'organizationId': ?0, 'status': {$in: ?1}}")
    List<TaskRequest> findByOrganizationIdAndStatusIn(String organizationId, List<String> statuses);
    
    // Count task requests by status
    long countByStatus(String status);
    
    // Count task requests by requester and status
    long countByRequesterIdAndStatus(String requesterId, String status);
    
    // Count task requests by organization and status
    long countByOrganizationIdAndStatus(String organizationId, String status);
}
