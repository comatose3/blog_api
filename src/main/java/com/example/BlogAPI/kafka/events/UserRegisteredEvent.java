package com.example.BlogAPI.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRegisteredEvent extends BaseEvent {
    private Long userId;
    private String username;
    private String email;

    public UserRegisteredEvent(String eventId, LocalDateTime timestamp, String eventType, Long userId, String username, String email) {
        super(UUID.randomUUID().toString(), LocalDateTime.now(), "USER_REGISTERED");
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}