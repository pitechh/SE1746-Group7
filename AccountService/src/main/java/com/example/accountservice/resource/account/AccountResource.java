package com.example.accountservice.resource.account;

import com.example.accountservice.model.dto.AccountDto;
import com.example.accountservice.model.dto.BaseResponseDto;
import com.example.accountservice.model.entity.Account;
import com.example.accountservice.model.entity.Outbox;
import com.example.accountservice.request.RegisterRequest;
import com.example.accountservice.resource.BaseResource;
import com.example.accountservice.service.account.AccountService;
import com.example.accountservice.service.mapper.AccountMapper;
import com.example.accountservice.service.outbox.OutboxService;
import com.example.accountservice.utils.ExcelUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.accountservice.utils.ConvertUtil.createEmail;
import static com.example.accountservice.utils.RandomUtils.generateRandomAlphanumericString;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountResource extends BaseResource {
    private AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<AccountDto>> getAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id") long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PreAuthorize("hasAnyRole('Manager')")
    @PostMapping
    public ResponseEntity<BaseResponseDto> createAccount(@RequestBody AccountDto accountDto) {
        boolean isDepartmentExist = accountDto.getDepartmentId() != null
                || (accountDto.getDepartmentCode() != null && !accountDto.getDepartmentCode().isBlank());

        if(!isDepartmentExist) {
            return badRequest("Department cannot be empty");
        }
        if(!accountService.isValidRoleAndDepartment(accountDto)) {
            return badRequest("Role or department does not exist");
        }
        accountService.CreateAccount(accountDto);
        return success("Account creation is successful");
    }

    @PreAuthorize("hasAnyRole('Manager')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            List<AccountDto> accountDtos = ExcelUtils.readFromExcel(file, AccountDto.class);
            accountService.processAccountDtos(accountDtos);
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body("Lỗi khi đọc file: " + e.getMessage());
        }
        return success("Account creation is successful");
    }
}
