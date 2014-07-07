package uk.ac.brighton.uni.ab607.hangman;

import java.io.*;

public class WordReader {
    private String fileName = "res/words.txt";
    private BufferedReader bf;

    private InputStream in = getClass().getResourceAsStream(fileName);

    private String temp = "";
    // to avoid NPE, just in case
    private String[] words = new String[0];

    public WordReader() {
        try {
            try {
                bf = new BufferedReader(new FileReader(fileName));
            }
            catch (Exception e) {
                bf = new BufferedReader(new InputStreamReader(in));
            }

            String line = "";

            while ((line = bf.readLine()) != null) {
                temp += " " + line;
            }

            words = temp.trim().split(" ");
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
