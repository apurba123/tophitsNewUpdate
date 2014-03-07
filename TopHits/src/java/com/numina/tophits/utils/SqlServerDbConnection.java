/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.numina.tophits.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author s
 */
public class SqlServerDbConnection {

    static Logger log = Logger.getLogger(SqlServerDbConnection.class.getName());

    public static Connection getDbConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Properties prop = new Properties();
            prop.load(SqlServerDbConnection.class.getClassLoader().getResourceAsStream("dbConnection.properties"));
            String connectionIpAddres = prop.getProperty("sqlServer.HostIpAddress");
            String connectionPort = prop.getProperty("sqlServer.HostPort");
            String dbName = prop.getProperty("sqlServer.DatabaseName");
            String userName = prop.getProperty("sqlServer.UserName");
            String password = prop.getProperty("sqlServer.Password");
            String connectionUrl = "jdbc:sqlserver://" + connectionIpAddres + ":" + connectionPort + ";databaseName=" + dbName;

            log.info("Starting SQL Server Connection using: " + connectionUrl);

            conn = DriverManager.getConnection(connectionUrl, userName, password);
//            Statement s1 = conn.createStatement();
//            ResultSet rs = s1.executeQuery("SELECT * FROM Employee");
//
//            if (rs != null) {
//                while (rs.next()) {
//                    System.out.println(rs.getString(1) + "--" + rs.getString(2) + "--" + rs.getString(3));
//                }
//            }

        } catch (ClassNotFoundException e) {
            System.out.println("Error in finding sql server connection driver class (com.microsoft.sqlserver.jdbc.SQLServerDriver):" + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error in sql server connection:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error in Property File Loading:" + ex.getMessage());
            ex.printStackTrace();
        }

        return conn;
    }

}
