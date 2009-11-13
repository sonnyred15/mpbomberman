package org.amse.bomberman.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.view.IView;

/**
 *
 * @author michail korovkin
 */
public class Model implements IModel{
    private static IModel model= null;
    private Map map;
    //private IConnector connector;
    private List<IView> listener = new ArrayList<IView>();

    private Model() {
    }

    public static IModel getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }
    public void setMap(Map map) {
        this.map = map;
    }
    // only for player from this connector
    public boolean movePlayer(int number, int direction) {
        if (Connector.getInstance().doMove(direction)) {
            map = Connector.getInstance().getMap();
            this.updateListeners();
            System.out.println("Map reloading");
            return true;
        } else return false;
    }
    public Map getMap() {
        // ???
        IConnector connect = Connector.getInstance();
        map = connect.getMap();
        return map;
    }
    public void addListener(IView view) {
        listener.add(view);
         // must be here or somewhere else???
        Timer timer = new Timer();
        // period???
        timer.schedule(new UpdateTimerTask(), (long)0,(long) 200);
    }
    public void removeListener(IView view) {
        listener.remove(view);
    }
    private void updateListeners() {
        for (IView elem : listener) {
            elem.update();
        }
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Map has been updated.");
            updateListeners();
        }
    }
}
