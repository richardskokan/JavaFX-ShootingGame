package Threads;

import java.util.Random;

public class FatigueSimulator extends Thread {
    private int rested;
    private int fatigueLevel = -1;

    private double offsetX;
    private double offsetY;

    public FatigueSimulator(int rested) {
        this.rested = rested;
    }

    @Override
    public void run() {
        super.run();

        //if (rested == 0) return;

        Random fatigueGenerator = new Random();

        do {
            fatigueLevel++;

            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}*/
        } while (true);
    }

    public int getFatigueLevel() {
        return fatigueLevel;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}