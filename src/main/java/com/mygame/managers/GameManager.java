package com.mygame.managers;

import com.jme3.app.SimpleApplication;

public class GameManager {
    private final SimpleApplication app;
    private AnimatronicManager animatronicManager;
    private boolean gameOver = false;
    private int currentCameraID = 1;

    // Estados de defensa
    private boolean ventClosed = false;
    private boolean lightOn = false;
    private boolean lookingAtCameras = false;

    public GameManager(SimpleApplication app) {
        this.app = app;
        // IMPORTANTE: Inicializamos el manager de animatrónicos aquí
        this.animatronicManager = new AnimatronicManager(this);
    }

    // --- ESTOS MÉTODOS CORRIGEN TUS 3 ERRORES ---

    // 1. Corrige los errores de "getAnimatronicManager()"
    public AnimatronicManager getAnimatronicManager() {
        return animatronicManager;
    }

    // 2. Corrige el error de "endGameWithVictory()"
    public void endGameWithVictory() {
        this.gameOver = true;
        System.out.println("*********************************");
        System.out.println("🏆 ¡SOBREVIVISTE! SON LAS 6 AM 🏆");
        System.out.println("*********************************");
        // Aquí podrías cambiar a una pantalla de victoria
    }

    // --- RESTO DE MÉTODOS (Getters y Setters) ---

    public void triggerJumpscare(String killer) {
        if (!gameOver) {
            this.gameOver = true;
            System.err.println("💥 JUMPSCARE: " + killer + " te atrapó.");
        }
    }

    public boolean isGameOver() { return gameOver; }
    public SimpleApplication getApp() { return app; }
    public int getCurrentCameraID() { return currentCameraID; }
    public void setCurrentCameraID(int id) { this.currentCameraID = id; }
    
    public boolean isVentClosedState() { return ventClosed; }
    public boolean isDeskLightShiningOnBooth() { return lightOn; }
    public boolean isPlayerLookingAtCameras() { return lookingAtCameras; }

    public void setVentClosed(boolean s) { ventClosed = s; }
    public void setLightOn(boolean s) { lightOn = s; }
    public void setLookingAtCameras(boolean s) { lookingAtCameras = s; }
}