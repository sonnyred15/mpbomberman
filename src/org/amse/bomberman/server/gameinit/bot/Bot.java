package org.amse.bomberman.server.gameinit.bot;

import org.amse.bomberman.server.gameinit.Pair;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants.Direction;

/**
 * Class for adding bots to the games.
 * @author michail korovkin
 * @author Kirilchuk V.E.
 */
public class Bot extends Player implements Runnable {

    private static final long BOT_STEP_DELAY = 100L;
    private final IModel model;    
    private BotStrategy strategy;

    public Bot(String nickName, int id, IModel model, BotStrategy strategy) {
        super(nickName, id);
        this.model = model;
        this.strategy = strategy;
    }

    public void run() {


        while (this.isAlive()) {
            try {
                this.strategy.thinkAction(this, model).executeAction(model);
                Thread.sleep(Bot.BOT_STEP_DELAY);
            } catch (InterruptedException ex) {
                System.out.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
            } catch (UnsupportedOperationException ex) {
                //System.out.println("Bot can not find the way to the target Pair.");
                //System.out.println("Bot dreamed new target.");
                }
        }
        System.out.println("Bot is dead!!!");
        this.model.removeBot(this);
    }
}
