package com.numina.tophits.scheduler.actions;

import com.numina.tophits.utils.DbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LaneDone implements Job {

    static Logger log = Logger.getLogger(UpdateAppLanes.class.getName());

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        //log.info("Update Cron Started @ " + new java.util.Date().toString());
        Connection conn = DbConnection.getDbConnection();
        try {
            String sql = "update app_closing set state='done' where state='closing'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println("ERROR::" + e.getMessage());
            log.error("Error::" + e);

        }

    }

}
