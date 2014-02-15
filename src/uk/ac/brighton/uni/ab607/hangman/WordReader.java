package uk.ac.brighton.uni.ab607.hangman;

import java.io.*;

public class WordReader {
    private String fileName = "res/words.txt";
    private BufferedReader bf;

    private InputStream in = getClass().getResourceAsStream(fileName);

    private String temp = "";
    private String[] words = new String[1882];

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
        }
    }

    public String getRandomWord() {
        int i = (int) (Math.random() * 1883);

        return words[i];
    }
}
