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
import javax.servlet.http.HttpSession;

public class CheckLaneState extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            HttpSession session = request.getSession();
            String clientid = (String) session.getAttribute("clientId");
            int laneno = Integer.parseInt(request.getParameter("laneno"));
            int cnt = 0;
            Connection conn = DbConnection.getDbConnection();
            if (conn != null) {
                String sql = "select count(state) from app_closing where client_id=" + clientid + " and state='closing'";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    cnt = rs.getInt(1);
                }
                pstmt.close();
                rs.close();
                if (cnt > 0) {
                    out.println("booked");
                } else {
                    sql = "select state from app_closing where lane=" + laneno;
                    PreparedStatement pstmt1 = conn.prepareStatement(sql);
                    ResultSet rs1 = pstmt1.executeQuery();
                    String state = "";
                    if (rs1.next()) {
                        state = rs1.getString("state");
                    }
                    out.println(state);
                    pstmt1.close();
                    rs1.close();
                }
                conn.close();
            }

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

}
