package com.careapp.controller;

import com.careapp.domain.Schedule;
import com.careapp.service.ScheduleService;
import com.careapp.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * Create a new schedule
     * @param schedule The schedule object to create
     * @return The created schedule
     */
    @PostMapping
    public Result<Schedule> createSchedule(@RequestBody Schedule schedule) {
        try {
            Schedule createdSchedule = scheduleService.createSchedule(schedule);
            return Result.success(createdSchedule, "Schedule created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create schedule: " + e.getMessage());
        }
    }

    /**
     * Get all schedules
     * @return A list of all schedules
     */
    @GetMapping
    public Result<List<Schedule>> getAllSchedules() {
        try {
            List<Schedule> schedules = scheduleService.getAllSchedules();
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedule by ID
     * @param id The ID of the schedule
     * @return The schedule if found, otherwise 404
     */
    @GetMapping("/{id}")
    public Result<Schedule> getScheduleById(@PathVariable String id) {
        try {
            Optional<Schedule> schedule = scheduleService.getScheduleById(id);
            if (schedule.isPresent()) {
                return Result.success(schedule.get(), "Schedule retrieved successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedule: " + e.getMessage());
        }
    }

    /**
     * Update an existing schedule
     * @param id The ID of the schedule to update
     * @param scheduleDetails The schedule object with updated details
     * @return The updated schedule, or 404 if not found
     */
    @PutMapping("/{id}")
    public Result<Schedule> updateSchedule(@PathVariable String id, @RequestBody Schedule scheduleDetails) {
        try {
            Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleDetails);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Schedule updated successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update schedule: " + e.getMessage());
        }
    }

    /**
     * Delete a schedule by ID
     * @param id The ID of the schedule to delete
     * @return True if deleted, false otherwise
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSchedule(@PathVariable String id) {
        try {
            boolean deleted = scheduleService.deleteSchedule(id);
            if (deleted) {
                return Result.success(true, "Schedule deleted successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete schedule: " + e.getMessage());
        }
    }

    /**
     * Get schedules by worker ID
     * @param workerId The worker ID
     * @return A list of schedules for the worker
     */
    @GetMapping("/worker/{workerId}")
    public Result<List<Schedule>> getSchedulesByWorkerId(@PathVariable String workerId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByWorkerId(workerId);
            return Result.success(schedules, "Worker schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by date
     * @param date The schedule date (YYYY-MM-DD format)
     * @return A list of schedules for the date
     */
    @GetMapping("/date/{date}")
    public Result<List<Schedule>> getSchedulesByDate(@PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByDate(scheduleDate);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by worker ID and date
     * @param workerId The worker ID
     * @param date The schedule date (YYYY-MM-DD format)
     * @return A list of schedules for the worker on the date
     */
    @GetMapping("/worker/{workerId}/date/{date}")
    public Result<List<Schedule>> getSchedulesByWorkerIdAndDate(@PathVariable String workerId, @PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByWorkerIdAndDate(workerId, scheduleDate);
            return Result.success(schedules, "Worker schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by organization ID
     * @param organizationId The organization ID
     * @return A list of schedules for the organization
     */
    @GetMapping("/organization/{organizationId}")
    public Result<List<Schedule>> getSchedulesByOrganizationId(@PathVariable String organizationId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByOrganizationId(organizationId);
            return Result.success(schedules, "Organization schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve organization schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by organization ID and date
     * @param organizationId The organization ID
     * @param date The schedule date (YYYY-MM-DD format)
     * @return A list of schedules for the organization on the date
     */
    @GetMapping("/organization/{organizationId}/date/{date}")
    public Result<List<Schedule>> getSchedulesByOrganizationIdAndDate(@PathVariable String organizationId, @PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByOrganizationIdAndDate(organizationId, scheduleDate);
            return Result.success(schedules, "Organization schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve organization schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by manager ID
     * @param managerId The manager ID
     * @return A list of schedules created by the manager
     */
    @GetMapping("/manager/{managerId}")
    public Result<List<Schedule>> getSchedulesByManagerId(@PathVariable String managerId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByManagerId(managerId);
            return Result.success(schedules, "Manager schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve manager schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by status
     * @param status The schedule status
     * @return A list of schedules with the specified status
     */
    @GetMapping("/status/{status}")
    public Result<List<Schedule>> getSchedulesByStatus(@PathVariable String status) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByStatus(status);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by shift type
     * @param shiftType The shift type
     * @return A list of schedules with the specified shift type
     */
    @GetMapping("/shift-type/{shiftType}")
    public Result<List<Schedule>> getSchedulesByShiftType(@PathVariable String shiftType) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByShiftType(shiftType);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Get schedules by date range
     * @param startDate The start date (YYYY-MM-DD format)
     * @param endDate The end date (YYYY-MM-DD format)
     * @return A list of schedules within the date range
     */
    @GetMapping("/date-range")
    public Result<List<Schedule>> getSchedulesByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
            List<Schedule> schedules = scheduleService.getSchedulesByDateRange(start, end);
            return Result.success(schedules, "Schedules retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedules: " + e.getMessage());
        }
    }

    /**
     * Upload worker photo for a schedule
     * @param id The schedule ID
     * @param body A map containing the photo URL
     * @return The updated schedule, or 404 if not found
     */
    @PostMapping("/{id}/upload-photo")
    public Result<Schedule> uploadWorkerPhoto(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            Schedule updatedSchedule = scheduleService.uploadWorkerPhoto(id, photoUrl);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Worker photo uploaded successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }

    /**
     * Update schedule status
     * @param id The schedule ID
     * @param body A map containing the new status
     * @return The updated schedule, or 404 if not found
     */
    @PutMapping("/{id}/status")
    public Result<Schedule> updateScheduleStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            Schedule updatedSchedule = scheduleService.updateScheduleStatus(id, status);
            if (updatedSchedule != null) {
                return Result.success(updatedSchedule, "Schedule status updated successfully!");
            } else {
                return Result.error("404", "Schedule not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update schedule status: " + e.getMessage());
        }
    }

    /**
     * Check if worker has schedule on specific date
     * @param workerId The worker ID
     * @param date The schedule date (YYYY-MM-DD format)
     * @return True if worker has schedule on the date, false otherwise
     */
    @GetMapping("/worker/{workerId}/has-schedule/{date}")
    public Result<Boolean> hasScheduleOnDate(@PathVariable String workerId, @PathVariable String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            boolean hasSchedule = scheduleService.hasScheduleOnDate(workerId, scheduleDate);
            return Result.success(hasSchedule, "Schedule check completed successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to check schedule: " + e.getMessage());
        }
    }

    /**
     * Get schedule statistics
     * @param organizationId The organization ID
     * @param date The date to get statistics for (YYYY-MM-DD format)
     * @return A map containing schedule statistics
     */
    @GetMapping("/stats/{organizationId}")
    public Result<Map<String, Long>> getScheduleStatistics(@PathVariable String organizationId, @RequestParam(required = false) String date) {
        try {
            LocalDate scheduleDate = (date != null && !date.isEmpty()) ? 
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE) : LocalDate.now();
            Map<String, Long> stats = scheduleService.getScheduleStatistics(organizationId, scheduleDate);
            return Result.success(stats, "Schedule statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve schedule statistics: " + e.getMessage());
        }
    }
}
