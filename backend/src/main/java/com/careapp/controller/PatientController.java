package com.careapp.controller;

import com.careapp.domain.Patient;
import com.careapp.service.PatientService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    @Resource
    private PatientService patientService;
    
    // Create a new patient
    @PostMapping
    public Result<Patient> createPatient(@RequestBody Patient patient) {
        try {
            Patient createdPatient = patientService.createPatient(patient);
            return Result.success(createdPatient, "Patient created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create patient: " + e.getMessage());
        }
    }
    
    // Get all patients
    @GetMapping
    public Result<List<Patient>> getAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            return Result.success(patients, "Patients retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patients: " + e.getMessage());
        }
    }
    
    // Get patient by ID
    @GetMapping("/{id}")
    public Result<Patient> getPatientById(@PathVariable String id) {
        try {
            Optional<Patient> patient = patientService.getPatientById(id);
            if (patient.isPresent()) {
                return Result.success(patient.get(), "Patient retrieved successfully!");
            } else {
                return Result.error("404", "Patient not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patient: " + e.getMessage());
        }
    }
    
    // Update patient
    @PutMapping("/{id}")
    public Result<Patient> updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        try {
            Optional<Patient> existingPatient = patientService.getPatientById(id);
            if (existingPatient.isPresent()) {
                patient.setId(id);
                Patient updatedPatient = patientService.updatePatient(patient);
                return Result.success(updatedPatient, "Patient updated successfully!");
            } else {
                return Result.error("404", "Patient not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to update patient: " + e.getMessage());
        }
    }
    
    // Delete patient
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePatient(@PathVariable String id) {
        try {
            boolean deleted = patientService.deletePatient(id);
            if (deleted) {
                return Result.success(true, "Patient deleted successfully!");
            } else {
                return Result.error("404", "Patient not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete patient: " + e.getMessage());
        }
    }
    
    // Get patients by family member ID
    @GetMapping("/family-member/{familyMemberId}")
    public Result<List<Patient>> getPatientsByFamilyMember(@PathVariable String familyMemberId) {
        try {
            List<Patient> patients = patientService.getPatientsByFamilyMember(familyMemberId);
            return Result.success(patients, "Patients retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patients: " + e.getMessage());
        }
    }
    
    // Get patients by POA ID
    @GetMapping("/poa/{poaId}")
    public Result<List<Patient>> getPatientsByPOA(@PathVariable String poaId) {
        try {
            List<Patient> patients = patientService.getPatientsByPOA(poaId);
            return Result.success(patients, "Patients retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patients: " + e.getMessage());
        }
    }
    
    // Get patients by medical record number
    @GetMapping("/medical-record/{medicalRecordNumber}")
    public Result<Patient> getPatientByMedicalRecordNumber(@PathVariable String medicalRecordNumber) {
        try {
            Optional<Patient> patient = patientService.getPatientByMedicalRecordNumber(medicalRecordNumber);
            if (patient.isPresent()) {
                return Result.success(patient.get(), "Patient retrieved successfully!");
            } else {
                return Result.error("404", "Patient not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve patient: " + e.getMessage());
        }
    }
    
    // Assign patient to family member
    @PostMapping("/{id}/assign-family-member")
    public Result<Patient> assignFamilyMember(@PathVariable String id, @RequestBody java.util.Map<String, String> body) {
        try {
            String familyMemberId = body.get("familyMemberId");
            Patient patient = patientService.assignFamilyMember(id, familyMemberId);
            if (patient != null) {
                return Result.success(patient, "Family member assigned successfully!");
            } else {
                return Result.error("404", "Patient not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to assign family member: " + e.getMessage());
        }
    }
    
    // Assign patient to POA
    @PostMapping("/{id}/assign-poa")
    public Result<Patient> assignPOA(@PathVariable String id, @RequestBody java.util.Map<String, String> body) {
        try {
            String poaId = body.get("poaId");
            Patient patient = patientService.assignPOA(id, poaId);
            if (patient != null) {
                return Result.success(patient, "POA assigned successfully!");
            } else {
                return Result.error("404", "Patient not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to assign POA: " + e.getMessage());
        }
    }
}