package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.ui.Picture;
import com.jme3.math.ColorRGBA;

import com.mygame.managers.*;
import com.mygame.animatronics.*;

public class Main extends SimpleApplication {

    GameManager gameManager = new GameManager();
    EnergyManager energyManager = new EnergyManager();
    CameraManager cameraManager = new CameraManager();

    Stalker stalker = new Stalker();
    Runner runner = new Runner();
    Phantom phantom = new Phantom();
    Watcher watcher = new Watcher();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Juego iniciado");

        // Fondo negro detrás de la imagen
        viewPort.setBackgroundColor(ColorRGBA.Black);

        // Mostrar imagen de fondo
        mostrarFondo();
    }

    private void mostrarFondo() {

        Picture fondo = new Picture("Fondo");

        // Ruta de tu imagen
        fondo.setImage(assetManager, "Textures/fondo.png", true);

        fondo.setWidth(settings.getWidth());
        fondo.setHeight(settings.getHeight());

        fondo.setPosition(0, 0);

        guiNode.attachChild(fondo);
    }

    @Override
    public void simpleUpdate(float tpf) {

        if (gameManager.isGameOver()) return;

        gameManager.update(tpf);

        boolean usandoCamaras = cameraManager.isActive();

        energyManager.update(tpf, usandoCamaras, false, false, false, false);

        stalker.update(tpf, gameManager);
        runner.update(tpf, gameManager);
        phantom.update(tpf, gameManager);
        watcher.update(tpf, gameManager);

        System.out.println("Energia: " + energyManager.getEnergia());

        if (energyManager.getEnergia() <= 0) {
            gameManager.endGame("Sin energía");
        }

        if (stalker.getPosicion() == cameraManager.getCamera()) {
            gameManager.endGame("El Stalker te atrapó");
        }
    }
}