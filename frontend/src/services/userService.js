// src/services/userService.js
import api from "./api";  // This api.js is the previously created axios configuration

// Login - Mock implementation for testing without backend
export async function login(credentials) {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Mock successful login response
    return {
        data: {
            token: "mock-jwt-token-12345",
            user: {
                role: "manager", // Change this to test different roles (poa = Power of Attorney): 'poa' (Power of Attorney), 'manager', 'worker'
                email: credentials.email,
                name: "Test User"
            },
            inviteStatus: {
                valid: false, // Set to true to skip invite code flow
                reason: "missing"
            }
        }
    };
}

// Get current user information - Mock implementation
export async function getMe() {
    await new Promise(resolve => setTimeout(resolve, 500));
    return {
        data: {
            role: "manager", // Change this to test different roles (poa = Power of Attorney)
            email: "test@example.com",
            name: "Test User"
        }
    };
}

// Get invite status - Mock implementation
export async function getInviteStatus() {
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // Check if invite code was submitted (stored in sessionStorage)
    const inviteSubmitted = sessionStorage.getItem('inviteSubmitted') === 'true';
    
    return {
        data: {
            valid: inviteSubmitted, // Set to true if invite code was submitted
            reason: inviteSubmitted ? "valid" : "missing"
        }
    };
}

// Submit invite code - Mock implementation
export async function submitInviteCode(inviteCode) {
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Mock invite code validation
    if (inviteCode && inviteCode.length > 0) {
        // Define valid invite codes for testing
        const validCodes = ['123', 'abc', 'test', 'valid', 'code'];
        
        if (validCodes.includes(inviteCode.toLowerCase())) {
            // Mark invite code as submitted
            sessionStorage.setItem('inviteSubmitted', 'true');
            
            return {
                data: {
                    success: true,
                    message: "Invite code accepted"
                }
            };
        } else {
            throw new Error("Invalid invite code. Please check and try again.");
        }
    } else {
        throw new Error("Please enter an invite code");
    }
}
