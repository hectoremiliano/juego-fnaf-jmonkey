package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import com.mygame.animatronics.*;
import com.mygame.managers.*;

public class Main extends SimpleApplication {

    private GameManager gameManager;
    private EnergyManager energyManager;
    private TimeManager timeManager;
    private AnimatronicManager animatronicManager;

    private Watcher watcher;
    private Stalker stalker;
    private Runner runner;
    private Phantom phantom;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Five Nights at My Game - Hector Edition");
        settings.setResolution(1280, 720);
        app.setSettings(settings);
        app.setShowSettings(false); 
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        viewPort.setBackgroundColor(ColorRGBA.Black);

        gameManager = new GameManager(this);
        energyManager = new EnergyManager();
        timeManager = new TimeManager(gameManager);
        animatronicManager = new AnimatronicManager(gameManager);

        watcher = new Watcher();
        stalker = new Stalker();
        runner = new Runner();
        phantom = new Phantom();

        mostrarFondoOficina();
        
        try {
            watcher.setupVisual(this, "Textures/watcher.png");
            stalker.setupVisual(this, "Textures/stalker.png");
            runner.setupVisual(this, "Textures/runner.png");
            phantom.setupVisual(this, "Textures/phantom.png");
        } catch (Exception e) {
            System.err.println("¡ERROR! No se encontró una imagen en assets/Textures: " + e.getMessage());
        }

        stateManager.attach(timeManager);
        stateManager.attach(animatronicManager);

        initKeys();

        timeManager.startNight(1);
        System.out.println("--- NOCHE 1 INICIADA ---");
    }

    private void initKeys() {
 
        inputManager.addMapping("ToggleCam", new KeyTrigger(KeyInput.KEY_SPACE));
  
        inputManager.addMapping("Cam1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Cam2", new KeyTrigger(KeyInput.KEY_2));

        inputManager.addMapping("Light", new KeyTrigger(KeyInput.KEY_L));

        inputManager.addListener(actionListener, "ToggleCam", "Cam1", "Cam2", "Light");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed && !gameManager.isGameOver()) {
                if (name.equals("ToggleCam")) {
                    gameManager.setLookingAtCameras(!gameManager.isPlayerLookingAtCameras());
                    System.out.println("\nMonitor: " + (gameManager.isPlayerLookingAtCameras() ? "ABIERTO" : "CERRADO"));
                }
                if (name.equals("Cam1")) gameManager.setCurrentCameraID(1);
                if (name.equals("Cam2")) gameManager.setCurrentCameraID(2);
                if (name.equals("Light")) gameManager.setLightOn(!gameManager.isDeskLightShiningOnBooth());
            }
        }
    };

    private void mostrarFondoOficina() {
        Picture fondo = new Picture("FondoOficina");
        fondo.setImage(assetManager, "Textures/fondo.png", true);
        fondo.setWidth(settings.getWidth());
        fondo.setHeight(settings.getHeight());
        fondo.setPosition(0, 0);
        guiNode.attachChild(fondo);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (gameManager.isGameOver()) return;

        energyManager.update(tpf, 
            gameManager.isPlayerLookingAtCameras(), 
            gameManager.isVentClosedState(), 
            gameManager.isDeskLightShiningOnBooth(), 
            false, false);

        if (energyManager.getEnergia() <= 0) {
            gameManager.triggerJumpscare("Blackout (Sin Energía)");
            guiNode.detachAllChildren(); 
        }
        watcher.update(tpf, gameManager);
        stalker.update(tpf, gameManager);
        runner.update(tpf, gameManager);
        phantom.update(tpf, gameManager);
    }
}