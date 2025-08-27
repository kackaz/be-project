package com.example.beproject.transfer.model;

import java.time.LocalDateTime;

public class Transfer {
    public Long id;
    public String fromAccountNumber;
    public String toAccountNumber;
    public String toAccountName;
    public Double amount;
    public String status; // SUCCESS, PENDING, FAILED
    public String transactionId;
    public LocalDateTime createdAt;
    public Long userId;
}
