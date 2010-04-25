package org.amse.bomberman.server.gameinit.bot;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.control.GameEndedListener;
import org.amse.bomberman.server.gameinit.control.GameStartedListener;
import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.server.gameinit.imodel.Player;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;

/**
 * Class that represents bots(AI controlled players).
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public class Bot extends Player
        implements GameStartedListener, GameEndedListener {
    private static final long   BOT_STEP_DELAY = 100L;
    private static final Random random = new Random();
    private boolean             gameEnded = false;
    private final Thread        botThread;
    private final Game          game;
    private final IModel        model;
    private BotStrategy         strategy;

    /**
     * Constructor for Bot with defined nickName, and strategy.
     * Also bot must know about his game and about model that owns bot.
     * @param nickName nickName of Bot
     * @param game game that owns bot.
     * @param model model that owns bot.
     * @param strategy bot strategy.
     */
    public Bot(String nickName, Game game, IModel model, BotStrategy strategy) {
        super(nickName);
        this.game = game;
        this.model = model;
        this.strategy = strategy;
        this.botThread = new Thread(new BotRun(this));
    }

    /**
     * Method from GameEndedListener interface.
     * In current realization switch bot gameEnded flag to true.
     * It affects on bot thread, so it is ending on next iteration.
     */
    public void gameEnded() {
        this.gameEnded = true;
    }

    /**
     * Method from GameStartedListener interface.
     * Starts the bot thread.
     */
    public void started() {
        botThread.start();
    }

    private class BotRun implements Runnable {
        Bot bot;

        public BotRun(Bot parent) {
            this.bot = parent;
        }

        public void run() {
            while (isAlive() &&!gameEnded) {
                try {
                    IAction action = strategy.thinkAction(this.bot, model);

                    action.executeAction(game);
                    Thread.sleep(Bot.BOT_STEP_DELAY + random.nextInt(100));
                } catch (InterruptedException ex) {
                    System.out.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
                } catch (UnsupportedOperationException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("Bot: removed from game(Game ended or he died)");
            game.removeGameStartedListener(this.bot);
            game.removeGameEndedListener(this.bot);
            game.tryRemoveBotFromGame(this.bot);
        }
    }
}
