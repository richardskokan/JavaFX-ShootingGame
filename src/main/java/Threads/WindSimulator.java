package Threads;

import java.util.Random;

public class WindSimulator extends Thread {

    private final Integer windStrength;
    private double windX;

    public WindSimulator(Integer windStrength) {
        this.windStrength = windStrength;
    }

    @Override
    public synchronized void run() {
        super.run();

        //Stops the thread if not needed (sim is off)
        if (windStrength == null || windStrength == 0) return;

        Random windGenerator = new Random();
        //Generates initial wind strength
        int WIND_LIMIT = 40;
        windX = windGenerator.nextInt(WIND_LIMIT) + WIND_LIMIT * (windStrength - 1);
        if (windGenerator.nextBoolean()) windX *= -1;

        do {
            //Generates how is the wind going to change (how much and direction)
            double windChange = windGenerator.nextDouble() * windGenerator.nextInt(5);
            boolean windDirection = windGenerator.nextBoolean();

            //Changes the wind offset (applies wind changes)
            if (windDirection) {
                if (windX + windChange <= WIND_LIMIT * windStrength) windX += windChange;
                else windX = WIND_LIMIT * windStrength;
            } else {
                if (windX - windChange >= -WIND_LIMIT * windStrength) windX -= windChange;
                else windX = -WIND_LIMIT * windStrength;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        } while (true);
    }

    //Returns wind strength value
    public double getWindX() {
        return windX;
    }
}
