package algoritms;

import objects.Game;
import objects.Point;

import java.util.LinkedList;

/**
 * Created by aka_npou on 22.07.2019.
 */
public class Li {

    public static int[][] getLiMap(int[][] map, Point position, int direction) {

        int[][] liMap = new int[Game.Y_CELLS_COUNT][Game.X_CELLS_COUNT];

        LinkedList<Point> q = new LinkedList();
        //q.add(new Point(Point.getCell(position.x), Point.getCell(position.y)));

        liMap[Point.getCell(position.y)][Point.getCell(position.x)] = -1;

        Point p = new Point(Point.getCell(position.x), Point.getCell(position.y));

        if (p.x-1>=0 && direction!=2) {
            setValue(liMap, map, p, q, -1, 0);
        }

        if (p.x+1<Game.X_CELLS_COUNT&& direction!=1) {
            setValue(liMap, map, p, q, 1, 0);
        }

        if (p.y-1>=0&& direction!=3) {
            setValue(liMap, map, p, q, 0, -1);
        }

        if (p.y+1<Game.Y_CELLS_COUNT&& direction!=4) {
            setValue(liMap, map, p, q, 0, 1);
        }

        while(true) {
            if (q.size()==0)
                break;
            Point cp = q.pop();

            if (cp.x-1>=0) {
                setValue(liMap, map, cp, q, -1, 0);
            }

            if (cp.x+1<Game.X_CELLS_COUNT) {
                setValue(liMap, map, cp, q, 1, 0);
            }

            if (cp.y-1>=0) {
                setValue(liMap, map, cp, q, 0, -1);
            }

            if (cp.y+1<Game.Y_CELLS_COUNT) {
                setValue(liMap, map, cp, q, 0, 1);
            }
        }

        return liMap;
    }


    private static void setValue(int[][] liMap, int[][] map, Point cp, LinkedList<Point> q, int dx, int dy) {
        if (map[cp.y+dy][cp.x+dx]!=Game.CELL_MY_LINE){
            if (liMap[cp.y+dy][cp.x+dx]==0) {
                liMap[cp.y+dy][cp.x+dx] = liMap[cp.y][cp.x]==-1?1:liMap[cp.y][cp.x]+1;
                //не помню почему убрал свою территорию, вернем прохождение по ней, а то противники не считают расстояние до меня по своей территории
                //if (map[cp.y+dy][cp.x+dx]!=Game.CELL_MY_TERRITORY || map[cp.y][cp.x]==Game.CELL_MY_TERRITORY)
                    q.add(new Point(cp.x+dx, cp.y+dy));
            } else {
                int a = liMap[cp.y][cp.x]==-1?1:liMap[cp.y][cp.x]+1;
                if (liMap[cp.y+dy][cp.x+dx]>a) {
                    liMap[cp.y+dy][cp.x+dx]=a;
                }
            }
        }
    }
}
