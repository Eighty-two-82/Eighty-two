// src/services/communicationService.js
import api from "./api";

// Note: These are mock implementations since the backend doesn't have communication APIs yet
// In a real application, these would connect to actual message/communication endpoints

// Get all messages for current user
export async function getAllMessages() {
    try {
        // For now, return mock data since backend doesn't have communication API
        // In the future, this would be: const response = await api.get('/messages');
        
        const mockMessages = [
            {
                id: 1,
                subject: 'Patient P1 Health Update',
                from: 'Manager',
                date: '2025-01-15 10:30',
                status: 'unread',
                content: 'Dear Family Member,\n\nI wanted to update you on P1\'s current health status. The recent examination shows improvement in blood pressure levels. Please let me know if you have any questions.\n\nBest regards,\nManager'
            },
            {
                id: 2,
                subject: 'Budget Approval Request',
                from: 'F1',
                date: '2025-01-14 14:20',
                status: 'replied',
                content: 'Hello Manager,\n\nI would like to request approval for additional budget allocation for P1\'s medication. The current budget is insufficient for the new prescription.\n\nPlease review and approve.\n\nThank you,\nF1'
            },
            {
                id: 3,
                subject: 'Weekly Care Report',
                from: 'Manager',
                date: '2025-01-13 09:15',
                status: 'read',
                content: 'Weekly care report for P1 is now available. All scheduled tasks have been completed successfully. No issues to report.\n\nManager'
            }
        ];
        
        return {
            data: mockMessages
        };
    } catch (error) {
        console.error('Failed to get messages:', error);
        throw new Error('Failed to get messages');
    }
}

// Send a new message
export async function sendMessage(messageData) {
    try {
        // For now, simulate API call since backend doesn't have communication API
        // In the future, this would be: const response = await api.post('/messages', messageData);
        
        await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate network delay
        
        const newMessage = {
            id: Date.now(),
            subject: messageData.subject,
            from: messageData.from || 'Current User',
            date: new Date().toISOString().slice(0, 16).replace('T', ' '),
            status: 'sent',
            content: messageData.content,
            to: messageData.to
        };
        
        return {
            data: newMessage
        };
    } catch (error) {
        console.error('Failed to send message:', error);
        throw new Error('Failed to send message');
    }
}

// Reply to a message
export async function replyToMessage(messageId, replyData) {
    try {
        // For now, simulate API call since backend doesn't have communication API
        // In the future, this would be: const response = await api.post(`/messages/${messageId}/reply`, replyData);
        
        await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate network delay
        
        const reply = {
            id: Date.now(),
            subject: `Re: ${replyData.originalSubject}`,
            from: replyData.from || 'Current User',
            date: new Date().toISOString().slice(0, 16).replace('T', ' '),
            status: 'sent',
            content: replyData.content,
            to: replyData.to,
            originalMessageId: messageId
        };
        
        return {
            data: reply
        };
    } catch (error) {
        console.error('Failed to reply to message:', error);
        throw new Error('Failed to reply to message');
    }
}

// Mark message as read
export async function markMessageAsRead(messageId) {
    try {
        // For now, simulate API call since backend doesn't have communication API
        // In the future, this would be: const response = await api.put(`/messages/${messageId}/read`);
        
        await new Promise(resolve => setTimeout(resolve, 500)); // Simulate network delay
        
        return {
            data: { success: true }
        };
    } catch (error) {
        console.error('Failed to mark message as read:', error);
        throw new Error('Failed to mark message as read');
    }
}

// Delete a message
export async function deleteMessage(messageId) {
    try {
        // For now, simulate API call since backend doesn't have communication API
        // In the future, this would be: const response = await api.delete(`/messages/${messageId}`);
        
        await new Promise(resolve => setTimeout(resolve, 500)); // Simulate network delay
        
        return {
            data: { success: true }
        };
    } catch (error) {
        console.error('Failed to delete message:', error);
        throw new Error('Failed to delete message');
    }
}

// Get message by ID
export async function getMessageById(messageId) {
    try {
        // For now, simulate API call since backend doesn't have communication API
        // In the future, this would be: const response = await api.get(`/messages/${messageId}`);
        
        await new Promise(resolve => setTimeout(resolve, 500)); // Simulate network delay
        
        // Return mock message
        const mockMessage = {
            id: messageId,
            subject: 'Sample Message',
            from: 'Manager',
            date: '2025-01-15 10:30',
            status: 'unread',
            content: 'This is a sample message content.'
        };
        
        return {
            data: mockMessage
        };
    } catch (error) {
        console.error('Failed to get message:', error);
        throw new Error('Failed to get message');
    }
}
