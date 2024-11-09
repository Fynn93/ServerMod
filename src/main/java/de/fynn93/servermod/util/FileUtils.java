package de.fynn93.servermod.util;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

public class FileUtils {
    public static String read(Path path) {
        Scanner sc;
        try {
            sc = new Scanner(path.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringBuilder output = new StringBuilder();
        while (sc.hasNextLine())
            output.append(sc.nextLine());

        return output.toString();
    }

    public static void write(Path configFilePath, String json) {

    }
}
