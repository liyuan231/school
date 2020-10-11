package com.school.dto;

import java.util.List;

public class NormalLikesInfoList {
    private Integer schoolId;
    private String schoolName;
    private List<NormalLikesInfo> list;

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public List<NormalLikesInfo> getList() {
        return list;
    }

    public void setList(List<NormalLikesInfo> list) {
        this.list = list;
    }
}
