package com.torn.api;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileReaderUtil {
    public static <T> String readFileAsString(Class<T> clazz, String file) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        URL resource = classLoader.getResource(file);

        assert resource != null;
        Path fileName = Path.of(resource.getPath());
        return Files.readString(fileName);
    }
}
