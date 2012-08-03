package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.GameInfoModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameInfoMessageHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        GameInfoModel gameInfoModel = controller.getContext().getGameInfoModel();

        gameInfoModel.setGameInfo(data);
    }

}
