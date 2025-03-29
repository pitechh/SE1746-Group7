package com.example.accountservice.service.outbox;

import com.example.accountservice.model.entity.Outbox;
import com.example.accountservice.repository.outbox.OutboxRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OutboxServiceImpl implements OutboxService {
    private OutboxRepository outboxRepository;
    @Override
    public Outbox save(Outbox outbox) {
        return outboxRepository.save(outbox);
    }
}
