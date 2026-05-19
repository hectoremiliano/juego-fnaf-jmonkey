package com.mygame.managers;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;

public class CameraButton implements ActionListener {
    
    private SimpleApplication app;
    private CameraManager cameraManager;
    private Node uiNode;
    private Picture buttonImage;
    private boolean isEnabled = true;
    private boolean isVisible = true;
    private AudioNode openCameraSound;
    private long lastPressTime = 0;
    private static final long COOLDOWN_MS = 700;
    
    public CameraButton(SimpleApplication app, CameraManager cameraManager, Node uiNode) {
        this.app = app;
        this.cameraManager = cameraManager;
        this.uiNode = uiNode;
        loadSound();
        createButton();
        registerInput();
    }
    
    private void loadSound() {
        try {
            openCameraSound = new AudioNode(app.getAssetManager(), "Sounds/open_camera.wav", AudioData.DataType.Buffer);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar el sonido de apertura de cámara: " + e.getMessage());
        }
    }
    
    private void createButton() {
        buttonImage = new Picture("CameraButton");
        
        try {
            Texture2D tex = (Texture2D) app.getAssetManager().loadTexture("Textures/camera_button.png");
            buttonImage.setTexture(app.getAssetManager(), tex, true);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar camera_button.png, creando botón por defecto");
            Texture2D tex = createDefaultButtonTexture();
            buttonImage.setTexture(app.getAssetManager(), tex, true);
        }
        
        buttonImage.setWidth(80);
        buttonImage.setHeight(80);
        buttonImage.setPosition(app.getCamera().getWidth() - 100, 50);
        buttonImage.setUserData("originalX", app.getCamera().getWidth() - 100);
        buttonImage.setUserData("originalY", 50);
        buttonImage.setCullHint(Node.CullHint.Always);
        uiNode.attachChild(buttonImage);
    }
    
    private Texture2D createDefaultButtonTexture() {
        com.jme3.texture.Image img = new com.jme3.texture.Image(
            com.jme3.texture.Image.Format.RGBA8, 
            64, 64, 
            BufferUtils.createByteBuffer(64 * 64 * 4)
        );
        
        ByteBuffer data = img.getData(0);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int idx = (y * 64 + x) * 4;
                if (x < 5 || x > 58 || y < 5 || y > 58) {
                    data.put(idx, (byte) 255);
                    data.put(idx+1, (byte) 255);
                    data.put(idx+2, (byte) 255);
                    data.put(idx+3, (byte) 255);
                } else {
                    data.put(idx, (byte) 100);
                    data.put(idx+1, (byte) 100);
                    data.put(idx+2, (byte) 100);
                    data.put(idx+3, (byte) 255);
                }
            }
        }
        
        Texture2D tex = new Texture2D(img);
        tex.setMinFilter(Texture2D.MinFilter.BilinearNoMipMaps);
        tex.setMagFilter(Texture2D.MagFilter.Bilinear);
        return tex;
    }
    
    private void registerInput() {
        InputManager inputManager = app.getInputManager();
        inputManager.addMapping("CameraButtonClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "CameraButtonClick");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isVisible || !isEnabled || !isPressed) return;
        
        Vector2f cursorPos = app.getInputManager().getCursorPosition();
        float mouseX = cursorPos.getX();
        float mouseY = app.getCamera().getHeight() - cursorPos.getY();
        
        float buttonX = (float) buttonImage.getUserData("originalX");
        float buttonY = (float) buttonImage.getUserData("originalY");
        
        if (mouseX >= buttonX && mouseX <= buttonX + buttonImage.getWidth() &&
            mouseY >= buttonY && mouseY <= buttonY + buttonImage.getHeight()) {
            handleCameraClick();
        }
    }
    
    private void handleCameraClick() {
        if (!isEnabled) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPressTime < COOLDOWN_MS) return;
        
        isEnabled = false;
        lastPressTime = currentTime;
        
        if (openCameraSound != null) {
            openCameraSound.playInstance();
        }
        
        if (cameraManager != null) {
            cameraManager.openCamera();
        }
        
        new Thread(() -> {
            try {
                Thread.sleep(COOLDOWN_MS);
                isEnabled = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    public void show() {
        this.isVisible = true;
        buttonImage.setCullHint(Node.CullHint.Never);
    }
    
    public void hide() {
        this.isVisible = false;
        buttonImage.setCullHint(Node.CullHint.Always);
    }
    
    public void setDisappear(boolean disappear) {
        if (disappear) hide();
        else show();
    }
    
    public void onResize(int width, int height) {
        float newX = width - 100;
        float newY = 50;
        buttonImage.setPosition(newX, newY);
        buttonImage.setUserData("originalX", newX);
        buttonImage.setUserData("originalY", newY);
    }
    
    public void cleanup() {
        if (buttonImage != null) buttonImage.removeFromParent();
        if (app != null && app.getInputManager() != null) {
            app.getInputManager().deleteMapping("CameraButtonClick");
            app.getInputManager().removeListener(this);
        }
    }
}