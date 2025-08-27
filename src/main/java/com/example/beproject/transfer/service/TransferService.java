package com.example.beproject.transfer.service;

import com.example.beproject.transfer.dto.TransferRequest;
import com.example.beproject.transfer.dto.TransferResponse;
import com.example.beproject.transfer.model.Account;
import com.example.beproject.transfer.model.Transfer;
import com.example.beproject.transfer.repository.AccountRepository;
import com.example.beproject.transfer.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    public TransferResponse transfer(Long userId, TransferRequest request) throws Exception {
        // Get sender account
        Optional<Account> senderAccount = accountRepository.findByUserId(userId);
        if (senderAccount.isEmpty()) {
            throw new Exception("Sender account not found");
        }

        // Get recipient account
        Optional<Account> recipientAccount = accountRepository.findByAccountNumber(request.toAccountNumber);
        if (recipientAccount.isEmpty()) {
            throw new Exception("Recipient account not found");
        }

        Account sender = senderAccount.get();
        Account recipient = recipientAccount.get();

        // Check balance
        if (sender.balance < request.amount) {
            throw new Exception("Insufficient balance");
        }

        // Process transfer
        sender.balance -= request.amount;
        recipient.balance += request.amount;

        // Save updated accounts
        accountRepository.save(sender);
        accountRepository.save(recipient);

        // Create transfer record
        Transfer transfer = new Transfer();
        transfer.fromAccountNumber = sender.accountNumber;
        transfer.toAccountNumber = recipient.accountNumber;
        transfer.toAccountName = recipient.accountName;
        transfer.amount = request.amount;
        transfer.status = "SUCCESS";
        transfer.transactionId = "LBK" + String.format("%06d", System.currentTimeMillis() % 1000000);
        transfer.createdAt = LocalDateTime.now();
        transfer.userId = userId;

        Transfer savedTransfer = transferRepository.save(transfer);

        // Create response
        TransferResponse response = new TransferResponse();
        response.transactionId = savedTransfer.transactionId;
        response.status = savedTransfer.status;
        response.amount = savedTransfer.amount;
        response.toAccountNumber = savedTransfer.toAccountNumber;
        response.toAccountName = savedTransfer.toAccountName;
        response.timestamp = savedTransfer.createdAt;

        return response;
    }

    public Account getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId).orElse(null);
    }

    public List<Transfer> getTransferHistory(Long userId) {
        return transferRepository.findByUserId(userId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}
