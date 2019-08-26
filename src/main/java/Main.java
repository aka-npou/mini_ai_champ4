import other.Draw;
import objects.Game;
import objects.Tick;
import org.json.JSONObject;
import strategies.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by aka_npou on 18.07.2019.
 */

// .\python.exe "E:\java\mail cups\ai\mini\mini_ai_champ4\miniaicups-master\miniaicups-master\paperio\local_runner\localrunner.py"
// .\python.exe "E:\java\mail cups\ai\mini\mini_ai_champ4\miniaicups-master\miniaicups-master\paperio\local_runner\localrunner.py" -p1 simple_bot -p2 simple_bot -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot
// .\python.exe "E:\java\mail cups\ai\mini\mini_ai_champ4\miniaicups-master\miniaicups-master\paperio\local_runner\localrunner.py" -p1 "java -jar 'E:\java\mail cups\ai\mini\mini_ai_champ4\miniaicups-master\miniaicups-master\paperio\local_runner\javaStrategy1.jar'" -p2 simple_bot -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot

// .\python.exe "E:\java\mail cups\ai\mini\mini_ai_champ4\miniaicups-master\miniaicups-master\paperio\local_runner\localrunner.py" -p1 "java -jar javaStrategy1-jar-with-dependencies.jar"
// .\python.exe localrunner.py -p1 "java -jar javaStrategy1-jar-with-dependencies.jar" -p2 simple_bot -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot

// cd "E:\java\mail cups\ai\mini\mini_ai_champ4\miniaicups-master\miniaicups-master\paperio\local_runner"
// python.exe localrunner.py -p1 simple_bot -p2 "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7007 -jar javaStrategy1-jar-with-dependencies.jar" -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot
// python.exe localrunner.py -p1 simple_bot -p2 "java  -jar javaStrategy1-jar-with-dependencies.jar" -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot
// python.exe localrunner.py -p1 simple_bot -p2 simple_bot -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 "java  -jar javaStrategy1-jar-with-dependencies.jar"
// python.exe localrunner.py -p6 simple_bot -p2 simple_bot -p3 simple_bot -p4 simple_bot -p5 simple_bot -p1 "java  -jar javaStrategy1-jar-with-dependencies.jar"
// python.exe localrunner.py -p2 simple_bot -p1 "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7007 -jar javaStrategy1-jar-with-dependencies.jar" -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot
// python.exe localrunner.py -p1 "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7007 -jar javaStrategy1-jar-with-dependencies.jar" -p2 simple_bot -p3 simple_bot -p4 simple_bot -p5 simple_bot -p6 simple_bot
// python.exe localrunner.py -p1 "java -jar javaStrategy1-jar-with-dependencies.jar" -p2 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p3 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p4 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p5 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p6 "java -jar javaStrategy-circle1-jar-with-dependencies.jar"
// python.exe localrunner.py -s 50 -p6 "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7007 -jar javaStrategy1-jar-with-dependencies.jar" -p2 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p3 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p4 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p5 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p1 "java -jar javaStrategy-circle1-jar-with-dependencies.jar"
// python.exe localrunner.py -p1 "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7007 -jar javaStrategy1-jar-with-dependencies.jar" -p2 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p3 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p4 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p5 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p6 "java -jar javaStrategy-circle1-jar-with-dependencies.jar"

// python.exe localrunner.py -p1 "java -jar javaStrategy1-jar-with-dependencies.jar" -p2 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p3 "python.exe bot.py" -p4 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p5 "java -jar javaStrategy-circle1-jar-with-dependencies.jar" -p6 "java -jar javaStrategy-circle1-jar-with-dependencies.jar"
// python.exe localrunner.py -p1 "java -jar javaStrategy1-jar-with-dependencies.jar" -p2 "java -jar javaStrategy1-jar-with-dependencies.jar" -p3 "java -jar javaStrategy1-jar-with-dependencies.jar" -p4 "java -jar javaStrategy1-jar-with-dependencies.jar" -p5 "java -jar javaStrategy1-jar-with-dependencies.jar" -p6 "java -jar javaStrategy1-jar-with-dependencies.jar"
// python.exe localrunner.py -p1 "java -jar javaStrategy1-jar-with-dependencies.jar" -p5 "java -jar javaStrategy1-jar-with-dependencies.jar" -p3 "java -jar javaStrategy1-jar-with-dependencies.jar" -p4 "java -jar javaStrategy1-jar-with-dependencies.jar" -p2 "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7007 -jar javaStrategy1-jar-with-dependencies.jar" -p6 "java -jar javaStrategy1-jar-with-dependencies.jar"

public class Main {

    private String line;

    public static void main(String args[]) {

        new Main().run();

    }


    public void run() {

        Game game = new Game();
        long t1=0,t2=0;

        Strategy strategy = new StrategyV2();

        if (Game.DEBUG)
            strategy.draw = new Draw(Game.X_CELLS_COUNT * Game.WIDTH, Game.Y_CELLS_COUNT * Game.WIDTH, 50, 50, 1, false);

        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
        {

            while ((line = br.readLine()) != null && line.length() != 0) {

                t1 = System.nanoTime();
                JSONObject parsed = new JSONObject(line);
                if (parsed.get("type").equals("start_game")) {
                    game.setParam(parsed);
                    //draw = new Draw(Game.X_CELLS_COUNT * Game.WIDTH, Game.Y_CELLS_COUNT * Game.WIDTH, 10, 10, 2, false);
                } else {

                    if (Game.DEBUG)
                        strategy.draw.clear = false;

                    Game.sb_debug = new StringBuilder();

                    String command = null;

                    try {

                        Tick tick = new Tick();
                        tick.setTick(parsed);

                        command = strategy.getCommand(tick);

                        Game.sb_debug.append(" cmd=").append(command.charAt(0));

                    }catch (Exception e) {

                        command = "up";
                        Game.sb_debug.append(" oooops ").append(Arrays.toString(e.getStackTrace()));
                    }


                    t2 = System.nanoTime();

                    Game.sb_debug.append(" tc:").append(t2-t1).append(" ").append((t2-t1)/1000000);

                    Game.allTicksTime +=(t2-t1);
                    Game.sb_debug.append("/").append(Game.allTicksTime);

                    System.out.printf("{\"command\": \"%s\", \"debug\": \"%s\"}\n", command, Game.sb_debug.toString());

                }

            }

        }
        catch(Exception ex){

            //System.out.println(ex.getMessage());
            System.out.printf("{\"command\": \"%s\", \"debug\": \"%s\"}\n", "up", "ooops");
        }

    }


}
