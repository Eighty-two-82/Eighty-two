package com.careapp.service;

import com.careapp.domain.User;

public interface UserService {
    // Login services
    User loginService(String uname, String password);
    User loginByEmailService(String email, String password);

    // Registration service
    User registerService(User user);
    // worker bind and status services
    String getWorkerStatus(String userId);
    boolean bindWorkerToPatient(String userId, String patientId);
    String getWorkerPatient(String userId);

    // Change password with old password verification
    boolean changePassword(String identifier, String oldPassword, String newPassword);

    // Forgot password: generate token and set expiry, return token
    String createPasswordResetToken(String identifier);

    // Reset password by token
    boolean resetPasswordByToken(String token, String newPassword);

}
