package com.careapp.controller;

import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class CommunicationController {

    // In-memory storage for messages (in production, this would be a database)
    private List<Map<String, Object>> messages = new ArrayList<>();
    private long messageIdCounter = 1;

    /**
     * Get all messages for current user
     * GET /api/messages
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getAllMessages() {
        try {
            // Initialize with some sample messages if empty
            if (messages.isEmpty()) {
                initializeSampleMessages();
            }
            
            return Result.success(messages, "Messages retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve messages: " + e.getMessage());
        }
    }

    /**
     * Send a new message
     * POST /api/messages
     */
    @PostMapping
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> messageData) {
        try {
            String subject = (String) messageData.get("subject");
            String content = (String) messageData.get("content");
            String to = (String) messageData.get("to");
            String from = (String) messageData.get("from");

            if (subject == null || content == null) {
                return Result.error("400", "Subject and content are required!");
            }

            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("id", messageIdCounter++);
            newMessage.put("subject", subject);
            newMessage.put("content", content);
            newMessage.put("from", from != null ? from : "Current User");
            newMessage.put("to", to);
            newMessage.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            newMessage.put("status", "sent");

            messages.add(newMessage);

            return Result.success(newMessage, "Message sent successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Reply to a message
     * POST /api/messages/{messageId}/reply
     */
    @PostMapping("/{messageId}/reply")
    public Result<Map<String, Object>> replyToMessage(@PathVariable Long messageId, @RequestBody Map<String, Object> replyData) {
        try {
            String content = (String) replyData.get("content");
            String originalSubject = (String) replyData.get("originalSubject");
            String to = (String) replyData.get("to");
            String from = (String) replyData.get("from");

            if (content == null) {
                return Result.error("400", "Content is required!");
            }

            // Find original message to get subject if not provided
            if (originalSubject == null) {
                for (Map<String, Object> msg : messages) {
                    if (msg.get("id").equals(messageId)) {
                        originalSubject = (String) msg.get("subject");
                        break;
                    }
                }
            }

            Map<String, Object> reply = new HashMap<>();
            reply.put("id", messageIdCounter++);
            reply.put("subject", "Re: " + (originalSubject != null ? originalSubject : "Message"));
            reply.put("content", content);
            reply.put("from", from != null ? from : "Current User");
            reply.put("to", to);
            reply.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            reply.put("status", "sent");
            reply.put("originalMessageId", messageId);

            messages.add(reply);

            return Result.success(reply, "Reply sent successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to send reply: " + e.getMessage());
        }
    }

    /**
     * Mark message as read
     * PUT /api/messages/{messageId}/read
     */
    @PutMapping("/{messageId}/read")
    public Result<Map<String, Object>> markMessageAsRead(@PathVariable Long messageId) {
        try {
            for (Map<String, Object> message : messages) {
                if (message.get("id").equals(messageId)) {
                    message.put("status", "read");
                    return Result.success(message, "Message marked as read!");
                }
            }
            return Result.error("404", "Message not found!");
        } catch (Exception e) {
            return Result.error("500", "Failed to mark message as read: " + e.getMessage());
        }
    }

    /**
     * Delete a message
     * DELETE /api/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public Result<Map<String, Object>> deleteMessage(@PathVariable Long messageId) {
        try {
            boolean removed = messages.removeIf(message -> message.get("id").equals(messageId));
            if (removed) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("messageId", messageId);
                return Result.success(result, "Message deleted successfully!");
            } else {
                return Result.error("404", "Message not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete message: " + e.getMessage());
        }
    }

    /**
     * Get message by ID
     * GET /api/messages/{messageId}
     */
    @GetMapping("/{messageId}")
    public Result<Map<String, Object>> getMessageById(@PathVariable Long messageId) {
        try {
            for (Map<String, Object> message : messages) {
                if (message.get("id").equals(messageId)) {
                    return Result.success(message, "Message retrieved successfully!");
                }
            }
            return Result.error("404", "Message not found!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve message: " + e.getMessage());
        }
    }

    /**
     * Initialize sample messages for demonstration
     */
    private void initializeSampleMessages() {
        List<Map<String, Object>> sampleMessages = List.of(
            Map.of(
                "id", messageIdCounter++,
                "subject", "Patient P1 Health Update",
                "from", "Manager",
                "date", "2025-01-15 10:30",
                "status", "unread",
                "content", "Dear Family Member,\n\nI wanted to update you on P1's current health status. The recent examination shows improvement in blood pressure levels. Please let me know if you have any questions.\n\nBest regards,\nManager"
            ),
            Map.of(
                "id", messageIdCounter++,
                "subject", "Budget Approval Request",
                "from", "F1",
                "date", "2025-01-14 14:20",
                "status", "replied",
                "content", "Hello Manager,\n\nI would like to request approval for additional budget allocation for P1's medication. The current budget is insufficient for the new prescription.\n\nPlease review and approve.\n\nThank you,\nF1"
            ),
            Map.of(
                "id", messageIdCounter++,
                "subject", "Weekly Care Report",
                "from", "Manager",
                "date", "2025-01-13 09:15",
                "status", "read",
                "content", "Weekly care report for P1 is now available. All scheduled tasks have been completed successfully. No issues to report.\n\nManager"
            )
        );
        
        messages.addAll(sampleMessages);
    }
}
