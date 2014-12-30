package model.database.layer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionBuilder {

    private static final Logger LOGGER = LogManager.getLogger();
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

        LOGGER.debug("Configuring the connection to the database.");

        if (!jdbcDriverExist()){
            return null;
        }

        Connection connection;

        try {
            connection = DriverManager.getConnection(URL, USER, PASS);

        } catch (SQLException e) {

            LOGGER.error("Could not connect to data base.", e);
            return null;
        }

        return connection;
    }

    private static boolean jdbcDriverExist() {

        try {

            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {

            LOGGER.error("JDBC driver is not installed.", e);
            return false;
        }

        return true;
    }

}
