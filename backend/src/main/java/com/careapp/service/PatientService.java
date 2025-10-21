package com.careapp.service;

import com.careapp.domain.Patient;
import com.careapp.domain.User;
import com.careapp.repository.PatientRepository;
import com.careapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PatientService {
    
    @Resource
    private PatientRepository patientRepository;
    
    @Resource
    private UserRepository userRepository;
    
    // Create a new patient
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    // Get patient by ID
    public Optional<Patient> getPatientById(String id) {
        return patientRepository.findById(id);
    }
    
    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    // Update patient
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    // Delete patient
    public boolean deletePatient(String id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Get patients by family member ID
    public List<Patient> getPatientsByFamilyMember(String familyMemberId) {
        return patientRepository.findByFamilyMemberId(familyMemberId);
    }
    
    // Get patients by POA ID
    public List<Patient> getPatientsByPOA(String poaId) {
        return patientRepository.findByPoaId(poaId);
    }
    
    // Get patient by medical record number
    public Optional<Patient> getPatientByMedicalRecordNumber(String medicalRecordNumber) {
        return patientRepository.findByMedicalRecordNumber(medicalRecordNumber);
    }
    
    // Get patient by Client ID
    public Optional<Patient> getPatientByClientId(String clientId) {
        return patientRepository.findByClientId(clientId);
    }
    
    // Assign patient to family member
    public Patient assignFamilyMember(String patientId, String familyMemberId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setFamilyMemberId(familyMemberId);
            return patientRepository.save(patient);
        }
        return null;
    }
    
    // Assign patient to POA
    public Patient assignPOA(String patientId, String poaId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setPoaId(poaId);
            return patientRepository.save(patient);
        }
        return null;
    }
    
    // Remove organization from patient and revoke manager permissions
    public boolean removeOrganizationFromPatient(String patientId, Map<String, Object> removalData) {
        try {
            System.out.println("🔍 Backend - removeOrganizationFromPatient called for patientId: " + patientId);
            
            // Get patient record
            Optional<Patient> patientOpt = patientRepository.findById(patientId);
            if (!patientOpt.isPresent()) {
                System.out.println("❌ Backend - Patient not found: " + patientId);
                return false;
            }
            
            Patient patient = patientOpt.get();
            String organizationId = (String) removalData.get("organizationId");
            
            System.out.println("🔍 Backend - Removing organization: " + organizationId + " from patient: " + patientId);
            
            // Clear organization information from patient record
            patient.setOrganizationName(null);
            patient.setOrganizationId(null);
            patient.setInviteToken(null);
            patient.setTokenExpiration(null);
            patientRepository.save(patient);
            
            System.out.println("✅ Backend - Cleared organization info from patient record");
            
            // Find all managers bound to this patient and revoke their permissions
            System.out.println("🔍 Backend - Searching for managers with patientId: " + patientId + " and role: manager");
            
            // First, let's see all users with manager role
            List<User> allManagers = userRepository.findByRole("manager");
            System.out.println("🔍 Backend - Found " + allManagers.size() + " total managers in system");
            
            for (User manager : allManagers) {
                System.out.println("🔍 Backend - Manager: " + manager.getId() + ", email: " + manager.getEmail() + 
                                 ", patientId: " + manager.getPatientId() + ", organizationId: " + manager.getOrganizationId());
            }
            
            // Now find managers specifically bound to this patient
            List<User> managers = userRepository.findByPatientIdAndRole(patientId, "manager");
            System.out.println("🔍 Backend - Found " + managers.size() + " managers bound to patient: " + patientId);
            
            for (User manager : managers) {
                // Clear organization info from manager
                manager.setOrganizationName(null);
                manager.setOrganizationId(null);
                manager.setPatientId(null);
                manager.setStatus("PENDING");
                manager.setHasUsedInviteCode(false); // Reset invite code status so they need to enter code again
                userRepository.save(manager);
                
                System.out.println("✅ Backend - Revoked permissions for manager: " + manager.getId() + " (" + manager.getEmail() + ")");
            }
            
            System.out.println("✅ Backend - Organization removal completed successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Backend - Error in removeOrganizationFromPatient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}