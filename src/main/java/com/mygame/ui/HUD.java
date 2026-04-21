package com.mygame.ui;

import com.jme3.font.BitmapText;

public class HUD {
    private BitmapText energiaText;

    public void actualizar(float energia) {
        energiaText.setText("Energia: " + (int)energia + "%");
    }
}
