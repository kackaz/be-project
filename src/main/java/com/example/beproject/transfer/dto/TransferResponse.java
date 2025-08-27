package com.example.beproject.transfer.dto;

import java.time.LocalDateTime;

public class TransferResponse {
    public String transactionId;
    public String status;
    public Double amount;
    public String toAccountNumber;
    public String toAccountName;
    public LocalDateTime timestamp;
}
