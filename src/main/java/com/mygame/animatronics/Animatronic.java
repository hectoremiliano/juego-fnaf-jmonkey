package com.mygame.animatronics;

import com.jme3.app.SimpleApplication;
import com.jme3.ui.Picture;
import com.mygame.managers.GameManager;
import com.jme3.math.FastMath;

public abstract class Animatronic {
    protected int aggressionLevel;
    protected int currentPosition = 0;
    protected float stateTimer = 0;
    protected float patienceTimer = 0;
    protected boolean isActiveAttackState = false;
    protected Picture visualSprite;

    public Animatronic(int aggressionLevel) {
        this.aggressionLevel = aggressionLevel;
    }

    public void setupVisual(SimpleApplication app, String texturePath) {
        visualSprite = new Picture(getKillerName() + "_Sprite");
        visualSprite.setImage(app.getAssetManager(), texturePath, true);
        // Ajustar tamaño estándar (puedes cambiarlo luego)
        visualSprite.setWidth(app.getContext().getSettings().getWidth());
        visualSprite.setHeight(app.getContext().getSettings().getHeight());
        visualSprite.setPosition(0, 0);
    }

    public void update(float tpf, GameManager game) {
        if (aggressionLevel == 0) return;

        if (isActiveAttackState) {
            stateTimer += tpf;
            handleAttackState(game);
        } else {
            patienceTimer += tpf;
            if (patienceTimer >= 3.5f) {
                patienceTimer = 0;
                attemptMovement(game);
            }
        }
        updateVisibility(game);
    }

    protected void updateVisibility(GameManager game) {
        if (visualSprite == null) return;
        
        // Estilo FNAF: Solo aparece si miras la cámara donde está el bicho
        if (game.isPlayerLookingAtCameras() && game.getCurrentCameraID() == this.currentPosition) {
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
        int luck = FastMath.nextRandomInt(1, 20);
        if (luck <= aggressionLevel) {
            currentPosition++;
            System.out.println(getKillerName() + " se movió a: " + currentPosition);
            if (currentPosition >= 5) initiateAttackState(game);
        }
    }

    public void reset() {
        currentPosition = 0;
        isActiveAttackState = false;
        stateTimer = 0;
        if (visualSprite != null) visualSprite.removeFromParent();
    }

    public int getPosicion() { return currentPosition; }
    public void setAggressionLevel(int level) { this.aggressionLevel = level; }
    public int getAggressionLevel() { return aggressionLevel; }
    
    protected abstract void initiateAttackState(GameManager game);
    protected abstract void handleAttackState(GameManager game);
    protected abstract String getKillerName();
}