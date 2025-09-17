package com.careapp.controller;

import com.careapp.domain.Patient;
import com.careapp.service.PatientService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Resource
    private PatientService patientService;

    // Create patient
    @PostMapping
    public Result<Patient> createPatient(@RequestBody Patient patient) {
        Patient createdPatient = patientService.createPatient(patient);
        if (createdPatient != null) {
            return Result.<Patient>success(createdPatient, "Patient created successfully!");
        } else {
            return Result.<Patient>error("400", "Failed to create patient!");
        }
    }

    // Get patient by ID
    @GetMapping("/{patientId}")
    public Result<Patient> getPatientById(@PathVariable String patientId) {
        Patient patient = patientService.getPatientById(patientId);
        if (patient != null) {
            return Result.<Patient>success(patient, "Patient retrieved successfully!");
        } else {
            return Result.<Patient>error("404", "Patient not found!");
        }
    }

    // Get patients by family member
    @GetMapping("/family-member/{familyMemberId}")
    public Result<List<Patient>> getPatientsByFamilyMember(@PathVariable String familyMemberId) {
        List<Patient> patients = patientService.getPatientsByFamilyMember(familyMemberId);
        return Result.<List<Patient>>success(patients, "Patients retrieved successfully!");
    }

    // Get patients by POA
    @GetMapping("/poa/{poaId}")
    public Result<List<Patient>> getPatientsByPOA(@PathVariable String poaId) {
        List<Patient> patients = patientService.getPatientsByPOA(poaId);
        return Result.<List<Patient>>success(patients, "Patients retrieved successfully!");
    }

    // Get authorized patients for user
    @GetMapping("/authorized/{userId}")
    public Result<List<Patient>> getAuthorizedPatients(@PathVariable String userId, @RequestParam String userType) {
        List<Patient> patients = patientService.getAuthorizedPatients(userId, userType);
        return Result.<List<Patient>>success(patients, "Authorized patients retrieved successfully!");
    }

    // Update patient
    @PutMapping("/{patientId}")
    public Result<Patient> updatePatient(@PathVariable String patientId, @RequestBody Patient patient) {
        patient.setId(patientId);
        Patient updatedPatient = patientService.updatePatient(patient);
        if (updatedPatient != null) {
            return Result.<Patient>success(updatedPatient, "Patient updated successfully!");
        } else {
            return Result.<Patient>error("400", "Failed to update patient!");
        }
    }

    // Delete patient
    @DeleteMapping("/{patientId}")
    public Result<String> deletePatient(@PathVariable String patientId) {
        boolean success = patientService.deletePatient(patientId);
        if (success) {
            return Result.<String>success("Patient deleted!", "Patient deleted successfully!");
        } else {
            return Result.<String>error("400", "Failed to delete patient!");
        }
    }


}
