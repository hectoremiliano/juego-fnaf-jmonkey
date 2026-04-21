package com.mygame.animatronics;

import com.mygame.managers.GameManager;

/**
 * Stalker ataca desde la casita central.
 * Sube temblando. Si no lo alumbras en 5 segundos, te matará.
 */
public class Stalker extends Animatronic {

    public Stalker() { super(0); }

    @Override
    public void reset() {
        this.currentPosition = 0;
        this.stateTimer = 0;
        this.isActiveAttackState = false;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        this.currentPosition = 1; // Posición del mostrador
        this.stateTimer = 0;
        this.isActiveAttackState = true;
        System.out.println("⚠️ Stalker está temblando en el mostrador. ¡Alúmbralo!");
        // Aquí podrías iniciar un efecto visual de "shaking" en la casita.
    }

    @Override
    protected void handleAttackState(GameManager game) {
        // ¿Lo alumbró el jugador a tiempo?
        if (game.isDeskLightShiningOnBooth()) {
            System.out.println("🛑 Stalker fue asustado por la linterna y se escondió.");
            reset();
            return;
        }

        if (stateTimer >= 5.0f) {
            game.triggerJumpscare(getKillerName());
        }
    }

    @Override
    protected String getKillerName() { return "Stalker"; }
}