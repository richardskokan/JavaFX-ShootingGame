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

        if (windStrength == null) return;

        Random windGenerator = new Random();

        do {
        } while (true);
    }

    public int getWindX() {
        return windX;
    }
}
