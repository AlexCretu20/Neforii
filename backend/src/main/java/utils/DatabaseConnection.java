package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://13.48.203.214:5432/neforiidb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "parola";

    private DatabaseConnection(){};

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
