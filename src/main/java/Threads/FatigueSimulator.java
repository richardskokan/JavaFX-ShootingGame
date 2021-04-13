package Threads;

import java.util.Random;

public class FatigueSimulator extends Thread {
    private int rested;
    private double fatigueLevel = -1;

    private double offsetX;
    private double offsetY;

    public FatigueSimulator(int rested) {
        this.rested = rested;
    }

    @Override
    public synchronized void run() {
        super.run();

        fatigueLevel = 10;

        if (rested == 1) {
            return;
        }

        fatigueLevel = 0;

        Random fatigueGenerator = new Random();

        do {
            if (fatigueLevel < 10) fatigueLevel = Math.round((fatigueLevel + 0.02) * 100.0) / 100.0;

            offsetX = (fatigueGenerator.nextDouble() * 2 -1) * 1;
            offsetY = (fatigueGenerator.nextDouble() * 2 -1) * 1;

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        } while (true);
    }

    public void playerFired() {
        if (rested != 1) {
            if (fatigueLevel - 5 < 0) fatigueLevel = 0;
            else fatigueLevel -= 5;
        }
    }

    public double getFatigueLevel() {
        return fatigueLevel;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}
