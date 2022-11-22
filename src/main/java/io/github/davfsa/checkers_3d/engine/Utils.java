package io.github.davfsa.checkers_3d.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    public static String readFile(String filePath) {
        String str;
        try {
            str = Files.readString(Paths.get(filePath));
        } catch (IOException exc) {
            throw new RuntimeException("Error reading file [" + filePath + "]", exc);
        }
        return str;
    }
}
