package com.torn.assistant.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {
    public static Long getUserId(String username) {
        Pattern p = Pattern.compile("\\w+\\[(\\d+)]");
        Matcher m = p.matcher(username);
        if (m.find()) {
            return Long.valueOf(m.group(1));
        }
        return null;
    }
}
