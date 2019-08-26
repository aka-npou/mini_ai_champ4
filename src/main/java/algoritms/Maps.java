package algoritms;

import objects.Game;
import objects.Player;
import objects.Point;
import objects.Tick;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aka_npou on 13.08.2019.
 */
public class Maps {

    public static int[][] getMap(Player player) {

        int[][] map = new int[Game.Y_CELLS_COUNT][Game.X_CELLS_COUNT];
        for (Point t:player.territory) {
            map[(t.y-Game.WIDTH2)/Game.WIDTH][(t.x-Game.WIDTH2)/Game.WIDTH] = Game.CELL_MY_TERRITORY;
        }
        for (Point l:player.lines) {
            map[(l.y-Game.WIDTH2)/Game.WIDTH][(l.x-Game.WIDTH2)/Game.WIDTH] = Game.CELL_MY_LINE;
        }

        return map;

    }

    public static int[][] getMhtMap(Point p, int direction) {

        Point pc = new Point(Point.getCell(p.x), Point.getCell(p.y));
        int[][] map = new int[Game.Y_CELLS_COUNT][Game.X_CELLS_COUNT];

        for (int y=0;y<Game.Y_CELLS_COUNT;y++) {
            for(int x=0; x<Game.X_CELLS_COUNT;x++) {
                map[y][x] = Math.abs(pc.x-x)+Math.abs(pc.y-y);
            }
        }

        map[pc.y][pc.x]=-1;

        return map;

    }

    public static int[][] getLMap(Tick tick, HashMap<String, int[][]> mhtMaps) {

        int[][] map = new int[Game.Y_CELLS_COUNT][Game.X_CELLS_COUNT];

        for (Map.Entry<String, int[][]> entry:mhtMaps.entrySet()) {
            if (entry.getKey().equals("i"))
                continue;

            for (int y=0;y<Game.Y_CELLS_COUNT;y++) {
                for(int x=0; x<Game.X_CELLS_COUNT;x++) {
                    if (entry.getValue()[y][x]==-1 || map[y][x]==0)
                        map[y][x] = entry.getValue()[y][x];
                    else
                        map[y][x] = Math.min(map[y][x], entry.getValue()[y][x]);
                }
            }
        }

        return map;

    }



    public static String printMap(int[][] _map) {

        StringBuilder sb = new StringBuilder();
        for (int y=0;y<Game.Y_CELLS_COUNT;y++) {
            for (int x=0;x<Game.X_CELLS_COUNT;x++) {

                sb.append(_map[Game.Y_CELLS_COUNT-1-y][x]);
                sb.append("\t");
            }
            sb.append("\n");
        }

        return sb.toString();
    }


}
