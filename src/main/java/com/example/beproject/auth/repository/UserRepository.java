package com.example.beproject.auth.repository;

import com.example.beproject.auth.model.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UserRepository {

    private final DataSource ds;

    public UserRepository(DataSource ds) {
        this.ds = ds;
    }

    public void init() throws SQLException {
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE NOT NULL, password TEXT NOT NULL, firstname TEXT, lastname TEXT, phone TEXT, birthday TEXT)");
        }
    }

    public void save(User u) throws SQLException {
        String sql = "INSERT INTO users(email,password,firstname,lastname,phone,birthday) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.email);
            ps.setString(2, u.password);
            ps.setString(3, u.firstname);
            ps.setString(4, u.lastname);
            ps.setString(5, u.phone);
            ps.setString(6, u.birthday);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.id = rs.getLong(1);
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id,email,password,firstname,lastname,phone,birthday FROM users WHERE email = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.id = rs.getLong("id");
                u.email = rs.getString("email");
                u.password = rs.getString("password");
                u.firstname = rs.getString("firstname");
                u.lastname = rs.getString("lastname");
                u.phone = rs.getString("phone");
                u.birthday = rs.getString("birthday");
                return u;
            }
        }
    }

    public User findById(Long id) throws SQLException {
        String sql = "SELECT id,email,firstname,lastname,phone,birthday FROM users WHERE id = ?";
        try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.id = rs.getLong("id");
                u.email = rs.getString("email");
                u.firstname = rs.getString("firstname");
                u.lastname = rs.getString("lastname");
                u.phone = rs.getString("phone");
                u.birthday = rs.getString("birthday");
                return u;
            }
        }
    }
}
