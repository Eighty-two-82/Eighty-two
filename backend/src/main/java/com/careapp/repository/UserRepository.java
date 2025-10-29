package com.careapp.repository;

import com.careapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Query methods based on username (backward compatibility)
    User findByUname(String uname);
    User findByUnameAndPassword(String uname, String password);
    
    // Query methods based on email (frontend usage)
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);

    // Password reset
    User findByPasswordResetToken(String passwordResetToken);
    
    // Find users (workers/managers) bound to a patient
    List<User> findByPatientId(String patientId);
    
    // Find workers bound to a patient
    List<User> findByPatientIdAndUserType(String patientId, String userType);
    
    // Find users by patientId and organizationId
    List<User> findByPatientIdAndOrganizationId(String patientId, String organizationId);
    
    // Find users by organizationId
    List<User> findByOrganizationId(String organizationId);
    
    // Find users by organizationId and userType
    List<User> findByOrganizationIdAndUserType(String organizationId, String userType);
}

