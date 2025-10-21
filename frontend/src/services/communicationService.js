// src/services/communicationService.js
import api from "./api";

// Get all messages for current user
export async function getAllMessages() {
    try {
        const response = await api.get('/messages');
        return response.data;
    } catch (error) {
        console.error('Failed to get messages:', error);
        // Fallback to mock data if API fails
        const mockMessages = [
            {
                id: 1,
                subject: 'Patient P1 Health Update',
                fromUserName: 'Manager',
                createdAt: '2025-01-15T10:30:00',
                status: 'sent',
                content: 'Dear Family Member,\n\nI wanted to update you on P1\'s current health status. The recent examination shows improvement in blood pressure levels. Please let me know if you have any questions.\n\nBest regards,\nManager'
            },
            {
                id: 2,
                subject: 'Budget Approval Request',
                fromUserName: 'F1',
                createdAt: '2025-01-14T14:20:00',
                status: 'read',
                content: 'Hello Manager,\n\nI would like to request approval for additional budget allocation for P1\'s medication. The current budget is insufficient for the new prescription.\n\nPlease review and approve.\n\nThank you,\nF1'
            },
            {
                id: 3,
                subject: 'Weekly Care Report',
                fromUserName: 'Manager',
                createdAt: '2025-01-13T09:15:00',
                status: 'read',
                content: 'Weekly care report for P1 is now available. All scheduled tasks have been completed successfully. No issues to report.\n\nManager'
            }
        ];
        
        return {
            data: mockMessages
        };
    }
}

// Send a new message
export async function sendMessage(messageData) {
    try {
        // Map frontend data to backend format
        const backendMessageData = {
            subject: messageData.subject,
            content: messageData.content,
            toUserId: messageData.toUserId || messageData.to,
            toUserName: messageData.toUserName || messageData.to,
            fromUserName: messageData.fromUserName || messageData.from,
            category: messageData.category || 'general'
        };
        
        const response = await api.post('/messages', backendMessageData);
        return response.data;
    } catch (error) {
        console.error('Failed to send message:', error);
        throw new Error('Failed to send message');
    }
}

// Reply to a message
export async function replyToMessage(messageId, replyData) {
    try {
        const backendReplyData = {
            content: replyData.content,
            subject: replyData.subject,
            fromUserName: replyData.fromUserName || replyData.from
        };
        
        const response = await api.post(`/messages/${messageId}/reply`, backendReplyData);
        return response.data;
    } catch (error) {
        console.error('Failed to reply to message:', error);
        throw new Error('Failed to reply to message');
    }
}

// Mark message as read
export async function markMessageAsRead(messageId) {
    try {
        const response = await api.put(`/messages/${messageId}/read`);
        return response.data;
    } catch (error) {
        console.error('Failed to mark message as read:', error);
        throw new Error('Failed to mark message as read');
    }
}

// Delete a message
export async function deleteMessage(messageId) {
    try {
        const response = await api.delete(`/messages/${messageId}`);
        return response.data;
    } catch (error) {
        console.error('Failed to delete message:', error);
        throw new Error('Failed to delete message');
    }
}

// Get message by ID
export async function getMessageById(messageId) {
    try {
        const response = await api.get(`/messages/${messageId}`);
        return response.data;
    } catch (error) {
        console.error('Failed to get message:', error);
        throw new Error('Failed to get message');
    }
}

// Get unread message count
export async function getUnreadCount() {
    try {
        const response = await api.get('/messages/unread/count');
        return response.data;
    } catch (error) {
        console.error('Failed to get unread count:', error);
        throw new Error('Failed to get unread count');
    }
}

// Get conversation with another user
export async function getConversation(userId) {
    try {
        const response = await api.get(`/messages/conversation/${userId}`);
        return response.data;
    } catch (error) {
        console.error('Failed to get conversation:', error);
        throw new Error('Failed to get conversation');
    }
}
