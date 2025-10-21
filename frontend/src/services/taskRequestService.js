// src/services/taskRequestService.js
import api from "./api";

// Create a new task request
export async function createTaskRequest(requestData) {
    try {
        const response = await api.post('/task-requests', requestData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create task request');
        }
    }
}

// Get all task requests
export async function getAllTaskRequests() {
    try {
        const response = await api.get('/task-requests');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get task requests');
        }
    }
}

// Get task request by ID
export async function getTaskRequestById(requestId) {
    try {
        const response = await api.get(`/task-requests/${requestId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get task request');
        }
    }
}

// Get task requests by requester
export async function getTaskRequestsByRequester(requesterId) {
    try {
        const response = await api.get(`/task-requests/requester/${requesterId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get requester task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get requester task requests');
        }
    }
}

// Get task requests by status
export async function getTaskRequestsByStatus(status) {
    try {
        const response = await api.get(`/task-requests/status/${status}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get task requests by status');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get task requests by status');
        }
    }
}

// Get pending task requests
export async function getPendingTaskRequests() {
    try {
        const response = await api.get('/task-requests/pending');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get pending task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get pending task requests');
        }
    }
}

// Get pending task requests for organization
export async function getPendingTaskRequestsForOrganization(organizationId) {
    try {
        const response = await api.get(`/task-requests/pending/organization/${organizationId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get pending task requests for organization');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get pending task requests for organization');
        }
    }
}

// Get task requests by organization
export async function getTaskRequestsByOrganization(organizationId) {
    try {
        const response = await api.get(`/task-requests/organization/${organizationId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get organization task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get organization task requests');
        }
    }
}

// Update task request
export async function updateTaskRequest(requestId, requestData) {
    try {
        const response = await api.put(`/task-requests/${requestId}`, requestData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update task request');
        }
    }
}

// Approve task request
export async function approveTaskRequest(requestId, approvalData) {
    try {
        const response = await api.post(`/task-requests/${requestId}/approve`, approvalData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to approve task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to approve task request');
        }
    }
}

// Reject task request
export async function rejectTaskRequest(requestId, rejectionData) {
    try {
        const response = await api.post(`/task-requests/${requestId}/reject`, rejectionData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to reject task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to reject task request');
        }
    }
}

// Delete task request
export async function deleteTaskRequest(requestId) {
    try {
        const response = await api.delete(`/task-requests/${requestId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete task request');
        }
    }
}

// Get task request statistics
export async function getTaskRequestStats() {
    try {
        const response = await api.get('/task-requests/stats');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get task request statistics');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get task request statistics');
        }
    }
}

// Get task request statistics for organization
export async function getTaskRequestStatsForOrganization(organizationId) {
    try {
        const response = await api.get(`/task-requests/stats/organization/${organizationId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get organization task request statistics');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get organization task request statistics');
        }
    }
}

// Get task request statistics for requester
export async function getTaskRequestStatsForRequester(requesterId) {
    try {
        const response = await api.get(`/task-requests/stats/requester/${requesterId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get requester task request statistics');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get requester task request statistics');
        }
    }
}
