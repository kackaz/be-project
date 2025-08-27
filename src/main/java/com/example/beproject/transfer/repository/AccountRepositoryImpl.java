package com.example.beproject.transfer.repository;

import com.example.beproject.transfer.model.Account;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final DataSource ds;

    public AccountRepositoryImpl(DataSource ds) {
        this.ds = ds;
        try {
            init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() throws SQLException {
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY AUTOINCREMENT, account_number TEXT UNIQUE NOT NULL, account_name TEXT NOT NULL, balance REAL DEFAULT 0.0, membership_level TEXT DEFAULT 'Gold', user_id INTEGER)");
        }
    }

    @Override
    public Optional<Account> findByUserId(Long userId) {
        String sql = "SELECT id, account_number, account_name, balance, membership_level, user_id FROM accounts WHERE user_id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Account account = new Account();
                account.id = rs.getLong("id");
                account.accountNumber = rs.getString("account_number");
                account.accountName = rs.getString("account_name");
                account.balance = rs.getDouble("balance");
                account.membershipLevel = rs.getString("membership_level");
                account.userId = rs.getLong("user_id");
                return Optional.of(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT id, account_number, account_name, balance, membership_level, user_id FROM accounts WHERE account_number = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Account account = new Account();
                account.id = rs.getLong("id");
                account.accountNumber = rs.getString("account_number");
                account.accountName = rs.getString("account_name");
                account.balance = rs.getDouble("balance");
                account.membershipLevel = rs.getString("membership_level");
                account.userId = rs.getLong("user_id");
                return Optional.of(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Account> findAll() {
        String sql = "SELECT id, account_number, account_name, balance, membership_level, user_id FROM accounts";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account();
                    account.id = rs.getLong("id");
                    account.accountNumber = rs.getString("account_number");
                    account.accountName = rs.getString("account_name");
                    account.balance = rs.getDouble("balance");
                    account.membershipLevel = rs.getString("membership_level");
                    account.userId = rs.getLong("user_id");
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }

    @Override
    public Account save(Account account) {
        if (account.id == null) {
            String sql = "INSERT INTO accounts(account_number, account_name, balance, membership_level, user_id) VALUES (?,?,?,?,?)";
            try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, account.accountNumber);
                ps.setString(2, account.accountName);
                ps.setDouble(3, account.balance);
                ps.setString(4, account.membershipLevel);
                ps.setLong(5, account.userId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) account.id = rs.getLong(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE accounts SET account_number=?, account_name=?, balance=?, membership_level=?, user_id=? WHERE id=?";
            try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, account.accountNumber);
                ps.setString(2, account.accountName);
                ps.setDouble(3, account.balance);
                ps.setString(4, account.membershipLevel);
                ps.setLong(5, account.userId);
                ps.setLong(6, account.id);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return account;
    }
}
