package com.example.accountservice.client;

import com.example.accountservice.request.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "AuthService", url = "http://localhost:5009/api/auth")
public interface AuthServiceClient {
    @PostMapping("/register")
    ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest);

    @PostMapping("/registers")
    ResponseEntity<String> registers(@RequestBody List<RegisterRequest> registerRequests);
}
