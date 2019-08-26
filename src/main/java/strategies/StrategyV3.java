package strategies;

import algoritms.Li;
import algoritms.Maps;
import objects.Player;
import objects.Tick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aka_npou on 06.08.2019.
 */
public class StrategyV3 extends Strategy {

    private static String d = "ldru";
    private static ArrayList<String> paths = new ArrayList<>();

    private HashMap<String, int[][]> maps;
    private HashMap<String, int[][]> liMaps;
    private HashMap<String, int[][]> mhtMaps;

    @Override
    public String getCommand(Tick tick) {

        Player myPlayer = tick.players.get("i");

        maps = new HashMap<>();
        liMaps = new HashMap<>();
        mhtMaps = new HashMap<>();

        for (Map.Entry<String, Player> entry:tick.players.entrySet()) {
            maps.put(entry.getKey(), Maps.getMap(entry.getValue()));
            liMaps.put(entry.getKey(), Li.getLiMap(maps.get(entry.getKey()), entry.getValue().position, entry.getValue().direction));
            mhtMaps.put(entry.getKey(), Maps.getMhtMap(entry.getValue().position, entry.getValue().direction));
        }

        int[][] minLMap = Maps.getLMap(tick, mhtMaps);

        String path = getBestPath(tick, myPlayer, minLMap);

        return "up";
    }

    String getBestPath(Tick tick, Player myPlayer, int[][] minLMap) {

        move("", 0);

        return null;
    }

    void move(String pp, int start) {

        for (int i = start; i<4;i++) {
            if (canMove(pp, d.substring(i,i+1))) {
                String p= pp + d.substring(i,i+1);
                if (isMyTerritoty(p))
                    paths.add(p);
                else
                    move(p, i);
            }
        }

    }

    boolean canMove(String p, String np) {
        if (p.length()>3)
            return false;
        return true;
    }

    boolean isMyTerritoty(String p) {
        if (p.length()>=4)
            return true;
        return false;
    }

}
