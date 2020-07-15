package common;

import oracle.jdbc.driver.OracleConnection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 *
 */
public class Database {

    //
    public static Connection getConnection(String url, String username, String password) throws SQLException {
        // get connect
        // Load the Oracle JDBC driver
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        Log.println(String.format("getDriverVersion(): [%s]", oracle.jdbc.OracleDriver.getDriverVersion()));

        // Connect to the database
        Log.println(String.format("jdbc: %s/***@%s", username, url));
        Connection connection = DriverManager.getConnection(url, username, password);

        // print list of drivers
        Enumeration<Driver> e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            Driver d = e.nextElement();
            Log.println("Class for Oracle JDBC Driver: [" + d.getClass() + "]");
        }

        //
        Log.println("Connected.");
        Log.println(((OracleConnection) connection).getDatabaseProductVersion());

        connection.setAutoCommit(false);
        ((OracleConnection) connection).setDefaultRowPrefetch(100);
        //
        return connection;
    }

}
