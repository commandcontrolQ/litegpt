package com.litegpt;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen() {
        JLabel splashLabel = new JLabel(new ImageIcon(
                getClass().getResource("/com/litegpt/splash.png")));
        getContentPane().add(splashLabel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    public void showSplash() {
        setVisible(true);
        try {
            Thread.sleep(2000); // Show for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
