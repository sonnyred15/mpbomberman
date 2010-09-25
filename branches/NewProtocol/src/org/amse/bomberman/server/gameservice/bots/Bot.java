package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.models.Model;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;

/**
 * Class that represents bots(AI controlled players).
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public class Bot extends ModelPlayer
        implements GameChangeListener {
    private static final long   BOT_STEP_DELAY = 75L;
    private static final Random random = new Random();
    private volatile boolean    gameEnded = false;
    private final Thread        botThread;
    private final Game          game;
    private final Model        model;
    private BotStrategy         strategy;

    /**
     * Constructor for Bot with defined nickName, and strategy.
     * Also bot must know about his game and about model that owns bot.
     * @param nickName nickName of Bot
     * @param game game that owns bot.
     * @param model model that owns bot.
     * @param strategy bot strategy.
     */
    public Bot(String nickName, Game game, Model model,
    		                    BotStrategy strategy, 
    		                    ScheduledExecutorService timer) {
        super(nickName, timer);
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
    @Override
    public void gameTerminated(Game gameThatEnded) {
        this.gameEnded = true;
//        this.game.removeGameStartedListener(this);
        this.game.removeGameEndedListener(this);
//        this.game.tryRemoveBotFromGame(this); //TODO BIG
        System.out.println("Bot removed.");
    }

    /**
     * Method from GameStartedListener interface.
     * Starts the bot thread.
     */
    @Override
    public void gameStarted(Game game) {
        botThread.start();
    }

    public void parametersChanged(Game game) {
        // TODO BIG
    }

    private class BotRun implements Runnable {
        Bot bot;

        public BotRun(Bot parent) {
            this.bot = parent;
        }

        @Override
        public void run() {
            while (isAlive() && !gameEnded) {
                try {
                    Action action = strategy.thinkAction(this.bot, model);
                    //TODO this is hack fix of problem when bot make move after death.
                    if(isAlive()) {
                        action.executeAction(game);
                    }
                    Thread.sleep(Bot.BOT_STEP_DELAY + random.nextInt(75));
                } catch (InterruptedException ex) {
                    System.err.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
                } catch (UnsupportedOperationException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("Bot thread ended.");
        }
    }
}
