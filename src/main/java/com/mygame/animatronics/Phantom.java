package com.mygame.animatronics;

import com.mygame.managers.GameManager;
import java.util.Arrays;

public class Phantom extends Animatronic {

    private float alphaValue = 0.0f;
    private boolean hasKnocked = false;

    public Phantom() {
        super("Phantom", 1, 13.0f);
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
    public void playLaughSound() {
        System.out.println("🔊 ¡Golpe de Phantom!");
        hasKnocked = true;
    }

    @Override
    protected boolean shouldPlayLaugh() {
        return true;
    }

    @Override
    protected void initiateAttackState(GameManager game) {
        this.stateTimer = 0;
        this.alphaValue = 0.0f;
        this.isActiveAttackState = true;
        hasKnocked = false;
        System.out.println("⚠️ Phantom ha aparecido en la oficina. ¡Se está coloreando!");
    }

    @Override
    protected void handleAttackState(GameManager game) {
        alphaValue = stateTimer / 10.0f;
        if (alphaValue > 1.0f) alphaValue = 1.0f;

        if (stateTimer >= 10.0f) {
            if (game != null) {
                game.triggerJumpscare(getName());
            }
        }
    }

    @Override
    protected void checkDoors(GameManager game) {
        String door = getDoorSide();
        
        new Thread(() -> {
            try {
                Thread.sleep(8000);
                if (game != null) {
                    if (game.isDoorOpen(door)) {
                        animatronicFailed(game);
                    } else {
                        // Llamar directamente a la lógica de reintento
                        checkDoorAgainLogic(game, door, 1);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    private void checkDoorAgainLogic(GameManager game, String door, int attempt) {
        if (attempt > 3) {
            if (game != null) {
                game.triggerJumpscare(getName());
            }
            return;
        }
        
        long delay = (attempt == 1) ? 5000 : 3000;
        
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                if (game != null) {
                    if (game.isDoorOpen(door)) {
                        animatronicFailed(game);
                    } else {
                        checkDoorAgainLogic(game, door, attempt + 1);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void reset() {
        super.reset();
        alphaValue = 0.0f;
        hasKnocked = false;
    }

    public float getAlphaValue() {
        return alphaValue;
    }
}