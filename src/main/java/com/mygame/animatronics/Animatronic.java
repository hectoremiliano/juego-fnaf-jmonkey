package com.mygame.animatronics;

import com.mygame.managers.GameManager;

public abstract class Animatronic {

    protected float tiempo = 0;
    protected float intervaloMovimiento;
    protected int posicion = 0;

    public Animatronic(float intervaloMovimiento, int posicionInicial) {
        this.intervaloMovimiento = intervaloMovimiento;
        this.posicion = posicionInicial;
    }

    public void update(float tpf, GameManager game) {
        tiempo += tpf;

        if (tiempo >= intervaloMovimiento) {
            mover(game);
            tiempo = 0;
        }
    }

    protected abstract void mover(GameManager game);

    public int getPosicion() {
        return posicion;
    }
}