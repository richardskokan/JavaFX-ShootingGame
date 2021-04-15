package Threads;

import java.util.Random;

public class FatigueSimulator extends Thread {
    private final int rested;
    private double fatigueLevel = -1;

    private double offsetX;
    private double offsetY;

    public FatigueSimulator(int rested) {
        this.rested = rested;
    }

    @Override
    public synchronized void run() {
        super.run();

        //Resets energy level
        fatigueLevel = 5;

        //Returns from thread (stops it) when not needed (sim is off)
        if (rested == 1) {
            return;
        }

        Random fatigueGenerator = new Random();

        do {
            //Regenerates energy
            if (fatigueLevel > 0) fatigueLevel = Math.round((fatigueLevel - 0.02) * 100.0) / 100.0;

            //Generates new offsets
            offsetX = (fatigueGenerator.nextDouble() * 2 - 1) * ((fatigueLevel / 2 + 1) / 1.25);
            offsetY = (fatigueGenerator.nextDouble() * 2 - 1) * ((fatigueLevel / 2 + 1) / 1.25);

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        } while (true);
    }

    //Reduces energy when player fires
    public void playerFired() {
        if (rested != 1) {
            if (fatigueLevel + 5 > 10) fatigueLevel = 10;
            else fatigueLevel += 5;
        }
    }

    //Returns fatigue value
    public double getFatigueLevel() {
        return fatigueLevel;
    }

    //Returns horizontal offset (how much it is going to move)
    public double getOffsetX() {
        return offsetX;
    }

    //Returns vertical offset (how much it is going to move)
    public double getOffsetY() {
        return offsetY;
    }
}
