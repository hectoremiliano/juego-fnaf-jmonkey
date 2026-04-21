package com.mygame.managers;

public class CameraManager {

    private int currentCamera = 0;
    private boolean active = false;

    public void toggleCamera() {
        active = !active;
    }

    public void changeCamera(int cam) {
        currentCamera = cam;
    }

    public int getCamera() {
        return currentCamera;
    }

    public boolean isActive() {
        return active;
    }
}