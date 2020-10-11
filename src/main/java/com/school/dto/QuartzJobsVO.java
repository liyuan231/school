package com.school.dto;

public class QuartzJobsVO {
    private String jobDetailName;
    private String timeZone;
    private String groupName;
    private String jobExpression;

    public String getJobDetailName() {
        return jobDetailName;
    }

    public void setJobDetailName(String jobDetailName) {
        this.jobDetailName = jobDetailName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getJobExpression() {
        return jobExpression;
    }

    public void setJobExpression(String jobExpression) {
        this.jobExpression = jobExpression;
    }
}
