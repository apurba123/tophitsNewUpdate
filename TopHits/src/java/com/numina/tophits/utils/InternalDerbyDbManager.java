package com.numina.tophits.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.derby.impl.tools.ij.util;
import org.apache.derby.jdbc.EmbeddedDataSource40;
import org.apache.log4j.Logger;

/**
 * @author Bhaskar 14-Jan-2013 Internal/Local Embedded Derby Database Manager
 */
public class InternalDerbyDbManager {

    private static final Logger log = Logger.getLogger(InternalDerbyDbManager.class.getName());
    //private Connection connection = null;
    //private static InternalDerbyDbManager derbyDbManager = null;
    private static EmbeddedDataSource40 ds;

    public static String AUDIT_COMMENTS_TABLE = "AUDIT_COMMENTS";
    public static String PRINTER_LIST_TABLE = "PRINTER_LIST";
    public static String DEFAULT_PRINTER_TABLE = "DEFAULT_PRINTER";
    public static String UTILITY_TABLE = "UTILITY";
    public static String LOGIN_TABLE = "EMPLOYEE";
    public static String DEVICE_TABLE = "DEVICE_LIST";

    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String dbName = "TopHitsDerbyDb"; // the name of the database

    // We want to keep the same connection for a given thread
    // as long as we're in the same transaction
    private static final ThreadLocal<Connection> tranConnection = new ThreadLocal();

    private static void initDataSource() throws SQLException {

        if (ds != null
                && ds instanceof EmbeddedDataSource40) {
            ds.setDatabaseName(dbName);
        } else {
            ds = new EmbeddedDataSource40();
            ds.setDatabaseName(dbName);
            ds.setCreateDatabase("create");
        }
        System.out.println("Connected to and created database: " + dbName);
    }

    /**
     * Loads the appropriate JDBC driver for this environment/framework. For
     * example, if we are in an embedded environment, we load Derby's embedded
     * Driver, <code>org.apache.derby.jdbc.EmbeddedDriver</code>.
     */
    private static void loadDriver() {
        /*
         *  The JDBC driver is loaded by loading its class.
         *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
         *  be automatically loaded, making this code optional.
         *
         *  In an embedded environment, this will also start up the Derby
         *  engine (though not any databases), since it is not already
         *  running. In a client environment, the Derby engine is being run
         *  by the network server framework.
         *
         *  In an embedded environment, any static Derby system properties
         *  must be set before loading the driver to take effect.
         */
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
            log.info("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            log.error("\nUnable to load the JDBC driver " + driver);
            log.error("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            log.error("\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            log.error("\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }
    }

    /**
     * Method to Shut Down the database
     *
     * @throws SQLException
     */
    public static void shutdownDatabase() throws SQLException {
        loadDriver();
        DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
    }

    public static void logSql() throws Exception {
        executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY("
                + "'derby.language.logStatementText', 'true')");
    }

    public static synchronized void beginTransaction() throws Exception {
        if (tranConnection.get() != null) {
            throw new Exception("This thread is already in a transaction");
        }
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        tranConnection.set(conn);
    }

    public static void commitTransaction() throws Exception {
        if (tranConnection.get() == null) {
            throw new Exception("Can't commit: this thread isn't currently in a transaction");
        }
        tranConnection.get().commit();
        tranConnection.set(null);
    }

    public static void rollbackTransaction() throws Exception {
        if (tranConnection.get() == null) {
            throw new Exception("Can't rollback: this thread isn't currently in a transaction");
        }
        tranConnection.get().rollback();
        tranConnection.set(null);
    }

    /**
     * get Class Conection
     *
     * public static Connection getConnection() { derbyDbManager = new
     * InternalDerbyDbManager(); return derbyDbManager.connection; } /* /** get
     * Datasource a connection
     *
     * @return
     * @throws java.lang.Exception
     */
    public static Connection getConnection() throws Exception {
        if (tranConnection.get() != null) {
            return tranConnection.get();
        } else {
            return ds.getConnection();
        }
    }

    public static void releaseConnection(Connection conn) throws Exception {
        // We don't close the connection while we're in a transaction,
        // as it needs to be used by others in the same transaction context
        if (tranConnection.get() == null) {
            conn.close();
        }
    }

    public static void initDatabase(boolean dropTablesIfExists)
            throws Exception {
        try {
            initDataSource();
        } catch (SQLException sqlEx) {
            log.error("Problem in Normal Database Initialization, but Continue Explicitely.");
            loadDriver();
            util.startJBMS(driver, dbName);
        }

        if (dropTablesIfExists) {
            //dropAllTables();
        }

        // Assumption: If the AUDIT_COMMENTS table doesn't exist, none of the
        // tables exists.  Avoids multiple queries to the database
        if (!tableExists("AUDIT_COMMENTS")) {
            createAuditCommentsTable();
            //createPrinterListTable();
            createDefaultPrinterTable();
            createDeviceListTable();
            createUtilityTable();
            createEmployeeTable();
            //As all the tables are newly created, so it is necessary 
            //to add the default data in the Database
            addAllDefaultData();
        }
    }

    private static boolean tableExists(String tablename) throws Exception {
        Connection conn = getConnection();
        ResultSet rs;
        boolean exists;
        try {
            DatabaseMetaData md = conn.getMetaData();
            rs = md.getTables(null, null, tablename, null);
            exists = rs.next();
        } finally {
            releaseConnection(conn);
        }
        return exists;
    }

    private static void createAuditCommentsTable() throws Exception {
        System.out.println("Creating AUDIT_COMMENTS table");

        executeUpdate(
                "CREATE TABLE " + AUDIT_COMMENTS_TABLE + " ("
                + "laneid       INT NOT NULL , "
                + "auditcomment VARCHAR(200) )");

        executeUpdate(
                "ALTER TABLE " + AUDIT_COMMENTS_TABLE + " ADD CONSTRAINT AuditComments_PK PRIMARY KEY(laneid)");
    }

    private static void createPrinterListTable() throws Exception {
        System.out.println("Creating PRINTER_LIST table");

        executeUpdate(
                "CREATE TABLE " + PRINTER_LIST_TABLE + " ("
                + "printerid   VARCHAR(10) NOT NULL, "
                + "printerdesc VARCHAR(150) NOT NULL )");

        executeUpdate(
                "ALTER TABLE " + PRINTER_LIST_TABLE + " ADD CONSTRAINT PrinterList_PK PRIMARY KEY(printerid)");
    }

    private static void createDefaultPrinterTable() throws Exception {
        System.out.println("Creating DEFAULT_PRINTER table");

        executeUpdate(
                "CREATE TABLE " + DEFAULT_PRINTER_TABLE + " ("
                + "clientid    INT NOT NULL, "
                + "printer   VARCHAR(300) NOT NULL )");

        executeUpdate(
                "ALTER TABLE " + DEFAULT_PRINTER_TABLE + " ADD CONSTRAINT DefaultPrinter_PK PRIMARY KEY(clientid)");
//        executeUpdate(
//                "ALTER TABLE " + DEFAULT_PRINTER_TABLE + " ADD CONSTRAINT DefaultPrinter_PrinterList_FK FOREIGN KEY(printerid) "
//                + "REFERENCES " + PRINTER_LIST_TABLE + " (printerid)");
    }

    private static void createUtilityTable() throws Exception {
        System.out.println("Creating UTILITY table");

        executeUpdate(
                "CREATE TABLE " + UTILITY_TABLE + " ("
                + "id           INT GENERATED ALWAYS AS IDENTITY CONSTRAINT Utility_PK PRIMARY KEY, "
                + "utilityname  VARCHAR(50) NOT NULL, "
                + "utilityvalue VARCHAR(50) DEFAULT '', "
                + "clientid     INT )");

    }
    
    private static void createDeviceListTable() throws Exception {
        System.out.println("Creating DEVICE_LIST table");

        executeUpdate(
                "CREATE TABLE " + DEVICE_TABLE + " ("
                + "id         INT GENERATED ALWAYS AS IDENTITY CONSTRAINT DeviceId_PK PRIMARY KEY, "
                + "deviceid   VARCHAR(120) NOT NULL, "
                + "devicename VARCHAR(150) DEFAULT '' )");

    }

    private static void createEmployeeTable() throws Exception {
        System.out.println("Creating EMPLOYEE table");

        executeUpdate(
                "CREATE TABLE " + LOGIN_TABLE + " ("
                + "employeeid   VARCHAR(20) NOT NULL, "
                + "logstatus VARCHAR(20) NOT NULL )");
        executeUpdate(
                "ALTER TABLE " + LOGIN_TABLE + " ADD CONSTRAINT Employee_PK PRIMARY KEY(employeeid)");

    }

    /**
     * Drop all the tables. Used mostly for unit testing, to get back to a clean
     * state
     *
     * @throws java.lang.Exception
     */
    public static void dropAllTables() throws Exception {
        try {
            executeUpdate("DROP TABLE " + AUDIT_COMMENTS_TABLE);
        } catch (SQLException sqle) {
            if (!tableDoesntExist(sqle.getSQLState())) {
                throw sqle;
            }
        }
        try {
            executeUpdate("DROP TABLE " + DEFAULT_PRINTER_TABLE);
        } catch (SQLException sqle) {
            if (!tableDoesntExist(sqle.getSQLState())) {
                throw sqle;
            }
        }
        try {
            executeUpdate("DROP TABLE " + PRINTER_LIST_TABLE);
        } catch (SQLException sqle) {
            if (!tableDoesntExist(sqle.getSQLState())) {
                throw sqle;
            }
        }
        try {
            executeUpdate("DROP TABLE " + UTILITY_TABLE);
        } catch (SQLException sqle) {
            if (!tableDoesntExist(sqle.getSQLState())) {
                throw sqle;
            }
        }
        try {
            executeUpdate("DROP TABLE " + LOGIN_TABLE);
        } catch (SQLException sqle) {
            if (!tableDoesntExist(sqle.getSQLState())) {
                throw sqle;
            }
        }
        try {
            executeUpdate("DROP TABLE " + DEVICE_TABLE);
        } catch (SQLException sqle) {
            if (!tableDoesntExist(sqle.getSQLState())) {
                throw sqle;
            }
        }
    }

    private static boolean tableDoesntExist(String sqlState) {
        return sqlState.equals("42X05") || sqlState.equals("42Y55");
    }

    /**
     * Clean out all the tables
     *
     * @throws java.lang.Exception
     */
    public static void clearAllTables() throws Exception {
        Connection conn = getConnection();
        try {
            executeUpdate("DELETE FROM " + AUDIT_COMMENTS_TABLE);
            executeUpdate("DELETE FROM " + PRINTER_LIST_TABLE);
            executeUpdate("DELETE FROM " + DEFAULT_PRINTER_TABLE);
            executeUpdate("DELETE FROM " + UTILITY_TABLE);
            executeUpdate("DELETE FROM " + DEVICE_TABLE);
        } finally {
            releaseConnection(conn);
        }
    }

    public static void addAllDefaultData() {
        Connection connDerby = null;
        PreparedStatement pstmt = null;
        try {
            connDerby = getConnection();
            for (int i = 1; i <= 152; i++) {
                pstmt = connDerby.prepareStatement("INSERT INTO " + AUDIT_COMMENTS_TABLE
                        + "(laneid,auditcomment) VALUES(?,?)");
                pstmt.setString(1, i + "");
                pstmt.setString(2, "");
                pstmt.executeUpdate();
            }
//            log.info("\nDatabase: 152 Rows Added in AUDIT_COMMENTS.");
//            pstmt = connDerby.prepareStatement("INSERT INTO " + DEVICE_TABLE
//                    + "(deviceid,devicename) VALUES(?,?)");
//            pstmt.setString(1, "win32");
//            pstmt.setString(2, "Windows");
//            pstmt.executeUpdate();
//            log.info("\nDatabase: DEVICE_LIST Default to win32 for testing.");
            connDerby.commit();

            pstmt.close();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(InternalDerbyDbManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pstmt = null;
                InternalDerbyDbManager.releaseConnection(connDerby);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(InternalDerbyDbManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Helper wrapper around boilerplate JDBC code. Execute a statement that
     * doesn't return results using a PreparedStatment, and returns the number
     * of rows affected
     *
     * @param statement
     * @return
     * @throws java.lang.Exception
     */
    public static int executeUpdate(String statement)
            throws Exception {
        Connection conn = getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(statement);
            return ps.executeUpdate();
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Helper wrapper around boilerplate JDBC code. Execute a statement that
     * returns results using a PreparedStatement that takes no parameters
     * (you're on your own if you're binding parameters).
     *
     * @param conn
     * @param statement
     * @return the results from the query
     * @throws java.lang.Exception
     */
    public static ResultSet executeQueryNoParams(Connection conn, String statement)
            throws Exception {
        PreparedStatement ps = conn.prepareStatement(statement);
        return ps.executeQuery();
    }

}
