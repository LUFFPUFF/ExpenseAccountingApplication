package database;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.*;

@Log4j2
@AllArgsConstructor
public class DatabaseManager {
    private String url;
    private String user;
    private String password;


    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            log.error("Failed to connect to the database: {}", e.getMessage());
            throw e;
        }
    }

}
