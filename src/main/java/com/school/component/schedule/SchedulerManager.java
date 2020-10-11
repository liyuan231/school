package com.school.component.schedule;

import com.school.dto.QuartzJobsVO;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SchedulerManager {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    public void startJob(LocalDateTime startTime,
                         String jobName,
                         String jobGroup,
                         Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        deleteJobIfExist(jobName, jobGroup);
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
        Trigger trigger = TriggerBuilder.newTrigger().startAt(Date.from(startTime.atZone(ZoneOffset.ofHours(8)).toInstant())).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void startJob(Date startTime,
                         String jobName,
                         String jobGroup,
                         Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
        Trigger trigger = TriggerBuilder.newTrigger().startAt(startTime).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteJobIfExist(String jobName, String jobGroup) throws SchedulerException {
        deleteJobIfExist(new JobKey(jobName, jobGroup));
    }

    public void deleteJobIfExist(JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }

    public void clearAll() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.clear();
    }


    public List<QuartzJobsVO> list() throws SchedulerException {
        List<QuartzJobsVO> quartzJobsVOS = new ArrayList<>();
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
        for (String groupName : triggerGroupNames) {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(groupName);
            Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);
            for (TriggerKey key : triggerKeySet) {
                String name = key.getName();
                String group = key.getGroup();
                JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(new JobKey(name, group));
//                jobDetail.getDescription().
                QuartzJobsVO quartzJobsVO = new QuartzJobsVO();
                quartzJobsVO.setGroupName(groupName);
                quartzJobsVO.setJobDetailName(jobDetail==null?"[NULL]":jobDetail.getName());
                quartzJobsVOS.add(quartzJobsVO);
            }
        }
        return quartzJobsVOS;
    }
}



