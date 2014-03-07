package com.numina.tophits.scheduler.actions;

import com.numina.tophits.utils.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpdateAppLanes implements Job {

    static Logger log = Logger.getLogger(UpdateAppLanes.class.getName());

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        //log.info("Update Cron Started @ " + new java.util.Date().toString());
       
        Connection conn = DbConnection.getDbConnection();
        int lane = 0 + (int) (Math.random() * 151);
        int boxQtySorted = 0 + (int) (Math.random() * 11);
        int boxSize = 0 + (int) (Math.random() * 15);
        int qtyAsked = 0 + (int) (Math.random() * 15);
        try {
            String sql = "update app_lanes set box_qty_sorted="+boxQtySorted+",box_size="+boxSize+",qty_asked="+qtyAsked+",qty_closed=0"+
                         " where lane=" + lane;
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            System.out.println("ERROR::" + e.getMessage());
            log.error("Error::" + e);

        }

    }
}
