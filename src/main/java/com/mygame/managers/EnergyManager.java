package com.mygame.managers;

public class EnergyManager {

    private float energy = 100;

    public void update(float tpf, boolean cam, boolean doorL, boolean doorR, boolean lightL, boolean lightR) {

        float consumo = 0;

        if (cam) consumo += 2;
        if (doorL) consumo += 3;
        if (doorR) consumo += 3;
        if (lightL) consumo += 1;
        if (lightR) consumo += 1;

        energy -= consumo * tpf;

        if (energy < 0) energy = 0;
    }

    public float getEnergia() {
        return energy;
    }
}