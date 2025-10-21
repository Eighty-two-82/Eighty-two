package com.careapp.service;

import com.careapp.domain.TaskRequest;
import com.careapp.domain.Task;
import com.careapp.domain.RecurringTask;
import com.careapp.repository.TaskRequestRepository;
import com.careapp.repository.TaskRepository;
import com.careapp.repository.RecurringTaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskRequestService {
    
    @Resource
    private TaskRequestRepository taskRequestRepository;
    
    @Resource
    private TaskRepository taskRepository;
    
    @Resource
    private RecurringTaskRepository recurringTaskRepository;
    
    // Create a new task request
    public TaskRequest createTaskRequest(TaskRequest taskRequest) {
        if (taskRequest.getSubmittedDate() == null) {
            taskRequest.setSubmittedDate(LocalDateTime.now());
        }
        if (taskRequest.getStatus() == null || taskRequest.getStatus().isEmpty()) {
            taskRequest.setStatus("Pending");
        }
        
        // Set default organizationId if not provided
        if (taskRequest.getOrganizationId() == null || taskRequest.getOrganizationId().trim().isEmpty()) {
            taskRequest.setOrganizationId("default-org");
        }
        
        return taskRequestRepository.save(taskRequest);
    }
    
    // Get task request by ID
    public Optional<TaskRequest> getTaskRequestById(String id) {
        return taskRequestRepository.findById(id);
    }
    
    // Get all task requests
    public List<TaskRequest> getAllTaskRequests() {
        return taskRequestRepository.findAll();
    }
    
    // Get task requests by requester
    public List<TaskRequest> getTaskRequestsByRequester(String requesterId) {
        return taskRequestRepository.findByRequesterId(requesterId);
    }
    
    // Get task requests by status
    public List<TaskRequest> getTaskRequestsByStatus(String status) {
        return taskRequestRepository.findByStatus(status);
    }
    
    // Get pending task requests
    public List<TaskRequest> getPendingTaskRequests() {
        return taskRequestRepository.findByStatusOrderBySubmittedDateDesc("Pending");
    }
    
    // Get task requests by organization
    public List<TaskRequest> getTaskRequestsByOrganization(String organizationId) {
        return taskRequestRepository.findByOrganizationId(organizationId);
    }
    
    // Get task requests by organization and status
    public List<TaskRequest> getTaskRequestsByOrganizationAndStatus(String organizationId, String status) {
        return taskRequestRepository.findByOrganizationIdAndStatus(organizationId, status);
    }
    
    // Get pending task requests for organization
    public List<TaskRequest> getPendingTaskRequestsForOrganization(String organizationId) {
        // If organizationId is default-org-001, also include requests with null organizationId
        if ("default-org-001".equals(organizationId)) {
            List<TaskRequest> orgRequests = taskRequestRepository.findByOrganizationIdAndStatus(organizationId, "Pending");
            List<TaskRequest> nullOrgRequests = taskRequestRepository.findByOrganizationIdAndStatus(null, "Pending");
            
            // Combine both lists
            orgRequests.addAll(nullOrgRequests);
            return orgRequests;
        } else {
            return taskRequestRepository.findByOrganizationIdAndStatus(organizationId, "Pending");
        }
    }
    
    // Update task request
    public TaskRequest updateTaskRequest(TaskRequest taskRequest) {
        return taskRequestRepository.save(taskRequest);
    }
    
    // Approve task request
    public TaskRequest approveTaskRequest(String requestId, String reviewerId, String approvalReason) {
        Optional<TaskRequest> requestOpt = taskRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            TaskRequest request = requestOpt.get();
            request.approve(reviewerId, approvalReason);
            
            // If approved, create the actual task or recurring task
            if ("new".equals(request.getRequestType())) {
                createTaskFromRequest(request);
            } else if ("recurring".equals(request.getRequestType())) {
                createRecurringTaskFromRequest(request);
            } else if ("modify".equals(request.getRequestType())) {
                modifyTaskFromRequest(request);
            } else if ("remove".equals(request.getRequestType())) {
                removeTaskFromRequest(request);
            } else if ("reschedule".equals(request.getRequestType())) {
                rescheduleTaskFromRequest(request);
            }
            
            return taskRequestRepository.save(request);
        }
        return null;
    }
    
    // Reject task request
    public TaskRequest rejectTaskRequest(String requestId, String reviewerId, String rejectionReason) {
        Optional<TaskRequest> requestOpt = taskRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            TaskRequest request = requestOpt.get();
            request.reject(reviewerId, rejectionReason);
            return taskRequestRepository.save(request);
        }
        return null;
    }
    
    // Delete task request
    public boolean deleteTaskRequest(String requestId) {
        if (taskRequestRepository.existsById(requestId)) {
            taskRequestRepository.deleteById(requestId);
            return true;
        }
        return false;
    }
    
    // Get task request statistics
    public long getTaskRequestCountByStatus(String status) {
        return taskRequestRepository.countByStatus(status);
    }
    
    public long getTaskRequestCountByRequesterAndStatus(String requesterId, String status) {
        return taskRequestRepository.countByRequesterIdAndStatus(requesterId, status);
    }
    
    public long getTaskRequestCountByOrganizationAndStatus(String organizationId, String status) {
        return taskRequestRepository.countByOrganizationIdAndStatus(organizationId, status);
    }
    
    // Private helper methods for creating tasks from approved requests
    private void createTaskFromRequest(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setPatientId(request.getPatientId());
        task.setOrganizationId(request.getOrganizationId());
        task.setCreatedBy(request.getReviewedBy()); // Manager who approved
        task.setStatus("In Progress");
        
        // Set due date - could be calculated from request or set to a default
        if (request.getStartDate() != null) {
            task.setDueDate(request.getStartDate());
        } else {
            task.setDueDate(LocalDate.now().plusDays(1)); // Default to tomorrow
        }
        
        taskRepository.save(task);
    }
    
    private void createRecurringTaskFromRequest(TaskRequest request) {
        // This would create a RecurringTask template
        // Implementation depends on RecurringTask structure
        RecurringTask recurringTask = new RecurringTask();
        recurringTask.setTitle(request.getTaskTitle());
        recurringTask.setDescription(request.getDescription());
        recurringTask.setPriority(request.getPriority());
        recurringTask.setPatientId(request.getPatientId());
        recurringTask.setOrganizationId(request.getOrganizationId());
        recurringTask.setCreatedBy(request.getReviewedBy());
        recurringTask.setFrequency(request.getFrequency());
        recurringTask.setFrequencyNumber(request.getFrequencyNumber());
        recurringTask.setTimeOfDay(request.getTimeOfDay());
        recurringTask.setDayOfWeek(request.getDayOfWeek());
        recurringTask.setDayOfMonth(request.getDayOfMonth());
        recurringTask.setMonth(request.getMonth());
        recurringTask.setStartDate(request.getStartDate());
        recurringTask.setEndDate(request.getEndDate());
        recurringTask.setIsActive(true);
        
        recurringTaskRepository.save(recurringTask);
    }
    
    private void modifyTaskFromRequest(TaskRequest request) {
        if (request.getOriginalTaskId() != null) {
            Optional<Task> taskOpt = taskRepository.findById(request.getOriginalTaskId());
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setTitle(request.getTaskTitle());
                task.setDescription(request.getDescription());
                task.setPriority(request.getPriority());
                taskRepository.save(task);
            }
        }
    }
    
    private void removeTaskFromRequest(TaskRequest request) {
        if (request.getOriginalTaskId() != null) {
            taskRepository.deleteById(request.getOriginalTaskId());
        }
    }
    
    private void rescheduleTaskFromRequest(TaskRequest request) {
        if (request.getOriginalTaskId() != null && request.getNewDueDate() != null) {
            Optional<Task> taskOpt = taskRepository.findById(request.getOriginalTaskId());
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setDueDate(request.getNewDueDate());
                if (request.getNewAssignedTo() != null) {
                    task.setAssignedTo(request.getNewAssignedTo());
                }
                taskRepository.save(task);
            }
        }
    }
}
