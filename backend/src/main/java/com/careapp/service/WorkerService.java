package com.careapp.service;

import com.careapp.domain.Worker;
import com.careapp.dto.DailyScheduleRequest;
import com.careapp.dto.WorkerPhotoUploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.careapp.repository.WorkerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WorkerService {

    @Autowired
    private WorkerRepository workerRepository;

    // Create a new worker
    public Worker createWorker(Worker worker) {
        if (worker.getCreatedAt() == null) {
            worker.setCreatedAt(LocalDateTime.now());
        }
        if (worker.getUpdatedAt() == null) {
            worker.setUpdatedAt(LocalDateTime.now());
        }
        if (worker.getStatus() == null || worker.getStatus().isEmpty()) {
            worker.setStatus("pending");
        }
        return workerRepository.save(worker);
    }

    // Get worker by ID
    public Optional<Worker> getWorkerById(String id) {
        return workerRepository.findById(id);
    }

    // Get worker by worker ID (W001, W002, etc.)
    public Optional<Worker> getWorkerByWorkerId(String workerId) {
        return workerRepository.findByWorkerId(workerId);
    }

    // Get worker by email
    public Optional<Worker> getWorkerByEmail(String email) {
        return workerRepository.findByEmail(email);
    }

    // Get all workers
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    // Get workers by organization
    public List<Worker> getWorkersByOrganization(String organizationId) {
        return workerRepository.findByOrganizationId(organizationId);
    }

    // Get workers by status
    public List<Worker> getWorkersByStatus(String status) {
        return workerRepository.findByStatus(status);
    }

    // Update worker
    public Worker updateWorker(String id, Worker workerDetails) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker existingWorker = optionalWorker.get();
            // Only update fields that are provided (not null)
            if (workerDetails.getName() != null && !workerDetails.getName().isEmpty()) {
                existingWorker.setName(workerDetails.getName());
            }
            if (workerDetails.getEmail() != null && !workerDetails.getEmail().isEmpty()) {
                existingWorker.setEmail(workerDetails.getEmail());
            }
            if (workerDetails.getPhone() != null) {
                existingWorker.setPhone(workerDetails.getPhone());
            }
            if (workerDetails.getStatus() != null && !workerDetails.getStatus().isEmpty()) {
                existingWorker.setStatus(workerDetails.getStatus());
            }
            if (workerDetails.getNotes() != null) {
                existingWorker.setNotes(workerDetails.getNotes());
            }
            // Note: Removed setAssignedPatients because we no longer directly assign Patients
            if (workerDetails.getDailySchedule() != null) {
                existingWorker.setDailySchedule(workerDetails.getDailySchedule());
            }
            if (workerDetails.getSpecializations() != null) {
                existingWorker.setSpecializations(workerDetails.getSpecializations());
            }
            existingWorker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(existingWorker);
        }
        return null;
    }

    // Delete worker
    public boolean deleteWorker(String id) {
        if (workerRepository.existsById(id)) {
            workerRepository.deleteById(id);
            return true;
        }
        return false;
    }


    // Activate worker
    public Worker activateWorker(String workerId) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setStatus("active");
            worker.setActivatedAt(LocalDateTime.now());
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }

    // Deactivate worker
    public Worker deactivateWorker(String workerId) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setStatus("inactive");
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }


    


    // Allocate worker to a shift
    public Worker allocateShift(String workerId, Worker.ShiftAllocation shiftAllocation) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            List<Worker.ShiftAllocation> allocations = worker.getShiftAllocations();
            if (allocations == null) {
                allocations = new ArrayList<>();
            }
            allocations.add(shiftAllocation);
            worker.setShiftAllocations(allocations);
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }

    // Update shift allocation status
    public Worker updateShiftStatus(String workerId, String shiftDate, String shiftTime, String newStatus) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            List<Worker.ShiftAllocation> allocations = worker.getShiftAllocations();
            if (allocations != null) {
                for (Worker.ShiftAllocation allocation : allocations) {
                    if (allocation.getShiftDate().equals(shiftDate) && allocation.getShiftTime().equals(shiftTime)) {
                        allocation.setStatus(newStatus);
                        worker.setUpdatedAt(LocalDateTime.now());
                        return workerRepository.save(worker);
                    }
                }
            }
        }
        return null;
    }

    // Get worker's shift allocations for a specific date
    public List<Worker.ShiftAllocation> getWorkerShiftsForDate(String workerId, String date) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            List<Worker.ShiftAllocation> allocations = worker.getShiftAllocations();
            if (allocations != null) {
                return allocations.stream()
                    .filter(allocation -> allocation.getShiftDate().equals(date))
                    .collect(java.util.stream.Collectors.toList());
            }
        }
        return new ArrayList<>();
    }


    // Get all workers with shifts for a specific date
    public List<Worker> getWorkersWithShiftsForDate(String organizationId, String date) {
        List<Worker> workers = workerRepository.findByOrganizationId(organizationId);
        return workers.stream()
            .filter(worker -> {
                List<Worker.ShiftAllocation> allocations = worker.getShiftAllocations();
                if (allocations != null) {
                    return allocations.stream()
                        .anyMatch(allocation -> allocation.getShiftDate().equals(date));
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    // Remove shift allocation
    public Worker removeShiftAllocation(String workerId, String shiftDate, String shiftTime) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            List<Worker.ShiftAllocation> allocations = worker.getShiftAllocations();
            if (allocations != null) {
                allocations.removeIf(allocation -> 
                    allocation.getShiftDate().equals(shiftDate) && allocation.getShiftTime().equals(shiftTime));
                worker.setShiftAllocations(allocations);
                worker.setUpdatedAt(LocalDateTime.now());
                return workerRepository.save(worker);
            }
        }
         return null;
     }

    /**
     * Create daily schedule for multiple workers
     * @param scheduleRequest The daily schedule request
     * @param allocatedBy The manager who is creating the schedule
     * @return List of updated workers
     */
    public List<Worker> createDailySchedule(DailyScheduleRequest scheduleRequest, String allocatedBy) {
        List<Worker> updatedWorkers = new ArrayList<>();
        
        // Process morning shift workers (8:00-16:00)
        if (scheduleRequest.getMorningShiftWorkerIds() != null) {
            for (String workerId : scheduleRequest.getMorningShiftWorkerIds()) {
                Worker.ShiftAllocation morningShift = new Worker.ShiftAllocation(
                    scheduleRequest.getScheduleDate(),
                    "08:00-16:00",
                    null, // No specific patient assigned in this context
                    allocatedBy
                );
                morningShift.setNotes(scheduleRequest.getScheduleNotes());
                
                Worker updatedWorker = allocateShift(workerId, morningShift);
                if (updatedWorker != null) {
                    updatedWorkers.add(updatedWorker);
                }
            }
        }
        
        // Process evening shift workers (16:00-24:00)
        if (scheduleRequest.getEveningShiftWorkerIds() != null) {
            for (String workerId : scheduleRequest.getEveningShiftWorkerIds()) {
                Worker.ShiftAllocation eveningShift = new Worker.ShiftAllocation(
                    scheduleRequest.getScheduleDate(),
                    "16:00-24:00",
                    null, // No specific patient assigned in this context
                    allocatedBy
                );
                eveningShift.setNotes(scheduleRequest.getScheduleNotes());
                
                Worker updatedWorker = allocateShift(workerId, eveningShift);
                if (updatedWorker != null) {
                    updatedWorkers.add(updatedWorker);
                }
            }
        }
        
        return updatedWorkers;
    }

    /**
     * Upload photo for a worker
     * @param photoRequest The photo upload request
     * @return Updated worker with photo URL
     */
    public Worker uploadWorkerPhoto(WorkerPhotoUploadRequest photoRequest) {
        Optional<Worker> optionalWorker = workerRepository.findById(photoRequest.getWorkerId());
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setPhotoUrl(photoRequest.getPhotoUrl());
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }

    /**
     * Get available workers for scheduling (active workers)
     * @param organizationId The organization ID
     * @return List of available workers
     */
    public List<Worker> getAvailableWorkers(String organizationId) {
        return workerRepository.findByOrganizationIdAndStatus(organizationId, "active");
    }

    /**
     * Get daily schedule for a specific date
     * @param organizationId The organization ID
     * @param date The date in YYYY-MM-DD format
     * @return List of workers with their shifts for the date
     */
    public List<Worker> getDailySchedule(String organizationId, String date) {
        return getWorkersWithShiftsForDate(organizationId, date);
    }

    /**
     * Remove all shift allocations for a specific date
     * @param organizationId The organization ID
     * @param date The date in YYYY-MM-DD format
     * @return Number of workers updated
     */
    public int clearDailySchedule(String organizationId, String date) {
        List<Worker> workers = workerRepository.findByOrganizationId(organizationId);
        int updatedCount = 0;
        
        for (Worker worker : workers) {
            List<Worker.ShiftAllocation> allocations = worker.getShiftAllocations();
            if (allocations != null) {
                boolean hasChanges = allocations.removeIf(allocation -> 
                    allocation.getShiftDate().equals(date));
                
                if (hasChanges) {
                    worker.setShiftAllocations(allocations);
                    worker.setUpdatedAt(LocalDateTime.now());
                    workerRepository.save(worker);
                    updatedCount++;
                }
            }
        }
        
        return updatedCount;
    }
    
    /**
     * Upload photo for a worker (simplified version)
     * @param workerId The worker ID
     * @param photoUrl The photo URL
     * @return Updated worker with photo URL
     */
    public Worker uploadWorkerPhotoSimple(String workerId, String photoUrl) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setPhotoUrl(photoUrl);
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }
    
    /**
     * Batch upload photos for multiple workers
     * @param photoUploads Map of worker ID to photo URL
     * @return List of updated workers
     */
    public List<Worker> batchUploadWorkerPhotos(java.util.Map<String, String> photoUploads) {
        List<Worker> updatedWorkers = new ArrayList<>();
        
        for (java.util.Map.Entry<String, String> entry : photoUploads.entrySet()) {
            String workerId = entry.getKey();
            String photoUrl = entry.getValue();
            
            Worker updatedWorker = uploadWorkerPhotoSimple(workerId, photoUrl);
            if (updatedWorker != null) {
                updatedWorkers.add(updatedWorker);
            }
        }
        
        return updatedWorkers;
    }
    
    /**
     * Delete worker photo
     * @param workerId The worker ID
     * @return Updated worker with photo URL removed
     */
    public Worker deleteWorkerPhoto(String workerId) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setPhotoUrl(null);
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }
    
    /**
     * Get workers without photos
     * @param organizationId The organization ID
     * @return List of workers without photos
     */
    public List<Worker> getWorkersWithoutPhotos(String organizationId) {
        List<Worker> workers = workerRepository.findByOrganizationId(organizationId);
        return workers.stream()
            .filter(worker -> worker.getPhotoUrl() == null || worker.getPhotoUrl().isEmpty())
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Bind worker to manager
     * @param workerId The worker ID
     * @param managerId The manager ID
     * @return Updated worker or null if worker not found
     */
    public Worker bindWorkerToManager(String workerId, String managerId) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setManagerId(managerId);
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }

    /**
     * Unbind worker from manager
     * @param workerId The worker ID
     * @return Updated worker or null if worker not found
     */
    public Worker unbindWorkerFromManager(String workerId) {
        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.setManagerId(null);
            worker.setUpdatedAt(LocalDateTime.now());
            return workerRepository.save(worker);
        }
        return null;
    }

    /**
     * Get workers by manager ID
     * @param managerId The manager ID
     * @return List of workers managed by the manager
     */
    public List<Worker> getWorkersByManagerId(String managerId) {
        System.out.println("🔍 getWorkersByManagerId called with managerId: " + managerId);
        List<Worker> allWorkers = workerRepository.findAll();
        System.out.println("📊 Total workers in database: " + allWorkers.size());
        
        // Log all workers with their managerId for debugging
        System.out.println("📋 All workers in database:");
        for (Worker worker : allWorkers) {
            System.out.println("   - Worker ID: " + worker.getId() + 
                             ", Name: " + worker.getName() + 
                             ", ManagerId: " + (worker.getManagerId() != null ? worker.getManagerId() : "NULL") +
                             ", Email: " + worker.getEmail());
        }
        
        List<Worker> filteredWorkers = allWorkers.stream()
            .filter(worker -> {
                boolean matches = managerId.equals(worker.getManagerId());
                if (matches) {
                    System.out.println("✅ Found matching worker: " + worker.getId() + ", name: " + worker.getName() + ", managerId: " + worker.getManagerId());
                } else {
                    System.out.println("   ⚠️ Worker " + worker.getId() + " has managerId: " + (worker.getManagerId() != null ? worker.getManagerId() : "NULL") + " (not matching)");
                }
                return matches;
            })
            .collect(java.util.stream.Collectors.toList());
        
        System.out.println("✅ getWorkersByManagerId returning " + filteredWorkers.size() + " workers for manager " + managerId);
        return filteredWorkers;
    }

    /**
     * Save worker file (image-only or general docs) and update worker photoUrl
     * @param workerId Worker ID
     * @param file Multipart file
     * @param imageOnly If true, only accept image/*; otherwise accept image/PDF/Excel/Word
     * @return Updated worker or null if worker not found
     */
    public Worker saveWorkerFile(String workerId, MultipartFile file, boolean imageOnly) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload!");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File type cannot be determined!");
        }

        boolean allowed;
        if (imageOnly) {
            allowed = contentType.startsWith("image/");
        } else {
            allowed = contentType.startsWith("image/") ||
                    contentType.equals("application/pdf") ||
                    contentType.equals("application/vnd.ms-excel") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    contentType.equals("application/msword") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }
        if (!allowed) {
            throw new IllegalArgumentException(imageOnly ?
                "Only image files are allowed!" :
                "Only image, PDF, Excel and Word files are allowed!");
        }

        // Create upload directory (use absolute path)
        String uploadDir = imageOnly ? "uploads/worker-photos/" : "uploads/worker-files/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create upload directory: " + directory.getAbsolutePath());
            }
            System.out.println("📁 Created upload directory: " + directory.getAbsolutePath());
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = workerId + "_" + UUID.randomUUID().toString() + fileExtension;

        // Save file to disk
        Path filePath = Paths.get(uploadDir + newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Verify file was saved
        File savedFile = filePath.toFile();
        if (!savedFile.exists() || !savedFile.isFile()) {
            throw new IOException("File was not saved correctly. Path: " + filePath.toAbsolutePath());
        }
        System.out.println("✅ Photo saved successfully: " + savedFile.getAbsolutePath() + " (Size: " + savedFile.length() + " bytes)");

        // Build public url and persist to worker
        String fileUrl = (imageOnly ? "/uploads/worker-photos/" : "/uploads/worker-files/") + newFilename;

        Optional<Worker> optionalWorker = workerRepository.findById(workerId);
        if (optionalWorker.isEmpty()) {
            System.out.println("❌ Worker not found for ID: " + workerId);
            return null;
        }
        Worker worker = optionalWorker.get();
        System.out.println("📸 Updating worker photo - Worker ID: " + workerId + ", Worker Name: " + worker.getName());
        System.out.println("📸 Old photoUrl: " + worker.getPhotoUrl());
        System.out.println("📸 New photoUrl: " + fileUrl);
        
        worker.setPhotoUrl(fileUrl);
        worker.setUpdatedAt(LocalDateTime.now());
        Worker savedWorker = workerRepository.save(worker);
        
        // Verify the save
        Optional<Worker> verifyWorker = workerRepository.findById(workerId);
        if (verifyWorker.isPresent() && verifyWorker.get().getPhotoUrl() != null && verifyWorker.get().getPhotoUrl().equals(fileUrl)) {
            System.out.println("✅ Worker photo successfully saved to database. Verified photoUrl: " + verifyWorker.get().getPhotoUrl());
        } else {
            System.err.println("⚠️ WARNING: Worker photo may not have been saved correctly!");
            if (verifyWorker.isPresent()) {
                System.err.println("   Current photoUrl in DB: " + verifyWorker.get().getPhotoUrl());
            }
        }
        
        return savedWorker;
    }
}
