package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.GameMapsModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameMapsListHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        GameMapsModel gameMapsModel = controller.getContext().getGameMapsModel();

        if (!data.get(0).equals("No maps on server was founded.")) {
            gameMapsModel.setGameMapsList(data);
        } else {
            gameMapsModel.noGameMaps();
        }
    }
}
