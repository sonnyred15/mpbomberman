package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.gamemodel.impl.GameStateModel;
import org.amse.bomberman.client.models.impl.ResultsModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class EndResultsHandler implements ProtocolHandler {

    @Override
    public void process(Controller controller, List<String> data) {
        GameStateModel gameStateModel = controller.getContext().getGameStateModel();
        gameStateModel.setEnded(true);

        ResultsModel resultsModel = controller.getContext().getResultsModel();
        resultsModel.setResults(data);
    }
}
