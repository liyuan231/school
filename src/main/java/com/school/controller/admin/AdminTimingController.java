package com.school.controller.admin;

import com.school.component.schedule.SchedulerManager;
import com.school.dto.QuartzJobsVO;
import com.school.service.impl.RoleToAuthoritiesServiceImpl;
import com.school.utils.ResponseUtil;
import com.school.utils.RoleEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Api(value = "管理端定时开启某些接口", tags = {"定时开启某些接口"})
public class AdminTimingController {
    private static final String likeJobGroup = "LIKE_GROUP";
    private static final String onLikeJobName = "LIKE_ON";
    private static final String offLikeJobName = "LIKE_OFF";

    private static final String signJobGroup = "SIGN_GROUP";
    private static final String onSignJobName = "SIGN_ON";
    private static final String offSignJobName = "SIGN_OFF";
    @Autowired
    RoleToAuthoritiesServiceImpl roleToAuthoritiesService;
    @Autowired
    SchedulerManager schedulerManager;


    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @PostMapping("/configLikesPeriod")
    @ApiOperation(value = "配置用户意向时段", notes = "配置用户可以进行意向的时段")
//    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public Object controlLikes(@ApiParam(value = "开始时间", example = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                               @ApiParam(value = "结束时间", example = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) throws SchedulerException {
        schedulerManager.startJob(startTime, onLikeJobName, likeJobGroup, LikeOperationOnJob.class);
        schedulerManager.startJob(endTime, offLikeJobName, likeJobGroup, LikeOperationOffJob.class);
        return ResponseUtil.build(HttpStatus.OK.value(), "限制用户意向期间的成功", null);
    }

    @ApiOperation(value = "配置用户签约时段", notes = "配置用户可以进行签约的时间")
    @PostMapping("/configSignPeriod")
    //    @PreAuthorize("hasAnyRole('ADMINISTRATOR')")
    public Object controlSings(@ApiParam(value = "开始时间", example = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                               @ApiParam(value = "结束时间", example = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) throws SchedulerException {
        schedulerManager.startJob(startTime, onSignJobName, signJobGroup, SignOperationOnJob.class);
        schedulerManager.startJob(endTime, offSignJobName, signJobGroup, SignOperationOffJob.class);
        return ResponseUtil.build(HttpStatus.OK.value(), "限制用户意向期间的成功", null);
    }

    @GetMapping("/list")
    public Object list() throws SchedulerException {
        List<QuartzJobsVO> list = schedulerManager.list();
        System.out.println(list.size());
        return list;
    }


    class LikeOperationOnJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            roleToAuthoritiesService.addAuthority(RoleEnum.USER, "/like");
        }
    }

    class LikeOperationOffJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            roleToAuthoritiesService.removeAuthority(RoleEnum.USER, "/like");
        }
    }

    class SignOperationOnJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            roleToAuthoritiesService.addAuthority(RoleEnum.USER, "/sign");
        }
    }

    class SignOperationOffJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            roleToAuthoritiesService.removeAuthority(RoleEnum.USER, "/sign");
        }
    }
}
