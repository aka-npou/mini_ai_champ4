package objects;

import org.json.JSONObject;

/**
 * Created by aka_npou on 18.07.2019.
 */
public class Game {

    public static StringBuilder sb_debug;

    public static int X_CELLS_COUNT=31;
    public static int Y_CELLS_COUNT=31;
    public static int SPEED=5;
    public static int WIDTH=30;
    public static int WIDTH2=15;
    public static int CELL_MY_TERRITORY = 1;
    public static int CELL_MY_LINE = 2;
    public static final boolean DEBUG = false;
    public static int ticks = 2500;
    public static int maxL = X_CELLS_COUNT * Y_CELLS_COUNT;
    public static long allTicksTime=0;

    public void setParam(JSONObject parsed) {
        JSONObject param = (JSONObject) parsed.get("params");

        X_CELLS_COUNT = param.getInt("x_cells_count");
        Y_CELLS_COUNT = param.getInt("y_cells_count");
        SPEED = param.getInt("speed");
        WIDTH = param.getInt("width");
        WIDTH2 = WIDTH/2;

        //ticks = param.getInt("max_tick_count");

        maxL = X_CELLS_COUNT * Y_CELLS_COUNT;

    }
}
