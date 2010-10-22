package org.amse.bomberman.client.models.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ChatModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ChatModel {
    private final List<ChatModelListener> listeners
            = new CopyOnWriteArrayList<ChatModelListener>();

    private List<String> history = new CopyOnWriteArrayList<String>();

    public void addMessages(List<String> messages) {
        history.addAll(messages);
        updateListeners(messages);
    }

    public List<String> getHistory() {//not need synchronize cause history thread safe
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
