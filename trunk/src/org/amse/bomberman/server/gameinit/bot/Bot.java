package org.amse.bomberman.server.gameinit.bot;

//~--- non-JDK imports --------------------------------------------------------

import java.util.Random;
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
    private static final long BOT_STEP_DELAY = 100L;
    private static final Random random = new Random();
    private boolean           gameEnded = false;
    private final Thread      botThread;
    private final Game        game;
    private final IModel      model;
    private BotStrategy       strategy;

    public Bot(String nickName, Game game, IModel model, BotStrategy strategy) {
        super(nickName);
        this.game = game; 
        this.model = model; 
        this.strategy = strategy;
        this.botThread = new Thread(new BotRun(this));
    }

    public void gameEnded() {
        this.gameEnded = true;
    }

    public void started() {
        botThread.start();
    }

    private class BotRun implements Runnable {
        Bot bot;

        public BotRun(Bot parent) {
            this.bot = parent;
        }

        public void run() {
            while (isAlive() && !gameEnded) {
                try {
                    IAction action = strategy.thinkAction(this.bot, model);

                    action.executeAction(game);
                    Thread.sleep(Bot.BOT_STEP_DELAY+random.nextInt(100));
                } catch (InterruptedException ex) {
                    System.out.println("INTERRUPTED EXCEPTION IN BOT THREAD!!!!");
                } catch (UnsupportedOperationException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("Bot: removed from game(Game ended or he died)");
            
            game.removeGameStartedListener(this.bot);
            game.removeGameEndedListener(this.bot);
            game.removeBotFromGame(this.bot);
        }
    }
}
