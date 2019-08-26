package objects;

/**
 * Created by aka_npou on 06.08.2019.
 */
public class NextMoveData {

    public String command;
    public Point position;

    public int[][] liMap;
    public int[][] map;

    public int cellsToMyTerritory;
    public int cellsToNearestEnemy;

    public int totalPoint;


    public int getTotalPoint() {
        int answer = 0;

        if (cellsToMyTerritory==0) {

        } else {
            if (cellsToMyTerritory<cellsToNearestEnemy)
                answer+=cellsToMyTerritory;
            /*if (inMyTerritory())
                answer+=100;*/
        }

        return answer;
    }

    private boolean inMyTerritory() {
        if (map[Point.getCell(position.y)][Point.getCell(position.x)]==Game.CELL_MY_TERRITORY)
            return true;

        return false;
    }
}
