package com.mygame.managers;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.math.ColorRGBA;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Sistema de cámaras adaptado del código React original
 * para integrarse con AnimatronicManager y GameManager
 */
public class CameraManager {
    
    private SimpleApplication app;
    private GameManager gameManager;
    private AnimatronicManager animatronicManager;
    
    // Estados de la cámara (igual que en React)
    private boolean isCameraOpen = false;
    private String currentCamera = "Stage";
    private boolean areAnimatronicsMoving = false;
    private boolean cameraButtonDisappear = false;
    
    // Elementos visuales
    private Node cameraNode;
    private Picture cameraDisplay;
    private Picture staticOverlay;
    private Picture blackOverlay;
    private boolean isAnimating = false;
    
    // Mapeo de imágenes de cámaras (como getCam en React)
    private Map<String, String> cameraImageCache;
    
    // Sonidos
    private AudioNode cameraChangeSound;
    private AudioNode animatronicsMovingSound1;
    private AudioNode animatronicsMovingSound2;
    
    private Random random;
    
    public CameraManager(SimpleApplication app, GameManager gameManager, AnimatronicManager animatronicManager) {
        this.app = app;
        this.gameManager = gameManager;
        this.animatronicManager = animatronicManager;
        this.random = new Random();
        this.cameraImageCache = new HashMap<>();
        loadSounds();
        createCameraUI();
    }
    
    private void loadSounds() {
        try {
            cameraChangeSound = new AudioNode(app.getAssetManager(), "Sounds/camera_change.wav", AudioData.DataType.Buffer);
            animatronicsMovingSound1 = new AudioNode(app.getAssetManager(), "Sounds/garble1.mp3", AudioData.DataType.Buffer);
            animatronicsMovingSound2 = new AudioNode(app.getAssetManager(), "Sounds/garble2.mp3", AudioData.DataType.Buffer);
        } catch (Exception e) {
            System.out.println("No se pudieron cargar sonidos de cámara: " + e.getMessage());
        }
    }
    
    /**
     * Método helper para establecer alpha en Pictures
     */
    private void setPictureAlpha(Picture picture, float alpha) {
        if (picture == null) return;
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Gui/Gui.j3md");
        mat.setColor("Color", new ColorRGBA(1, 1, 1, alpha));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        picture.setMaterial(mat);
    }
    
    private void createCameraUI() {
        cameraNode = new Node("CameraSystem");
        
        // Pantalla principal de la cámara
        cameraDisplay = new Picture("CameraDisplay");
        cameraDisplay.setWidth(app.getCamera().getWidth());
        cameraDisplay.setHeight(app.getCamera().getHeight());
        cameraDisplay.setPosition(0, 0);
        
        // Overlay de estática (opacidad 0.1 como en React)
        staticOverlay = new Picture("StaticOverlay");
        staticOverlay.setWidth(app.getCamera().getWidth());
        staticOverlay.setHeight(app.getCamera().getHeight());
        staticOverlay.setPosition(0, 0);
        setPictureAlpha(staticOverlay, 0.1f);
        
        // Overlay negro (cuando los animatrónicos se mueven)
        blackOverlay = new Picture("BlackOverlay");
        blackOverlay.setWidth(app.getCamera().getWidth());
        blackOverlay.setHeight(app.getCamera().getHeight());
        blackOverlay.setPosition(0, 0);
        setPictureAlpha(blackOverlay, 0.0f);
        
        cameraNode.attachChild(cameraDisplay);
        cameraNode.attachChild(staticOverlay);
        cameraNode.attachChild(blackOverlay);
        
        // Oculto por defecto
        cameraNode.setCullHint(Node.CullHint.Always);
    }
    
    /**
     * Abre la cámara (como handleCameraButton en React)
     */
    public void openCamera() {
        if (isCameraOpen || isAnimating) return;
        
        isAnimating = true;
        
        // Mostrar animación de apertura
        cameraNode.setCullHint(Node.CullHint.Never);
        
        // Disparar sonido de cambio si está disponible
        if (cameraChangeSound != null) {
            cameraChangeSound.playInstance();
        }
        
        isCameraOpen = true;
        if (gameManager != null) {
            gameManager.setLookingAtCameras(true);
        }
        
        // Actualizar la imagen de la cámara actual
        updateCameraImage();
        
        // Simular tiempo de animación (350ms como en React)
        new Thread(() -> {
            try { 
                Thread.sleep(350); 
                app.enqueue(() -> {
                    isAnimating = false;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Cierra la cámara
     */
    public void closeCamera() {
        if (!isCameraOpen || isAnimating) return;
        
        isAnimating = true;
        cameraNode.setCullHint(Node.CullHint.Always);
        isCameraOpen = false;
        if (gameManager != null) {
            gameManager.setLookingAtCameras(false);
        }
        
        new Thread(() -> {
            try { 
                Thread.sleep(100); 
                app.enqueue(() -> {
                    isAnimating = false;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Cambia de cámara (como handleCameraChange en React)
     */
    public void changeCamera(String newCamera) {
        if (!isCameraOpen) return;
        if (currentCamera.equals(newCamera)) return;
        
        // Sonido de cambio de cámara
        if (cameraChangeSound != null) {
            cameraChangeSound.playInstance();
        }
        
        currentCamera = newCamera;
        if (gameManager != null) {
            gameManager.setCurrentCameraId(currentCamera);
        }
        updateCameraImage();
    }
    
    /**
     * Actualiza la imagen de la cámara según los animatrónicos presentes
     * Simula la función getCam(result, camera, Foxy.camera) de React
     */
    private void updateCameraImage() {
        if (animatronicManager == null) return;
        
        // Obtener posiciones de los animatrónicos
        String watcherPos = animatronicManager.getCurrentPosition("Watcher");
        String stalkerPos = animatronicManager.getCurrentPosition("Stalker");
        String runnerPos = animatronicManager.getCurrentPosition("Runner");
        String phantomPos = animatronicManager.getCurrentPosition("Phantom");
        
        // Construir el sufijo como en React: "_b" para Bonnie, "_c" para Chica, "_f" para Freddy
        // En tu caso: Watcher=Freddy, Stalker=Bonnie, Runner=Chica, Phantom=Foxy
        String suffix = "";
        if (stalkerPos != null && stalkerPos.equals(currentCamera)) suffix += "_s";
        if (runnerPos != null && runnerPos.equals(currentCamera)) suffix += "_r";
        if (watcherPos != null && watcherPos.equals(currentCamera)) suffix += "_w";
        
        // Phantom (Foxy) tiene un manejo especial en el código original
        String foxyCamera = (phantomPos != null && phantomPos.equals(currentCamera)) ? currentCamera : "";
        
        // Obtener la imagen correcta
        String imagePath = getCameraImage(currentCamera, suffix, foxyCamera);
        
        // Cargar la textura
        loadCameraTexture(imagePath);
    }
    
    /**
     * Simula la función getCam de React
     */
    private String getCameraImage(String camera, String suffix, String foxyCamera) {
        // Mapeo base de imágenes por cámara
        Map<String, String> baseImages = new HashMap<>();
        baseImages.put("Stage", "Textures/Cameras/stage");
        baseImages.put("Dinning Area", "Textures/Cameras/dinning");
        baseImages.put("Restrooms", "Textures/Cameras/restrooms");
        baseImages.put("Hall", "Textures/Cameras/hall");
        baseImages.put("Kitchen", "Textures/Cameras/kitchen");
        baseImages.put("East Hall", "Textures/Cameras/east_hall");
        baseImages.put("East Corner", "Textures/Cameras/east_corner");
        baseImages.put("Office", "Textures/Cameras/office");
        baseImages.put("West Hall", "Textures/Cameras/west_hall");
        baseImages.put("West Corner", "Textures/Cameras/west_corner");
        baseImages.put("Pirate Cove", "Textures/Cameras/pirate_cove");
        
        String base = baseImages.getOrDefault(camera, "Textures/Cameras/stage");
        
        // Manejo especial para Foxy (Phantom)
        if (!foxyCamera.isEmpty()) {
            return base + "_f.jpg";
        }
        
        // Combinaciones de sufijos
        if (suffix.contains("_w") && suffix.contains("_s")) {
            return base + "_ws.jpg";
        } else if (suffix.contains("_w") && suffix.contains("_r")) {
            return base + "_wr.jpg";
        } else if (suffix.contains("_s") && suffix.contains("_r")) {
            return base + "_sr.jpg";
        } else if (suffix.contains("_w")) {
            return base + "_w.jpg";
        } else if (suffix.contains("_s")) {
            return base + "_s.jpg";
        } else if (suffix.contains("_r")) {
            return base + "_r.jpg";
        }
        
        return base + ".jpg";
    }
    
    /**
     * Carga una textura en el display de la cámara
     */
    private void loadCameraTexture(String path) {
        try {
            Texture tex = app.getAssetManager().loadTexture(path);
            // Convertir a Texture2D explícitamente
            if (tex instanceof Texture2D) {
                cameraDisplay.setTexture(app.getAssetManager(), (Texture2D) tex, true);
            } else {
                // Si no es Texture2D, crear un material como fallback
                Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.DarkGray);
                cameraDisplay.setMaterial(mat);
            }
        } catch (Exception e) {
            // Si no hay textura, mostrar color gris
            Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.DarkGray);
            cameraDisplay.setMaterial(mat);
            System.out.println("No se pudo cargar textura: " + path);
        }
    }
    
    /**
     * Activa el efecto de "animatronics moving"
     * Se llama cuando areAnimatronicsMoving cambia a true
     */
    public void onAnimatronicsMoving() {
        if (!isCameraOpen) return;
        
        // Pantalla negra mientras se mueven
        setPictureAlpha(blackOverlay, 1.0f);
        
        // Sonido aleatorio como en React
        int musicNumber = random.nextInt(3);
        AudioNode sound = (musicNumber == 1 || musicNumber == 2) ? 
            animatronicsMovingSound1 : animatronicsMovingSound2;
        
        if (sound != null) {
            sound.playInstance();
        }
        
        areAnimatronicsMoving = true;
        
        // Después de 1.5 segundos (como setTimeout en React), ocultar el efecto
        new Thread(() -> {
            try { 
                Thread.sleep(1500); 
                app.enqueue(() -> {
                    setPictureAlpha(blackOverlay, 0.0f);
                    areAnimatronicsMoving = false;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Actualiza cuando un animatrónico se mueve
     * (debe llamarse desde AnimatronicManager)
     */
    public void onAnimatronicMove(String name, String newPlace) {
        if (isCameraOpen && newPlace != null && newPlace.equals(currentCamera)) {
            // Actualizar la imagen si el animatrónico está en la cámara actual
            updateCameraImage();
        }
    }
    
    /**
     * Alterna entre abrir y cerrar la cámara
     */
    public void toggleCamera() {
        if (isCameraOpen) {
            closeCamera();
        } else {
            openCamera();
        }
    }
    
    /**
     * Actualiza la estática (llamar en update del AppState)
     */
    public void updateStatic(float tpf) {
        if (isCameraOpen && staticOverlay != null) {
            // Parpadeo aleatorio de estática como en React (opacidad 0.05-0.15)
            float alpha = 0.05f + (random.nextFloat() * 0.1f);
            setPictureAlpha(staticOverlay, alpha);
        }
    }
    
    // Getters
    public boolean isCameraOpen() { return isCameraOpen; }
    public String getCurrentCamera() { return currentCamera; }
    public boolean areAnimatronicsMoving() { return areAnimatronicsMoving; }
    public Node getCameraNode() { return cameraNode; }
    
    // Setters
    public void setCameraButtonDisappear(boolean disappear) { 
        this.cameraButtonDisappear = disappear; 
    }
    
    /**
     * Limpiar recursos
     */
    public void cleanup() {
        if (cameraNode != null) {
            cameraNode.removeFromParent();
        }
    }
}