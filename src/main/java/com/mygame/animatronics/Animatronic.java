package com.mygame.animatronics;

import com.jme3.app.SimpleApplication;
import com.jme3.ui.Picture;
import com.mygame.managers.GameManager;
import com.jme3.math.FastMath;
import java.util.*;

public abstract class Animatronic {
    protected int aggressionLevel;
    protected String currentPosition;
    protected float stateTimer = 0;
    protected float moveCooldown = 0;
    protected boolean isAtDoor = false;
    protected boolean isActiveAttackState = false;
    protected Picture visualSprite;
    protected Iterator<String> positionIterator;
    protected List<String> path;
    protected String name;
    
    // Tiempos de movimiento (en segundos)
    protected float moveInterval = 0;
    protected float moveTimer = 0;

    public Animatronic(String name, int aggressionLevel, float moveInterval) {
        this.name = name;
        this.aggressionLevel = aggressionLevel;
        this.moveInterval = moveInterval;
        this.currentPosition = getStartPosition();
        initializePath();
        createIterator();
    }

    protected abstract void initializePath();
    protected abstract String getStartPosition();
    protected abstract String getDoorSide();
    protected abstract void playLaughSound();

    protected void createIterator() {
        positionIterator = new Iterator<String>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return true;
            }
            
            @Override
            public String next() {
                String value = path.get(index);
                index = (index + 1) % path.size();
                return value;
            }
        };
        
        if (shouldAdvanceFirstStep()) {
            if (positionIterator.hasNext()) positionIterator.next();
        }
    }

    protected boolean shouldAdvanceFirstStep() {
        return false;
    }

    public void setupVisual(SimpleApplication app, String texturePath) {
        visualSprite = new Picture(name + "_Sprite");
        visualSprite.setImage(app.getAssetManager(), texturePath, true);
        visualSprite.setWidth(app.getContext().getSettings().getWidth());
        visualSprite.setHeight(app.getContext().getSettings().getHeight());
        visualSprite.setPosition(0, 0);
    }

    public void update(float tpf, GameManager game) {
        if (aggressionLevel == 0) return;
        if (game.isGameOver()) return;
        
        if (isActiveAttackState) {
            stateTimer += tpf;
            handleAttackState(game);
        } else {
            moveTimer += tpf;
            if (moveTimer >= moveInterval) {
                moveTimer = 0;
                attemptMovement(game);
            }
        }
        updateVisibility(game);
    }

    protected void updateVisibility(GameManager game) {
        if (visualSprite == null) return;
        
        // Convertir currentPosition a Integer si es necesario
        Integer currentCamId = null;
        try {
            if (currentPosition != null && !currentPosition.equals("Door") && !currentPosition.equals("Jumpscare")) {
                currentCamId = Integer.parseInt(currentPosition);
            }
        } catch (NumberFormatException e) {
            // Si no es número, no mostrar en cámara
        }
        
        boolean shouldShow = game.isPlayerLookingAtCameras() && 
                            currentCamId != null &&
                            game.getCurrentCameraID() == currentCamId;
        
        if (shouldShow) {
            if (visualSprite.getParent() == null) {
                game.getApp().getGuiNode().attachChild(visualSprite);
            }
        } else {
            if (visualSprite.getParent() != null) {
                visualSprite.removeFromParent();
            }
        }
    }

    protected void attemptMovement(GameManager game) {
        // Este método será sobrescrito por las subclases
        if (game.isAnimatronicMoving()) return;
        
        int max = getRandomMax();
        int luckyNumber = FastMath.nextRandomInt(0, max - 1);
        boolean shouldMove = luckyNumber < aggressionLevel && !isAtDoor;
        
        if (shouldMove) {
            performMove(game);
        }
    }

    protected void performMove(GameManager game) {
        game.setAnimatronicMoving(true);
        
        String newPlace = positionIterator.next();
        currentPosition = newPlace;
        isAtDoor = newPlace.equals("Door");
        
        System.out.println("🎭 " + name + " se movió a: " + newPlace);
        
        game.onAnimatronicMove(name, newPlace);
        
        if (shouldPlayLaugh() && newPlace.equals("Door")) {
            playLaughSound();
        }
        
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                game.setAnimatronicMoving(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        if (isAtDoor && !game.isBlackout()) {
            checkDoors(game);
        }
    }

    protected int getRandomMax() {
        return 30;
    }

    protected boolean shouldPlayLaugh() {
        return false;
    }

    protected void checkDoors(GameManager game) {
        String door = getDoorSide();
        
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (game.isDoorOpen(door)) {
                    animatronicFailed(game);
                } else {
                    checkDoorAgain(game, door, 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void checkDoorAgain(GameManager game, String door, int attempt) {
        if (attempt > 3) {
            game.triggerJumpscare(name);
            return;
        }
        
        long delay = (attempt == 1) ? 5000 : 3000;
        
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                if (game.isDoorOpen(door)) {
                    animatronicFailed(game);
                } else {
                    checkDoorAgain(game, door, attempt + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    protected void animatronicFailed(GameManager game) {
        game.setAnimatronicMoving(true);
        
        System.out.println("🚪 " + name + " fue detenido en la puerta");
        
        resetPosition();
        
        game.onAnimatronicFailed(name);
        
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                game.setAnimatronicMoving(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    protected void resetPosition() {
        createIterator();
        currentPosition = getStartPosition();
        isAtDoor = false;
        isActiveAttackState = false;
        stateTimer = 0;
    }

    public void reset() {
        resetPosition();
        moveTimer = 0;
        if (visualSprite != null) visualSprite.removeFromParent();
    }

    public void setMoveInterval(float interval) {
        this.moveInterval = interval;
    }

    public String getCurrentPosition() { return currentPosition; }
    public boolean isAtDoor() { return isAtDoor; }
    public void setAggressionLevel(int level) { this.aggressionLevel = Math.min(level, 20); }
    public int getAggressionLevel() { return aggressionLevel; }
    public String getName() { return name; }
    
    protected abstract void initiateAttackState(GameManager game);
    protected abstract void handleAttackState(GameManager game);
}