package Threads;

import java.util.Random;

public class WindSimulator extends Thread {
    private Integer windStrength;
    private int windX;

    public WindSimulator(Integer windStrength) {
        this.windStrength = windStrength;
    }

    @Override
    public void run() {
        super.run();

        if (windStrength == null || windStrength == 0) return;

        Random windGenerator = new Random();
        windX = windGenerator.nextInt(40 * windStrength);
        if (windGenerator.nextBoolean()) windX = windX * -1;

        do {
            int windChange = windGenerator.nextInt(10 * windStrength);
            if (windGenerator.nextBoolean()) windX += windChange;
            else windX -= windChange;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        } while (true);
    }

    public int getWindX() {
        return windX;
    }
}
