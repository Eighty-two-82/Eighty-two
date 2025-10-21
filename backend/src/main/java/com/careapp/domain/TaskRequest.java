package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "task_requests")
public class TaskRequest {
    
    @Id
    private String id;
    
    @Field("requester_id")
    private String requesterId; // POA user ID
    
    @Field("requester_name")
    private String requesterName; // POA user name
    
    @Field("request_type")
    private String requestType; // new, recurring, modify, remove, reschedule
    
    @Field("task_title")
    private String taskTitle;
    
    @Field("description")
    private String description;
    
    @Field("priority")
    private String priority; // normal, urgent, very-urgent
    
    @Field("reason")
    private String reason; // Why the request is made
    
    @Field("status")
    private String status; // Pending, Approved, Rejected
    
    @Field("submitted_date")
    private LocalDateTime submittedDate;
    
    @Field("reviewed_date")
    private LocalDateTime reviewedDate;
    
    @Field("reviewed_by")
    private String reviewedBy; // Manager ID who reviewed
    
    @Field("approval_reason")
    private String approvalReason;
    
    @Field("rejection_reason")
    private String rejectionReason;
    
    @Field("patient_id")
    private String patientId; // Associated patient
    
    @Field("organization_id")
    private String organizationId; // Associated organization
    
    // Recurring task specific fields
    @Field("frequency")
    private String frequency; // daily, weekly, monthly, yearly
    
    @Field("frequency_number")
    private Integer frequencyNumber; // Every X days/weeks/months/years
    
    @Field("time_of_day")
    private String timeOfDay; // HH:mm format
    
    @Field("day_of_week")
    private String dayOfWeek; // Monday, Tuesday, etc.
    
    @Field("day_of_month")
    private Integer dayOfMonth; // 1-31
    
    @Field("month")
    private String month; // January, February, etc.
    
    @Field("start_date")
    private LocalDate startDate;
    
    @Field("end_date")
    private LocalDate endDate;
    
    // Task modification fields (for modify/remove/reschedule requests)
    @Field("original_task_id")
    private String originalTaskId; // For modify/remove/reschedule requests
    
    @Field("new_due_date")
    private LocalDate newDueDate; // For reschedule requests
    
    @Field("new_assigned_to")
    private String newAssignedTo; // For reassignment requests
    
    // Constructors
    public TaskRequest() {
        this.submittedDate = LocalDateTime.now();
        this.status = "Pending";
    }
    
    public TaskRequest(String requesterId, String requesterName, String requestType, 
                     String taskTitle, String description, String priority, String reason) {
        this();
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.requestType = requestType;
        this.taskTitle = taskTitle;
        this.description = description;
        this.priority = priority;
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }
    
    public String getRequesterName() {
        return requesterName;
    }
    
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }
    
    public String getRequestType() {
        return requestType;
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public String getTaskTitle() {
        return taskTitle;
    }
    
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.reviewedDate = LocalDateTime.now();
    }
    
    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }
    
    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }
    
    public LocalDateTime getReviewedDate() {
        return reviewedDate;
    }
    
    public void setReviewedDate(LocalDateTime reviewedDate) {
        this.reviewedDate = reviewedDate;
    }
    
    public String getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public String getApprovalReason() {
        return approvalReason;
    }
    
    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public Integer getFrequencyNumber() {
        return frequencyNumber;
    }
    
    public void setFrequencyNumber(Integer frequencyNumber) {
        this.frequencyNumber = frequencyNumber;
    }
    
    public String getTimeOfDay() {
        return timeOfDay;
    }
    
    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public Integer getDayOfMonth() {
        return dayOfMonth;
    }
    
    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
    
    public String getMonth() {
        return month;
    }
    
    public void setMonth(String month) {
        this.month = month;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getOriginalTaskId() {
        return originalTaskId;
    }
    
    public void setOriginalTaskId(String originalTaskId) {
        this.originalTaskId = originalTaskId;
    }
    
    public LocalDate getNewDueDate() {
        return newDueDate;
    }
    
    public void setNewDueDate(LocalDate newDueDate) {
        this.newDueDate = newDueDate;
    }
    
    public String getNewAssignedTo() {
        return newAssignedTo;
    }
    
    public void setNewAssignedTo(String newAssignedTo) {
        this.newAssignedTo = newAssignedTo;
    }
    
    // Business methods
    public void approve(String reviewerId, String approvalReason) {
        this.status = "Approved";
        this.reviewedBy = reviewerId;
        this.approvalReason = approvalReason;
        this.reviewedDate = LocalDateTime.now();
    }
    
    public void reject(String reviewerId, String rejectionReason) {
        this.status = "Rejected";
        this.reviewedBy = reviewerId;
        this.rejectionReason = rejectionReason;
        this.reviewedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "TaskRequest{" +
                "id='" + id + '\'' +
                ", requesterId='" + requesterId + '\'' +
                ", requestType='" + requestType + '\'' +
                ", taskTitle='" + taskTitle + '\'' +
                ", status='" + status + '\'' +
                ", submittedDate=" + submittedDate +
                '}';
    }
}
