package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.GameMapModel;
import org.amse.bomberman.client.models.impl.PlayerModel;
import org.amse.bomberman.util.Parser;
import org.amse.bomberman.util.impl.ParserImpl;

/**
 *
 * @author Kirilchuk V.E.
 */
public class SetGameMapHandler implements ProtocolHandler {
    private final Parser parser = new ParserImpl();

    @Override
    public void process(Controller controller, List<String> data) {
        GameMapModel gameMapModel = controller.getContext().getGameMapModel();
        gameMapModel.setGameMap(parser.parseGameMap(data));

        PlayerModel playerModel = controller.getContext().getPlayerModel();
        playerModel.setPlayer(parser.parsePlayer(data));
    }

}
