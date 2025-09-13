package com.careapp.repository;

import com.careapp.domain.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    // Find patients by family member ID
    List<Patient> findByFamilyMemberId(String familyMemberId);
    
    // Find patients by POA ID
    List<Patient> findByPoaId(String poaId);
    
    // Find patient by medical record number
    Patient findByMedicalRecordNumber(String medicalRecordNumber);
}
