package com;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InfoFileWriter {
    private static final String path = "D:\\shelter\\";
    private static final String TXT = ".txt";

    public static void write(String characterInfo, String fileNumber) throws IOException {
        String finalPath = path + fileNumber + TXT;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(finalPath))) {
            bufferedWriter.append(characterInfo);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + ". Cant write to file " + finalPath);
        }
    }

    public static void writeLine(String characterInfo, int line, String filePath) throws Exception {
        String finalPath = path + filePath + TXT;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(finalPath))) {
            String[] lines = characterInfo.split("\n");

            if (line < 1 || line > lines.length) {
                throw new Exception("Wrong line number");
            }

            bufferedWriter.append(lines[line - 1]);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + ". Cant write to file " + finalPath);
        }
    }
}
