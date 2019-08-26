package objects;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by aka_npou on 18.07.2019.
 */
public class Tick {
    public int tick_num;
    public HashMap<String, Player> players = new HashMap<>();

    public void setTick(JSONObject parsed) {

        JSONObject param = (JSONObject) parsed.get("params");

        tick_num = param.getInt("tick_num");

        JSONObject jsonPlayers = (JSONObject) param.get("players");

        Set<String> keys = jsonPlayers.keySet();
        for (String key:keys) {

            JSONObject jsonPlayer = (JSONObject) jsonPlayers.get(key);
            Player player = new Player();
            player.setParam(jsonPlayer);
            players.put(key, player);

        }

    }
}
