package com.torn.assistant.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserActivityDTO {
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private List<Date> active = new ArrayList<>();

    public UserActivityDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Date> getActive() {
        return active;
    }

    public void setActive(List<Date> active) {
        this.active = active;
    }
}
