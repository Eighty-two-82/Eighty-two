// src/services/userService.js
import api from "./api";  // This api.js is the previously created axios configuration

// List of removed workers (in real app, this would be stored in backend)
const removedWorkers = [
    // This list will be populated when workers are removed
    // Format: { email: 'worker@example.com', name: 'Worker Name', removedAt: '2024-01-15' }
];

// Login - Connect to real backend API
export async function login(credentials) {
    try {
        // Check if the user is in the removed workers list
        const isRemovedWorker = removedWorkers.some(worker => 
            worker.email === credentials.email || worker.name === credentials.email
        );
        
        if (isRemovedWorker) {
            throw new Error("Access denied: This account has been removed from the system.");
        }
        
        // Call backend login API
        const response = await api.post('/auth/login-email', {
            email: credentials.email,
            password: credentials.password
        });
        
        const result = response.data;
        
        // Check if login was successful
        if (result.code === "0" && result.data) {
            const user = result.data;
            
            // Generate a simple token (in real app, this would come from backend)
            const token = `token_${user.id}_${Date.now()}`;
            
            // Get invite status
            let inviteStatus = { valid: false, reason: "missing" };
            try {
                const inviteResponse = await api.get('/auth/invite-status');
                if (inviteResponse.data.code === "0") {
                    inviteStatus = inviteResponse.data.data;
                }
            } catch (e) {
                console.warn('Failed to get invite status:', e);
            }
            
            const userInfo = {
                id: user.id,
                role: user.role || user.userType || 'worker',
                email: user.email,
                name: `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email,
                firstName: user.firstName,
                lastName: user.lastName,
                userType: user.userType,
                organizationId: user.organizationId,
                patientId: user.patientId,
                status: user.status
            };
            
            // Store user info for later retrieval
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
            
            return {
                data: {
                    token: token,
                    user: userInfo,
                    inviteStatus: inviteStatus
                }
            };
        } else {
            throw new Error(result.msg || 'Login failed');
        }
    } catch (error) {
        // Handle network errors or API errors
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Login failed. Please check your credentials and try again.');
        }
    }
}

// Get current user information - Connect to real backend API
export async function getMe() {
    // Always prioritize localStorage user info since backend API returns hardcoded manager role
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
        // Try to get user info from localStorage (stored during login)
        const storedUserInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo');
        if (storedUserInfo) {
            try {
                const userInfo = JSON.parse(storedUserInfo);
                console.log('Using stored user info:', userInfo);
                return {
                    data: userInfo
                };
            } catch (e) {
                console.warn('Failed to parse stored user info:', e);
            }
        }
    }
    
    // Fallback: try backend API (but it returns hardcoded manager role)
    try {
        const response = await api.get('/auth/me');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            console.warn('Backend API returned hardcoded manager role, using fallback');
            // Don't use backend response since it's hardcoded
        }
    } catch (error) {
        console.warn('Backend API failed:', error);
    }
    
    // Final fallback
    return {
        data: {
            role: "poa", // Default to POA since that's what user registered as
            email: "user@example.com",
            name: "User"
        }
    };
}

// Get invite status - Connect to real backend API
export async function getInviteStatus() {
    try {
        const response = await api.get('/auth/invite-status');
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get invite status');
        }
    } catch (error) {
        // Fallback to sessionStorage check
        const inviteSubmitted = sessionStorage.getItem('inviteSubmitted') === 'true';
        
        return {
            data: {
                valid: inviteSubmitted,
                reason: inviteSubmitted ? "valid" : "missing"
            }
        };
    }
}

// Submit invite code - Connect to real backend API
export async function submitInviteCode(inviteCode) {
    try {
        const response = await api.post('/auth/submit-invite-code', {
            inviteCode: inviteCode
        });
        
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            // Mark invite code as submitted in sessionStorage for fallback
            sessionStorage.setItem('inviteSubmitted', 'true');
            
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Invalid invite code');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to submit invite code');
        }
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

// Register function - Connect to real backend API
export async function register(userData) {
    try {
        const response = await api.post('/auth/register', {
            firstName: userData.firstName,
            lastName: userData.lastName,
            email: userData.email,
            password: userData.password,
            role: userData.role || 'worker',
            userType: userData.userType || 'WORKER'
        });
        
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Registration failed');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Registration failed. Please try again.');
        }
    }
}

// Forgot password function - Connect to real backend API
export async function forgotPassword(identifier) {
    try {
        const response = await api.post('/auth/forgot-password', {
            identifier: identifier
        });
        
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to send reset token');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to send reset token');
        }
    }
}

// Reset password function - Connect to real backend API
export async function resetPassword(token, newPassword) {
    try {
        const response = await api.post('/auth/reset-password', {
            token: token,
            newPassword: newPassword
        });
        
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to reset password');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to reset password');
        }
    }
}

// Logout function - clears user session and redirects to login
export function logout() {
    try {
        console.log('Logging out user...');
        
        // Clear all session data
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('userInfo');
        sessionStorage.removeItem('inviteSubmitted');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('userInfo');
        
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
