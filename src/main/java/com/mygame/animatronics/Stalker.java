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
            "0", "1", "2", "3", "Door", "Jumpscare"
        );
    }

    @Override
    protected String getStartPosition() {
        return "0";
    }

    @Override
    protected String getDoorSide() {
        return "leftDoor";
    }

    @Override
    protected void playLaughSound() {
        // Stalker no se ríe
    }

    @Override
    protected int getRandomMax() {
        return 22;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        System.out.println("⚠️ Stalker está atacando!");
        isActiveAttackState = true;
        stateTimer = 0;
    }

    @Override
    protected void handleAttackState(GameManager game) {
        if (stateTimer >= 1.5f) {
            game.triggerJumpscare("Stalker");
        }
    }
}