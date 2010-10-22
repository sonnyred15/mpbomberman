package org.amse.bomberman.client.control.protocolhandlers.impl;

import java.util.List;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.models.impl.ResultsModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class PlayersStatsHandler implements ProtocolHandler {

    public void process(Controller controller, List<String> data) {
        ResultsModel resultsModel = controller.getContext().getResultsModel();
        resultsModel.setResults(data);
    }
}
