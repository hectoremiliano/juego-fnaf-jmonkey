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
            "0", "1", "2", "3", "4", "Door", "Jumpscare"
        );
    }

    @Override
    protected String getStartPosition() {
        return "0";
    }

    @Override
    protected String getDoorSide() {
        return "rightDoor";
    }

    @Override
    protected void playLaughSound() {
        // Runner no se ríe
    }

    @Override
    protected int getRandomMax() {
        return 22;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        System.out.println("⚠️ Runner está atacando!");
        isActiveAttackState = true;
        stateTimer = 0;
    }

    @Override
    protected void handleAttackState(GameManager game) {
        if (stateTimer >= 1.8f) {
            game.triggerJumpscare("Runner");
        }
    }
}