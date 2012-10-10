package org.amse.bomberman.server.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.gameservice.gamemap.impl.SimpleField;
import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.server.gameservice.models.impl.StatsTable;
import org.amse.bomberman.server.gameservice.models.impl.StatsTable.Stat;
import org.amse.bomberman.server.util.Creator;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Pair;

/**
 * Utility class.
 * Main appointment to make String r List of Strings from something.
 * @author Kirilchuk V.E
 */
public class ConverterToString implements GenericConverter<String> {

    @Override
    public List<String> convertPlayersStats(StatsTable stats) {
        Set<Entry<ModelPlayer, StatsTable.Stat>> set
                = stats.getStats().entrySet();

        List<Entry<ModelPlayer, StatsTable.Stat>> list = new
                ArrayList<Entry<ModelPlayer, StatsTable.Stat>>(set);

        Collections.sort(list,
                new Comparator<Entry<ModelPlayer, StatsTable.Stat>>() {

            @Override
            public int compare(Entry<ModelPlayer, Stat> e1,
                               Entry<ModelPlayer, Stat> e2) {
                int points1 = e1.getValue().getPoints();
                int points2 = e2.getValue().getPoints();

                return points2 - points1;
            }
        });

        List<String> result = new ArrayList<String>();
        for (Entry<ModelPlayer, StatsTable.Stat> entry : list) {
            result.add(entry.getKey().getNickName());
            Stat stat = entry.getValue();
            result.add(String.valueOf(stat.getKills()));
            result.add(String.valueOf(stat.getDeaths()));
            result.add(String.valueOf(stat.getPoints()));
        }
        
        return result;
    }

    /**
     * Creates list of explosions.
     * Each string of list is coordinate of explosion in next format:
     * <p>
     * "x=... y=..."
     *
     * @param expl explosions to stringazize.
     * @return list of explosions as list of strings.
     */
    @Override
    public List<String> convertExplosions(List<Pair> expl) {//TODO WHATS ABOUT SYNCHRONIZATION?
        List<String> result = new ArrayList<String>();

        result.add(String.valueOf(expl.size()));

        for (Pair pair : expl) {//TODO WHAT IF CONCURRENT MODIFICATION?
            result.add(pair.getX() + " " + pair.getY());
        }

        return result;
    }

    /**
     * Creates string of game parameters in next format:
     * <p>
     * "gameID gameName gameMapName curPlayersNum maxPlayers"
     * @param game game to get parameters from.
     * @param gameID ID of game.
     * @return string of game parameters.
     */
    @Override
    public String convertGameParams(Game game, int gameID) {
        StringBuilder result = new StringBuilder();

        result.append(gameID);
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getGameName());
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getGameMapName());
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getCurrentPlayersNum());
        result.append(ProtocolConstants.SPLIT_SYMBOL);
        result.append(game.getMaxPlayers());

        return result.toString();
    }

    /**
     * Creates string of some game parameters for client(controller)
     * in next format:
     * <p>
     * "true/false maxPlayers curGamePlayersNum player1info player2info ..."
     * <p>
     * true if this client is owner of this game, false otherwise.
     * <p>
     * To playerInfo watch playerInfo method.
     * @param player represent client to get info for.
     * @return string of some game parameters for client.
     */
    @Override
    public List<String> convertGameInfo(Game game, GamePlayer player) {
        List<String> result  = new ArrayList<String>();

        if (game.isGameOwner(player)) {
            result.add("true");
        } else {
            result.add("false");
        }

        result.add(String.valueOf(game.getMaxPlayers()));

        List<ModelPlayer> players = game.getCurrentPlayers();

        result.add(String.valueOf(players.size()));

        for (ModelPlayer player1 : players) {
            result.add(player1.getNickName());
        }

        return result;
    }

    /**
     * Returns list of string - names of availible gameMaps.
     * @return list of string - names of availible gameMaps.
     */
    @Override
    public List<String> convertGameMapsList() {
        return Creator.createGameMapsList();
    }

    /**
     * Returns game start status - started game or not.
     * @param game game to get status from.
     * @return "started" if game started, "not started" otherwise.
     */
    @Override
    public String convertGameStartStatus(Game game) {
        if (game.isStarted()) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * Returnes list of strings in next format:
     * <p>
     * dimension
     * <p>
     * int int int .. int
     * <p>
     * ..................
     * <p>
     * int int int .. int
     * @param gameMapField field to make list of strings from.
     * @return gameMapField as list of strings.
     */
    @Override
    public List<String> convertField(int[][] gameMapField) {//TODO WHATS ABOUT field SYNCHRONIZATION?
        List<String> lst = new ArrayList<String>();

        lst.add("" + gameMapField.length);

        for (int i = 0; i < gameMapField.length; ++i) {
            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < gameMapField.length; j++) {
                buff.append(gameMapField[i][j]);
                buff.append(" ");
            }

            lst.add(buff.toString());
        }

        return lst;
    }

    /**
     * Returns list of strings in next format:
     * <p>
     * dimension
     * <p>
     * int int int .. int
     * <p>
     * ..................
     * <p>
     * int int int .. int
     * <p>
     * x=... y=...
     * <p>
     * x=... y=... this is explosions.
     * <br>
     * @param game game to get field and exloions from.
     * @return list of strings of gameMapField and explosions
     */
    @Override
    public List<String> convertFieldAndExplosions(Game game) {
        List<String> result = convertField(((SimpleField)game.getGameField())
                .getField());

        result.addAll(convertExplosions(game.getExplosions()));

        return result;
    }

    /**
     * Returns list of strings in next format:
     * <p>
     * dimension
     * <p>
     * int int int .. int
     * <p>
     * ..................
     * <p>
     * int int int .. int
     * <p>
     * x=... y=...
     * <p>
     * x=... y=... (this is explosions.)
     * <p>
     * 1 (separator - always just one.)
     * <p>
     * playerInfo (watch playerInfo method)
     * @param game game to get field and exloions from.
     * @param player player to get info from.
     * @return list of strings of gameMapField and explosions
     */
    @Override
    public List<String> convertFieldExplPlayer(Game game, int playerID) {
        int[][] field = ((SimpleField)game.getGameField()).getField();
        List<ModelPlayer> players = game.getCurrentPlayers();
        List<String> stringalizedField = new ArrayList<String>();

        stringalizedField.add(String.valueOf((field.length)));

        for (int i = 0; i < field.length; ++i) {
            StringBuilder buff = new StringBuilder();

            for (int j = 0; j < field.length; ++j) {
                int n = field[i][j];

                if (n == Constants.MAP_BOMB) {
                    for (ModelPlayer pl : players) {
                        int x = pl.getPosition().getX();
                        int y = pl.getPosition().getY();

                        if ((x == i) && (y == j) && pl.isAlive()) {
                            n = (n + 100 + pl.getId()); //player and bomb in one cell
                        }
                    }
                }

                buff.append(n);
                buff.append(" ");//TODO not need for last n in row
            }

            stringalizedField.add(buff.toString());
        }

        stringalizedField.addAll(convertExplosions(game.getExplosions()));
        
        ModelPlayer player = game.getPlayer(playerID); //TODO if playerID was incorrect.
        stringalizedField.addAll(convertPlayerInfo(player));

        return stringalizedField;
    }

    /**
     * Returns string in next format:
     * <p>
     * positionX positionY nickName lives bombs maxBombs
     * @param player player to get info from.
     * @return players info.
     */
    @Override
    public List<String> convertPlayerInfo(ModelPlayer player) {
        List<String> result = new ArrayList<String>();

        Pair position = player.getPosition();
        result.add(String.valueOf(position.getX()));
        result.add(String.valueOf(position.getY()));
        result.add(player.getNickName());
        result.add(String.valueOf(player.getLives()));
        result.add(String.valueOf(player.getSettedBombsNum()));
        result.add(String.valueOf(player.getMaxBombs()));
        result.add(String.valueOf(player.getRadius()));

        return result;
    }

    /**
     * List of strings - unstarted games strings in next format:
     *  <p>
     *  "gameID gameName gameMapName curPlayersNum maxPlayers"
     *  @param allGames started and unstarted games.
     *  @return list of strings - unstarted games strings.
     */
    @Override
    public List<String> convertUnstartedGames(List<Game> allGames) {
        if (allGames == null) {
            throw new IllegalArgumentException("Argument can`t be null.");
        }

        List<String> unstartedGames = new ArrayList<String>();

        Iterator<Game> it = allGames.iterator();

        for (int i = 0; it.hasNext(); ++i) { //TODO what if allGames will change concurrently?
            Game game = it.next();

            // send only games that are not started!!!
            if (!game.isStarted()) {
                unstartedGames.add(convertGameParams(game, i));
            }

        }

        return unstartedGames;
    }
}
