package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ResultModelListener;
import org.amse.bomberman.client.net.ServerListener;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ResultsModel implements ServerListener {

    private final List<ResultModelListener> listeners 
            = new CopyOnWriteArrayList<ResultModelListener>();

    private List<String> results = new ArrayList<String>();

    public void received(ProtocolMessage<Integer, String> message) {
        int messageId = message.getMessageId();
        List<String> data = message.getData();
        if (messageId == ProtocolConstants.END_RESULTS_MESSAGE_ID) {
            setResults(data);
        } else if (messageId == ProtocolConstants.PLAYERS_STATS_MESSAGE_ID) {
            setResults(data);
        }
    }

    private void setResults(List<String> data) {
        results = new ArrayList<String>(data);
        updateListeners();
    }

    public List<String> getResults() {//don`t need synchronize - volatile is enough here
        return results;
    }

    public void addListener(ResultModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ResultModelListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        for (ResultModelListener listener : listeners) {
            listener.updateResults();
        }
    }
}
