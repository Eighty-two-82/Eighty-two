package com.careapp.service;

import com.careapp.domain.Patient;

import java.util.List;

public interface PatientService {
    // Create patient
    Patient createPatient(Patient patient);
    
    // Get patient by ID
    Patient getPatientById(String patientId);
    
    // Get patients by family member
    List<Patient> getPatientsByFamilyMember(String familyMemberId);
    
    // Get patients by POA
    List<Patient> getPatientsByPOA(String poaId);
    
    // Get authorized patients for a user
    List<Patient> getAuthorizedPatients(String userId, String userType);
    
    // Update patient
    Patient updatePatient(Patient patient);
    
    // Delete patient
    boolean deletePatient(String patientId);
}
