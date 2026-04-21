package com.mygame.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 * Gestiona el reloj del juego y la progresión de la noche.
 */
public class TimeManager extends AbstractAppState {

    private final GameManager game;
    private int currentNight;
    private int currentHour;
    private float timeInHour; // Tiempo real transcurrido en la hora actual
    private boolean isNightActive;

    // Configuración del tiempo: Segundos reales por hora de juego.
    // Una noche completa dura (6 horas) * (60 segundos/hora) = 360 segundos (6 minutos).
    private final float REAL_SECONDS_PER_GAME_HOUR = 60.0f;

    public TimeManager(GameManager game) {
        this.game = game;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        // La inicialización se maneja cuando GameManager arranca una noche.
    }

    @Override
    public void update(float tpf) {
        if (!isNightActive) return;

        timeInHour += tpf;

        // ¿Se completó la hora?
        if (timeInHour >= REAL_SECONDS_PER_GAME_HOUR) {
            timeInHour = 0;
            advanceHour();
        }
    }

    /**
     * Arranca una nueva noche desde las 12 AM (medianoche).
     */
    public void startNight(int nightNum) {
        this.currentNight = nightNum;
        this.currentHour = 0; // 0 significa 12 AM
        this.timeInHour = 0;
        this.isNightActive = true;
        System.out.println("🌙 Noche " + nightNum + " iniciada. Son las 12 AM.");
        game.getAnimatronicManager().setAgressionForNight(nightNum);
    }

    private void advanceHour() {
        currentHour++;
        if (currentHour >= 6) { // Fin de la noche a las 6 AM
            winNight();
        } else {
            System.out.println("⏰ Son las " + getFormattedHour());
            // Aumentar la agresión al cambiar la hora
            game.getAnimatronicManager().increaseAgressionAtHourChange(currentNight, currentHour);
        }
    }

    private void winNight() {
        isNightActive = false;
        System.out.println("🎉 🎉 🎉 ¡6 AM! ¡SOBREVIVISTE LA NOCHE " + currentNight + "!");
        game.endGameWithVictory();
    }

    /**
     * @return La hora formateada para la UI (ej. "1 AM", "12 AM").
     */
    public String getFormattedHour() {
        if (currentHour == 0) return "12 AM";
        return currentHour + " AM";
    }

    public int getCurrentNight() { return currentNight; }
    public int getCurrentHour() { return currentHour; }
}