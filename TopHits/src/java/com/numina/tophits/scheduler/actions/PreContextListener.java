package com.numina.tophits.scheduler.actions;

import com.numina.tophits.scheduler.SetSchedule;
import com.numina.tophits.utils.InternalDerbyDbManager;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.log4j.Logger;

@WebListener()
public class PreContextListener implements ServletContextListener {

    Logger log = Logger.getLogger(PreContextListener.class.getName());
    SetSchedule msedule = new SetSchedule();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            msedule.setCron();
            log.info("Cron Job started");
            InternalDerbyDbManager.initDatabase(true);
            InternalDerbyDbManager.logSql();
            log.info("Context started");
        } catch (Exception ex) {
            log.error("Context Cannot be started:" + ex.getMessage());

        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            msedule.stopCron();
            InternalDerbyDbManager.shutdownDatabase();
            log.info("Context destroyed");
        } catch (SQLException ex) {
            log.error("Context not destroyed" + ex.getMessage());

        }
    }
}
