package com.litegpt;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Show splash screen
        final SplashScreen splash = new SplashScreen();
        splash.showSplash();

        // Launch UI after splash
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChatInterface();
                splash.dispose();
            }
        });
    }
}