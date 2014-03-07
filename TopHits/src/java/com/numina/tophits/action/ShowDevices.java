/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.numina.tophits.action;

import com.numina.tophits.utils.InternalDerbyDbManager;
import static com.numina.tophits.utils.InternalDerbyDbManager.DEVICE_TABLE;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pc
 */
public class ShowDevices extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection connDerby = null;
        try {

            String sqlQuery = "select id,deviceid,devicename from " + DEVICE_TABLE;
            System.out.println("=============================> Device Data <=============================");
            connDerby = InternalDerbyDbManager.getConnection();
            ResultSet rs = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQuery);
            System.out.println("ID~\tDeviceId~\tDeviceName");
            while (rs.next()) {
                System.out.println(rs.getString(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3));
            }
            System.out.println("=============================> END <=============================");
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                InternalDerbyDbManager.releaseConnection(connDerby);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
