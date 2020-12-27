package com.torn.assistant.model.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
