package com.example.accountservice.service.cronjob;

import com.example.accountservice.enums.OutboxStatus;
import com.example.accountservice.model.entity.Outbox;
import com.example.accountservice.repository.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OutboxProcesser {
    private final OutboxRepository outboxRepository;  // Repository để lấy dữ liệu
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // Jackson để chuyển DTO thành JSON

    @Scheduled(fixedRate = 2000) // Chạy mỗi 2 giây
    @Transactional
    public void processOutboxMessages() {
        List<Outbox> outboxMessages = outboxRepository.findOutboxByStatusAndRetryBefore(OutboxStatus.PENDING, 3); // Đọc tất cả bản ghi chưa gửi

        for (Outbox outbox : outboxMessages) {
            try {
                String messagePayload = objectMapper.writeValueAsString(outbox.getPayload()); // Convert DTO sang JSON

                kafkaTemplate.send("account-events", outbox.getAggregateId(), messagePayload); // Gửi tới Kafka
                outboxRepository.delete(outbox); // Xóa sau khi gửi thành công
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Log lỗi nếu JSON bị lỗi
            }
        }
    }
}
