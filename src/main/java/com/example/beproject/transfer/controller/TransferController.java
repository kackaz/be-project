package com.example.beproject.transfer.controller;

import com.example.beproject.transfer.dto.TransferRequest;
import com.example.beproject.transfer.dto.TransferResponse;
import com.example.beproject.transfer.model.Account;
import com.example.beproject.transfer.model.Transfer;
import com.example.beproject.transfer.service.TransferService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;
    private final byte[] jwtSecret;

    public TransferController(TransferService transferService,
                            @Value("${app.jwt.secret:defaultsecretkeydefaultsecret}") String secret) {
        this.transferService = transferService;
        this.jwtSecret = secret.getBytes();
    }

    private Long getUserIdFromToken(HttpServletRequest request) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new Exception("Missing or invalid authorization header");
        }
        String token = auth.substring(7);
        Jws<Claims> claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret))
            .build()
            .parseClaimsJws(token);
        return Long.parseLong(claims.getBody().getSubject());
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMoney(@RequestBody TransferRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            TransferResponse response = transferService.transfer(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Account account = transferService.getAccountByUserId(userId);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of(
                "accountNumber", account.accountNumber,
                "accountName", account.accountName,
                "balance", account.balance,
                "membershipLevel", account.membershipLevel
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTransferHistory(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<Transfer> transfers = transferService.getTransferHistory(userId);
            return ResponseEntity.ok(transfers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(HttpServletRequest request) {
        try {
            getUserIdFromToken(request); // Validate token
            List<Account> accounts = transferService.getAllAccounts();
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
