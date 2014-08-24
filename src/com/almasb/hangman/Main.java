package com.almasb.hangman;

import com.almasb.hangman.jfxgui.FXGUI;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        new GUI();
        Application.launch(FXGUI.class, args);
    }
}
