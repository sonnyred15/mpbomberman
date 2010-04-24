package org.amse.bomberman.client.net;
import java.util.List;
/**
 * @author Kirilchuk V.E
 */
public interface RequestResultListener {
    void received(List<String> result);
}
