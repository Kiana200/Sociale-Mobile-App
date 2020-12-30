package com.julor200.crossdress.java_beans;

import com.julor200.crossdress.java_beans.Date;

import java.io.Serializable;
import java.util.List;

/**
 * Javabean class for list with Dates.
 */
public class DateList implements Serializable {
    private List<Date> dateList;

    public DateList() {
    }

    public List<Date> getDateList() {
        return dateList;
    }

    public void setDateList(List<Date> dateList) {
        this.dateList = dateList;
    }
}
