package com.mygame.animatronics;

import com.mygame.managers.GameManager;

/**
 * Runner ataca desde la puerta izquierda.
 * Está parado ahí por 5 segundos. Si en ese tiempo no miras las cámaras, te matará.
 */
public class Runner extends Animatronic {

    public Runner() { super(0); }

    @Override
    public void reset() {
        this.currentPosition = 0;
        this.stateTimer = 0;
        this.isActiveAttackState = false;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        this.currentPosition = 2; // Posición de la puerta
        this.stateTimer = 0;
        this.isActiveAttackState = true;
        System.out.println("⚠️ Runner está en la puerta izquierda. ¡Revisa las cámaras!");
    }

    @Override
    protected void handleAttackState(GameManager game) {
        // La condición de derrota de Runner es terminal. Si el temporizador llega a 5s, verifica.
        if (stateTimer >= 5.0f) {
            // Al final del contador, ¿está el jugador mirando las cámaras?
            if (game.isPlayerLookingAtCameras()) {
                System.out.println("🛑 Runner se aburrió porque estabas ocupado con las cámaras y retrocedió.");
                reset(); // Fue evadido
            } else {
                game.triggerJumpscare(getKillerName());
            }
        }
    }

    @Override
    protected String getKillerName() { return "Runner"; }
}