package com.numina.tophits.action;

import com.numina.tophits.utils.DbConnection;
import com.numina.tophits.utils.InternalDerbyDbManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class LaneStatus extends HttpServlet {

    Logger log = Logger.getLogger(this.getClass());
    String LANE_ACTIVE = "GREEN";
    String LANE_NEARFULL = "YELLOW";
    String LANE_FULL = "RED";
    String LANE_COMPLETE = "BLUE";
    String LANE_BOOKED = "BOOKED";
    Integer NEAR_PERCNT = 90;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.setLevel(org.apache.log4j.Level.ALL);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //int laneno = Integer.parseInt(request.getParameter("laneno"));
        String percent = "90";
        HttpSession session = request.getSession();
        String cid = (String) session.getAttribute("clientId");

        Connection connDerby = null;
        if (cid != null) {
            try {
                String sqlQuery = "select UTILITYVALUE from UTILITY where UTILITYNAME='NearFull'and clientid=" + cid;
                connDerby = InternalDerbyDbManager.getConnection();
                ResultSet rs = InternalDerbyDbManager.executeQueryNoParams(connDerby, sqlQuery);
                if (rs.next()) {
                    percent = rs.getString(1).trim();
                }
                if (!percent.trim().equals("")) {
                    if (Integer.parseInt(percent) > 0 && Integer.parseInt(percent) < 100) {
                        NEAR_PERCNT = Integer.parseInt(percent);
                    }
                } else {
                    NEAR_PERCNT = 90;
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
        }

        String laneCount = request.getParameter("laneCount");
        Integer laneCountNo = 0;
        Connection conn = null;
        String state = "";
        try {
            conn = DbConnection.getDbConnection();
            String sql = "select lane from app_closing where state='closing'";

            PreparedStatement pstmt1 = conn.prepareStatement(sql);
            ResultSet rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                state = state + rs1.getInt("lane") + ",";

            }
            rs1.close();
            pstmt1.close();
            conn.close();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
            log.error("Error::" + ex);
        }
        String laneid[] = state.split(",");

        if (laneCount == null
                || laneCount.equalsIgnoreCase("")) {
            out.println("Invalid Lane Count");
        } else {
            try {
                laneCountNo = Integer.parseInt(laneCount);
            } catch (NumberFormatException nfe) {
                out.println("Invalid Lane No");
                log.error("Invalid Lane No:" + nfe.getMessage());
            }

            if (laneCountNo > 0) {
                try {
                    String laneStatus = LANE_ACTIVE;
                    String outString = "";
                    String sql = "select lane,box_size,qty_asked,box_qty_sorted from app_lanes where lane<=" + laneCountNo;
                    conn = DbConnection.getDbConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery();
                    Integer laneId = 0, boxSize = 0, qtyAsked = 0, boxQtySorted = 0;
                    // The Result set can have max. of 1 data as per the condition
                    while (rs.next()) {
                        laneId = rs.getInt(1);
                        boxSize = rs.getInt(2);
                        qtyAsked = rs.getInt(3);
                        boxQtySorted = rs.getInt(4);

                        if (qtyAsked == 0) {
                            laneStatus = LANE_COMPLETE;
                        } else if (qtyAsked == boxQtySorted) {
                            laneStatus = LANE_FULL;
                        } else if (qtyAsked != boxSize) {
                            Integer denominator = 1;
                            if (qtyAsked <= boxSize) {
                                denominator = qtyAsked;
                            } else {
                                denominator = boxSize;
                            }
                            
                            Integer fillPercent = (int) (((double) boxQtySorted.doubleValue() / denominator.doubleValue()) * 100.0000);
                            if(fillPercent>=100){
                                laneStatus = LANE_FULL;
                            }
                            else if (fillPercent >= NEAR_PERCNT) {
                                laneStatus = LANE_NEARFULL;
                            } else {
                                laneStatus = LANE_ACTIVE;
                            }

                        }
                        for (String laneid1 : laneid) {
                            if (laneid1.equals(laneId.toString())) {
                                laneStatus = laneStatus + LANE_BOOKED;
                            }
                        }

                        outString += laneId + "~" + laneStatus + "||";

                    }
                   rs.close();
                   pstmt.close();
                   conn.close();
                   out.println("" + outString.toString());

                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(LaneStatus.class.getName()).log(Level.SEVERE, null, ex);
                    log.error("Error::" + ex);
                } 
            }
        }

    }

}
