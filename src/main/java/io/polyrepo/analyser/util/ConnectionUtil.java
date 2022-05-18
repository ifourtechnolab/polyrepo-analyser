package io.polyrepo.analyser.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {

    private static final BasicDataSource basicDataSource = new BasicDataSource();
    private static Properties prop;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

    static{
        try {
            prop = new Properties();
            prop.load(new FileReader("src/main/resources/databaseConfig.properties"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static String getPropertyValue(String key){
        return prop.getProperty(key);
    }

    static {

        basicDataSource.setUrl(getPropertyValue("datasourceUrl"));
        basicDataSource.setUsername(getPropertyValue("datasourceUsername"));
        basicDataSource.setPassword(getPropertyValue("datasourcePassword"));
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMaxOpenPreparedStatements(100);
    }

    private ConnectionUtil(){}

    public static Connection getConnection()throws SQLException {
        return basicDataSource.getConnection();
    }
}
