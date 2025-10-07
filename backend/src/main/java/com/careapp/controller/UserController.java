package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.service.UserService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Resource
    private UserService userService;

    // based on uname
    @PostMapping("/login")
    public Result<User> loginController(@RequestBody User user) {
        User u = userService.loginService(user.getUname(), user.getPassword());
        if (u != null) {
            return Result.<User>success(u, "Login successful!");
        } else {
            return Result.<User>error("401", "Invalid username or password!");
        }
    }

    // based on email
    @PostMapping("/login-email")
    public Result<User> loginByEmailController(@RequestBody User user) {
        User u = userService.loginByEmailService(user.getEmail(), user.getPassword());
        if (u != null) {
            return Result.<User>success(u, "Login successful!");
        } else {
            return Result.<User>error("401", "Invalid email or password!");
        }
    }

    @PostMapping("/register")
    public Result<User> registerController(@RequestBody User newUser) {
        User user = userService.registerService(newUser);
        if (user != null) {
            return Result.<User>success(user, "Registration successful!");
        } else {
            return Result.<User>error("409", "Email already exists!");
        }
    }

    @GetMapping("/worker/{userId}/status")
    public Result<String> getWorkerStatus(@PathVariable String userId) {
        String status = userService.getWorkerStatus(userId);
        return Result.success(status, "Worker status retrieved!");
    }

    // Worker bind patient
    @PostMapping("/worker/{userId}/bind/{patientId}")
    public Result<String> bindWorkerToPatient(@PathVariable String userId,
                                              @PathVariable String patientId) {
        boolean success = userService.bindWorkerToPatient(userId, patientId);
        if (success) {
            return Result.success("Worker bound to patient successfully!");
        } else {
            return Result.<String>error("400", "Failed to bind worker to patient!");
        }
    }

    // Worker get bound patient
    @GetMapping("/worker/{userId}/patient")
    public Result<String> getWorkerPatient(@PathVariable String userId) {
        String patientId = userService.getWorkerPatient(userId);
        if (patientId != null) {
            return Result.success(patientId, "Worker patient retrieved successfully!");
        } else {
            return Result.<String>error("404", "Worker not bound to any patient!");
        }
    }


    // Change password with old password verification
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier"); // email or uname
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        boolean ok = userService.changePassword(identifier, oldPassword, newPassword);
        if (ok) {
            return Result.success(true, "Password updated successfully!");
        }
        return Result.<Boolean>error("400", "Invalid credentials or parameters");
    }

    // Forgot password: generate token
    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier"); // email or uname
        String token = userService.createPasswordResetToken(identifier);
        if (token != null) {
            return Result.success(token, "Reset token generated!");
        }
        return Result.<String>error("404", "User not found");
    }

    // Reset password by token
    @PostMapping("/reset-password")
    public Result<Boolean> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        boolean ok = userService.resetPasswordByToken(token, newPassword);
        if (ok) {
            return Result.success(true, "Password reset successfully!");
        }
        return Result.<Boolean>error("400", "Invalid or expired token");
    }

    // Get invite status - Mock implementation for frontend testing
    @GetMapping("/invite-status")
    public Result<Map<String, Object>> getInviteStatus() {
        // For now, return mock data
        // TODO: Implement proper invite status logic
        Map<String, Object> inviteStatus = Map.of(
            "valid", false,
            "reason", "missing"
        );
        return Result.success(inviteStatus, "Invite status retrieved!");
    }

    // Submit invite code - Mock implementation for frontend testing
    @PostMapping("/submit-invite-code")
    public Result<Map<String, Object>> submitInviteCode(@RequestBody Map<String, String> body) {
        String inviteCode = body.get("inviteCode");
        
        // Mock invite code validation
        if (inviteCode != null && !inviteCode.trim().isEmpty()) {
            // Define valid invite codes for testing
            String[] validCodes = {"123", "abc", "test", "valid", "code"};
            boolean isValid = false;
            for (String validCode : validCodes) {
                if (validCode.equalsIgnoreCase(inviteCode.trim())) {
                    isValid = true;
                    break;
                }
            }
            
            if (isValid) {
                Map<String, Object> result = Map.of(
                    "success", true,
                    "message", "Invite code accepted"
                );
                return Result.success(result, "Invite code validated successfully!");
            } else {
                return Result.error("400", "Invalid invite code. Please check and try again.");
            }
        } else {
            return Result.error("400", "Please enter an invite code");
        }
    }

    // Get current user information (for frontend)
    @GetMapping("/me")
    public Result<Map<String, Object>> getMe(@RequestParam(required = false) String token) {
        // For now, return mock data based on frontend expectations
        // In a real implementation, this would validate the token and return actual user data
        // TODO: Implement proper token validation and user lookup
        
        Map<String, Object> userInfo = Map.of(
            "role", "manager", // Change this to test different roles
            "email", "test@example.com",
            "name", "Test User"
        );
        return Result.success(userInfo, "User information retrieved successfully!");
    }

    // Logout endpoint
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                @RequestParam(required = false) String token) {
        try {
            // In a real implementation, you would:
            // 1. Validate the token from Authorization header or request parameter
            // 2. Invalidate the token on the server side (if using JWT blacklist)
            // 3. Clear any server-side session data
            // 4. Log the logout event for security purposes
            
            // For now, we'll just return a success message
            // TODO: Implement proper token invalidation and session cleanup
            
            return Result.success("Logout successful", "User logged out successfully!");
        } catch (Exception e) {
            return Result.error("500", "Logout failed: " + e.getMessage());
        }
    }

}