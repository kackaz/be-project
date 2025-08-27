package com.example.beproject.transfer.repository;

import com.example.beproject.transfer.model.Transfer;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransferRepositoryImpl implements TransferRepository {

    private final DataSource ds;

    public TransferRepositoryImpl(DataSource ds) {
        this.ds = ds;
        try {
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() throws SQLException {
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS transfers (id INTEGER PRIMARY KEY AUTOINCREMENT, from_account_number TEXT NOT NULL, to_account_number TEXT NOT NULL, to_account_name TEXT NOT NULL, amount REAL NOT NULL, status TEXT NOT NULL, transaction_id TEXT UNIQUE NOT NULL, created_at TEXT NOT NULL, user_id INTEGER NOT NULL)");
        }
    }

    @Override
    public Transfer save(Transfer transfer) {
        String sql = "INSERT INTO transfers(from_account_number, to_account_number, to_account_name, amount, status, transaction_id, created_at, user_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transfer.fromAccountNumber);
            ps.setString(2, transfer.toAccountNumber);
            ps.setString(3, transfer.toAccountName);
            ps.setDouble(4, transfer.amount);
            ps.setString(5, transfer.status);
            ps.setString(6, transfer.transactionId);
            ps.setString(7, transfer.createdAt.toString());
            ps.setLong(8, transfer.userId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) transfer.id = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transfer;
    }

    @Override
    public List<Transfer> findByUserId(Long userId) {
        String sql = "SELECT id, from_account_number, to_account_number, to_account_name, amount, status, transaction_id, created_at, user_id FROM transfers WHERE user_id = ? ORDER BY created_at DESC";
        List<Transfer> transfers = new ArrayList<>();
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transfer transfer = new Transfer();
                    transfer.id = rs.getLong("id");
                    transfer.fromAccountNumber = rs.getString("from_account_number");
                    transfer.toAccountNumber = rs.getString("to_account_number");
                    transfer.toAccountName = rs.getString("to_account_name");
                    transfer.amount = rs.getDouble("amount");
                    transfer.status = rs.getString("status");
                    transfer.transactionId = rs.getString("transaction_id");
                    transfer.createdAt = LocalDateTime.parse(rs.getString("created_at"));
                    transfer.userId = rs.getLong("user_id");
                    transfers.add(transfer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transfers;
    }

    @Override
    public List<Transfer> findByFromAccountNumber(String accountNumber) {
        String sql = "SELECT id, from_account_number, to_account_number, to_account_name, amount, status, transaction_id, created_at, user_id FROM transfers WHERE from_account_number = ? ORDER BY created_at DESC";
        List<Transfer> transfers = new ArrayList<>();
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transfer transfer = new Transfer();
                    transfer.id = rs.getLong("id");
                    transfer.fromAccountNumber = rs.getString("from_account_number");
                    transfer.toAccountNumber = rs.getString("to_account_number");
                    transfer.toAccountName = rs.getString("to_account_name");
                    transfer.amount = rs.getDouble("amount");
                    transfer.status = rs.getString("status");
                    transfer.transactionId = rs.getString("transaction_id");
                    transfer.createdAt = LocalDateTime.parse(rs.getString("created_at"));
                    transfer.userId = rs.getLong("user_id");
                    transfers.add(transfer);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transfers;
    }
}
