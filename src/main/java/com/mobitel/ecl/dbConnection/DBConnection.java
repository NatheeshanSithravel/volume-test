package com.mobitel.ecl.dbConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class);

    @Value("${spring.datasource.ifx.url}")
    private String ifxDbUrl;

    @Value("${spring.datasource.ifx.username}")
    private String ifxDbUsername;

    @Value("${spring.datasource.ifx.password}")
    private String ifxDbPassword;

    @Value("${spring.datasource.mysql.url}")
    private String mysqlDbUrl;

    @Value("${spring.datasource.mysql.username}")
    private String mysqlDbUsername;

    @Value("${spring.datasource.mysql.password}")
    private String mysqlDbPassword;

    public Connection openConnection(String ifxMysql, Logger logger) {
        Connection conn = null;
        DataSource ds = null;
        try {
            if (ifxMysql.equalsIgnoreCase("Ifx")) {
                Class.forName("com.informix.jdbc.IfxDriver").newInstance();
//                conn = DriverManager.getConnection("jdbc:informix-sqli://172.27.16.95:1551/gsmcpsm:INFORMIXSERVER=test_java_online", "javatest", "javatest");   //test
                conn = DriverManager.getConnection(ifxDbUrl, ifxDbUsername, ifxDbPassword);
            } else if (ifxMysql.equalsIgnoreCase("MySql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
//                conn = DriverManager.getConnection("jdbc:mysql://192.168.6.170:3306/gsmcpsm_online", "sanjayaa", "sanjayaa");    //test
                conn = DriverManager.getConnection(mysqlDbUrl, mysqlDbUsername, mysqlDbPassword);
            }
        } catch (SQLException ex) {
            logger.error(ex);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return conn;
    }
}
