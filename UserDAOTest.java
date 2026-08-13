package com.jagadeesh.jagadeeshcart;

import com.jagadeesh.jagadeeshcart.dao.UserDAO;
import com.jagadeesh.jagadeeshcart.dao.impl.UserDAOImpl;
import com.jagadeesh.jagadeeshcart.model.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static HikariDataSource dataSource;
    private static UserDAO userDAO;

    @BeforeAll
    static void setUp() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
             InputStream in = UserDAOTest.class.getClassLoader().getResourceAsStream("schema.sql");
             Statement stmt = conn.createStatement()) {
            String sql = new String(in.readAllBytes());
            for (String s : sql.split(";")) {
                if (!s.trim().isEmpty()) {
                    stmt.execute(s.trim());
                }
            }
        }
        userDAO = new UserDAOImpl(dataSource);
    }

    @AfterAll
    static void tearDown() {
        dataSource.close();
    }

    @Test
    void savesAndFindsUserByEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed");
        user.setRole(User.Role.BUYER);

        User saved = userDAO.save(user);
        assertNotNull(saved.getId());

        Optional<User> found = userDAO.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
        assertEquals(User.Role.BUYER, found.get().getRole());
    }

    @Test
    void returnsEmptyWhenEmailNotFound() {
        assertTrue(userDAO.findByEmail("nobody@example.com").isEmpty());
    }
}
