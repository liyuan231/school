package com.school.dto;

import java.util.Arrays;
import java.util.List;

public class SimpleLikesInfo {
    private Integer schoolId;
    private String schoolName;
    private List<String> likesSchoolName;

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

    public List<String> getLikesSchoolName() {
        return likesSchoolName;
    }

    @Override
    public String toString() {
        return "SimpleLikesInfo{" +
                "schoolId=" + schoolId +
                ", schoolName='" + schoolName + '\'' +
                ", likesSchoolName=" + Arrays.toString(likesSchoolName.toArray()) +
                '}';
    }

    public void setLikesSchoolName(List<String> likesSchoolName) {


        this.likesSchoolName = likesSchoolName;
    }
}
