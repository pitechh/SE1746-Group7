package com.example.accountservice.model.entity;

import com.example.accountservice.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId;

    private String eventType;

    private String payload;

    private int retry = 0;

    private OutboxStatus status = OutboxStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
