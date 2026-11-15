package com.mcms.web.rest.dto;

import jakarta.validation.constraints.*;

/**
 * DTO for monthly report generation request.
 */
public class GenerateReportRequest {

    @NotNull
    @Min(value = 2000)
    @Max(value = 2100)
    private Integer year;

    @NotNull
    @Min(value = 1)
    @Max(value = 12)
    private Integer month;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "GenerateReportRequest{" + "year=" + year + ", month=" + month + '}';
    }
}
