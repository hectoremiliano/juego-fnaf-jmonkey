package com.mygame.managers;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.mygame.managers.GameManager;
import com.mygame.animatronics.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gestiona la lista de animatrónicos y sus niveles de agresión.
 * Adaptado del código React original con la lógica de movimiento y comportamiento.
 */
public class AnimatronicManager extends AbstractAppState {

    private final GameManager game;
    private boolean isNightActive = false;
    private boolean isBlackout = false;
    private boolean isGameOver = false;
    private boolean isMoving = false;

 
    private Iterator<String> watcherIterator;
    private Iterator<String> stalkerIterator;
    private Iterator<String> runnerIterator;
    private Iterator<String> phantomIterator;

   
    private int watcherTime = 10000;   
    private int stalkerTime = 5000;     
    private int runnerTime = 7300;      
    private int phantomTime = 13000;    

    
    private final Map<String, Integer> ranges = new HashMap<>();

    
    private final Map<String, String> currentPositions = new HashMap<>();
    private final Map<String, Boolean> isAtDoor = new HashMap<>();

   
    private ScheduledExecutorService watcherScheduler;
    private ScheduledExecutorService stalkerScheduler;
    private ScheduledExecutorService runnerScheduler;
    private ScheduledExecutorService phantomScheduler;

 
    private Watcher watcher;
    private Stalker stalker;
    private Runner runner;
    private Phantom phantom;

    public AnimatronicManager(GameManager game) {
        this.game = game;
        initializeAnimatronics();
        initializeIterators();
        initializeRanges();
    }

    private void initializeAnimatronics() {
        watcher = new Watcher();
        stalker = new Stalker();
        runner = new Runner();
        phantom = new Phantom();

        currentPositions.put("Watcher", "Stage");
        currentPositions.put("Stalker", "Dinning Area");
        currentPositions.put("Runner", "Dinning Area");
        currentPositions.put("Phantom", "Pirate Cove");

        isAtDoor.put("Watcher", false);
        isAtDoor.put("Stalker", false);
        isAtDoor.put("Runner", false);
        isAtDoor.put("Phantom", false);
    }

    private void initializeRanges() {
        ranges.put("Watcher", 1);
        ranges.put("Stalker", 1);
        ranges.put("Runner", 2);
        ranges.put("Phantom", 1);
    }


    private Iterator<String> createWatcherIterator() {
        List<String> path = Arrays.asList(
            "Stage", "Dinning Area", "Restrooms", "Hall", "Kitchen",
            "East Hall", "East Corner", "Office", "Door", "Jumpscare"
        );
        return new CyclicIterator(path);
    }

    private Iterator<String> createStalkerIterator() {
        List<String> path = Arrays.asList(
            "Stage", "Dinning Area", "West Hall", "West Corner", "Door", "Jumpscare"
        );
        return new CyclicIterator(path);
    }

    private Iterator<String> createRunnerIterator() {
        List<String> path = Arrays.asList(
            "Stage", "Dinning Area", "Restrooms", "East Hall", "East Corner", "Door", "Jumpscare"
        );
        return new CyclicIterator(path);
    }

    private Iterator<String> createPhantomIterator() {
        List<String> path = Arrays.asList(
            "Pirate Cove", "Hall", "West Hall", "West Corner", "Door", "Jumpscare"
        );
        return new CyclicIterator(path);
    }

    private void initializeIterators() {
        watcherIterator = createWatcherIterator();
        stalkerIterator = createStalkerIterator();
        runnerIterator = createRunnerIterator();
        phantomIterator = createPhantomIterator();
        

        if (watcherIterator.hasNext()) watcherIterator.next();
    }


    private static class CyclicIterator implements Iterator<String> {
        private final List<String> path;
        private int index = 0;
        
        public CyclicIterator(List<String> path) {
            this.path = path;
        }
        
        @Override
        public boolean hasNext() {
            return true;
        }
        
        @Override
        public String next() {
            String value = path.get(index);
            index = (index + 1) % path.size();
            return value;
        }
    }

    public void startNight(int nightNum) {
        isNightActive = true;
        isBlackout = false;
        isGameOver = false;
        isMoving = false;
        
      
        currentPositions.put("Watcher", "Stage");
        currentPositions.put("Stalker", "Dinning Area");
        currentPositions.put("Runner", "Dinning Area");
        currentPositions.put("Phantom", "Pirate Cove");
        
        isAtDoor.put("Watcher", false);
        isAtDoor.put("Stalker", false);
        isAtDoor.put("Runner", false);
        isAtDoor.put("Phantom", false);
        
     
        initializeIterators();
        
        setAggressionForNight(nightNum);
        startMovementTimers();
    }

    public void stopNight() {
        isNightActive = false;
        cancelAllTimers();
    }

    public void setAggressionForNight(int nightNum) {
        switch (nightNum) {
            case 1:
                ranges.put("Watcher", 1);
                ranges.put("Stalker", 1);
                ranges.put("Runner", 2);
                ranges.put("Phantom", 1);
                break;
            case 2:
                ranges.put("Watcher", 3);
                ranges.put("Stalker", 3);
                ranges.put("Runner", 4);
                ranges.put("Phantom", 2);
                break;
            case 3:
                ranges.put("Watcher", 5);
                ranges.put("Stalker", 6);
                ranges.put("Runner", 6);
                ranges.put("Phantom", 4);
                break;
            case 4:
                ranges.put("Watcher", 8);
                ranges.put("Stalker", 9);
                ranges.put("Runner", 9);
                ranges.put("Phantom", 7);
                break;
            case 5:
                ranges.put("Watcher", 12);
                ranges.put("Stalker", 14);
                ranges.put("Runner", 14);
                ranges.put("Phantom", 12);
                break;
            default:
                ranges.put("Watcher", 1);
                ranges.put("Stalker", 1);
                ranges.put("Runner", 2);
                ranges.put("Phantom", 1);
                break;
        }
        
       
        watcher.setAggressionLevel(ranges.get("Watcher"));
        stalker.setAggressionLevel(ranges.get("Stalker"));
        runner.setAggressionLevel(ranges.get("Runner"));
        phantom.setAggressionLevel(ranges.get("Phantom"));
    }

    public void increaseAggressionAtHourChange(int nightNum, int newHour) {
        System.out.println("📊 Aumentando agresión a las " + newHour + " AM.");
        
        if (newHour == 2) {
            watcherTime = 9500;
            stalkerTime = 4700;
            runnerTime = 6800;
            phantomTime = 10000;
            
            ranges.put("Stalker", ranges.get("Stalker") + 1);
            ranges.put("Runner", ranges.get("Runner") + 1);
        } else if (newHour == 4) {
            ranges.put("Stalker", ranges.get("Stalker") + 2);
            ranges.put("Runner", ranges.get("Runner") + 2);
            ranges.put("Watcher", ranges.get("Watcher") + 1);
            ranges.put("Phantom", ranges.get("Phantom") + 1);
        } else if (newHour == 5) {
            ranges.put("Stalker", ranges.get("Stalker") + 2);
            ranges.put("Runner", ranges.get("Runner") + 2);
            ranges.put("Watcher", ranges.get("Watcher") + 2);
            ranges.put("Phantom", ranges.get("Phantom") + 2);
        }
        
     
        watcher.setAggressionLevel(ranges.get("Watcher"));
        stalker.setAggressionLevel(ranges.get("Stalker"));
        runner.setAggressionLevel(ranges.get("Runner"));
        phantom.setAggressionLevel(ranges.get("Phantom"));
        
      
        watcher.setMoveInterval(watcherTime / 1000.0f);
        stalker.setMoveInterval(stalkerTime / 1000.0f);
        runner.setMoveInterval(runnerTime / 1000.0f);
        phantom.setMoveInterval(phantomTime / 1000.0f);
        
       
        restartTimers();
    }

    private void restartTimers() {
        cancelAllTimers();
        startMovementTimers();
    }

    private void startMovementTimers() {
        startWatcherTimer();
        startStalkerTimer();
        startRunnerTimer();
        startPhantomTimer();
    }

    private void startWatcherTimer() {
        if (watcherScheduler != null && !watcherScheduler.isShutdown()) {
            watcherScheduler.shutdownNow();
        }
        watcherScheduler = Executors.newSingleThreadScheduledExecutor();
        watcherScheduler.scheduleAtFixedRate(() -> {
            if (isNightActive && !isBlackout && !isGameOver && !isMoving) {
                willMove("Watcher", watcherIterator, watcherTime, true);
            }
        }, watcherTime, watcherTime, TimeUnit.MILLISECONDS);
    }

    private void startStalkerTimer() {
        if (stalkerScheduler != null && !stalkerScheduler.isShutdown()) {
            stalkerScheduler.shutdownNow();
        }
        stalkerScheduler = Executors.newSingleThreadScheduledExecutor();
        stalkerScheduler.scheduleAtFixedRate(() -> {
            if (isNightActive && !isBlackout && !isGameOver && !isMoving) {
                willMove("Stalker", stalkerIterator, stalkerTime, false);
            }
        }, stalkerTime, stalkerTime, TimeUnit.MILLISECONDS);
    }

    private void startRunnerTimer() {
        if (runnerScheduler != null && !runnerScheduler.isShutdown()) {
            runnerScheduler.shutdownNow();
        }
        runnerScheduler = Executors.newSingleThreadScheduledExecutor();
        runnerScheduler.scheduleAtFixedRate(() -> {
            if (isNightActive && !isBlackout && !isGameOver && !isMoving) {
                willMove("Runner", runnerIterator, runnerTime, false);
            }
        }, runnerTime, runnerTime, TimeUnit.MILLISECONDS);
    }

    private void startPhantomTimer() {
        if (phantomScheduler != null && !phantomScheduler.isShutdown()) {
            phantomScheduler.shutdownNow();
        }
        phantomScheduler = Executors.newSingleThreadScheduledExecutor();
        phantomScheduler.scheduleAtFixedRate(() -> {
            if (isNightActive && !isBlackout && !isGameOver && !isMoving) {
                willMove("Phantom", phantomIterator, phantomTime, true);
            }
        }, phantomTime, phantomTime, TimeUnit.MILLISECONDS);
    }

    private void willMove(String character, Iterator<String> iterator, int animTime, boolean canLaugh) {
        if (isMoving) return;
        
        int max = (character.equals("Stalker") || character.equals("Runner")) ? 22 : 30;
        int luckyNumber = (int) (Math.random() * max);
        
        int rangeValue = ranges.getOrDefault(character, 1);
        boolean condition = luckyNumber < rangeValue && !isAtDoor.getOrDefault(character, false);
        
        if (condition) {
            changeAnimatronic(() -> {
                String newPlace = iterator.next();
                currentPositions.put(character, newPlace);
                
                boolean isAtDoorNow = newPlace.equals("Door");
                isAtDoor.put(character, isAtDoorNow);
                
                System.out.println("🎭 " + character + " se movió a: " + newPlace + " (Suerte: " + luckyNumber + "/" + rangeValue + ")");
                
 
                if (game != null) {
                    game.onAnimatronicMove(character, newPlace);
                }
                
                if (canLaugh && character.equals("Watcher")) {
                    watcherLaugh();
                }
                
                if (isAtDoorNow && !isBlackout && game != null) {
                    checkDoors(character);
                }
            });
        }
    }

    private void changeAnimatronic(Runnable action) {
        isMoving = true;
        action.run();
        
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            isMoving = false;
            scheduler.shutdown();
        }, 1500, TimeUnit.MILLISECONDS);
    }

    private void watcherLaugh() {
        if (isBlackout) return;
        int random = (int) (Math.random() * 2);
        if (random == 0) {
            System.out.println("🔊 Risas de Watcher - Tipo 1");
            if (watcher != null) watcher.playLaughSound();
        } else {
            System.out.println("🔊 Risas de Watcher - Tipo 2");
            if (watcher != null) watcher.playLaughSound();
        }
    }

    private void checkDoors(String character) {
        String door = (character.equals("Stalker") || character.equals("Phantom")) ? "leftDoor" : "rightDoor";
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (game != null && !isGameOver) {
                boolean isDoorOpen = game.isDoorOpen(door);
                if (!isDoorOpen) {
                    checkDoorAgain(character, door, 1);
                } else {
                    animatronicFailed(character);
                }
            }
            scheduler.shutdown();
        }, 10000, TimeUnit.MILLISECONDS);
    }

    private void checkDoorAgain(String character, String door, int attempt) {
        if (attempt > 3) {
            if (game != null) {
                game.triggerJumpscare(character);
            }
            return;
        }
        
        long delay = (attempt == 1) ? 5000 : 3000;
        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (game != null && !isGameOver) {
                boolean isDoorOpen = game.isDoorOpen(door);
                if (!isDoorOpen) {
                    checkDoorAgain(character, door, attempt + 1);
                } else {
                    animatronicFailed(character);
                }
            }
            scheduler.shutdown();
        }, delay, TimeUnit.MILLISECONDS);
    }

    private void animatronicFailed(String character) {
        changeAnimatronic(() -> {
            System.out.println("🚪 " + character + " fue detenido en la puerta");
            
            
            switch (character) {
                case "Stalker":
                    stalkerIterator = createStalkerIterator();
                    currentPositions.put("Stalker", "Dinning Area");
                    isAtDoor.put("Stalker", false);
                    if (stalker != null) stalker.reset();
                    break;
                case "Runner":
                    runnerIterator = createRunnerIterator();
                    currentPositions.put("Runner", "Dinning Area");
                    isAtDoor.put("Runner", false);
                    if (runner != null) runner.reset();
                    break;
                case "Phantom":
                    phantomIterator = createPhantomIterator();
                    currentPositions.put("Phantom", "Pirate Cove");
                    isAtDoor.put("Phantom", false);
                    System.out.println("🔊 Golpe de Phantom");
                    if (phantom != null) phantom.reset();
                    break;
                case "Watcher":
                    watcherIterator = createWatcherIterator();
                    if (watcherIterator.hasNext()) watcherIterator.next();
                    currentPositions.put("Watcher", "Stage");
                    isAtDoor.put("Watcher", false);
                    if (watcher != null) watcher.reset();
                    break;
            }
            
            if (game != null) {
                game.onAnimatronicFailed(character);
            }
        });
    }

    private void cancelAllTimers() {
        if (watcherScheduler != null && !watcherScheduler.isShutdown()) {
            watcherScheduler.shutdownNow();
            watcherScheduler = null;
        }
        if (stalkerScheduler != null && !stalkerScheduler.isShutdown()) {
            stalkerScheduler.shutdownNow();
            stalkerScheduler = null;
        }
        if (runnerScheduler != null && !runnerScheduler.isShutdown()) {
            runnerScheduler.shutdownNow();
            runnerScheduler = null;
        }
        if (phantomScheduler != null && !phantomScheduler.isShutdown()) {
            phantomScheduler.shutdownNow();
            phantomScheduler = null;
        }
    }

    public void setBlackout(boolean blackout) {
        this.isBlackout = blackout;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
        if (gameOver) {
            cancelAllTimers();
        }
    }

    public String getCurrentPosition(String character) {
        return currentPositions.getOrDefault(character, "Unknown");
    }

    public boolean isAnimatronicAtDoor(String character) {
        return isAtDoor.getOrDefault(character, false);
    }
    
    public void resetAllAnimatronics() {
        if (watcher != null) watcher.reset();
        if (stalker != null) stalker.reset();
        if (runner != null) runner.reset();
        if (phantom != null) phantom.reset();
        
        currentPositions.put("Watcher", "Stage");
        currentPositions.put("Stalker", "Dinning Area");
        currentPositions.put("Runner", "Dinning Area");
        currentPositions.put("Phantom", "Pirate Cove");
        
        isAtDoor.put("Watcher", false);
        isAtDoor.put("Stalker", false);
        isAtDoor.put("Runner", false);
        isAtDoor.put("Phantom", false);
        
        isMoving = false;
        isBlackout = false;
        isGameOver = false;
    }
    
    public void updateAnimatronics(float tpf) {
        if (!isNightActive || isGameOver) return;
        
     
        if (watcher != null) watcher.update(tpf, game);
        if (stalker != null) stalker.update(tpf, game);
        if (runner != null) runner.update(tpf, game);
        if (phantom != null) phantom.update(tpf, game);
    }

    @Override
    public void update(float tpf) {
        if (!isNightActive || (game != null && game.isGameOver())) return;
        
        if (game != null) {
            if (game.isBlackout()) setBlackout(true);
            if (game.isGameOver()) setGameOver(true);
        }
        
        updateAnimatronics(tpf);
    }
    

    public Watcher getWatcher() { return watcher; }
    public Stalker getStalker() { return stalker; }
    public Runner getRunner() { return runner; }
    public Phantom getPhantom() { return phantom; }
    
    public boolean isNightActive() { return isNightActive; }
    public boolean isBlackout() { return isBlackout; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isMoving() { return isMoving; }
    
    public Map<String, Integer> getRanges() { return ranges; }
}