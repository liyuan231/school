package com.school.dto;

public class SimpleUserInfoPlus extends SimpleUserInfo{
    private String schoolLogo;
    private String schoolSignature;

    public String getSchoolLogo() {
        return schoolLogo;
    }

    public void setSchoolLogo(String schoolLogo) {
        this.schoolLogo = schoolLogo;
    }

    public String getSchoolSignature() {
        return schoolSignature;
    }

    public void setSchoolSignature(String schoolSignature) {
        this.schoolSignature = schoolSignature;
    }
}
