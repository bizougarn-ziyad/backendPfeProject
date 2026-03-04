package com.projection.entity.enums;

public enum ConversationStatus {
    PENDING, // Waiting for recipient to accept (sender can only send one message)
    ACCEPTED, // Both users can chat freely
    BLOCKED // User has blocked the conversation
}
