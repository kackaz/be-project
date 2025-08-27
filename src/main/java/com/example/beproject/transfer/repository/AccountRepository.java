package com.example.beproject.transfer.repository;

import com.example.beproject.transfer.model.Account;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByUserId(Long userId);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAll();
    Account save(Account account);
}
