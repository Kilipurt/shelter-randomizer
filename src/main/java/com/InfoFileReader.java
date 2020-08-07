package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InfoFileReader {
    private static final String path = "../../shelter";
    private static final String TXT = ".txt";

    public static String readLine(int line, String fileNumber) throws Exception {
        String finalPath = path + File.separator + fileNumber + TXT;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(finalPath))) {
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());

            if (line < 1 || line > lines.size()) {
                throw new Exception("Wrong line");
            }

            return lines.get(line - 1);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + ". Cant write to file " + finalPath);
        }
    }

    public static String readFile(String fileNumber) throws Exception {
        String finalPath = path + File.separator + fileNumber + TXT;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(finalPath))) {
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            return String.join("\r\n", lines);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + ". Cant write to file " + finalPath);
        }
    }

    public static List<String> readFileNames() {
        File directory = new File(path);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles((file, s) -> s.endsWith(TXT));

            if (files == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
