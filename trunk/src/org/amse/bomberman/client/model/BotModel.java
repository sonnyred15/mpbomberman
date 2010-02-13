package org.amse.bomberman.client.model;

import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.Model;

/**
 *
 * @author michail korovkin
 */
public class BotModel extends Model implements IModel{
    private static IModel botModel= null;

    private BotModel() {
    }

    public static IModel getInstance() {
        if (botModel == null) {
            botModel = new BotModel();
        }
        return botModel;
    }

}
