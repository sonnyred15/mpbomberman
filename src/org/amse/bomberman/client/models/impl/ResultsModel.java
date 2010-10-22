package org.amse.bomberman.client.models.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.amse.bomberman.client.models.listeners.ResultModelListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ResultsModel {

    private final List<ResultModelListener> listeners 
            = new CopyOnWriteArrayList<ResultModelListener>();

    private volatile List<String> results = new ArrayList<String>();

    public void setResults(List<String> data) {
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
