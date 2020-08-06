package com;

public class Demo {

    public static void main(String[] args) throws Exception {
        CharacterGenerator characterGenerator = new CharacterGenerator();
        for (int i = 0; i < 4; i++) {
            String characterInfo = characterGenerator.generate();
            InfoFileWriter.write(characterInfo, String.valueOf(i + 1));
        }
    }
}
