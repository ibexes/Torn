package com.torn.assistant.model.dto;

import java.util.Date;

public class DataPointDTO {
    private Date time;
    private Long difference;

    public DataPointDTO(Date time, Long difference) {
        this.time = time;
        this.difference = difference;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getDifference() {
        return difference;
    }

    public void setDifference(Long difference) {
        this.difference = difference;
    }
}
