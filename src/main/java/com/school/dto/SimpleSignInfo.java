package com.school.dto;

import java.time.LocalDateTime;

public class SimpleSignInfo {
    private Integer signId;
    private String schoolName;//学校名
    private String signedSchoolName;//该学校主动签约的记录
    private LocalDateTime signTime;//签约时间

    public Integer getSignId() {
        return signId;
    }

    public void setSignId(Integer signId) {
        this.signId = signId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSignedSchoolName() {
        return signedSchoolName;
    }

    public void setSignedSchoolName(String signedSchoolName) {
        this.signedSchoolName = signedSchoolName;
    }

    public LocalDateTime getSignTime() {
        return signTime;
    }

    public void setSignTime(LocalDateTime signTime) {
        this.signTime = signTime;
    }
}
