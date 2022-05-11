package io.polyrepo.analyser.util;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionUtil {

    private static BasicDataSource basicDataSource = new BasicDataSource();

    static {
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/polyrepodb");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("Admin@123");
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
    }

    private ConnectionUtil(){}

    public static Connection getConnection()throws SQLException {
        return basicDataSource.getConnection();
    }
}
