package com.mygame.animatronics;

import com.mygame.managers.GameManager;
import java.util.Arrays;

public class Stalker extends Animatronic {

    public Stalker() {
        super("Stalker", 1, 5.0f);
    }

    @Override
    protected void initializePath() {

        path = Arrays.asList(
            "2",
            "1",
            "2",
            "1",
            "Door",
            "Jumpscare"
        );
    }

    @Override
    protected String getStartPosition() {
        return "2";
    }

    @Override
    protected String getDoorSide() {
        return "leftDoor";
    }

    @Override
    public void playLaughSound() {
        System.out.println("🔊 Sonido de Stalker!");
    }

    @Override
    protected void initiateAttackState(GameManager game) {

        System.out.println("⚠️ Stalker está atacando!");

        isActiveAttackState = true;

        stateTimer = 0;
    }

    @Override
    protected void handleAttackState(GameManager game) {

        if (stateTimer >= 2.0f) {

            game.triggerJumpscare("Stalker");
        }
    }
}