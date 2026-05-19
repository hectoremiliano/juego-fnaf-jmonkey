package com.mygame.animatronics;

import com.mygame.managers.GameManager;
import java.util.Arrays;

public class Runner extends Animatronic {

    public Runner() {
        super("Runner", 2, 7.3f);
    }

    @Override
    protected void initializePath() {

        path = Arrays.asList(
            "1",
            "2",
            "1",
            "2",
            "Door",
            "Jumpscare"
        );
    }

    @Override
    protected String getStartPosition() {
        return "1";
    }

    @Override
    protected String getDoorSide() {
        return "rightDoor";
    }

    @Override
    public void playLaughSound() {
        System.out.println("🔊 Sonido de Runner!");
    }

    @Override
    protected void initiateAttackState(GameManager game) {

        System.out.println("⚠️ Runner está atacando!");

        isActiveAttackState = true;

        stateTimer = 0;
    }

    @Override
    protected void handleAttackState(GameManager game) {

        if (stateTimer >= 2.0f) {

            game.triggerJumpscare("Runner");
        }
    }
}