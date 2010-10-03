package org.amse.bomberman.server.gameservice.bots;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameservice.Game;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;

/**
 * Class that represents bots(AI controlled players).
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public class Bot implements GameChangeListener {
    private static final long   BOT_STEP_DELAY = 150L;

    private static final Random random = new Random();
    
    private final Thread        botThread;
    private final BotGamePlayer player;
    private final Game          game;
    private final BotStrategy   strategy;

    /**
     * Constructor for Bot with defined nickName, and strategy.
     * Also bot must know about his game and about model that owns bot.
     * @param nickName nickName of Bot
     * @param game game that owns bot.
     * @param model model that owns bot.
     * @param strategy bot strategy.
     */
    public Bot(BotGamePlayer player, Game game) {
        this.player = player;
        this.game = game;
        this.strategy = new RandomFullBotStrategy();
        this.botThread = new Thread(new BotRun());
        this.botThread.setDaemon(true);
    }

    public BotGamePlayer getGamePlayer() {
        return player;
    }

    public void gameStarted(Game game) {
        this.botThread.start();
    }

    public void gameTerminated(Game game) {
        game.removeGameChangeListener(this);
    }

    public void fieldChanged() {
        //ignore
    }

    public void gameEnded(Game game) {
        this.botThread.interrupt();
    }

    public void newChatMessage(String message) {
        //ignore
    }

    public void parametersChanged(Game game) {
        //ignore
    }

    public void statsChanged(Game game) {
        //ignore
    }

    private class BotRun implements Runnable {

        @Override
        public void run() {
            try {
                ModelPlayer bot = game.getPlayer(player.getPlayerId());
                while (!Thread.interrupted() && bot.isAlive()) {
                    Action action = strategy.thinkAction(game, player);
                    action.executeAction(game);
                    Thread.sleep(Bot.BOT_STEP_DELAY + random.nextInt(100));
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Bot thread ended.");
        }
    }
}
