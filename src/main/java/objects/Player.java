package objects;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aka_npou on 18.07.2019.
 */
public class Player {
    public int score;
    public int direction;
    public ArrayList<Point> territory = new ArrayList<>();
    public ArrayList<Point> lines = new ArrayList<>();
    public Point position;

    public void setParam(JSONObject params) {
        score = params.getInt("score");

        if (params.get("direction").toString().equals("null"))
            direction = 0;
        else
            direction = getDirection(params.getString("direction"));

        JSONArray jsonPosition = (JSONArray) params.get("position");
        position = new Point(jsonPosition.getInt(0), jsonPosition.getInt(1));
        fillTerritory((JSONArray) params.get("territory"));
        fillLines((JSONArray) params.get("lines"));
    }

    public static int getDirection(String direction) {

        switch (direction) {
            case "left":{return 1;}
            case "right":{return 2;}
            case "up":{return 3;}
            case "down":{return 4;}
            case "":{return 0;}
        }

        return 0;
    }

    private void fillTerritory(JSONArray jsonTerritory) {
        int length = jsonTerritory.length();

        for (int i=0;i<length;i++) {
            JSONArray jt = jsonTerritory.getJSONArray(i);
            Point t = new Point(jt.getInt(0), jt.getInt(1));
            territory.add(t);
        }
    }

    private void fillLines(JSONArray jsonLines) {
        int length = jsonLines.length();

        for (int i=0;i<length;i++) {
            JSONArray jt = jsonLines.getJSONArray(i);
            Point t = new Point(jt.getInt(0), jt.getInt(1));
            lines.add(t);
        }
    }

    public static String getStringDirection(int _direction) {

        switch (_direction) {
            case 1:{return "left";}
            case 2:{return "right";}
            case 3:{return "up";}
            case 4:{return "down";}
            case 0:{return "init";}
        }

        return "";
    }
}
