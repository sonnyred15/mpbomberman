package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.GameInfoModel;
import org.amse.bomberman.protocol.impl.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class KickResultHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        GameInfoModel gameInfoModel = controller.getContext().getGameInfoModel();

        if (!data.get(0).equals("Kicked.")) {
            gameInfoModel.gameInfoError("Kick error.\n" + data.get(0));
        }

    }
}
