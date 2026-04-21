package com.mygame.managers;

public class GameManager {

    private boolean gameOver = false;
    private float tiempoJuego = 0;
    private float tiempoParaGanar = 60;

    public void update(float tpf) {
        if (!gameOver) {
            tiempoJuego += tpf;

            if (tiempoJuego >= tiempoParaGanar) {
                winGame();
            }
        }
    }

    public void endGame(String reason) {
        if (!gameOver) {
            gameOver = true;
            System.out.println("GAME OVER: " + reason);
        }
    }

    public void winGame() {
        gameOver = true;
        System.out.println("¡GANASTE!");
    }

    public boolean isGameOver() {
        return gameOver;
    }
}