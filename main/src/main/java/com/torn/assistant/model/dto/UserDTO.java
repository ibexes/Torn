package com.torn.assistant.model.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private Long userId;
    private String name;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static UserDTO unknownUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Unknown user");
        return userDTO;
    }
}
