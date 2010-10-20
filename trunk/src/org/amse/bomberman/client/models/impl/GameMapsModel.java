package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GameMapsModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameMapsModel implements ServerListener {

    private final List<GameMapsModelListener> listeners
            = new CopyOnWriteArrayList<GameMapsModelListener>();

    private volatile List<String> gameMaps = new ArrayList<String>(0);

    //TODO CLIENT must be not here
    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if (messageId == ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID) {
            if (!data.get(0).equals("No maps on server was founded.")) {
                setMaps(data);
            } else {
                setMaps(new ArrayList<String>(0));
                updateListeners("No maps on server was founded.");
            }
        }
    }

    public List<String> getGameMaps() {//don`t need syncronized - volatile is enough here
        return gameMaps;
    }

    private void setMaps(List<String> data) {
        gameMaps = data;
        updateListeners();
    }

    public void addListener(GameMapsModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameMapsModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (GameMapsModelListener listener : listeners) {
            listener.updateGameMaps();
        }
    }

    private void updateListeners(String string) {
        for (GameMapsModelListener listener : listeners) {
            listener.gameMapsError(string);
        }
    }
}
