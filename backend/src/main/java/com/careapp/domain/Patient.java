package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "patients")
public class Patient {
    @Id
    private String id;
    
    private String firstName;
    private String lastName;
    private String dateOfBirth;
   
    private String currentStatus;
    private String notes;
    private String familyMemberId; // associated family member ID
    private String poaId; // associated POA ID
    private String medicalRecordNumber;

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }



    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFamilyMemberId() { return familyMemberId; }
    public void setFamilyMemberId(String familyMemberId) { this.familyMemberId = familyMemberId; }

    public String getPoaId() { return poaId; }
    public void setPoaId(String poaId) { this.poaId = poaId; }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }
}
