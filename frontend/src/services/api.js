// src/services/api.js
import axios from "axios";

// Switch API address based on environment
const API_BASE_URL =
    import.meta.env.MODE === "production"
        ? "https://care-scheduling-app-e8951cd9f9c6.herokuapp.com/api"
        : "http://localhost:8081/api";

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000, // 10 second timeout
    headers: {
        'Content-Type': 'application/json',
    }
});

// Request interceptor to add auth token and user headers
api.interceptors.request.use(
    (config) => {
        // Get token from sessionStorage only
        const token = sessionStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        
        // Add user ID header for backend permission checks
        const userId = sessionStorage.getItem('userId');
        if (userId) {
            config.headers['X-User-Id'] = userId;
        }
        
        // Add organization ID header if available
        const organizationId = sessionStorage.getItem('organizationId');
        if (organizationId) {
            config.headers['X-Organization-Id'] = organizationId;
        }
        
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle common errors
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        // Handle 401 unauthorized
        if (error.response?.status === 401) {
            // Clear tokens and redirect to login
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('userId');
            sessionStorage.removeItem('inviteSubmitted');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
