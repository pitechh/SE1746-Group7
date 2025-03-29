package com.example.accountservice.repository.outbox;

import com.example.accountservice.enums.OutboxStatus;
import com.example.accountservice.model.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    List<Outbox> findOutboxByStatusAndRetryBefore(OutboxStatus status, int retry);
}
