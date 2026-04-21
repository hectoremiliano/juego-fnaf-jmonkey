package com.mygame.animatronics;

import com.mygame.managers.GameManager;

/**
 * Watcher ataca desde la ventilación (derecha).
 * Debes cerrar la ventilación en 5 segundos o te matará.
 */
public class Watcher extends Animatronic {

    public Watcher() { super(0); } // Agresión se establece por GameManager

    @Override
    public void reset() {
        this.currentPosition = 0;
        this.stateTimer = 0;
        this.isActiveAttackState = false;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        this.currentPosition = 5; // Posición de ventilación
        this.stateTimer = 0;
        this.isActiveAttackState = true;
        System.out.println("⚠️ Watcher ha entrado en la ventilación. ¡Ciérrala!");
    }

    @Override
    protected void handleAttackState(GameManager game) {
        // ¿Cerró el jugador la ventilación en tiempo?
        if (game.isVentClosedState()) {
            System.out.println("🛑 Watcher fue bloqueado por la ventilación y retrocedió.");
            reset(); // Volver a inactivo
            return;
        }

        // ¿Se acabó el tiempo?
        if (stateTimer >= 5.0f) {
            game.triggerJumpscare(getKillerName());
        }
    }

    @Override
    protected String getKillerName() { return "Watcher"; }
}