package org.amse.bomberman.server.gameinit.bot;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.server.gameinit.control.GameStartedListener;
import org.amse.bomberman.server.gameinit.imodel.IModel;

/**
 * Class for adding bots to the games.
 * @author michail korovkin
 * @author Kirilchuk V.E.
 */
public class Bot extends Player
        implements GameStartedListener, GameEndedListener {
    private static final long BOT_STEP_DELAY = 150L;
    private boolean           gameEnded = false;
    private final Thread      botThread;
    private final IModel      model;
    private BotStrategy       strategy;

    public Bot(String nickName, IModel model, BotStrategy strategy) {
        super(nickName);
        this.model = model;
        this.strategy = strategy;
        this.botThread = new Thread(new BotRun(this));
    }

    public void gameEnded(Game game) {
        this.gameEnded = true;
    }

    public void started() {
        botThread.start();
    }

    private class BotRun implements Runnable {
        Bot parent;

        public BotRun(Bot parent) {
            this.parent = parent;
        }

        public void run() {
            while (isAlive() && !gameEnded) {
                try {
                    IAction action = strategy.thinkAction(this.parent, model);

                    action.executeAction(model);
                    Thread.sleep(Bot.BOT_STEP_DELAY);
                } catch (InterruptedException ex) {
                    System.out.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
                } catch (UnsupportedOperationException ex) {

                    // System.out.println("Bot can not find the way to the target Pair.");
                    // System.out.println("Bot dreamed new target.");
                }
            }

            System.out.println("Bot: removed from game(Game ended or he died)");
            model.removePlayer(this.parent.getID());
        }
    }
}
