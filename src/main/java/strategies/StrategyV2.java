package strategies;

import algoritms.Li;
import objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aka_npou on 20.07.2019.
 */
public class StrategyV2 extends Strategy{

    private HashMap<String, int[][]> maps;
    private HashMap<String, int[][]> liMaps;

    @Override
    public String getCommand(Tick tick) {

        Player myPlayer = tick.players.get("i");

        maps = new HashMap<>();
        liMaps = new HashMap<>();

        for (Map.Entry<String, Player> entry:tick.players.entrySet()) {
            maps.put(entry.getKey(), getMap(entry.getValue()));
            liMaps.put(entry.getKey(), Li.getLiMap(maps.get(entry.getKey()), entry.getValue().position, entry.getValue().direction));
        }

        if (Game.DEBUG) {
            draw.drawPlayer(myPlayer, 0);
            for (Map.Entry<String, Player> entry : tick.players.entrySet()) {
                if (entry.getKey().equals("i"))
                    continue;

                draw.drawPlayer(entry.getValue(), Integer.valueOf(entry.getKey()) - 1);
            }
        }

        String command = myPlayer.getStringDirection(myPlayer.direction);

        //todo не ходить в клетку с противником
        //todo не ходить если хвост больше
        //todo бонусы смотреть
        //todo съедать чужие клетки и хвосты

        if (canAttack(tick, myPlayer, command)) {

        } else if (inMyTerritory(myPlayer)) {
            Game.sb_debug.append("inMyTerritory(myPlayer),");
            command = goToNearestBoundaryIn(tick, myPlayer);
        } else {
            if (myPlayer.lines.size()>5 && neighborCellIsMyTerritory(myPlayer)) {
                Game.sb_debug.append("myPlayer.lines.size()>5 && neighborCellIsMyTerritory(myPlayer),");
                command = goToMyTerritoryNextStep(tick, myPlayer);
            } else if (myPlayer.lines.size()>15){
                Game.sb_debug.append("myPlayer.lines.size()>15,");
                command = goToNearestBoundaryOut(tick, myPlayer);
            } else if (!canFillTerritoryToEndGame(tick, myPlayer)){
                Game.sb_debug.append("!canFillTerritoryToEndGame(tick, myPlayer),");
                command = goToNearestBoundaryOut(tick, myPlayer);
            } else {
                //int minCellsToMyTerritory = getMinCellsToMyTerritory(myPlayer.position, myPlayer.territory, liMaps.get("i"));
                //int minCellsToEnemy = getMinCellsToEnemy(tick, myPlayer);


                return goToBoundaryOut(tick, myPlayer);

                /*if (minCellsToMyTerritory + 2 >= minCellsToEnemy) {
                    Game.sb_debug.append("minCellsToMyTerritory + 2 >= minCellsToEnemy true ").append(minCellsToMyTerritory+2).append(" ").append(minCellsToEnemy).append(",");
                    command = goToNearestBoundaryOut(tick, myPlayer);
                } else {
                    Game.sb_debug.append("minCellsToMyTerritory + 2 >= minCellsToEnemy false ").append(minCellsToMyTerritory+2).append(" ").append(minCellsToEnemy).append(",");
                    command = goToFarthestBoundaryOut(tick, myPlayer);
                }*/
            }
        }

        return command;
    }

    //идти след шагом на свою территорию
    private String goToMyTerritoryNextStep(Tick tick, Player player) {

        Point p = new Point(Point.getCell(player.position.x), Point.getCell(player.position.y));
        if (player.direction!=2 && p.x>0 && maps.get("i")[p.y][p.x-1]==Game.CELL_MY_TERRITORY)
            return "left";
        if (player.direction!=1 && p.x<Game.X_CELLS_COUNT-1 && maps.get("i")[p.y][p.x+1]==Game.CELL_MY_TERRITORY)
            return "right";
        if (player.direction!=3 && p.y>0 && maps.get("i")[p.y-1][p.x]==Game.CELL_MY_TERRITORY)
            return "down";
        if (player.direction!=4 && p.y<Game.Y_CELLS_COUNT-1 && maps.get("i")[p.y+1][p.x]==Game.CELL_MY_TERRITORY)
            return "up";

        return "cant step";
    }

    //я на своей территории
    private boolean inMyTerritory(Player player) {

        Point p = player.position;

        for (Point t:player.territory) {
            if (p.x == t.x && p.y == t.y)
                return true;
        }
        return false;
    }

    //идти к ближайшей границе изнутри
    private String goToNearestBoundaryIn (Tick tick, Player player) {

        //todo проверить что не выйду во врага
        //проверим что рядом есть не территория и можно туда пойти
        Point p = new Point(Point.getCell(player.position.x), Point.getCell(player.position.y));
        if (player.direction!=2 && p.x>0 && maps.get("i")[p.y][p.x-1]==0 && !nextPointCollisionEnemy(tick, new Point(player.position.x-Game.WIDTH, player.position.y),1))
            return "left";
        if (player.direction!=1 && p.x<Game.X_CELLS_COUNT-1 && maps.get("i")[p.y][p.x+1]==0 && !nextPointCollisionEnemy(tick, new Point(player.position.x+Game.WIDTH, player.position.y),2))
            return "right";
        if (player.direction!=3 && p.y>0 && maps.get("i")[p.y-1][p.x]==0 && !nextPointCollisionEnemy(tick, new Point(player.position.x, player.position.y-Game.WIDTH),4))
            return "down";
        if (player.direction!=4 && p.y<Game.Y_CELLS_COUNT-1 && maps.get("i")[p.y+1][p.x]==0 && !nextPointCollisionEnemy(tick, new Point(player.position.x, player.position.y+Game.WIDTH),3))
            return "up";


        //todo возможно надо валить чтобы не помереть
        if (nextPointCollisionEnemy(tick, player.position, player.direction)) {
            if (player.direction!=2 && p.x>0 && !nextPointCollisionEnemy(tick, new Point(player.position.x-Game.WIDTH, player.position.y),1))
                return "left";
            if (player.direction!=1 && p.x<Game.X_CELLS_COUNT-1 && !nextPointCollisionEnemy(tick, new Point(player.position.x+Game.WIDTH, player.position.y),2))
                return "right";
            if (player.direction!=3 && p.y>0 && !nextPointCollisionEnemy(tick, new Point(player.position.x, player.position.y-Game.WIDTH),4))
                return "down";
            if (player.direction!=4 && p.y<Game.Y_CELLS_COUNT-1 && !nextPointCollisionEnemy(tick, new Point(player.position.x, player.position.y+Game.WIDTH),3))
                return "up";
        }


        //todo искать буквой Г путь, или горизонтально потом вертикально или наоборот
        //todo убрать непроходиме участки


        //иначе
        //ищем минимальное число на границе территории
        int minCells = Game.maxL;
        Point minPoint = null;
        for (Point t:player.territory) {
            if (isBoundaryTerritory(t, maps.get("i")) && !nextPointCollisionEnemy(tick, t, 10)) {
                //если в обратную сторону, то +3 к длине(разворот)
                /*if (isBackGo(t, player))
                    add = 3;
                else
                    add = 0;*/
                if (liMaps.get("i")[Point.getCell(t.y)][Point.getCell(t.x)]!=-1 && liMaps.get("i")[Point.getCell(t.y)][Point.getCell(t.x)] < minCells) {
                    minCells = liMaps.get("i")[Point.getCell(t.y)][Point.getCell(t.x)];
                    minPoint = t;
                }
            }
        }

        //идем до начальной точки и запоминаем куда поворачивали
        int direction=0;
        Point minPointCell = new Point(Point.getCell(minPoint.x), Point.getCell(minPoint.y));
        int dx,dy;
        while (true) {
            minCells-=1;
            if (minCells==0)
                minCells=-1;

            if (liMaps.get("i")[minPointCell.y][minPointCell.x]==-1)
                break;

            dx=0;
            dy=0;

            /*if (minPointCell.x>0 && liMaps.get("i")[minPointCell.y][minPointCell.x]>liMaps.get("i")[minPointCell.y][minPointCell.x-1]) {
                dx = -1;
                direction = 2;
            }
            if (minPointCell.x<Game.X_CELLS_COUNT-1 && liMaps.get("i")[minPointCell.y][minPointCell.x+dx]>liMaps.get("i")[minPointCell.y][minPointCell.x+1]) {
                dx = 1;
                direction = 1;
            }
            if (minPointCell.y>0 && liMaps.get("i")[minPointCell.y][minPointCell.x+dx]>liMaps.get("i")[minPointCell.y-1][minPointCell.x]) {
                dy = -1;
                direction = 3;
            }
            if (minPointCell.y<Game.Y_CELLS_COUNT-1 && liMaps.get("i")[minPointCell.y+dy][minPointCell.x+dx]>liMaps.get("i")[minPointCell.y+1][minPointCell.x]) {
                dy = 1;
                direction = 4;
            }*/
            if (minPointCell.x>0 && minCells==liMaps.get("i")[minPointCell.y][minPointCell.x-1]) {
                dx = -1;
                direction = 2;
            }
            else if (minPointCell.x<Game.X_CELLS_COUNT-1 && minCells==liMaps.get("i")[minPointCell.y][minPointCell.x+1]) {
                dx = 1;
                direction = 1;
            }
            else if (minPointCell.y>0 && minCells==liMaps.get("i")[minPointCell.y-1][minPointCell.x]) {
                dy = -1;
                direction = 3;
            }
            else if (minPointCell.y<Game.Y_CELLS_COUNT-1 && minCells==liMaps.get("i")[minPointCell.y+1][minPointCell.x]) {
                dy = 1;
                direction = 4;
            }


            minPointCell.x+=dx;
            minPointCell.y+=dy;
        }

        //надеемся что он не найдет минимум в обратной стороне
        return Player.getStringDirection(direction);

        /*ArrayList<String> commands = getPossiblyCommands(null, player, false);
        return commands.get(0);*/
    }

    //минимальное расстояния от головы до своей территории
    //если 0 - то нет хода
    private int getMinCellsToMyTerritory (Point position, ArrayList<Point> territory, int[][] _liMap) {

        int minCells = Game.maxL;

        int currentCells = 0;

        for (Point t:territory) {

            currentCells = _liMap[Point.getCell(t.y)][Point.getCell(t.x)];
            if (currentCells != 0 && currentCells < minCells)
                minCells = currentCells;
        }

        return minCells;
    }

    private int getMinCellsToMyTerritoryCheckEnemies (Tick tick, Point position, ArrayList<Point> territory, int[][] _liMap) {

        int minCells = Game.maxL;

        int currentCells = 0;

        for (Point t:territory) {

            currentCells = _liMap[Point.getCell(t.y)][Point.getCell(t.x)];
            if (currentCells != 0 && currentCells < minCells)
                if (!enemyCanMoveTo(tick, t))
                    minCells = currentCells;
        }

        return minCells;
    }

    private boolean enemyCanMoveTo(Tick tick, Point t) {

        for (Map.Entry<String, Player> entry:tick.players.entrySet()) {
            if (entry.getKey().equals("i"))
                continue;

            if (getEnemySteps(entry.getValue()).contains(t))
                return true;
        }

        return false;
    }

    private ArrayList<Point> getEnemySteps(Player enemy) {
        ArrayList<Point> steps = new ArrayList<>();

        if (enemy.direction!=1) {

            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y));
            steps.add(new Point(enemy.position.x+2*Game.WIDTH, enemy.position.y));
            //steps.add(new Point(enemy.position.x+3*Game.WIDTH, enemy.position.y));

            steps.add(new Point(enemy.position.x, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x+2*Game.WIDTH, enemy.position.y+Game.WIDTH));

            steps.add(new Point(enemy.position.x, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x+2*Game.WIDTH, enemy.position.y-Game.WIDTH));

            //steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y+2*Game.WIDTH));
            //steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y-2*Game.WIDTH));

        }

        if (enemy.direction!=2) {

            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y));
            steps.add(new Point(enemy.position.x-2*Game.WIDTH, enemy.position.y));
            //steps.add(new Point(enemy.position.x-3*Game.WIDTH, enemy.position.y));

            steps.add(new Point(enemy.position.x, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x-2*Game.WIDTH, enemy.position.y+Game.WIDTH));

            steps.add(new Point(enemy.position.x, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x-2*Game.WIDTH, enemy.position.y-Game.WIDTH));

            //steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y+2*Game.WIDTH));
            //steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y-2*Game.WIDTH));

        }

        if (enemy.direction!=4) {

            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y));

            //steps.add(new Point(enemy.position.x-2*Game.WIDTH, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x, enemy.position.y+Game.WIDTH));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y+Game.WIDTH));
            //steps.add(new Point(enemy.position.x+2*Game.WIDTH, enemy.position.y+Game.WIDTH));

            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y+2*Game.WIDTH));
            steps.add(new Point(enemy.position.x, enemy.position.y+2*Game.WIDTH));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y+2*Game.WIDTH));

            //steps.add(new Point(enemy.position.x, enemy.position.y+3*Game.WIDTH));

        }

        if (enemy.direction!=3) {

            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y));

            //steps.add(new Point(enemy.position.x-2*Game.WIDTH, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x, enemy.position.y-Game.WIDTH));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y-Game.WIDTH));
            //steps.add(new Point(enemy.position.x+2*Game.WIDTH, enemy.position.y-Game.WIDTH));

            steps.add(new Point(enemy.position.x-Game.WIDTH, enemy.position.y-2*Game.WIDTH));
            steps.add(new Point(enemy.position.x, enemy.position.y-2*Game.WIDTH));
            steps.add(new Point(enemy.position.x+Game.WIDTH, enemy.position.y-2*Game.WIDTH));

            //steps.add(new Point(enemy.position.x, enemy.position.y+3*Game.WIDTH));

        }


        return steps;
    }

    //минимальное расстояние от головы противника до ближайшей к нему моей линии
    private int getMinCellsToEnemy (Tick tick, Player player) {

        //todo считать не только сколько ходов до меня так, а еще сколько минимум до закрытия линии и сколько оттуда до меня
        //а то может быть ситуация что закроет область и станет ближе

        int minCells = Game.maxL;

        int currentCells = 0;

        for (Map.Entry<String, Player> enemy:tick.players.entrySet()) {
            if (enemy.getKey().equals("i"))
                continue;

            for (Point l:player.lines) {
                currentCells = liMaps.get(enemy.getKey())[Point.getCell(l.y)][Point.getCell(l.x)];

                if (currentCells != 0 && currentCells < minCells)
                    minCells = currentCells;
            }
        }

        return minCells;
    }

    //идти к ближайшей своей территории снаружи
    private String goToNearestBoundaryOut (Tick tick, Player player) {

        /*ArrayList<String> commands = getPossiblyCommands(tick, player, false);

        //todo чтобы ход был дальший от противников

        //todo еще смотреть все ходы противников и их ближайшие расстояния до моих ходов

        //нужно получить все варианты моего хода и все варианты ходов противника
        String needCommand = null;
        int needCell = 0;
        int minCellsToEnemy = 0;

        for (String c:commands) {
            Point np = getNextPoint(player.position, c);

            int[][] _map = getMap(player);
            _map[Point.getCell(player.position.y)][Point.getCell(player.position.x)] = Game.CELL_MY_LINE;
            int[][] _liMap = Li.getLiMap(_map, np, Player.getDirection(c));

            int stepMinCellsToEnemy = getMinCellsToEnemyStep(tick, np);

            if (needCommand == null) {
                //int npc = getMinCellsToMyTerritory(np, player.territory, _liMap);
                int npc = getMinCellsToMyTerritoryCheckEnemies(tick, np, player.territory, _liMap);
                if (npc != Game.maxL) {
                    needCommand = c;
                    needCell = npc;
                    minCellsToEnemy = stepMinCellsToEnemy;
                }
            } else {
                //int npc = getMinCellsToMyTerritory(np, player.territory, _liMap);
                int npc = getMinCellsToMyTerritoryCheckEnemies(tick, np, player.territory, _liMap);
                if (needCell > npc) {
                    needCommand = c;
                    needCell = npc;
                    minCellsToEnemy = stepMinCellsToEnemy;
                } else if (needCell == npc && stepMinCellsToEnemy < minCellsToEnemy) {
                    needCommand = c;
                    needCell = npc;
                    minCellsToEnemy = stepMinCellsToEnemy;
                }
            }
        }

        return needCommand;*/

        ArrayList<NextMoveData> nextMoveDataArrayList = getNextMoveData(tick, player);

        NextMoveData bestNMD = null;

        for (NextMoveData nmd:nextMoveDataArrayList) {
            if (bestNMD==null) {
                if (nmd.totalPoint > 0)
                    bestNMD = nmd;
            } else {
                if (bestNMD.totalPoint<nmd.totalPoint)
                    bestNMD = nmd;
            }
        }

        if (bestNMD==null) {
            Game.sb_debug.append(" cmd=null");
            int maxDc = Game.maxL;
            for (NextMoveData nmd:nextMoveDataArrayList) {
                if (nmd.cellsToMyTerritory-nmd.cellsToNearestEnemy<maxDc) {
                    bestNMD = nmd;
                    maxDc = nmd.cellsToMyTerritory-nmd.cellsToNearestEnemy;
                }
            }
        }

        return bestNMD==null?"i can't move":bestNMD.command;

    }

    //идти к более дальней ячейке от своей территории
    private String goToFarthestBoundaryOut (Tick tick, Player player) {

        ArrayList<String> commands = getPossiblyCommands(tick, player, false);

        //todo расстояние по каждой команде и сколько клеток с линией вокруг. если равно - идти где меньше клеток

        //todo можно при равенстве еще какие поля смотреть где поля вкуснее

        //todo еще смотреть все ходы противников и их ближайшие расстояния до моих ходов
        String needCommand = null;
        int needCell = 0;
        int zeroCells = 0;
        int minCellsToEnemy = 0;

        for (String c:commands) {
            Point np = getNextPoint(player.position, c);

            int[][] _map = getMap(player);
            _map[Point.getCell(player.position.y)][Point.getCell(player.position.x)] = Game.CELL_MY_LINE;
            int[][] _liMap = Li.getLiMap(_map, np, player.direction);

            int stepMinCellsToEnemy = getMinCellsToEnemyStep(tick, np);

            int czc = getNeighborZeroCells(_map, np);

            if (needCommand == null) {
                int npc = getMinCellsToMyTerritory(np, player.territory, _liMap);
                if (npc != Game.maxL) {
                    needCommand = c;
                    needCell = npc;
                    zeroCells = czc;
                    minCellsToEnemy = stepMinCellsToEnemy;
                }
            } else {
                int npc = getMinCellsToMyTerritory(np, player.territory, _liMap);
                if (npc != Game.maxL) {
                    if (needCell < npc) {
                        needCommand = c;
                        needCell = npc;
                        zeroCells = czc;
                        minCellsToEnemy = stepMinCellsToEnemy;
                    } else if (needCell == npc) {

                        //todo возможно смотреть на расстояние, если далеко - то не смотреть
                        if (minCellsToEnemy<stepMinCellsToEnemy) {
                            needCommand = c;
                            needCell = npc;
                            zeroCells = czc;
                            minCellsToEnemy = stepMinCellsToEnemy;
                        } else if (minCellsToEnemy==stepMinCellsToEnemy){
                            if (zeroCells < czc) {
                                needCommand = c;
                                needCell = npc;
                                zeroCells = czc;
                                minCellsToEnemy = stepMinCellsToEnemy;
                            }
                        }
                    }
                }
            }
        }

        return needCommand;
    }


    private ArrayList<NextMoveData> getNextMoveData(Tick tick, Player player) {

        //ArrayList<String> commands = getPossiblyCommands(tick, player, false);

        ArrayList<String> commands = getCommands(tick, player);



        ArrayList<NextMoveData> nextMoveDataArrayList = new ArrayList<>();

        for (String c:commands) {

            NextMoveData nmd = new NextMoveData();
            nmd.command = c;
            nmd.position = getNextPoint(player.position, c);
            nmd.map = getMap(player);
            nmd.map[Point.getCell(player.position.y)][Point.getCell(player.position.x)] = Game.CELL_MY_LINE;
            nmd.liMap = Li.getLiMap(nmd.map, nmd.position, Player.getDirection(c));
            nmd.cellsToMyTerritory = getMinCellsToMyTerritoryCheckEnemies(tick, nmd.position, player.territory, nmd.liMap);
            nmd.cellsToNearestEnemy = Math.min(getMinCellsToEnemyStep(tick, nmd.position), getMinCellsToEnemy(tick, player)-2);

            nmd.totalPoint = nmd.getTotalPoint();

            nextMoveDataArrayList.add(nmd);

            Game.sb_debug.append(" cmd=").append(c.charAt(0));
            Game.sb_debug.append(" ct=").append(nmd.cellsToMyTerritory);
            Game.sb_debug.append(" ce=").append(nmd.cellsToNearestEnemy);
            Game.sb_debug.append(" tp=").append(nmd.totalPoint);
            Game.sb_debug.append(";");
        }

        return nextMoveDataArrayList;

    }

    private String goToBoundaryOut (Tick tick, Player player) {

        Game.sb_debug.append(" gtbo");

        //ArrayList<String> commands = getPossiblyCommands(tick, player, false);

        ArrayList<NextMoveData> nextMoveDataArrayList = getNextMoveData(tick, player);

        NextMoveData bestNMD = null;

        for (NextMoveData nmd:nextMoveDataArrayList) {
            if (bestNMD==null) {
                if (nmd.totalPoint > 0)
                    bestNMD = nmd;
            } else {
                if (bestNMD.totalPoint<nmd.totalPoint)
                    bestNMD = nmd;
            }
        }

        if (bestNMD==null) {
            Game.sb_debug.append(" cmd=null");
            int maxDc = Game.maxL;
            for (NextMoveData nmd:nextMoveDataArrayList) {
                if (nmd.cellsToMyTerritory-nmd.cellsToNearestEnemy<maxDc) {
                    bestNMD = nmd;
                    maxDc = nmd.cellsToMyTerritory-nmd.cellsToNearestEnemy;
                }
            }
        }

        return bestNMD==null?"i can't move":bestNMD.command;
    }

    private ArrayList<String> getPossiblyCommands(Tick tick, Player player, boolean check) {

        ArrayList<String> commands = new ArrayList<>();

        //проверку что в той стороне нет линий наших
        //если есть, но след точка территория - идти можно
        if (player.direction != 2) {
            if (player.position.x - Game.WIDTH >= Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "left");
                if (!nextPointInLines(player, np) && !nextPointCollisionEnemy(tick, np, 1))
                    if (!check || checkLine(player.position, 1))
                        commands.add("left");
            }
        }

        if (player.direction != 1) {
            if (player.position.x + Game.WIDTH <= Game.X_CELLS_COUNT * Game.WIDTH - Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "right");
                if (!nextPointInLines(player, np) && !nextPointCollisionEnemy(tick, np,2))
                    if (!check || checkLine(player.position, 2))
                        commands.add("right");
            }
        }

        if (player.direction != 4) {
            if (player.position.y + Game.WIDTH <= Game.Y_CELLS_COUNT * Game.WIDTH - Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "up");
                if (!nextPointInLines(player, np) && !nextPointCollisionEnemy(tick, np,3))
                    if (!check || checkLine(player.position, 3))
                        commands.add("up");
            }
        }

        if (player.direction != 3) {
            if (player.position.y - Game.WIDTH >= Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "down");
                if (!nextPointInLines(player, np) && !nextPointCollisionEnemy(tick, np,4))
                    if (!check || checkLine(player.position, 4))
                        commands.add("down");
            }
        }

        return commands;
    }

    private ArrayList<String> getCommands(Tick tick, Player player) {

        ArrayList<String> commands = new ArrayList<>();

        if (player.direction != 2) {
            if (player.position.x - Game.WIDTH >= Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "left");
                if (!nextPointInLines(player, np))
                    commands.add("left");
            }
        }

        if (player.direction != 1) {
            if (player.position.x + Game.WIDTH <= Game.X_CELLS_COUNT * Game.WIDTH - Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "right");
                if (!nextPointInLines(player, np))
                    commands.add("right");
            }
        }

        if (player.direction != 4) {
            if (player.position.y + Game.WIDTH <= Game.Y_CELLS_COUNT * Game.WIDTH - Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "up");
                if (!nextPointInLines(player, np))
                    commands.add("up");
            }
        }

        if (player.direction != 3) {
            if (player.position.y - Game.WIDTH >= Game.WIDTH/2) {
                Point np = getNextPoint(player.position, "down");
                if (!nextPointInLines(player, np))
                    commands.add("down");
            }
        }

        return commands;
    }


    //следующая ячейка своя линия
    private boolean nextPointInLines(Player player, Point np) {

        for(Point l:player.lines) {
            if (l.x==np.x && l.y==np.y)
                return true;
        }

        return false;
    }

    //врезаюсь в любого врага
    private boolean nextPointCollisionEnemy(Tick tick, Point np, int direction) {
        boolean answer = false;


        //todo не бояться если хвост длинее у противника, но он может и закрыть поле
        //==x==
        //=xxx=
        //xxxxx
        //=xxx=
        //==x==

        for (Map.Entry<String, Player> entry:tick.players.entrySet()) {
            if (entry.getKey().equals("i"))
                continue;

            //===
            //=x=
            //===
            if (np.x==entry.getValue().position.x && np.y==entry.getValue().position.y) {
                answer = true;
                break;
            }

            //===
            //x==
            //===
            if (np.x==entry.getValue().position.x-Game.WIDTH && np.y==entry.getValue().position.y) {
                answer = true;
                break;
            }

            //===
            //==x
            //===
            if (np.x==entry.getValue().position.x+Game.WIDTH && np.y==entry.getValue().position.y) {
                answer = true;
                break;
            }

            //===
            //===
            //=x=
            if (np.x==entry.getValue().position.x && np.y==entry.getValue().position.y-Game.WIDTH) {
                answer = true;
                break;
            }

            //=x=
            //===
            //===
            if (np.x==entry.getValue().position.x && np.y==entry.getValue().position.y+Game.WIDTH) {
                answer = true;
                break;
            }

            //x==
            //===
            //===
            if (np.x==entry.getValue().position.x-Game.WIDTH && np.y==entry.getValue().position.y+Game.WIDTH) {
                answer = true;
                break;
            }

            //==x
            //===
            //===
            if (np.x==entry.getValue().position.x+Game.WIDTH && np.y==entry.getValue().position.y+Game.WIDTH) {
                answer = true;
                break;
            }

            //===
            //===
            //==x
            if (np.x==entry.getValue().position.x+Game.WIDTH && np.y==entry.getValue().position.y-Game.WIDTH) {
                answer = true;
                break;
            }

            //===
            //===
            //x==
            if (np.x==entry.getValue().position.x-Game.WIDTH && np.y==entry.getValue().position.y-Game.WIDTH) {
                answer = true;
                break;
            }



            //===
            //x==
            //===
            if (direction==2 && np.x==entry.getValue().position.x-2*Game.WIDTH && np.y==entry.getValue().position.y) {
                answer = true;
                break;
            }

            //===
            //==x
            //===
            if (direction == 1 && np.x==entry.getValue().position.x+2*Game.WIDTH && np.y==entry.getValue().position.y) {
                answer = true;
                break;
            }

            //===
            //===
            //=x=
            if (direction == 3 && np.x==entry.getValue().position.x && np.y==entry.getValue().position.y-2*Game.WIDTH) {
                answer = true;
                break;
            }

            //=x=
            //===
            //===
            if (direction == 4 && np.x==entry.getValue().position.x && np.y==entry.getValue().position.y+2*Game.WIDTH) {
                answer = true;
                break;
            }

        }

        return answer;
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

    //проверка что не поворот в сторону своей линии
    private boolean checkLine(Point p, int direction) {

        boolean check = true;

        Point cell = new Point((p.x-Game.WIDTH/2)/Game.WIDTH, (p.y-Game.WIDTH/2)/Game.WIDTH);

        if (direction == 1) {

            for (int i=0;i<cell.x;i++) {
                if (maps.get("i")[cell.y][i]==Game.CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        } else if (direction == 2) {

            for (int i=cell.x+1;i<Game.X_CELLS_COUNT;i++) {
                if (maps.get("i")[cell.y][i]==Game.CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        } else if (direction == 3) {

            for (int i=cell.y+1;i<Game.Y_CELLS_COUNT;i++) {
                if (maps.get("i")[i][cell.x]==Game.CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        } else if (direction == 4) {

            for (int i=0;i<cell.y;i++) {
                if (maps.get("i")[i][cell.x]==Game.CELL_MY_LINE) {
                    check = false;
                    break;
                }
            }

        }



        return check;

    }

    //есть ближайшая ячейка своя территория
    private boolean neighborCellIsMyTerritory(Player player) {

        Point p = new Point((player.position.x-Game.WIDTH/2)/Game.WIDTH, (player.position.y-Game.WIDTH/2)/Game.WIDTH);

        if (p.x>0 && maps.get("i")[p.y][p.x-1]==Game.CELL_MY_TERRITORY)
            return true;
        if (p.x<Game.X_CELLS_COUNT-1 && maps.get("i")[p.y][p.x+1]==Game.CELL_MY_TERRITORY)
            return true;
        if (p.y>0 && maps.get("i")[p.y-1][p.x]==Game.CELL_MY_TERRITORY)
            return true;
        if (p.y<Game.Y_CELLS_COUNT-1 && maps.get("i")[p.y+1][p.x]==Game.CELL_MY_TERRITORY)
            return true;

        return false;
    }

    //проверку на расстояние и остаток тиков
    private boolean canFillTerritoryToEndGame(Tick tick, Player player) {

        //todo добавить бонус скорости/замедления
        int tickToEndGame = (Game.ticks - tick.tick_num)/6;

        int minCellsToMyTerritory = getMinCellsToMyTerritory(player.position, player.territory, liMaps.get("i"));

        //если ходов столько же или меньше, чтобы шел уже обратно если столько же
        //может не хватить тика до конца хода, потому -1
        if (tickToEndGame-1>minCellsToMyTerritory)
            return true;

        return false;
    }

    //это край территории
    private boolean isBoundaryTerritory(Point t, int[][] _maps) {

        Point tc = new Point(Point.getCell(t.x), Point.getCell(t.y));

        if (tc.x>0 && _maps[tc.y][tc.x-1]==0)
            return true;

        if (tc.x<Game.X_CELLS_COUNT-1 && _maps[tc.y][tc.x+1]==0)
            return true;

        if (tc.y>0 && _maps[tc.y-1][tc.x]==0)
            return true;

        if (tc.y<Game.Y_CELLS_COUNT-1 && _maps[tc.y+1][tc.x]==0)
            return true;

        return false;
    }

    private boolean isBackGo(Point t, Player player) {

        if (player.direction==1)
            if (player.position.y==t.y && player.position.x<t.x)
                return true;

        if (player.direction==2)
            if (player.position.y==t.y && player.position.x>t.x)
                return true;

        if (player.direction==3)
            if (player.position.x==t.x && player.position.y>t.y)
                return true;

        if (player.direction==4)
            if (player.position.x==t.x && player.position.y<t.y)
                return true;

        return false;
    }

    private int[][] getMap(Player player) {

        int[][] map = new int[Game.Y_CELLS_COUNT][Game.X_CELLS_COUNT];
        for (Point t:player.territory) {
            map[(t.y-Game.WIDTH/2)/Game.WIDTH][(t.x-Game.WIDTH/2)/Game.WIDTH] = Game.CELL_MY_TERRITORY;
        }
        for (Point l:player.lines) {
            map[(l.y-Game.WIDTH/2)/Game.WIDTH][(l.x-Game.WIDTH/2)/Game.WIDTH] = Game.CELL_MY_LINE;
        }

        return map;

    }

    private int getNeighborZeroCells(int[][] _map, Point p) {
        int z = 0;

        Point pc = new Point(Point.getCell(p.x), Point.getCell(p.y));
        if (pc.x>0 && _map[pc.y][pc.x-1]==0)
            z++;
        if (pc.x<Game.X_CELLS_COUNT-1 && _map[pc.y][pc.x+1]==0)
            z++;
        if (pc.y>0 && _map[pc.y-1][pc.x]==0)
            z++;
        if (pc.y<Game.Y_CELLS_COUNT-1 && _map[pc.y+1][pc.x]==0)
            z++;
        if (pc.x>0 && pc.y>0 && _map[pc.y-1][pc.x-1]==0)
            z++;
        if (pc.x>0 && pc.y<Game.Y_CELLS_COUNT-1 && _map[pc.y+1][pc.x-1]==0)
            z++;
        if (pc.x<Game.X_CELLS_COUNT-1 && pc.y>0 && _map[pc.y-1][pc.x+1]==0)
            z++;
        if (pc.x<Game.X_CELLS_COUNT-1 && pc.y<Game.Y_CELLS_COUNT-1 && _map[pc.y+1][pc.x+1]==0)
            z++;

        return z;
    }

    private boolean canAttack(Tick tick, Player myPlayer, String command) {

        //получить мин расстояния соперников до территорий
        //сравнить мои линии и до соперника и их
        //если атака безопасна, то атаковать

        return false;
    }

    private int getMinCellsToEnemyStep(Tick tick, Point np) {

        int minCells = Game.maxL;

        int currentCells = 0;

        for (Map.Entry<String, Player> enemy:tick.players.entrySet()) {
            if (enemy.getKey().equals("i"))
                continue;

            currentCells = liMaps.get(enemy.getKey())[Point.getCell(np.y)][Point.getCell(np.x)];

            if (currentCells != 0 && currentCells < minCells)
                minCells = currentCells;
        }

        return minCells-1;

    }


    String printMap(int[][] _map) {

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

    String print2Map(int[][] _map1, int[][] _map2) {

        StringBuilder sb = new StringBuilder();
        for (int y=0;y<Game.Y_CELLS_COUNT;y++) {
            for (int x=0;x<Game.X_CELLS_COUNT;x++) {

                sb.append(_map1[Game.Y_CELLS_COUNT-1-y][x]);
                sb.append("-");
                sb.append(_map2[Game.Y_CELLS_COUNT-1-y][x]);

                sb.append("\t");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
