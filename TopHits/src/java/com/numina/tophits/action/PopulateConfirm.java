package com.numina.tophits.action;

import com.numina.tophits.utils.DbConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PopulateConfirm extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            int laneno = Integer.parseInt(request.getParameter("laneno"));
            String sql = "select audit,box_qty_sorted from app_lanes where lane=" + laneno;
            Connection conn = DbConnection.getDbConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            String audit = "";
            Integer boxSize = 0;

            if (rs.next()) {
                audit = rs.getString("audit");
                boxSize = rs.getInt("box_qty_sorted");
            }
            out.println(audit + "," + boxSize);

        } catch (NumberFormatException e) {

        } catch (SQLException e) {
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
