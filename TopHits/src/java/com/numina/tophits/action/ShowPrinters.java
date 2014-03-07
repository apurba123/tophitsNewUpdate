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
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.servlet.http.HttpSession;

public class ShowPrinters extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String printerNames = "";
        String printer = "";
        Connection connDerby = null;
        try {

            HttpSession session = request.getSession();
            String cid = (String) session.getAttribute("clientId");
            String sqlQuery = "select printer from DEFAULT_PRINTER where clientid=" + cid;

            connDerby = InternalDerbyDbManager.getConnection();
            ResultSet rs = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQuery);

            if (rs.next()) {
                printer = rs.getString(1);
            }

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                InternalDerbyDbManager.releaseConnection(connDerby);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            PrintService[] ser = PrintServiceLookup.lookupPrintServices(null, null);
            String selectedprinter = "";
            for (int i = 0; i < ser.length; ++i) {
                String p_name = ser[i].getName();
                if (p_name.trim().equals(printer)) {
                    selectedprinter = p_name;
                    printerNames = printerNames + "<option value='" + p_name + "' selected>" + p_name + "</option>";
                } else {
                    printerNames = printerNames + "<option value='" + p_name + "'>" + p_name + "</option>";
                }
            }
            printerNames = printerNames + "," + selectedprinter;
            if (selectedprinter.equals("")) {
                printerNames = "<option value='' selected>Select</option>" + printerNames;
            }
            out.println(printerNames);
        } finally {
            out.close();
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
