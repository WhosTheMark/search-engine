package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionBuilder {

    private static final Logger LOGGER = Logger.getLogger(ConnectionBuilder.class.getName());
    private static final String DB_DRIVER = "org.postgresql.Driver";

    /* change these as needed */
    private static final String URL = "jdbc:postgresql://localhost:5432/marcos";
    private static final String USER = "marcos";
    private static final String PASS = "12345678";

    // To avoid instantiation
    private ConnectionBuilder() {
    }

    // Get the connection to access the database
    public static Connection getConnection() {

        LOGGER.log(Level.CONFIG, "Configuring the connection to the database.");

        try {
            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {

            LOGGER.log(Level.SEVERE, "JDBC driver is not installed.", e);
            return null;
        }

        Connection conexion;

        try {
            conexion = DriverManager.getConnection(URL, USER, PASS);

        } catch (SQLException e) {

            LOGGER.log(Level.SEVERE, "Could not connect to data base.", e);
            return null;
        }

        return conexion;
    }

}
