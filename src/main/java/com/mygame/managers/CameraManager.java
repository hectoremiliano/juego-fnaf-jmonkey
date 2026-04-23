package com.mygame.managers;

public class CameraManager {

    private boolean active = false;
    private int currentCameraId = 1; 

    public void update(float tpf) {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCurrentCameraId() {
        return currentCameraId;
    }

    public void setCamera(int id) {
        this.currentCameraId = id;
        System.out.println("Cámara actual: " + id);
    }
}