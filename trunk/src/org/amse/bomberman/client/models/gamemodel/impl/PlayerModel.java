package org.amse.bomberman.client.models.gamemodel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.gamemodel.Player;
import org.amse.bomberman.client.models.listeners.PlayerModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.Parser;
import org.amse.bomberman.util.impl.ParserImpl;

/**
 *
 * @author Kirilchuk V.E.
 */
public class PlayerModel implements ServerListener {

    private final List<PlayerModelListener> listeners
            = new CopyOnWriteArrayList<PlayerModelListener>();

    private volatile Player player  = new PlayerImpl();
    private volatile String name = "unnamed";

    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if(messageId == ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID) {
            Parser parser = new ParserImpl();
            setPlayer(parser.parsePlayer(data));//will updateListeners
        } else if (messageId == ProtocolConstants.SET_NAME_MESSAGE_ID) {
            name = data.get(0);
        }
    }

    private void setPlayer(Player player) {
        this.player  = player;
        updateListeners();
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    private void updateListeners() {
        for(PlayerModelListener listener : listeners) {
            listener.updatePlayer();
        }
    }

    public void addListener(PlayerModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlayerModelListener listener) {
        listeners.remove(listener);
    }
}
