package com.torn.assistant.service;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationUserDetailsServiceTest {

    @Test
    public void loginWithApiKey() {
        String username = "Jia[12345]";
        Pattern p = Pattern.compile("\\w+\\[(\\d+)]");
        Matcher m = p.matcher(username);

        assertTrue(m.find());
        assertEquals("12345", m.group(1));
    }
}
