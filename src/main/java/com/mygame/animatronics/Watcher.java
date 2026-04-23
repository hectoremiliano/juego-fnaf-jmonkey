package com.mygame.animatronics;

import com.mygame.managers.GameManager;
import java.util.Arrays;

public class Watcher extends Animatronic {
    
    public Watcher() {
        super("Watcher", 1, 10.0f);
    }

    @Override
    protected void initializePath() {
        path = Arrays.asList(
            "0", "1", "2", "3", "4",
            "5", "6", "7", "Door", "Jumpscare"
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
    public void playLaughSound() {  // Asegúrate que sea PUBLIC
        System.out.println("🔊 Risas de Watcher!");
    }

    @Override
    protected boolean shouldAdvanceFirstStep() {
        return true;
    }

    @Override
    protected boolean shouldPlayLaugh() {
        return true;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        System.out.println("⚠️ Watcher está atacando!");
        isActiveAttackState = true;
        stateTimer = 0;
    }

    @Override
    protected void handleAttackState(GameManager game) {
        if (stateTimer >= 2.0f) {
            game.triggerJumpscare("Watcher");
        }
    }
}