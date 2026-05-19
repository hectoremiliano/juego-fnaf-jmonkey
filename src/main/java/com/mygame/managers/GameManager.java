package com.mygame.managers;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private TimeManager timeManager;
    private AnimatronicManager animatronicManager;
    
    private boolean isGameOver = false;
    private boolean isBlackout = false;
    private boolean isAnimatronicMoving = false;
    private boolean isLookingAtCameras = false;
    private int currentCameraID = -1;
    private String currentCameraId = "Stage"; // Nueva variable para el ID de cámara como String
    
    private boolean isLeftDoorOpen = true;
    private boolean isRightDoorOpen = true;

    private boolean isLightOn = true;
    private boolean isVentClosed = false;
    private boolean isDeskLightShiningOnBooth = false;
    
    public GameManager(SimpleApplication app) {
        this.app = app;
        this.stateManager = app.getStateManager();
        initManagers();
    }
    
    private void initManagers() {
        this.timeManager = new TimeManager(this);
        this.animatronicManager = new AnimatronicManager(this);
        
        stateManager.attach(timeManager);
        stateManager.attach(animatronicManager);
    }
    
    public boolean isAnimatronicMoving() { 
        return isAnimatronicMoving; 
    }
    
    public void setAnimatronicMoving(boolean moving) { 
        this.isAnimatronicMoving = moving; 
    }
    
    public void onAnimatronicMove(String name, String position) {
        System.out.println("📍 " + name + " se movió a: " + position);
    }
    
    public void onAnimatronicFailed(String name) {
        System.out.println("✅ " + name + " fue detenido exitosamente en la puerta");
    }
    
    public boolean isDoorOpen(String door) {
        if (door.equals("leftDoor")) {
            return isLeftDoorOpen;
        } else if (door.equals("rightDoor")) {
            return isRightDoorOpen;
        }
        return true;
    }
    
    public void setLeftDoorOpen(boolean open) {
        this.isLeftDoorOpen = open;
        System.out.println("🚪 Puerta izquierda " + (open ? "abierta" : "cerrada"));
    }
    
    public void setRightDoorOpen(boolean open) {
        this.isRightDoorOpen = open;
        System.out.println("🚪 Puerta derecha " + (open ? "abierta" : "cerrada"));
    }
    
    public boolean isPlayerLookingAtCameras() {
        return isLookingAtCameras;
    }
    
    public void setLookingAtCameras(boolean looking) {
        this.isLookingAtCameras = looking;
    }
    
    public int getCurrentCameraID() {
        return currentCameraID;
    }
    
    public void setCurrentCameraID(int cameraID) {
        this.currentCameraID = cameraID;
        System.out.println("📷 Cámara cambiada a ID: " + cameraID);
    }
    
    /**
     * Establece el ID de la cámara actual como String
     * @param cameraId Nombre/ID de la cámara (ej: "Stage", "Hall", "Office")
     */
    public void setCurrentCameraId(String cameraId) {
        this.currentCameraId = cameraId;
        System.out.println("📷 Cámara cambiada a: " + cameraId);
        
        // También mantener compatibilidad con el ID numérico si es necesario
        // Convertir el String a ID numérico si tienes un mapeo
        this.currentCameraID = mapCameraStringToId(cameraId);
    }
    
    /**
     * Obtiene el ID de la cámara actual como String
     * @return Nombre/ID de la cámara actual
     */
    public String getCurrentCameraId() {
        return currentCameraId;
    }
    
    /**
     * Mapea el nombre de la cámara a un ID numérico
     * @param cameraString Nombre de la cámara
     * @return ID numérico de la cámara
     */
    private int mapCameraStringToId(String cameraString) {
        Map<String, Integer> cameraMap = new HashMap<>();
        cameraMap.put("Stage", 0);
        cameraMap.put("Dinning Area", 1);
        cameraMap.put("Restrooms", 2);
        cameraMap.put("Hall", 3);
        cameraMap.put("Kitchen", 4);
        cameraMap.put("East Hall", 5);
        cameraMap.put("East Corner", 6);
        cameraMap.put("Office", 7);
        cameraMap.put("West Hall", 8);
        cameraMap.put("West Corner", 9);
        cameraMap.put("Pirate Cove", 10);
        
        return cameraMap.getOrDefault(cameraString, -1);
    }
    
    public boolean isLightOn() {
        return isLightOn;
    }
    
    public void setLightOn(boolean on) {
        this.isLightOn = on;
        System.out.println("💡 Luz " + (on ? "encendida" : "apagada"));
    }
    
    public boolean isVentClosed() {
        return isVentClosed;
    }
    
    public void setVentClosed(boolean closed) {
        this.isVentClosed = closed;
        System.out.println("🌬️ Ventilación " + (closed ? "cerrada" : "abierta"));
    }
    
    public boolean isDeskLightShiningOnBooth() {
        return isDeskLightShiningOnBooth;
    }
    
    public void setDeskLightShiningOnBooth(boolean shining) {
        this.isDeskLightShiningOnBooth = shining;
    }
    
    public boolean isVentClosedState() {
        return isVentClosed;
    }
    
    public boolean isGameOver() { 
        return isGameOver; 
    }
    
    public void setGameOver(boolean gameOver) { 
        this.isGameOver = gameOver;
        if (gameOver) {
            System.out.println("💀 GAME OVER 💀");
            if (animatronicManager != null) {
                animatronicManager.setGameOver(true);
            }
            if (timeManager != null) {
                timeManager.stopNight();
            }
        }
    }
    
    public boolean isBlackout() { 
        return isBlackout; 
    }
    
    public void setBlackout(boolean blackout) { 
        this.isBlackout = blackout;
        if (animatronicManager != null) {
            animatronicManager.setBlackout(blackout);
        }
        System.out.println("💡 Blackout: " + (blackout ? "ACTIVADO" : "DESACTIVADO"));
    }
    
    public void startNight(int nightNumber) {
        isGameOver = false;
        isBlackout = false;
        isAnimatronicMoving = false;
        
        if (timeManager != null) {
            timeManager.startNight(nightNumber);
        }
    }
    
    public void endGameWithVictory() {
        isGameOver = true;
        System.out.println("🏆 ¡VICTORIA! Has sobrevivido la noche");
    }
    
    public void triggerJumpscare(String animatronicName) {
        if (isGameOver) return;
        
        isGameOver = true;
        System.out.println("💀 ¡" + animatronicName + " te atrapó! GAME OVER!");
        
        if (timeManager != null) {
            timeManager.stopNight();
        }
        
        if (animatronicManager != null) {
            animatronicManager.setGameOver(true);
        }
    }
    
    public void onHourChanged(int newHour) {
        System.out.println("🕐 Ha llegado la hora: " + newHour + " AM");
        
        switch (newHour) {
            case 2:
                System.out.println("🎭 Los animatrónicos se vuelven más agresivos...");
                break;
            case 3:
                System.out.println("👻 La hora más oscura...");
                break;
            case 4:
                System.out.println("⚡ La tensión aumenta...");
                break;
            case 5:
                System.out.println("🌅 ¡Última hora! ¡Resiste!");
                break;
        }
    }
    
    public SimpleApplication getApp() { 
        return app; 
    }
    
    public TimeManager getTimeManager() { 
        return timeManager; 
    }
    
    public AnimatronicManager getAnimatronicManager() { 
        return animatronicManager; 
    }
    
    public void resetGame() {
        isGameOver = false;
        isBlackout = false;
        isAnimatronicMoving = false;
        isLeftDoorOpen = true;
        isRightDoorOpen = true;
        currentCameraID = -1;
        currentCameraId = "Stage"; // Resetear el ID de cámara como String
        isLookingAtCameras = false;
        isLightOn = true;
        isVentClosed = false;
        isDeskLightShiningOnBooth = false;
        
        if (timeManager != null) {
            timeManager.reset();
        }
        
        if (animatronicManager != null) {
            animatronicManager.resetAllAnimatronics();
        }
    }
}