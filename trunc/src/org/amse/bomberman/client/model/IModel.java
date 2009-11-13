package org.amse.bomberman.client.model;
import org.amse.bomberman.client.view.IView;
/**
 *
 * @author maverick
 */
public interface IModel {
    public boolean movePlayer(int number, int direction);
    public void addListener(IView view);
    public void removeListener(IView view);
    public Map getMap();
}
