package com.example.beproject.transfer.repository;

import com.example.beproject.transfer.model.Transfer;
import java.util.List;

public interface TransferRepository {
    Transfer save(Transfer transfer);
    List<Transfer> findByUserId(Long userId);
    List<Transfer> findByFromAccountNumber(String accountNumber);
}
