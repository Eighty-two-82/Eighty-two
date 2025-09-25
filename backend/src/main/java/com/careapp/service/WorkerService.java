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
            existingWorker.setName(workerDetails.getName());
            existingWorker.setEmail(workerDetails.getEmail());
            existingWorker.setPhone(workerDetails.getPhone());
            existingWorker.setStatus(workerDetails.getStatus());
            existingWorker.setNotes(workerDetails.getNotes());
            // 注意：移除了setAssignedPatients，因为不再直接分配Patient
            existingWorker.setDailySchedule(workerDetails.getDailySchedule());
            existingWorker.setSpecializations(workerDetails.getSpecializations());
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
}
