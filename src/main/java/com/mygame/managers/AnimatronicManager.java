// 1. CORRECCIÓN CRÍTICA: Cambiamos el package para que coincida con la carpeta managers
package com.mygame.managers; 

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.mygame.managers.GameManager;
import com.mygame.animatronics.*; // 2. IMPORTANTE: Importamos los personajes desde su carpeta
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lista de animatrónicos y sus niveles de agresión.
 */
public class AnimatronicManager extends AbstractAppState {

    private final GameManager game;
    private final List<Animatronic> animatronics;
    private boolean isNightActive = false;

    private Watcher watcher;
    private Stalker stalker;
    private Runner runner;
    private Phantom phantom;

    public AnimatronicManager(GameManager game) {
        this.game = game;
        this.animatronics = new ArrayList<>();
        initializeAnimatronics();
    }

    private void initializeAnimatronics() {
        // Asegúrate de que estas clases estén en el paquete com.mygame.animatronics
        watcher = new Watcher();
        stalker = new Stalker();
        runner = new Runner();
        phantom = new Phantom();

        animatronics.add(watcher);
        animatronics.add(stalker);
        animatronics.add(runner);
        animatronics.add(phantom);
    }

    @Override
    public void update(float tpf) {
        if (!isNightActive || game.isGameOver()) return;

        for (Animatronic animatronic : animatronics) {
            animatronic.update(tpf, game);
        }
    }

    public void setAgressionForNight(int nightNum) {
        isNightActive = true;
        
        switch (nightNum) {
            case 1:
                setAgressionLevels(20, 20, 20, 20);
                break;
            case 2:
                setAgressionLevels(20, 20, 20, 20);
                break;
            case 3:
                setAgressionLevels(20, 20, 20, 20);
                break;
            case 4:
                setAgressionLevels(20, 20, 20, 20);
                break;
            case 5:
                setAgressionLevels(20, 20, 20, 20);
                break;
            default:
                setAgressionLevels(20, 20, 20, 20);
                break;
        }
        
        for (Animatronic a : animatronics) a.reset();
    }

    private void setAgressionLevels(int w, int s, int r, int p) {
        watcher.setAggressionLevel(w);
        stalker.setAggressionLevel(s);
        runner.setAggressionLevel(r);
        phantom.setAggressionLevel(p);
    }

    public void increaseAgressionAtHourChange(int nightNum, int newHour) {
        System.out.println("📊 Aumentando agresión a las " + newHour + " AM.");
        
        watcher.setAggressionLevel(Math.min(watcher.getAggressionLevel() + 1, 20));
        stalker.setAggressionLevel(Math.min(stalker.getAggressionLevel() + 1, 20));
        runner.setAggressionLevel(Math.min(runner.getAggressionLevel() + 2, 20));
        phantom.setAggressionLevel(Math.min(phantom.getAggressionLevel() + 2, 20));
    }
}