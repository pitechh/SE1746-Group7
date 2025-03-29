package com.example.accountservice.service.account;

import com.example.accountservice.client.AuthServiceClient;
import com.example.accountservice.model.dto.AccountDto;
import com.example.accountservice.model.entity.Account;
import com.example.accountservice.model.entity.Outbox;
import com.example.accountservice.repository.account.AccountRepository;
import com.example.accountservice.request.RegisterRequest;
import com.example.accountservice.service.BaseServiceImpl;
import com.example.accountservice.service.Department.DepartmentService;
import com.example.accountservice.service.mapper.AccountMapper;
import com.example.accountservice.service.outbox.OutboxService;
import com.example.accountservice.service.role.RoleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.StringUtil.capitalizeFirstLetter;
import static com.example.accountservice.utils.ConvertUtil.createEmail;
import static com.example.accountservice.utils.ConvertUtil.removeDiacritics;
import static com.example.accountservice.utils.RandomUtils.generateRandomAlphanumericString;

@Service
@AllArgsConstructor
public class AccountServiceImpl extends BaseServiceImpl<Account, Long, AccountRepository>
    implements AccountService
{
    private AccountMapper accountMapper;
    private RoleService roleService;
    private DepartmentService departmentService;
    private OutboxService outboxService;
    private AuthServiceClient authServiceClient;

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = repository.findAll();
        List<AccountDto> accountDtos = new ArrayList<>();
        for (Account account : accounts) {
            accountDtos.add(accountMapper.toDTO(account));
        }
        return accountDtos;
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = repository.findById(id).orElse(null);
        return accountMapper.toDTO(account);
    }

    @Override
    public void CreateAccount(AccountDto accountDto) {
        long accountCount = countByFullname(accountDto.getFullname());
        String accountCode = createAccountCode(accountDto.getFullname(), accountCount);
        accountDto.setCode(accountCode);
        accountDto.setEmail(createEmail(accountCode));
        accountDto.setPassword(generateRandomAlphanumericString(6));

        RegisterRequest registerRequest = accountMapper.toRegisterRequest(accountDto);
        authServiceClient.register(registerRequest);

//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String payloadJson = objectMapper.writeValueAsString(accountDto);
//
//            Outbox outbox = Outbox.builder()
//                    .aggregateId(accountCode)
//                    .eventType("CREATE_ACCOUNT")
//                    .payload(payloadJson)
//                    .build();
//            outboxService.save(outbox);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Hoặc log lỗi bằng Logger
//        }
    }

    @Override
    public void processAccountDtos(List<AccountDto> accountDtos) {
        HashMap<String, Long> roleMap = roleService.getRoleNameIdMap();
        HashMap<String, Long> departmentMap = departmentService.getDepartmentCodeIdMap();

        List<String> fullnames = accountDtos.stream()
                .map(AccountDto::getFullname)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        HashMap<String, Integer> fullnameCountMap = getFullnameCountMap(fullnames);

        List<RegisterRequest> registerRequests = new ArrayList<>();
        for(AccountDto accountDto : accountDtos) {
            String accountCode;
            if(fullnameCountMap.containsKey(accountDto.getFullname())) {
                Integer count = fullnameCountMap.get(accountDto.getFullname());
                accountCode = createAccountCode(accountDto.getFullname(), count);
                accountDto.setCode(accountCode);

                fullnameCountMap.put(accountDto.getFullname(), count + 1);
            }
            else {
                accountCode = createAccountCode(accountDto.getFullname(), 0);
                accountDto.setCode(accountCode);
            }
            accountDto.setEmail(createEmail(accountCode));
            accountDto.setPassword(generateRandomAlphanumericString(6));
            if(roleMap.containsKey(accountDto.getRoleName())) {
                accountDto.setRoleId(roleMap.get(accountDto.getRoleName()));
            }
            if(departmentMap.containsKey(accountDto.getDepartmentCode())) {
                accountDto.setDepartmentId(departmentMap.get(accountDto.getDepartmentCode()));
            }
            registerRequests.add(accountMapper.toRegisterRequest(accountDto));
        }
        authServiceClient.registers(registerRequests);

//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String payloadJson = objectMapper.writeValueAsString(accountDtos);
//
//            Outbox outbox = Outbox.builder()
//                    .aggregateId(accountDtos.get(0).getCode())
//                    .eventType("CREATE_ACCOUNT")
//                    .payload(payloadJson)
//                    .build();
//            outboxService.save(outbox);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Hoặc log lỗi bằng Logger
//        }
    }

    @Override
    public boolean isValidRoleAndDepartment(AccountDto accountDto) {
        return departmentService.exists(accountDto.getDepartmentId())
                && roleService.exists(accountDto.getRoleId());
    }

    private long countByFullname(String fullname) {
        return repository.countByFullname(fullname);
    }

    public HashMap<String, Integer> getFullnameCountMap(List<String> fullnames) {
        List<Object[]> results = repository.countFullnames(fullnames);
        HashMap<String, Integer> map = new HashMap<>();

        for (Object[] result : results) {
            String name = (String) result[0];
            Integer count = ((Long) result[1]).intValue();
            map.put(name, count);
        }
        return map;
    }

    private String createAccountCode(String fullname, long accountCount) {
        if (fullname == null || fullname.isBlank()) return null;

        // Chuẩn hóa tên (xoá dấu và khoảng trắng)
        String normalizedFullname = removeDiacritics(fullname.trim());

        // Lấy phần last name (họ cuối) và viết hoa chữ cái đầu
        String[] parts = normalizedFullname.split("\\s+");
        String lastName = capitalizeFirstLetter(parts[parts.length - 1]);

        // Lấy chữ cái đầu của từng từ còn lại
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            initials.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        if(accountCount == 0) {
            return lastName + initials;
        }
        return lastName + initials + (accountCount + 1);
    }
}
