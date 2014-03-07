package com.numina.tophits.action;

import com.numina.tophits.utils.DbConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.numina.tophits.utils.InternalDerbyDbManager;
import static com.numina.tophits.utils.InternalDerbyDbManager.DEVICE_TABLE;
import com.numina.tophits.utils.SqlServerDbConnection;
import java.net.InetAddress;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class Login extends HttpServlet {

    Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        //Connection conn = DbConnection.getDbConnection();

        // This is added by Subrata for Testing the MySql Connectivity.
        Connection conn = null;
        try {
            conn = SqlServerDbConnection.getDbConnection();
            System.out.println(" ######## SQL SERVER connection Successful ###########  " + conn.toString());
        } catch (Exception ee) {
            System.out.println(" SQL SERVER connection Error: " + ee.getMessage());
            ee.printStackTrace();
        }
        try {
            Connection mysqlConnection = DbConnection.getDbConnection();
            System.out.println(" ######## RDB connection Successful ########### " + mysqlConnection.toString());
            mysqlConnection.close();
        } catch (Exception me) {
            System.out.println("RDB connection Error: " + me.getMessage());
            me.printStackTrace();

        }
        boolean loginflag = false;
        session.setAttribute("LoginFlag", "0");
        if (conn != null) {
            try {
                String deviceId = "";
                String deviceName = "";
                int clientId = 0;

                // For Rho Mobile Motorolla Device
                //if (request.getParameter("clientId") != null) {
                //    deviceId = request.getParameter("clientId");
                //}
                //if (request.getParameter("clientName") != null) {
                //    deviceName = request.getParameter("clientName");
                //}
                // For Non-Motorolla Devices and From Browsers
                //Add Cookie to the device as unique Id
                Cookie cookie = null;
                Cookie[] cookies = null;
                boolean hasCookie = false;
                // Get an array of Cookies associated with this domain
                cookies = request.getCookies();
                if (cookies != null) {
                    for (int i = 0; i < cookies.length; i++) {
                        cookie = cookies[i];
                        if (cookie.getName().equalsIgnoreCase("deviceId")) {
                            hasCookie = true;
                            break;
                        }
                    }
                    if (hasCookie) {
                        //Read the cookie value
                        log.info("Cookie deviceId Name: " + cookie.getName() + ", Value: " + cookie.getValue());
                        deviceId = cookie.getValue();
                    } else {
                        deviceId = "" + Math.random();
                        Cookie deviceIdCookie = new Cookie("deviceId", ""+deviceId);
                        deviceIdCookie.setMaxAge(60 * 60 * 24 * 365 * 50);
                        //System.out.println(">>>>>>>>>>>>>>>>>>>>>> New Device Cookie @ Step 4.");
                        response.addCookie(deviceIdCookie);
                    }
                } else {
                    // Create cookies.
                    deviceId = "" + Math.random();
                    Cookie deviceIdCookie = new Cookie("deviceId", ""+deviceId);
                    deviceIdCookie.setMaxAge(60 * 60 * 24 * 365 * 50);
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>> New Device Cookie @ Step 3.");
                    response.addCookie(deviceIdCookie);
                }
                //Set Device Name
                InetAddress inetAddress = InetAddress.getByName(request.getRemoteAddr());
                deviceName = inetAddress.getCanonicalHostName();

                //System.out.println("Client UniqueId:" + deviceId + " Name:" + deviceName + " Current Time in Milis:" + System.currentTimeMillis());
                String uid = "";
                if (request.getParameter("uid_r") != null) {
                    uid = request.getParameter("uid_r");
                }

                String pwd = "";
                if (request.getParameter("pwd_r") == null
                        || request.getParameter("pwd_r").length() <= 0) {
                    pwd = "";
                } else {
                    pwd = request.getParameter("pwd_r");
                }

                log.info("Employee Id/UserName:" + uid);
                session.setAttribute("employeeId", uid);

                String sql = "";
                //String sql = "select employee_id,password from employee_login where employee_id='" + uid + "' and password='" + pwd + "'";
                if (pwd != null && pwd.length()>0) {
                    sql = "select EmployeeId,Password from Employee where EmployeeId='" + uid + "' and Password='" + pwd + "'";
                } else {
                    sql = "select EmployeeId,Password from Employee where EmployeeId='" + uid + "'";
                }
                
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    loginflag = true;
                }
                conn.close();

                if (loginflag) {

                    // rd = request.getRequestDispatcher("home.jsp");
                    Connection connDerby = null;
                    //====================== Edited by Bhaskar on 04-02-2014 ====================================
                    // For Adding Logic for Unique Client Id
                    // Get the Unique Client Id corrosponding to the Device Id
                    try {
                        String sqlQuery = "select id,deviceid,devicename from " + DEVICE_TABLE + " where deviceid='" + deviceId.trim() + "'";
                        connDerby = InternalDerbyDbManager.getConnection();
                        ResultSet rst = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQuery);
                        if (rst.next()) {
                            clientId = rst.getInt(1);
                        } else {
                            sqlQuery = "insert into " + DEVICE_TABLE + " (deviceid,devicename) VALUES('" + deviceId.trim() + "','" + deviceName + "')";
                            InternalDerbyDbManager.executeUpdate(sqlQuery);
                            InternalDerbyDbManager.releaseConnection(connDerby);

                            String sqlQueryNew = "select id,deviceid,devicename from " + DEVICE_TABLE + " where deviceid='" + deviceId.trim() + "'";
                            connDerby = InternalDerbyDbManager.getConnection();
                            ResultSet rstNew = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQueryNew);
                            while (rstNew.next()) {
                                clientId = rstNew.getInt(1);
                            }
                        }
                        session.setAttribute("clientId", ("" + clientId));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error("Error in DerbyDb Accessing 1st Part: " + ex.getMessage(), ex);
                    } finally {
                        try {
                            InternalDerbyDbManager.releaseConnection(connDerby);
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //==================== Edit Ends ===================
                    // Setting up the Default Values for the specific Client/Device
                    int statusflag = 0;
                    try {
                        int cnt = 0;
                        String sqlQuery = "select count(*) from UTILITY where UTILITYNAME='NearFull' and clientid=" + clientId;
                        connDerby = InternalDerbyDbManager.getConnection();
                        ResultSet rst = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQuery);
                        if (rst.next()) {
                            cnt = rst.getInt(1);
                        }
                        if (cnt == 0) {
                            sqlQuery = "insert into UTILITY (utilityname,utilityvalue,clientid) VALUES('NearFull','90'," + clientId + ")";
                            InternalDerbyDbManager.executeUpdate(sqlQuery);
                        }
                        InternalDerbyDbManager.releaseConnection(connDerby);

                        // Employee Status For Login
                        connDerby = InternalDerbyDbManager.getConnection();
                        String status = "";
                        String qry = "select logstatus from EMPLOYEE where employeeid='" + uid + "'";
                        ResultSet rslt = InternalDerbyDbManager.executeQueryNoParams(connDerby, qry);
                        if (rslt.next()) {
                            status = rslt.getString(1);
                        }
                        InternalDerbyDbManager.releaseConnection(connDerby);
                        // If No status Found, Add the new Employee
                        if (status.trim().equals("")) {
                            // By-Pass Single Login Validation
                            //sqlQuery = "insert into EMPLOYEE (employeeid,logstatus) VALUES('" + uid + "','active')";
                            sqlQuery = "insert into EMPLOYEE (employeeid,logstatus) VALUES('" + uid + "','idle')";
                            InternalDerbyDbManager.executeUpdate(sqlQuery);
                            statusflag = 1;
                        } else if (status.trim().equals("idle")) {
                            // By-Pass Single Login Validation
                            //try {
                            //    sqlQuery = "update EMPLOYEE set logstatus='active' where employeeid='" + uid + "'";
                            //    InternalDerbyDbManager.executeUpdate(sqlQuery);
                            //} catch (Exception e) {
                            //}
                            statusflag = 1;
                        } else if (status.trim().equals("active")) {
                            // By-Pass Single Login Validation
                            //statusflag = 0;
                            statusflag = 1;
                        }
                        //System.out.println("Status Db:" + status.trim() + " Status Value:" + statusflag);
                    } catch (Exception ex) {
                        log.error("Error in DerbyDb Accessing 2nd Part: " + ex.getMessage(), ex);
                    } finally {
                        try {
                            InternalDerbyDbManager.releaseConnection(connDerby);
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if (statusflag == 1) {
                        session.setAttribute("LoginFlag", "1");
                        response.sendRedirect("home.jsp");
                    } else {
                        session.setAttribute("LoginFlag", "0");
                        RequestDispatcher rd = request.getRequestDispatcher("index.jsp?status=1");
                        rd.forward(request, response);
                    }
                    session.setAttribute("employeeId", uid);

                } else {
                    session.setAttribute("LoginFlag", "0");
                    RequestDispatcher rd = request.getRequestDispatcher("index.jsp?status=0");
                    rd.forward(request, response);
                    // response.sendRedirect("index.jsp?status=0");
                }

            } catch (SQLException e) {
                log.error("Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
            }
        }

    }
}
