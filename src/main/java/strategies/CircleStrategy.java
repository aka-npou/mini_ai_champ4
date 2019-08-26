package strategies;

import objects.Tick;

/**
 * Created by aka_npou on 23.07.2019.
 */
public class CircleStrategy extends Strategy {

    private static final String[] commands = {"left", "down", "right", "up"};
    private int numCommand=0;
    @Override
    public String getCommand(Tick tick) {

        numCommand++;
        if (numCommand==4)
            numCommand=0;

        return commands[numCommand];
    }
}
