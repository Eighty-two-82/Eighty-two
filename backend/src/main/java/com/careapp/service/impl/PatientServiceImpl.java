package com.careapp.service.impl;

import com.careapp.domain.Patient;
import com.careapp.repository.PatientRepository;
import com.careapp.service.AuthorizationService;
import com.careapp.service.PatientService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Resource
    private PatientRepository patientRepository;

    @Resource
    private AuthorizationService authorizationService;

    @Override
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Patient getPatientById(String patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    @Override
    public List<Patient> getPatientsByFamilyMember(String familyMemberId) {
        return patientRepository.findByFamilyMemberId(familyMemberId);
    }

    @Override
    public List<Patient> getPatientsByPOA(String poaId) {
        return patientRepository.findByPoaId(poaId);
    }

    @Override
    public List<Patient> getAuthorizedPatients(String userId, String userType) {
        // Get patient IDs that user has access to
        List<String> accessiblePatientIds = authorizationService.getAccessiblePatientIds(userId);
        
        // Convert to Patient objects
        return accessiblePatientIds.stream()
                .map(this::getPatientById)
                .filter(patient -> patient != null)
                .collect(Collectors.toList());
    }

    @Override
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public boolean deletePatient(String patientId) {
        try {
            patientRepository.deleteById(patientId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
