package com.almasb.hangman;

import java.io.*;

public class WordReader {
    private static final String fileName = "/res/words.txt";

    private String[] words = new String[0];

    public WordReader() {
        try (InputStream in = getClass().getResourceAsStream(fileName);
                BufferedReader bf = new BufferedReader(new InputStreamReader(in))) {

            String line = "", accumulator = "";
            while ((line = bf.readLine()) != null)
                accumulator += " " + line;

            words = accumulator.trim().split(" ");
        }
        catch (Exception e) {
            System.out.println("Couldn't find/read file: " + fileName);
            System.out.println("Error message: " + e.getMessage());
        }
    }

    public String getRandomWord() {
        if (words.length == 0) return "NODATA";
        return words[(int)(Math.random()*words.length)];
    }
}
