package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.GameInfoModel;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class BotAddResultHandler implements ProtocolHandler {

    public void process(Controller controller, List<String> data) {
        GameInfoModel gameInfoModel = controller.getContext().getGameInfoModel();

        if (!data.get(0).equals("Bot added.")) {
            gameInfoModel.gameInfoError("Can not join bot.\n" + data.get(0));
        }

    }
}
