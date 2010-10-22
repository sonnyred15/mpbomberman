package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.GamesModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GamesListHandler implements ProtocolHandler {

    public void process(Controller controller, List<String> data) {
        GamesModel gamesModel = controller.getContext().getGamesModel();

        if (!data.get(0).equals("No unstarted games finded.")) {
            gamesModel.setGames(data);
        } else {
            gamesModel.setGames(new ArrayList<String>(0));
        }
    }
}

