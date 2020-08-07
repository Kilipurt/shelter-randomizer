package com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InfoFileWriter {
    private static final String path = "../../shelter";
    private static final String TXT = ".txt";

    public static void write(String characterInfo, int fileNumber) throws IOException {
        String finalPath = path + File.separator + fileNumber + TXT;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(finalPath))) {
            bufferedWriter.append(characterInfo);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + ". Cant write to file " + finalPath);
        }
    }

    public static void writeLine(String characterInfo, int line, String filePath) throws Exception {
        String finalPath = path + File.separator + filePath + TXT;
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

    public static void removeAllFiles() {
        File directory = new File(path);
        File[] files = null;

        if (directory.isDirectory()) {
            files = directory.listFiles(file -> file.getPath().endsWith(TXT));
        }

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}
