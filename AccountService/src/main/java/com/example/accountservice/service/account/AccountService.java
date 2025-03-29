package com.example.accountservice.service.account;

import com.example.accountservice.model.dto.AccountDto;
import com.example.accountservice.model.entity.Account;
import com.example.accountservice.service.BaseService;

import java.util.HashMap;
import java.util.List;

public interface AccountService extends BaseService<Account, Long> {
    List<AccountDto> getAllAccounts();

    AccountDto getAccountById(Long id);

    void CreateAccount(AccountDto accountDto);

    void processAccountDtos(List<AccountDto> accountDtos);

    boolean isValidRoleAndDepartment(AccountDto accountDto);

}
