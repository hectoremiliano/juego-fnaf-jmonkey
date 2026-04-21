package com.mygame.animatronics;

import com.mygame.managers.GameManager;

public class Runner extends Animatronic {

    public Runner() {
        super(2f, 0);
    }

    @Override
    protected void mover(GameManager game) {
        posicion++;
        System.out.println("Runner en sala: " + posicion);

        if (posicion >= 5) {
            game.endGame("El Runner te alcanzó");
        }
    }
}