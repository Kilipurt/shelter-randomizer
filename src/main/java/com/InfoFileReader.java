package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class InfoFileReader {
    private static final String path = "D:\\shelter\\";

    public static String readLine(int line, String fileNumber) throws Exception {
        String finalPath = path + fileNumber;
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
}
