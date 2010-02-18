package org.amse.bomberman.client.model;

import org.amse.bomberman.client.model.impl.Model;

/**
 *
 * @author Michail Korovkin
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
