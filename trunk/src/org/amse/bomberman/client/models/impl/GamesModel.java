package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.GamesModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GamesModel implements ServerListener {

    private final List<GamesModelListener> listeners 
            = new CopyOnWriteArrayList<GamesModelListener>();

    private volatile List<String> games = new ArrayList<String>();

    //TODO CLIENT must be not here
    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if (messageId == ProtocolConstants.GAMES_LIST_MESSAGE_ID) {
            if (!data.get(0).equals("No unstarted games finded.")) {
                setGames(data);
            } else {
                setGames(new ArrayList<String>(0));
            }
        }
    }

    private void setGames(List<String> data) {
        games = data;
        updateListeners();
    }

    public List<String> getGames() {
        return games;
    }

    public void addListener(GamesModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GamesModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (GamesModelListener listener : listeners) {
            listener.updateGamesList();
        }
    }
}
