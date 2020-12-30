package com.torn.api.utils;

import com.torn.api.model.exceptions.TornApiAccessException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.torn.api.FileReaderUtil.readFileAsString;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonConverterTest {

    @Test
    public void throwsExceptionWhenIncorrectKey() throws IOException, TornApiAccessException {
        String string = readFileAsString(JsonConverterTest.class, "incorrectkey.json");
        try {
            JsonConverter.convertToJson(string);
            fail();
        } catch (TornApiAccessException ignored) {

        }
    }
}
