package com.careapp.controller;

import com.careapp.domain.Worker;
import com.careapp.domain.Schedule;
import com.careapp.dto.DailyScheduleRequest;
import com.careapp.domain.FileRecord;
import com.careapp.service.FileService;
import com.careapp.dto.WorkerPhotoUploadRequest;
import com.careapp.service.WorkerService;
import com.careapp.service.ScheduleService;
import com.careapp.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    private WorkerService workerService;
    
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FileService fileService;

    // Create a new worker (for management to create worker records)
    @PostMapping
    public Result<Worker> createWorker(@RequestBody Worker worker) {
        try {
            Worker createdWorker = workerService.createWorker(worker);
            return Result.success(createdWorker, "Worker created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create worker: " + e.getMessage());
        }
    }

    // Get all workers
    @GetMapping
    public Result<List<Worker>> getAllWorkers() {
        try {
            List<Worker> workers = workerService.getAllWorkers();
            return Result.success(workers, "Workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers: " + e.getMessage());
        }
    }

    // Get worker by ID
    @GetMapping("/{id}")
    public Result<Worker> getWorkerById(@PathVariable String id) {
        try {
            Optional<Worker> worker = workerService.getWorkerById(id);
            if (worker.isPresent()) {
                return Result.success(worker.get(), "Worker retrieved successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker: " + e.getMessage());
        }
    }

    // Get workers by organization
    @GetMapping("/organization/{organizationId}")
    public Result<List<Worker>> getWorkersByOrganization(@PathVariable String organizationId) {
        try {
            List<Worker> workers = workerService.getWorkersByOrganization(organizationId);
            return Result.success(workers, "Workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers: " + e.getMessage());
        }
    }

    // Update worker
    @PutMapping("/{id}")
    public Result<Worker> updateWorker(@PathVariable String id, @RequestBody Worker workerDetails) {
        try {
            Worker updatedWorker = workerService.updateWorker(id, workerDetails);
            if (updatedWorker != null) {
                return Result.success(updatedWorker, "Worker updated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update worker: " + e.getMessage());
        }
    }

    // Delete worker
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteWorker(@PathVariable String id) {
        try {
            boolean deleted = workerService.deleteWorker(id);
            if (deleted) {
                return Result.success(true, "Worker deleted successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete worker: " + e.getMessage());
        }
    }


    // Activate worker
    @PostMapping("/{id}/activate")
    public Result<Worker> activateWorker(@PathVariable String id) {
        try {
            Worker worker = workerService.activateWorker(id);
            if (worker != null) {
                return Result.success(worker, "Worker activated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to activate worker: " + e.getMessage());
        }
    }

    // Deactivate worker
    @PostMapping("/{id}/deactivate")
    public Result<Worker> deactivateWorker(@PathVariable String id) {
        try {
            Worker worker = workerService.deactivateWorker(id);
            if (worker != null) {
                return Result.success(worker, "Worker deactivated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to deactivate worker: " + e.getMessage());
        }
    }


    // 注意：移除了assign-patient和remove-patient端点
    // 因为Worker应该通过Task分配来访问Patient数据，而不是直接分配Patient

    // Allocate worker to a shift (management uploads staff ID for shift)
    @PostMapping("/{id}/allocate-shift")
    public Result<Worker> allocateShift(@PathVariable String id, @RequestBody Worker.ShiftAllocation shiftAllocation) {
        try {
            Worker worker = workerService.allocateShift(id, shiftAllocation);
            if (worker != null) {
                return Result.success(worker, "Shift allocated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to allocate shift: " + e.getMessage());
        }
    }

    // Update shift status
    @PutMapping("/{id}/shift-status")
    public Result<Worker> updateShiftStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String shiftDate = body.get("shiftDate");
            String shiftTime = body.get("shiftTime");
            String newStatus = body.get("status");
            
            if (shiftDate == null || shiftTime == null || newStatus == null) {
                return Result.error("400", "Shift date, time, and status are required!");
            }
            
            Worker worker = workerService.updateShiftStatus(id, shiftDate, shiftTime, newStatus);
            if (worker != null) {
                return Result.success(worker, "Shift status updated successfully!");
            } else {
                return Result.error("404", "Worker or shift not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update shift status: " + e.getMessage());
        }
    }

    // Get worker's shifts for a specific date
    @GetMapping("/{id}/shifts/{date}")
    public Result<List<Worker.ShiftAllocation>> getWorkerShiftsForDate(@PathVariable String id, @PathVariable String date) {
        try {
            List<Worker.ShiftAllocation> shifts = workerService.getWorkerShiftsForDate(id, date);
            return Result.success(shifts, "Worker shifts retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve worker shifts: " + e.getMessage());
        }
    }

    // Get all workers with shifts for a specific date
    @GetMapping("/organization/{organizationId}/shifts/{date}")
    public Result<List<Worker>> getWorkersWithShiftsForDate(@PathVariable String organizationId, @PathVariable String date) {
        try {
            List<Worker> workers = workerService.getWorkersWithShiftsForDate(organizationId, date);
            return Result.success(workers, "Workers with shifts retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers with shifts: " + e.getMessage());
        }
    }

    // Remove shift allocation
    @DeleteMapping("/{id}/shifts/{date}/{time}")
    public Result<Worker> removeShiftAllocation(@PathVariable String id, @PathVariable String date, @PathVariable String time) {
        try {
            Worker worker = workerService.removeShiftAllocation(id, date, time);
            if (worker != null) {
                return Result.success(worker, "Shift allocation removed successfully!");
            } else {
                return Result.error("404", "Worker or shift not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to remove shift allocation: " + e.getMessage());
        }
    }

    // ========== Daily Management API Endpoints ==========

    // Get available workers for scheduling
    @GetMapping("/organization/{organizationId}/available")
    public Result<List<Worker>> getAvailableWorkers(@PathVariable String organizationId) {
        try {
            List<Worker> workers = workerService.getAvailableWorkers(organizationId);
            return Result.success(workers, "Available workers retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve available workers: " + e.getMessage());
        }
    }

    // Create daily schedule
    @PostMapping("/daily-schedule")
    public Result<List<Schedule>> createDailySchedule(@RequestBody DailyScheduleRequest scheduleRequest, 
                                                     @RequestHeader("X-Manager-Id") String managerId) {
        try {
            if (scheduleRequest.getScheduleDate() == null || scheduleRequest.getScheduleDate().isEmpty()) {
                return Result.error("400", "Schedule date is required!");
            }
            
            // Get organization ID from manager
            String organizationId = "default-org"; // TODO: Get from manager's data
            
            List<Schedule> createdSchedules = scheduleService.batchCreateDailySchedules(scheduleRequest, organizationId, managerId);
            return Result.success(createdSchedules, "Daily schedule created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create daily schedule: " + e.getMessage());
        }
    }

    // Get daily schedule for a specific date
    @GetMapping("/organization/{organizationId}/daily-schedule/{date}")
    public Result<List<Schedule>> getDailySchedule(@PathVariable String organizationId, @PathVariable String date) {
        try {
            java.time.LocalDate scheduleDate = java.time.LocalDate.parse(date);
            List<Schedule> schedules = scheduleService.getSchedulesByOrganizationIdAndDate(organizationId, scheduleDate);
            return Result.success(schedules, "Daily schedule retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve daily schedule: " + e.getMessage());
        }
    }

    // Clear daily schedule for a specific date
    @DeleteMapping("/organization/{organizationId}/daily-schedule/{date}")
    public Result<String> clearDailySchedule(@PathVariable String organizationId, @PathVariable String date) {
        try {
            int updatedCount = workerService.clearDailySchedule(organizationId, date);
            return Result.success("Cleared " + updatedCount + " worker schedules", 
                                "Daily schedule cleared successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to clear daily schedule: " + e.getMessage());
        }
    }

    // Upload worker photo
    @PostMapping("/upload-photo")
    public Result<Worker> uploadWorkerPhoto(@RequestBody WorkerPhotoUploadRequest photoRequest) {
        try {
            if (photoRequest.getWorkerId() == null || photoRequest.getWorkerId().isEmpty()) {
                return Result.error("400", "Worker ID is required!");
            }
            if (photoRequest.getPhotoUrl() == null || photoRequest.getPhotoUrl().isEmpty()) {
                return Result.error("400", "Photo URL is required!");
            }
            
            Worker worker = workerService.uploadWorkerPhoto(photoRequest);
            if (worker != null) {
                return Result.success(worker, "Worker photo uploaded successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker photo FILE (真正的文件上传 - Swagger会显示Choose File按钮)
     * POST /api/workers/{id}/photo-file
     * @param id Worker ID
     * @param file Photo file
     * @return Updated worker
     */
    @PostMapping(value = "/{id}/photo-file", consumes = "multipart/form-data")
    public Result<Worker> uploadWorkerPhotoFile(
            @PathVariable String id, 
            @RequestPart("file") MultipartFile file) {
        try {
            System.out.println("🔍 Backend - uploadWorkerPhotoFile called for workerId: " + id);
            System.out.println("🔍 Backend - file received: " + (file != null ? file.getOriginalFilename() : "null"));
            System.out.println("🔍 Backend - file size: " + (file != null ? file.getSize() : "null"));
            System.out.println("🔍 Backend - content type: " + (file != null ? file.getContentType() : "null"));
            
            if (file.isEmpty()) {
                System.out.println("❌ Backend - File is empty");
                return Result.error("400", "Please select a file to upload!");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("400", "Only image files are allowed!");
            }
            
            // 创建上传目录
            String uploadDir = "uploads/worker-photos/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = id + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = Paths.get(uploadDir + newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 生成访问URL（这里简化为相对路径，实际应该是完整的URL）
            String photoUrl = "/uploads/worker-photos/" + newFilename;

            // 保存文件记录到数据库，便于后续在文件列表中展示
            try {
                FileRecord record = new FileRecord();
                record.setName(originalFilename != null ? originalFilename : newFilename);
                record.setCategory("Worker Photo");
                record.setUploadedBy(id);
                record.setFileUrl(photoUrl);
                record.setContentType(contentType);
                record.setSize(file.getSize());
                record.setComment("");
                fileService.save(record);
            } catch (Exception ignore) {
                // 即使文件记录保存失败，也不阻断头像上传主流程
            }
            
            // 更新数据库
            Worker worker = workerService.uploadWorkerPhotoSimple(id, photoUrl);
            if (worker != null) {
                return Result.success(worker, "Worker photo uploaded successfully! URL: " + photoUrl);
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (IOException e) {
            return Result.error("500", "Failed to save file: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker photo URL (通过URL上传 - 接收JSON)
     * POST /api/workers/{id}/photo-url
     * @param id Worker ID
     * @param body Map containing photoUrl
     * @return Updated worker
     */
    @PostMapping("/{id}/photo-url")
    public Result<Worker> uploadWorkerPhotoUrl(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            
            if (photoUrl == null || photoUrl.isEmpty()) {
                return Result.error("400", "Photo URL is required!");
            }
            
            Worker worker = workerService.uploadWorkerPhotoSimple(id, photoUrl);
            if (worker != null) {
                return Result.success(worker, "Worker photo URL updated successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Upload worker photo (兼容旧版本 - 接收JSON)
     * POST /api/workers/{id}/photo
     * @param id Worker ID
     * @param body Map containing photoUrl
     * @return Updated worker
     */
    @PostMapping("/{id}/photo")
    public Result<Worker> uploadWorkerPhotoSimple(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            String photoUrl = body.get("photoUrl");
            
            if (photoUrl == null || photoUrl.isEmpty()) {
                return Result.error("400", "Photo URL is required!");
            }
            
            Worker worker = workerService.uploadWorkerPhotoSimple(id, photoUrl);
            if (worker != null) {
                return Result.success(worker, "Worker photo uploaded successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to upload worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Batch upload worker photos
     * POST /api/workers/batch-upload-photos
     * @param body Map of worker ID to photo URL
     * @return List of updated workers
     */
    @PostMapping("/batch-upload-photos")
    public Result<List<Worker>> batchUploadWorkerPhotos(@RequestBody Map<String, String> body) {
        try {
            if (body == null || body.isEmpty()) {
                return Result.error("400", "Photo upload data is required!");
            }
            
            List<Worker> updatedWorkers = workerService.batchUploadWorkerPhotos(body);
            return Result.success(updatedWorkers, "Successfully uploaded photos for " + updatedWorkers.size() + " workers!");
        } catch (Exception e) {
            return Result.error("500", "Failed to batch upload worker photos: " + e.getMessage());
        }
    }
    
    /**
     * Delete worker photo
     * DELETE /api/workers/{id}/photo
     * @param id Worker ID
     * @return Updated worker
     */
    @DeleteMapping("/{id}/photo")
    public Result<Worker> deleteWorkerPhoto(@PathVariable String id) {
        try {
            Worker worker = workerService.deleteWorkerPhoto(id);
            if (worker != null) {
                return Result.success(worker, "Worker photo deleted successfully!");
            } else {
                return Result.error("404", "Worker not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete worker photo: " + e.getMessage());
        }
    }
    
    /**
     * Get workers without photos
     * GET /api/workers/organization/{organizationId}/without-photos
     * @param organizationId Organization ID
     * @return List of workers without photos
     */
    @GetMapping("/organization/{organizationId}/without-photos")
    public Result<List<Worker>> getWorkersWithoutPhotos(@PathVariable String organizationId) {
        try {
            List<Worker> workers = workerService.getWorkersWithoutPhotos(organizationId);
            return Result.success(workers, "Retrieved " + workers.size() + " workers without photos!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve workers without photos: " + e.getMessage());
        }
    }

}