package com.torn.assistant.model.dto;

import java.io.Serializable;
import java.util.Date;

public class StatDTO implements Serializable {
    private Long start;
    private Long end;
    private Long difference;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getDifference() {
        return difference;
    }

    public void setDifference(Long difference) {
        this.difference = difference;
    }
}
