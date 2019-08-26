package strategies;

import objects.Player;
import objects.Tick;

/**
 * Created by aka_npou on 20.07.2019.
 */
public class SimpleStrategy extends Strategy {

    @Override
    public String getCommand(Tick tick) {

        Player myPlayer = tick.players.get("i");

        String command = myPlayer.getStringDirection(myPlayer.direction);

        if (myPlayer.position.y <= 15)
            if (myPlayer.lines.size() == 0) {
                switch (myPlayer.direction) {
                    case 1:{command = "up";break;}
                    case 3:{command = "up";break;}
                    case 4:{command = "left";break;}
                }

            } else if (myPlayer.lines.size() == 1) {
                command = "right";
            } else if (myPlayer.lines.size() == 2) {
                command = "down";
            }

        return command;

    }


}
