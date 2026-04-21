package com.mygame.managers;

public class CameraManager {

    private boolean active = false;
    private int currentCameraId = 1; // 1 = Pasillo, 2 = Ventilación, etc.

    public void update(float tpf) {
        // Lógica de transición de cámaras
    }

    // --- MÉTODOS PARA EL MAIN ---
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