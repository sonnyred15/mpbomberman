package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ChatModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ChatModel implements ServerListener {
    private final List<ChatModelListener> listeners
            = new CopyOnWriteArrayList<ChatModelListener>();

    private List<String> history = new ArrayList<String>();

    //TODO CLIENT must be not here
    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if (messageId == ProtocolConstants.CHAT_GET_MESSAGE_ID) {//TODO hardcoded string
            if (!data.get(0).equals("No new messages.")) {
                history.addAll(data);
                updateListeners(data);
            }
        }
    }

    public List<String> getHistory() {
        return history;
    }

    public void addListener(ChatModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChatModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners(List<String> newMessages) {
        for (ChatModelListener listener : listeners) {
            listener.updateChat(newMessages);
        }
    }
}
