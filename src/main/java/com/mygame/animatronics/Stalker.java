package com.mygame.animatronics;

import com.mygame.managers.GameManager;

public class Stalker extends Animatronic {

    private int[][] mapa;

    public Stalker() {
        super(5f, 0);

        mapa = new int[][]{
            {1},
            {2},
            {3},
            {4}
        };
    }

    @Override
    protected void mover(GameManager game) {
        int[] opciones = mapa[posicion];
        posicion = opciones[(int)(Math.random() * opciones.length)];

        System.out.println("Stalker en sala: " + posicion);

        if (posicion >= 4) {
            game.endGame("El Stalker te atrapó");
        }
    }
}