package strategies;

import objects.Tick;

import java.util.Random;

/**
 * Created by aka_npou on 18.07.2019.
 */
public class RandomStrategy extends Strategy {

    static final String[] commands = {"left", "right", "up", "down"};

    @Override
    public String getCommand(Tick tick) {

        String command = getRandom(commands);

        return "up";
    }

    private static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
