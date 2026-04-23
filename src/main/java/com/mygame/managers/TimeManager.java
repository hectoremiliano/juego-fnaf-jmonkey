package com.mygame.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gestiona el reloj del juego y la progresión de la noche.
 * Adaptado del código React original con la lógica de tiempo.
 */
public class TimeManager extends AbstractAppState {

    private final GameManager game;
    private int currentNight;
    private int currentHour;
    private float timeInHour;
    private boolean isNightActive;
    private boolean isGameWon = false;
    private boolean isGameOver = false;

    // Constantes de tiempo (en segundos)
    private final float REAL_SECONDS_PER_GAME_HOUR = 60.0f;
    
    // Timer para eventos de hora
    private ScheduledExecutorService hourChecker;
    
    // Lista de eventos especiales por hora
    private final boolean[] hourEvents = new boolean[6];

    public TimeManager(GameManager game) {
        this.game = game;
        initializeHourEvents();
    }

    private void initializeHourEvents() {
        // Configurar eventos especiales por hora
        hourEvents[0] = false; // 12 AM - inicio
        hourEvents[1] = false; // 1 AM
        hourEvents[2] = true;  // 2 AM - primer aumento de agresión
        hourEvents[3] = false; // 3 AM
        hourEvents[4] = true;  // 4 AM - segundo aumento de agresión
        hourEvents[5] = true;  // 5 AM - aumento final de agresión
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if (!isNightActive) return;
        if (game != null && game.isGameOver()) {
            if (!isGameOver) {
                isGameOver = true;
                stopNight();
            }
            return;
        }
        if (isGameWon) return;

        timeInHour += tpf;

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
        this.currentHour = 0;
        this.timeInHour = 0;
        this.isNightActive = true;
        this.isGameWon = false;
        this.isGameOver = false;
        
        System.out.println("========================================");
        System.out.println("🌙 NOCHE " + nightNum + " INICIADA");
        System.out.println("🕛 Son las 12 AM");
        System.out.println("========================================");
        
        // Iniciar el AnimatronicManager
        if (game != null && game.getAnimatronicManager() != null) {
            game.getAnimatronicManager().startNight(nightNum);
        }
        
        // Iniciar el verificador de eventos por hora
        startHourEventChecker();
    }

    public void stopNight() {
        this.isNightActive = false;
        if (hourChecker != null && !hourChecker.isShutdown()) {
            hourChecker.shutdownNow();
            hourChecker = null;
        }
        if (game != null && game.getAnimatronicManager() != null) {
            game.getAnimatronicManager().stopNight();
        }
    }

    private void advanceHour() {
        if (!isNightActive) return;
        
        currentHour++;
        
        if (currentHour >= 6) {
            winNight();
        } else {
            // Mostrar mensaje de hora
            System.out.println("========================================");
            System.out.println("⏰ ⏰ ⏰ SON LAS " + getFormattedHour().toUpperCase() + " ⏰ ⏰ ⏰");
            System.out.println("========================================");
            
            // Ejecutar eventos especiales de la hora
            if (currentHour < hourEvents.length && hourEvents[currentHour]) {
                handleHourSpecialEvent();
            }
            
            // Aumentar agresión al cambiar de hora (como en React)
            if (game != null && game.getAnimatronicManager() != null) {
                game.getAnimatronicManager().increaseAggressionAtHourChange(currentNight, currentHour);
            }
            
            // Notificar al GameManager sobre el cambio de hora
            if (game != null) {
                game.onHourChanged(currentHour);
            }
            
            // Efectos especiales según la hora
            applyHourEffects();
        }
    }

    private void handleHourSpecialEvent() {
        switch (currentHour) {
            case 2:
                System.out.println("🎭 Los animatrónicos se vuelven más activos...");
                if (game != null && game.getAnimatronicManager() != null) {
                    // Evento especial de la hora 2
                    game.getAnimatronicManager().setAggressionForNight(currentNight);
                }
                break;
            case 4:
                System.out.println("🎭 La tensión aumenta... los movimientos son más rápidos");
                break;
            case 5:
                System.out.println("🎭 ¡ÚLTIMA HORA! ¡Los animatrónicos están desesperados!");
                break;
        }
    }

    private void applyHourEffects() {
        // Efectos visuales o de sonido según la hora
        switch (currentHour) {
            case 1:
                System.out.println("🔊 Los pasillos están en silencio...");
                break;
            case 2:
                System.out.println("🔊 Escuchas ruidos en la cocina...");
                break;
            case 3:
                System.out.println("👻 La hora más oscura... los animatrónicos acechan...");
                break;
            case 4:
                System.out.println("⚡ La energía fluctúa...");
                break;
            case 5:
                System.out.println("🌅 El amanecer está cerca... ¡RESISTE!");
                break;
        }
    }

    private void startHourEventChecker() {
        if (hourChecker != null && !hourChecker.isShutdown()) {
            hourChecker.shutdownNow();
        }
        
        hourChecker = Executors.newSingleThreadScheduledExecutor();
        
        // Verificar eventos cada 10 segundos
        hourChecker.scheduleAtFixedRate(() -> {
            if (isNightActive && !isGameWon && !isGameOver && game != null && !game.isGameOver()) {
                checkHourEvents();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void checkHourEvents() {
        // Eventos aleatorios basados en la hora actual
        float progress = getHourProgress();
        int random = (int) (Math.random() * 100);
        
        if (currentHour >= 4 && progress > 0.5f && random < 15) {
            System.out.println("⚠️ ¡Escuchas algo acercándose rápidamente!");
            if (game != null && game.getAnimatronicManager() != null) {
                // Forzar un movimiento rápido de algún animatrónico
                forceRandomMovement();
            }
        }
    }

    private void forceRandomMovement() {
        if (game == null || game.getAnimatronicManager() == null) return;
        
        String[] animatronics = {"Stalker", "Runner", "Phantom", "Watcher"};
        String randomAnimatronic = animatronics[(int) (Math.random() * animatronics.length)];
        
        System.out.println("🎭 Movimiento forzado de " + randomAnimatronic + " por evento de hora");
    }

    private void winNight() {
        isNightActive = false;
        isGameWon = true;
        
        if (hourChecker != null && !hourChecker.isShutdown()) {
            hourChecker.shutdownNow();
            hourChecker = null;
        }
        
        System.out.println("========================================");
        System.out.println("🎉 🎉 🎉 ¡6 AM! ¡SOBREVIVISTE LA NOCHE " + currentNight + "! 🎉 🎉 🎉");
        System.out.println("========================================");
        
        if (game != null) {
            game.endGameWithVictory();
        }
    }

    /**
     * @return La hora formateada para la UI (ej. "1 AM", "12 AM").
     */
    public String getFormattedHour() {
        if (currentHour == 0) return "12 AM";
        if (currentHour == 12) return "12 PM";
        if (currentHour > 12) return (currentHour - 12) + " PM";
        return currentHour + " AM";
    }
    
    /**
     * @return La hora en formato 24 horas (0-5)
     */
    public int getCurrentHour24() {
        return currentHour;
    }
    
    /**
     * @return El progreso de la hora actual (0.0 a 1.0)
     */
    public float getHourProgress() {
        return Math.min(1.0f, timeInHour / REAL_SECONDS_PER_GAME_HOUR);
    }
    
    /**
     * @return El tiempo restante en la hora actual (segundos)
     */
    public float getTimeRemainingInHour() {
        return Math.max(0, REAL_SECONDS_PER_GAME_HOUR - timeInHour);
    }
    
    /**
     * @return El tiempo total transcurrido de la noche (segundos)
     */
    public float getTotalNightTime() {
        return (currentHour * REAL_SECONDS_PER_GAME_HOUR) + timeInHour;
    }
    
    /**
     * @return El porcentaje completado de la noche (0.0 a 1.0)
     */
    public float getNightProgress() {
        float totalNightSeconds = 6 * REAL_SECONDS_PER_GAME_HOUR;
        float elapsedSeconds = getTotalNightTime();
        return Math.min(1.0f, elapsedSeconds / totalNightSeconds);
    }
    
    /**
     * @return El tiempo restante de la noche (segundos)
     */
    public float getTimeRemainingInNight() {
        float totalNightSeconds = 6 * REAL_SECONDS_PER_GAME_HOUR;
        float elapsedSeconds = getTotalNightTime();
        return Math.max(0, totalNightSeconds - elapsedSeconds);
    }

    public int getCurrentNight() { 
        return currentNight; 
    }
    
    public int getCurrentHour() { 
        return currentHour; 
    }
    
    public boolean isNightActive() { 
        return isNightActive; 
    }
    
    public boolean isGameWon() { 
        return isGameWon; 
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }
    
    /**
     * Reinicia el TimeManager para una nueva partida
     */
    public void reset() {
        stopNight();
        currentNight = 1;
        currentHour = 0;
        timeInHour = 0;
        isNightActive = false;
        isGameWon = false;
        isGameOver = false;
    }
    
    /**
     * Acelera el tiempo (útil para pruebas)
     */
    public void setTimeSpeedMultiplier(float multiplier) {
        // Esto requeriría modificar REAL_SECONDS_PER_GAME_HOUR dinámicamente
        // Para pruebas, puedes cambiar este valor
        System.out.println("⚠️ Velocidad del tiempo cambiada a x" + multiplier);
    }
    
    /**
     * Salta a una hora específica (útil para pruebas)
     */
    public void skipToHour(int hour) {
        if (hour >= 0 && hour <= 6) {
            this.currentHour = hour;
            this.timeInHour = 0;
            System.out.println("⏩ Saltando a las " + getFormattedHour());
        }
    }
}