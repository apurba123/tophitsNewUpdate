package com.numina.tophits.action;

import com.numina.tophits.utils.InternalDerbyDbManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Settings extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String cid = (String) session.getAttribute("clientId");

        String percent = request.getParameter("percent");
        String printer = request.getParameter("printer");

        Connection connDerby = null;
        String sqlQuery;
        try {
            sqlQuery = "update UTILITY set UTILITYVALUE='" + percent + "' where UTILITYNAME='NearFull' and clientid=" + cid;
            connDerby = InternalDerbyDbManager.getConnection();
            int r = InternalDerbyDbManager.executeUpdate(sqlQuery);

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LaneClosing.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                InternalDerbyDbManager.releaseConnection(connDerby);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(LaneClosing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int cnt = 0;
        try {

            connDerby = InternalDerbyDbManager.getConnection();
            sqlQuery = "select count(*) from DEFAULT_PRINTER where clientid=" + cid;
            ResultSet rslt = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQuery);

            if (rslt.next()) {
                cnt = rslt.getInt(1);
            }

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LaneClosing.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                InternalDerbyDbManager.releaseConnection(connDerby);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(LaneClosing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {

            connDerby = InternalDerbyDbManager.getConnection();

            if (cnt > 0) {
                sqlQuery = "update DEFAULT_PRINTER set printer='" + printer + "' where clientid=" + cid;
            } else {
                sqlQuery = "insert into DEFAULT_PRINTER (clientid,printer)values(" + cid + ",'" + printer + "')";
            }
            int s = InternalDerbyDbManager.executeUpdate(sqlQuery);
            if (s > 0) {
                out.print("success");
            } else {
                out.print(" ");
            }

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LaneClosing.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                InternalDerbyDbManager.releaseConnection(connDerby);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(LaneClosing.class.getName()).log(Level.SEVERE, null, ex);
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
