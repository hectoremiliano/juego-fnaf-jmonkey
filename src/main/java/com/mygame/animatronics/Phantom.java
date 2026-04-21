package com.mygame.animatronics;

import com.mygame.managers.GameManager;

public class Phantom extends Animatronic {

    public Phantom() {
        super(6f, 0);
    }

    @Override
    protected void mover(GameManager game) {
        posicion++;
        System.out.println("Watcher en sala: " + posicion);

        if (posicion >= 5) {
            game.endGame("El Watcher te observó demasiado");
        }
    }
}