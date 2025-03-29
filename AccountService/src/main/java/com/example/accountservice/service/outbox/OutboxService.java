package com.example.accountservice.service.outbox;

import com.example.accountservice.model.entity.Outbox;

public interface OutboxService {
    Outbox save(Outbox outbox);
}
