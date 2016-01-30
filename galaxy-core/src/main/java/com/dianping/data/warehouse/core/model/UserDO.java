package com.dianping.data.warehouse.core.model;

import javax.validation.constraints.NotNull;

public class UserDO {
    @NotNull(message="loginId is not null")
	private Integer loginId;
    @NotNull(message="employPinyin is not null")
    private String employPinyin;
    @NotNull(message="employeeId is not null")
    private String employeeId;
    @NotNull(message="employeeName is not null")
	private String employeeName;
    @NotNull(message="employeeEmail is not null")
	private String employeeEmail;

    public Integer getLoginId() {
        return loginId;
    }

    public void setLoginId(Integer loginId) {
        this.loginId = loginId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getEmployPinyin() {
        return employPinyin;
    }

    public void setEmployPinyin(String employPinyin) {
        this.employPinyin = employPinyin;
    }
}
