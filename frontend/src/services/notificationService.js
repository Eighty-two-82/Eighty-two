import api from './api'

/**
 * Notification Service
 * Handles all notification-related API calls
 */

// Get all notifications for current user
export async function getMyNotifications() {
    try {
        const userId = sessionStorage.getItem('userId')
        if (!userId) {
            console.warn('No user ID found in sessionStorage')
            return { data: [] }
        }
        
        const response = await api.get(`/notifications/recipient/${userId}`)
        const result = response.data
        
        if (result.code === "0" && result.data) {
            console.log('🔔 Retrieved notifications from backend:', result.data)
            return {
                data: result.data.map(notification => ({
                    id: notification.id,
                    type: getNotificationType(notification.priority),
                    category: notification.category,
                    title: notification.title,
                    message: notification.message,
                    timestamp: new Date(notification.createdAt),
                    read: notification.isRead,
                    priority: notification.priority,
                    senderName: notification.senderName,
                    actionUrl: notification.actionUrl
                }))
            }
        } else {
            throw new Error(result.msg || 'Failed to get notifications')
        }
    } catch (error) {
        console.error('Failed to get notifications from backend:', error)
        return { data: [] }
    }
}

// Get unread notification count
export async function getUnreadCount() {
    try {
        const userId = sessionStorage.getItem('userId')
        if (!userId) {
            return { data: 0 }
        }
        
        const response = await api.get(`/notifications/unread/count`, {
            headers: {
                'X-User-Id': userId
            }
        })
        const result = response.data
        
        if (result.code === "0") {
            return { data: result.data }
        } else {
            throw new Error(result.msg || 'Failed to get unread count')
        }
    } catch (error) {
        console.error('Failed to get unread count:', error)
        return { data: 0 }
    }
}

// Mark notification as read
export async function markNotificationAsRead(notificationId) {
    try {
        const response = await api.put(`/notifications/${notificationId}/read`)
        const result = response.data
        
        if (result.code === "0") {
            return {
                data: result.data,
                success: true
            }
        } else {
            throw new Error(result.msg || 'Failed to mark notification as read')
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg)
        } else if (error.message) {
            throw error
        } else {
            throw new Error('Failed to mark notification as read')
        }
    }
}

// Mark all notifications as read
export async function markAllNotificationsAsRead() {
    try {
        const userId = sessionStorage.getItem('userId')
        if (!userId) {
            throw new Error('User not authenticated')
        }
        
        const response = await api.put('/notifications/read-all', {}, {
            headers: {
                'X-User-Id': userId
            }
        })
        const result = response.data
        
        if (result.code === "0") {
            return {
                data: result.data,
                success: true
            }
        } else {
            throw new Error(result.msg || 'Failed to mark all notifications as read')
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg)
        } else if (error.message) {
            throw error
        } else {
            throw new Error('Failed to mark all notifications as read')
        }
    }
}

// Create a new notification (for testing purposes)
export async function createNotification(notificationData) {
    try {
        const response = await api.post('/notifications', notificationData)
        const result = response.data
        
        if (result.code === "0") {
            return {
                data: result.data,
                success: true
            }
        } else {
            throw new Error(result.msg || 'Failed to create notification')
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg)
        } else if (error.message) {
            throw error
        } else {
            throw new Error('Failed to create notification')
        }
    }
}

// Helper function to map priority to notification type
function getNotificationType(priority) {
    switch (priority) {
        case 'urgent':
            return 'error'
        case 'high':
            return 'warning'
        case 'normal':
        default:
            return 'info'
    }
}
