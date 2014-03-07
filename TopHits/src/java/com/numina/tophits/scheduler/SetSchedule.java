/*
 * CRON initiator
 */
package com.numina.tophits.scheduler;

import com.numina.tophits.scheduler.actions.LaneDone;
import com.numina.tophits.scheduler.actions.UpdateAppLanes;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class SetSchedule {

    static Logger log = Logger.getLogger(SetSchedule.class.getName());
    static Scheduler scheduler = null;
    long preInterval;
    CronTrigger trigger1 = null;
    CronTrigger trigger2 = null;

    public synchronized void setCron() {
        try {
            
            SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");
            /*
            String strDbUpdateCronJobInterval = "0 0/3 * * * ?";
            String strUpdateSchedule = "0 0/2 * * * ?";

            JobKey updatejob = new JobKey("updatejob", "notificationGroup");
            JobDetail updateDbJob = JobBuilder.newJob(UpdateAppLanes.class)
                    .withIdentity(updatejob).build();

            JobKey laneDone = new JobKey("rdsjob", "notificationGroup");
            JobDetail laneDoneJob = JobBuilder.newJob(LaneDone.class)
                    .withIdentity(laneDone).build();

            trigger1 = TriggerBuilder
                    .newTrigger()
                    .withIdentity("Trigger1", "notificationGroup")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(strUpdateSchedule))
                    .build();
            trigger2 = TriggerBuilder
                    .newTrigger()
                    .withIdentity("Trigger2", "notificationGroup")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(strDbUpdateCronJobInterval))
                    .build();
            */
            scheduler = sf.getScheduler();
            /*
            scheduler.start();
            scheduler.scheduleJob(updateDbJob, trigger1);
            scheduler.scheduleJob(laneDoneJob, trigger2);
            */
        } catch (SchedulerException e) {
            log.error("Error initializing notification scheduler " + e.getMessage());
        }
    }

    public void pauseCron() {
        try {
            if (scheduler.isStarted()) {
                log.warn("pause schedular");
//                Thread.sleep(5000L);
//                scheduler.pauseAll();
                scheduler.pauseAll();
//                log.debug("schedular stopped");
            } else {
                log.warn("schedular already stopped");
            }

        } catch (SchedulerException e) {
            log.error("Exception in cron shutdown = " + e);
        }
    }

    public void resumeCron() {
        try {
            if (scheduler.isStarted()) {
                log.warn("resume schedular");
//                Thread.sleep(5000L);
//                scheduler.pauseAll();
                scheduler.resumeAll();
//                log.debug("schedular stopped");
            } else {
                log.warn("schedular already stopped");
            }

        } catch (SchedulerException e) {
            log.error("Exception in cron shutdown = " + e);
        }
    }

    public void stopCron() {
        try {
            if (scheduler.isStarted() || scheduler.isInStandbyMode()) {
                log.warn("stopping schedular");
//               Thread.sleep(5000L);
//               scheduler.pauseAll();
                scheduler.shutdown(true);
//                log.debug("schedular stopped");
            } else {
                log.warn("schedular already stopped");
            }

        } catch (SchedulerException e) {
            log.error("Exception in cron shutdown = " + e);
        }
    }
}
