package com.careapp.service;

import com.careapp.domain.Schedule;
import com.careapp.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * Create a new schedule
     * @param schedule The schedule object to create
     * @return The created schedule
     */
    public Schedule createSchedule(Schedule schedule) {
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        if (schedule.getStatus() == null || schedule.getStatus().isEmpty()) {
            schedule.setStatus("scheduled");
        }
        return scheduleRepository.save(schedule);
    }

    /**
     * Get schedule by ID
     * @param id The ID of the schedule
     * @return An Optional containing the schedule if found, otherwise empty
     */
    public Optional<Schedule> getScheduleById(String id) {
        return scheduleRepository.findById(id);
    }

    /**
     * Get all schedules
     * @return A list of all schedules
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    /**
     * Update an existing schedule
     * @param id The ID of the schedule to update
     * @param scheduleDetails The schedule object with updated details
     * @return The updated schedule, or null if not found
     */
    public Schedule updateSchedule(String id, Schedule scheduleDetails) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
        if (optionalSchedule.isPresent()) {
            Schedule existingSchedule = optionalSchedule.get();
            existingSchedule.setWorkerId(scheduleDetails.getWorkerId());
            existingSchedule.setWorkerName(scheduleDetails.getWorkerName());
            existingSchedule.setScheduleDate(scheduleDetails.getScheduleDate());
            existingSchedule.setShiftType(scheduleDetails.getShiftType());
            existingSchedule.setShiftStartTime(scheduleDetails.getShiftStartTime());
            existingSchedule.setShiftEndTime(scheduleDetails.getShiftEndTime());
            existingSchedule.setOrganizationId(scheduleDetails.getOrganizationId());
            existingSchedule.setManagerId(scheduleDetails.getManagerId());
            existingSchedule.setStatus(scheduleDetails.getStatus());
            existingSchedule.setWorkerPhotoUrl(scheduleDetails.getWorkerPhotoUrl());
            existingSchedule.setNotes(scheduleDetails.getNotes());
            existingSchedule.setUpdatedAt(LocalDateTime.now());
            return scheduleRepository.save(existingSchedule);
        }
        return null;
    }

    /**
     * Delete a schedule by ID
     * @param id The ID of the schedule to delete
     * @return True if deleted, false otherwise
     */
    public boolean deleteSchedule(String id) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(id);
        if (optionalSchedule.isPresent()) {
            scheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get schedules by worker ID
     * @param workerId The worker ID
     * @return A list of schedules for the worker
     */
    public List<Schedule> getSchedulesByWorkerId(String workerId) {
        return scheduleRepository.findByWorkerId(workerId);
    }

    /**
     * Get schedules by date
     * @param date The schedule date
     * @return A list of schedules for the date
     */
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByScheduleDate(date);
    }

    /**
     * Get schedules by worker ID and date
     * @param workerId The worker ID
     * @param date The schedule date
     * @return A list of schedules for the worker on the date
     */
    public List<Schedule> getSchedulesByWorkerIdAndDate(String workerId, LocalDate date) {
        return scheduleRepository.findByWorkerIdAndScheduleDate(workerId, date);
    }

    /**
     * Get schedules by organization ID
     * @param organizationId The organization ID
     * @return A list of schedules for the organization
     */
    public List<Schedule> getSchedulesByOrganizationId(String organizationId) {
        return scheduleRepository.findByOrganizationId(organizationId);
    }

    /**
     * Get schedules by organization ID and date
     * @param organizationId The organization ID
     * @param date The schedule date
     * @return A list of schedules for the organization on the date
     */
    public List<Schedule> getSchedulesByOrganizationIdAndDate(String organizationId, LocalDate date) {
        return scheduleRepository.findByOrganizationIdAndScheduleDate(organizationId, date);
    }

    /**
     * Get schedules by manager ID
     * @param managerId The manager ID
     * @return A list of schedules created by the manager
     */
    public List<Schedule> getSchedulesByManagerId(String managerId) {
        return scheduleRepository.findByManagerId(managerId);
    }

    /**
     * Get schedules by status
     * @param status The schedule status
     * @return A list of schedules with the specified status
     */
    public List<Schedule> getSchedulesByStatus(String status) {
        return scheduleRepository.findByStatus(status);
    }

    /**
     * Get schedules by shift type
     * @param shiftType The shift type
     * @return A list of schedules with the specified shift type
     */
    public List<Schedule> getSchedulesByShiftType(String shiftType) {
        return scheduleRepository.findByShiftType(shiftType);
    }

    /**
     * Get schedules by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of schedules within the date range
     */
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByScheduleDateBetween(startDate, endDate);
    }

    /**
     * Get schedules by worker ID and date range
     * @param workerId The worker ID
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of schedules for the worker within the date range
     */
    public List<Schedule> getSchedulesByWorkerIdAndDateRange(String workerId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByWorkerIdAndScheduleDateBetween(workerId, startDate, endDate);
    }

    /**
     * Get schedules by organization ID and date range
     * @param organizationId The organization ID
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of schedules for the organization within the date range
     */
    public List<Schedule> getSchedulesByOrganizationIdAndDateRange(String organizationId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByOrganizationIdAndScheduleDateBetween(organizationId, startDate, endDate);
    }

    /**
     * Check if worker has schedule on specific date
     * @param workerId The worker ID
     * @param date The schedule date
     * @return True if worker has schedule on the date, false otherwise
     */
    public boolean hasScheduleOnDate(String workerId, LocalDate date) {
        return scheduleRepository.existsByWorkerIdAndScheduleDate(workerId, date);
    }

    /**
     * Upload worker photo for a schedule
     * @param scheduleId The schedule ID
     * @param photoUrl The photo URL
     * @return The updated schedule, or null if not found
     */
    public Schedule uploadWorkerPhoto(String scheduleId, String photoUrl) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setWorkerPhotoUrl(photoUrl);
            schedule.setWorkerPhotoUploadedAt(LocalDateTime.now());
            schedule.setUpdatedAt(LocalDateTime.now());
            return scheduleRepository.save(schedule);
        }
        return null;
    }

    /**
     * Update schedule status
     * @param scheduleId The schedule ID
     * @param status The new status
     * @return The updated schedule, or null if not found
     */
    public Schedule updateScheduleStatus(String scheduleId, String status) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.setStatus(status);
            schedule.setUpdatedAt(LocalDateTime.now());
            return scheduleRepository.save(schedule);
        }
        return null;
    }

    /**
     * Get schedule statistics
     * @param organizationId The organization ID
     * @param date The date to get statistics for
     * @return A map containing schedule statistics
     */
    public java.util.Map<String, Long> getScheduleStatistics(String organizationId, LocalDate date) {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("total", scheduleRepository.countByOrganizationId(organizationId));
        stats.put("scheduled", scheduleRepository.countByStatus("scheduled"));
        stats.put("confirmed", scheduleRepository.countByStatus("confirmed"));
        stats.put("completed", scheduleRepository.countByStatus("completed"));
        stats.put("cancelled", scheduleRepository.countByStatus("cancelled"));
        stats.put("today", scheduleRepository.countByScheduleDate(date));
        return stats;
    }
}
