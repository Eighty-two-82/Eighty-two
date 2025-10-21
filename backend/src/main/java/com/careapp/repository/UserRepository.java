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
    
    // Find users by patient ID and role
    List<User> findByPatientIdAndRole(String patientId, String role);
    
    // Find users by role
    List<User> findByRole(String role);
    
    // Find workers by patient ID (users with userType = "WORKER" and specific patientId)
    List<User> findByPatientIdAndUserType(String patientId, String userType);
}

