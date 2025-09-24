// src/services/userService.js
import api from "./api";  // This api.js is the previously created axios configuration

// List of removed workers (in real app, this would be stored in backend)
const removedWorkers = [
    // This list will be populated when workers are removed
    // Format: { email: 'worker@example.com', name: 'Worker Name', removedAt: '2024-01-15' }
];

// Login - Mock implementation for testing without backend
export async function login(credentials) {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Check if the user is in the removed workers list
    const isRemovedWorker = removedWorkers.some(worker => 
        worker.email === credentials.email || worker.name === credentials.email
    );
    
    if (isRemovedWorker) {
        throw new Error("Access denied: This account has been removed from the system.");
    }
    
    // Mock successful login response
    return {
        data: {
            token: "mock-jwt-token-12345",
            user: {
                role: "poa", // Change this to test different roles (poa = Power of Attorney): 'poa' (Power of Attorney), 'manager', 'worker'
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
            role: "poa", // Change this to test different roles (poa = Power of Attorney)
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

// Add removed worker to the list (called when a worker is removed)
export function addRemovedWorker(workerInfo) {
    const removedWorker = {
        email: workerInfo.email,
        name: workerInfo.name,
        workerId: workerInfo.workerId,
        removedAt: new Date().toISOString()
    };
    
    removedWorkers.push(removedWorker);
    
    // In a real app, this would be sent to the backend
    console.log('Worker removed from system:', removedWorker);
}

// Get list of removed workers (for debugging/admin purposes)
export function getRemovedWorkers() {
    return [...removedWorkers];
}

// Logout function - clears user session and redirects to login
export function logout() {
    try {
        console.log('Logging out user...');
        
        // Clear all session data
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('inviteSubmitted');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        
        console.log('User session cleared successfully');
        
        // In a real app, you might also call a logout API endpoint
        // await api.post('/auth/logout');
        
        // Redirect to login page
        window.location.href = '/login';
        
    } catch (error) {
        console.error('Error during logout:', error);
        // Still redirect to login page even if there's an error
        window.location.href = '/login';
    }
}
