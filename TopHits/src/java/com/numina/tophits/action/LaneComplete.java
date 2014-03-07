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
import org.apache.log4j.Logger;

public class LaneComplete extends HttpServlet {

    Logger log = Logger.getLogger(this.getClass());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection conn = DbConnection.getDbConnection();
        boolean closed = false;
        HttpSession session = request.getSession();
        String cid = (String) session.getAttribute("clientId");
        int lane = 0;
        String error = "";
        if (conn != null) {
            try {

                String sql = "select * from app_closing where state='done' and client_id='" + cid + "'";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    lane = rs.getInt("lane");
                    error = rs.getString("err_msg");
                    closed = true;
                }
                conn.close();

            } catch (SQLException e) {
                log.error("Error::" + e);

            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {

                }
            }
        }
        if (closed) {
            out.print(lane + ",closed," + error);
        } else {
            out.print(lane + ",open," + error);
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
