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

        if (windStrength == null || windStrength == 0) return;

        Random windGenerator = new Random();
        int WIND_LIMIT = 40;
        windX = windGenerator.nextInt(WIND_LIMIT) + WIND_LIMIT * (windStrength - 1);
        if (windGenerator.nextBoolean()) windX *= -1;

        do {
            double windChange = windGenerator.nextDouble() * windGenerator.nextInt(5);
            boolean windDirection = windGenerator.nextBoolean();

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

    public double getWindX() {
        return windX;
    }
}
