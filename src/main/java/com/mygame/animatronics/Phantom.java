package com.mygame.animatronics;

import com.mygame.managers.GameManager;

/**
 * Phantom aparece frente al jugador.
 * Va agarrando color lentamente. Si se hace de su color original (10s), te matará.
 */
public class Phantom extends Animatronic {

    private float alphaValue = 0.0f; // 0 = Transparente, 1 = Coloreado completo

    public Phantom() { super(0); }

    @Override
    public void reset() {
        this.stateTimer = 0;
        this.alphaValue = 0.0f;
        this.isActiveAttackState = false;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        this.stateTimer = 0;
        this.alphaValue = 0.0f;
        this.isActiveAttackState = true;
        System.out.println("⚠️ Phantom ha aparecido en la oficina. ¡Se está coloreando!");
        // Aquí podrías iniciar un efecto visual que proyecte un Quad con alpha de 0 a 1.
    }

    @Override
    protected void handleAttackState(GameManager game) {
        // Phantom no tiene mecánica de evasión activa. Su mecánica es el tiempo.
        
        // Actualizar el valor de alpha conceptual de 0 a 1 a lo largo de 10s.
        alphaValue = stateTimer / 10.0f;
        if (alphaValue > 1.0f) alphaValue = 1.0f;

        // Podrías pasar este alphaValue a tu shader o material visual.

        if (stateTimer >= 10.0f) {
            game.triggerJumpscare(getKillerName());
        }
    }

    @Override
    protected String getKillerName() { return "Phantom"; }
}