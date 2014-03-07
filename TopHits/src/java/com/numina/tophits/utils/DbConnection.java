package com.numina.tophits.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

public class DbConnection {

    static Logger log = Logger.getLogger(DbConnection.class.getName());

    public static Connection getDbConnection() {
        Connection conn = null;
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/rds");
            conn = ds.getConnection();

        } catch (NamingException ex) {
            System.out.println("RDB DB ->Naming Exception -> Error in getting DB Connection " + ex);
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("RDB DB ->SQL Exception->Error in getting DB Connection " + ex);
        }
        return conn;
    }
}
