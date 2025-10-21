package com.careapp.controller;

import com.careapp.domain.TaskRequest;
import com.careapp.service.TaskRequestService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/task-requests")
public class TaskRequestController {
    
    @Resource
    private TaskRequestService taskRequestService;
    
    // Create a new task request
    @PostMapping
    public Result<TaskRequest> createTaskRequest(
            @RequestBody TaskRequest taskRequest,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId,
            @RequestHeader(value = "X-Patient-Id", required = false) String patientId) {
        try {
            // Set user information from headers if provided
            if (userId != null) {
                taskRequest.setRequesterId(userId);
            }
            if (organizationId != null) {
                taskRequest.setOrganizationId(organizationId);
            }
            if (patientId != null) {
                taskRequest.setPatientId(patientId);
            }
            
            TaskRequest createdRequest = taskRequestService.createTaskRequest(taskRequest);
            return Result.success(createdRequest, "Task request created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create task request: " + e.getMessage());
        }
    }
    
    // Get all task requests
    @GetMapping
    public Result<List<TaskRequest>> getAllTaskRequests() {
        try {
            List<TaskRequest> requests = taskRequestService.getAllTaskRequests();
            return Result.success(requests, "Task requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve task requests: " + e.getMessage());
        }
    }
    
    // Get task request by ID
    @GetMapping("/{id}")
    public Result<TaskRequest> getTaskRequestById(@PathVariable String id) {
        try {
            Optional<TaskRequest> request = taskRequestService.getTaskRequestById(id);
            if (request.isPresent()) {
                return Result.success(request.get(), "Task request retrieved successfully!");
            } else {
                return Result.error("404", "Task request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve task request: " + e.getMessage());
        }
    }
    
    // Get task requests by requester
    @GetMapping("/requester/{requesterId}")
    public Result<List<TaskRequest>> getTaskRequestsByRequester(@PathVariable String requesterId) {
        try {
            List<TaskRequest> requests = taskRequestService.getTaskRequestsByRequester(requesterId);
            return Result.success(requests, "Requester task requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve requester task requests: " + e.getMessage());
        }
    }
    
    // Get task requests by status
    @GetMapping("/status/{status}")
    public Result<List<TaskRequest>> getTaskRequestsByStatus(@PathVariable String status) {
        try {
            List<TaskRequest> requests = taskRequestService.getTaskRequestsByStatus(status);
            return Result.success(requests, "Task requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve task requests: " + e.getMessage());
        }
    }
    
    // Get pending task requests
    @GetMapping("/pending")
    public Result<List<TaskRequest>> getPendingTaskRequests() {
        try {
            List<TaskRequest> requests = taskRequestService.getPendingTaskRequests();
            return Result.success(requests, "Pending task requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve pending task requests: " + e.getMessage());
        }
    }
    
    // Get pending task requests for organization
    @GetMapping("/pending/organization/{organizationId}")
    public Result<List<TaskRequest>> getPendingTaskRequestsForOrganization(@PathVariable String organizationId) {
        try {
            // Handle null or empty organizationId
            if (organizationId == null || organizationId.trim().isEmpty() || "null".equals(organizationId)) {
                return Result.error("400", "Organization ID is required!");
            }
            
            List<TaskRequest> requests = taskRequestService.getPendingTaskRequestsForOrganization(organizationId);
            return Result.success(requests, "Pending task requests for organization retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve pending task requests: " + e.getMessage());
        }
    }
    
    // Get task requests by organization
    @GetMapping("/organization/{organizationId}")
    public Result<List<TaskRequest>> getTaskRequestsByOrganization(@PathVariable String organizationId) {
        try {
            List<TaskRequest> requests = taskRequestService.getTaskRequestsByOrganization(organizationId);
            return Result.success(requests, "Organization task requests retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve organization task requests: " + e.getMessage());
        }
    }
    
    // Update task request
    @PutMapping("/{id}")
    public Result<TaskRequest> updateTaskRequest(@PathVariable String id, @RequestBody TaskRequest taskRequest) {
        try {
            Optional<TaskRequest> existingRequest = taskRequestService.getTaskRequestById(id);
            if (existingRequest.isPresent()) {
                taskRequest.setId(id);
                TaskRequest updatedRequest = taskRequestService.updateTaskRequest(taskRequest);
                return Result.success(updatedRequest, "Task request updated successfully!");
            } else {
                return Result.error("404", "Task request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update task request: " + e.getMessage());
        }
    }
    
    // Approve task request
    @PostMapping("/{id}/approve")
    public Result<TaskRequest> approveTaskRequest(
            @PathVariable String id, 
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String reviewerId) {
        try {
            String approvalReason = body.get("approvalReason");
            String reviewer = reviewerId != null ? reviewerId : "manager-001"; // Default reviewer
            
            TaskRequest approvedRequest = taskRequestService.approveTaskRequest(id, reviewer, approvalReason);
            if (approvedRequest != null) {
                return Result.success(approvedRequest, "Task request approved successfully!");
            } else {
                return Result.error("404", "Task request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to approve task request: " + e.getMessage());
        }
    }
    
    // Reject task request
    @PostMapping("/{id}/reject")
    public Result<TaskRequest> rejectTaskRequest(
            @PathVariable String id, 
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String reviewerId) {
        try {
            String rejectionReason = body.get("rejectionReason");
            String reviewer = reviewerId != null ? reviewerId : "manager-001"; // Default reviewer
            
            TaskRequest rejectedRequest = taskRequestService.rejectTaskRequest(id, reviewer, rejectionReason);
            if (rejectedRequest != null) {
                return Result.success(rejectedRequest, "Task request rejected successfully!");
            } else {
                return Result.error("404", "Task request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to reject task request: " + e.getMessage());
        }
    }
    
    // Delete task request
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteTaskRequest(@PathVariable String id) {
        try {
            boolean deleted = taskRequestService.deleteTaskRequest(id);
            if (deleted) {
                return Result.success(true, "Task request deleted successfully!");
            } else {
                return Result.error("404", "Task request not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete task request: " + e.getMessage());
        }
    }
    
    // Get task request statistics
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTaskRequestStats() {
        try {
            Map<String, Object> stats = Map.of(
                "total", taskRequestService.getAllTaskRequests().size(),
                "pending", taskRequestService.getTaskRequestCountByStatus("Pending"),
                "approved", taskRequestService.getTaskRequestCountByStatus("Approved"),
                "rejected", taskRequestService.getTaskRequestCountByStatus("Rejected")
            );
            return Result.success(stats, "Task request statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve task request statistics: " + e.getMessage());
        }
    }
    
    // Get task request statistics for organization
    @GetMapping("/stats/organization/{organizationId}")
    public Result<Map<String, Object>> getTaskRequestStatsForOrganization(@PathVariable String organizationId) {
        try {
            Map<String, Object> stats = Map.of(
                "total", taskRequestService.getTaskRequestsByOrganization(organizationId).size(),
                "pending", taskRequestService.getTaskRequestCountByOrganizationAndStatus(organizationId, "Pending"),
                "approved", taskRequestService.getTaskRequestCountByOrganizationAndStatus(organizationId, "Approved"),
                "rejected", taskRequestService.getTaskRequestCountByOrganizationAndStatus(organizationId, "Rejected")
            );
            return Result.success(stats, "Organization task request statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve organization task request statistics: " + e.getMessage());
        }
    }
    
    // Get task request statistics for requester
    @GetMapping("/stats/requester/{requesterId}")
    public Result<Map<String, Object>> getTaskRequestStatsForRequester(@PathVariable String requesterId) {
        try {
            Map<String, Object> stats = Map.of(
                "total", taskRequestService.getTaskRequestsByRequester(requesterId).size(),
                "pending", taskRequestService.getTaskRequestCountByRequesterAndStatus(requesterId, "Pending"),
                "approved", taskRequestService.getTaskRequestCountByRequesterAndStatus(requesterId, "Approved"),
                "rejected", taskRequestService.getTaskRequestCountByRequesterAndStatus(requesterId, "Rejected")
            );
            return Result.success(stats, "Requester task request statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve requester task request statistics: " + e.getMessage());
        }
    }
}
