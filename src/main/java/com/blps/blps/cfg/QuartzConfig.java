package com.blps.blps.cfg;

import com.blps.blps.job.CourierReleaseJob;
import com.blps.blps.job.OldOrdersCancelJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail oldOrdersCancelJobDetail() {
        return JobBuilder.newJob(OldOrdersCancelJob.class)
                .withIdentity("oldOrdersCancelJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger oldOrdersCancelTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(oldOrdersCancelJobDetail())
                .withIdentity("oldOrdersCancelTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }

    @Bean
    public JobDetail courierReleaseJobDetail() {
        return JobBuilder.newJob(CourierReleaseJob.class)
                .withIdentity("courierReleaseJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger courierReleaseTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(courierReleaseJobDetail())
                .withIdentity("courierReleaseTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?"))
                .build();
    }
}