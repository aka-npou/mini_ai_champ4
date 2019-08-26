package strategies;

import other.Draw;
import objects.Tick;

/**
 * Created by aka_npou on 18.07.2019.
 */
public abstract class Strategy {

    public static Draw draw;

    public abstract String getCommand(Tick tick);

}
