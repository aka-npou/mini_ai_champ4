package strategies;

import objects.Game;
import objects.Player;
import objects.Point;
import objects.Tick;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by aka_npou on 20.07.2019.
 */
public class StrategyV1 extends Strategy{

    private static final int CELL_MY_TERRITORY = 1;
    private static final int CELL_MY_LINE = 2;
    private int[][] map;

    @Override
    public String getCommand(Tick tick) {

        Player myPlayer = tick.players.get("i");

        map = new int[Game.Y_CELLS_COUNT][Game.X_CELLS_COUNT];
        for (Point t:myPlayer.territory) {
            map[(t.y-Game.WIDTH/2)/Game.WIDTH][(t.x-Game.WIDTH/2)/Game.WIDTH] = CELL_MY_TERRITORY;
        }
        for (Point l:myPlayer.lines) {
            map[(l.y-Game.WIDTH/2)/Game.WIDTH][(l.x-Game.WIDTH/2)/Game.WIDTH] = CELL_MY_LINE;
        }

        String command = myPlayer.getStringDirection(myPlayer.direction);

        if (inMyTerritory(myPlayer)) {
            command = goToNearestBoundaryIn(myPlayer);
        } else {
            if (myPlayer.lines.size()>5 && neighborCellIsMyTerritory(myPlayer)) {
                command = goToNearestBoundaryOut(tick, myPlayer);
            } else if (!canFillTerritoryToEndGame(tick, myPlayer)){
                command = goToNearestBoundaryOut(tick, myPlayer);
            } else {
                int minCellsToMyTerritory = getMinCellsToMyTerritory(myPlayer.position, myPlayer.territory);
                int minCellsToEnemy = getMinCellsToEnemy(tick, myPlayer);

                if (minCellsToMyTerritory + 2 * Game.WIDTH >= minCellsToEnemy) {
                    command = goToNearestBoundaryOut(tick, myPlayer);
                } else {
                    command = goToFarthestBoundaryOut(tick, myPlayer);
                }
            }
        }

        return command;
    }


    private boolean inMyTerritory(Player player) {

        Point p = player.position;

        for (Point t:player.territory) {
            if (p.x == t.x && p.y == t.y)
                return true;
        }
        return false;
    }

    private String goToNearestBoundaryIn (Player player) {

        ArrayList<String> commands = getPossiblyCommands(null, player, false);
        return commands.get(0);
    }

    private int getMinCellsToMyTerritory (Point position, ArrayList<Point> territory) {

        int minCells = Math.max(Game.X_CELLS_COUNT, Game.Y_CELLS_COUNT) * Game.WIDTH * 2;

        int currentCells = 0;

        for (Point t:territory) {

            currentCells = Math.abs(position.x-t.x) + Math.abs(position.y-t.y);
            if (currentCells < minCells)
                minCells = currentCells;
        }

        return minCells;
    }

    private int getMinCellsToEnemy (Tick tick, Player player) {

        int minCells = Math.max(Game.X_CELLS_COUNT, Game.Y_CELLS_COUNT) * Game.WIDTH * 2;

        int currentCells = 0;

        for (Map.Entry<String, Player> enemy:tick.players.entrySet()) {
            if (enemy.getKey().equals("i"))
                continue;

            for (Point l:player.lines) {
                currentCells = Math.abs(l.x-enemy.getValue().position.x) + Math.abs(l.y-enemy.getValue().position.y);

                if (currentCells < minCells)
                    minCells = currentCells;
            }

        }

        return minCells;
    }

    private String goToNearestBoundaryOut (Tick tick, Player player) {

        ArrayList<String> commands = getPossiblyCommands(tick, player, false);

        String needCommand = null;
        Point needPoint = null;

        for (String c:commands) {
            Point np = getNextPoint(player.position, c);

            if (needCommand == null) {
                needCommand = c;
                needPoint = np;
            } else if (getMinCellsToMyTerritory(needPoint, player.territory) > getMinCellsToMyTerritory(np, player.territory))
                needCommand = c;

        }

        return needCommand;
    }

    private String goToFarthestBoundaryOut (Tick tick, Player player) {

        ArrayList<String> commands = getPossiblyCommands(tick, player, true);

        String needCommand = null;

        for (String c:commands) {
            Point np = getNextPoint(player.position, c);

            if (needCommand == null)
                needCommand = c;
            else if (getMinCellsToMyTerritory(player.position, player.territory) < getMinCellsToMyTerritory(np, player.territory))
                needCommand = c;

        }

        return needCommand;
    }

    private ArrayList<String> getPossiblyCommands(Tick tick, Player player, boolean check) {

        ArrayList<String> commands = new ArrayList<>();

        //проверку что в той стороне нет линий наших
        //если есть, но след точка территория - идти можно
        if (player.direction != 2) {
            if (player.position.x - Game.WIDTH >= Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "left");
                if (!nextPointInLines(player, np))
                    if (!check || checkLine(player.position, 1))
                        commands.add("left");
            }
        }

        if (player.direction != 1) {
            if (player.position.x + Game.WIDTH <= Game.X_CELLS_COUNT * Game.WIDTH - Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "right");
                if (!nextPointInLines(player, np))
                    if (!check || checkLine(player.position, 2))
                        commands.add("right");
            }
        }

        if (player.direction != 4) {
            if (player.position.y + Game.WIDTH <= Game.Y_CELLS_COUNT * Game.WIDTH - Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "up");
                if (!nextPointInLines(player, np))
                    if (!check || checkLine(player.position, 3))
                        commands.add("up");
            }
        }

        if (player.direction != 3) {
            if (player.position.y - Game.WIDTH >= Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "down");
                if (!nextPointInLines(player, np))
                    if (!check || checkLine(player.position, 4))
                        commands.add("down");
            }
        }

        return commands;
    }

    private boolean nextPointInLines(Player player, Point np) {

        for(Point l:player.lines) {
            if (l.x==np.x && l.y==np.y)
                return true;
        }

        return false;
    }

    private Point getNextPoint(Point p, String command) {
        if (command.equals("left"))
            return new Point(p.x-Game.WIDTH, p.y);
        if (command.equals("right"))
            return new Point(p.x+Game.WIDTH, p.y);
        if (command.equals("up"))
            return new Point(p.x, p.y+Game.WIDTH);
        if (command.equals("down"))
            return new Point(p.x, p.y-Game.WIDTH);

        return null;
    }

    private boolean checkLine(Point p, int direction) {

        boolean check = true;

        Point cell = new Point((p.x-Game.WIDTH/2)/Game.WIDTH, (p.y-Game.WIDTH/2)/Game.WIDTH);

        if (direction == 1) {

            for (int i=0;i<cell.x;i++) {
                if (map[cell.y][i]==CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        } else if (direction == 2) {

            for (int i=cell.x+1;i<Game.X_CELLS_COUNT;i++) {
                if (map[cell.y][i]==CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        } else if (direction == 3) {

            for (int i=cell.y+1;i<Game.Y_CELLS_COUNT;i++) {
                if (map[i][cell.x]==CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        } else if (direction == 4) {

            for (int i=0;i<cell.y;i++) {
                if (map[i][cell.x]==CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        }



        return check;

    }

    private boolean neighborCellIsMyTerritory(Player player) {

        Point p = new Point((player.position.x-Game.WIDTH/2)/Game.WIDTH, (player.position.y-Game.WIDTH/2)/Game.WIDTH);

        if (p.x>0 && map[p.y][p.x-1]==CELL_MY_TERRITORY)
            return true;
        if (p.x<Game.X_CELLS_COUNT-1 && map[p.y][p.x+1]==CELL_MY_TERRITORY)
            return true;
        if (p.y>0 && map[p.y-1][p.x]==CELL_MY_TERRITORY)
            return true;
        if (p.y<Game.Y_CELLS_COUNT-1 && map[p.y+1][p.x]==CELL_MY_TERRITORY)
            return true;

        return false;
    }

    private boolean canFillTerritoryToEndGame(Tick tick, Player player) {


        return true;
    }






    String getMap() {

        StringBuilder sb = new StringBuilder();
        for (int y=0;y<Game.Y_CELLS_COUNT;y++) {
            for (int x=0;x<Game.X_CELLS_COUNT;x++) {

                sb.append(map[Game.Y_CELLS_COUNT-1-y][x]);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
