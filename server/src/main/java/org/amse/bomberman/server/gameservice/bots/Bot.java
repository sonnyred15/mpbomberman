package org.amse.bomberman.server.gameservice.bots;

import java.util.Random;
import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents bots(AI controlled players).
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public class Bot implements GameChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);
    
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

    @Override
    public void gameStarted(Game game) {
        this.botThread.start();
    }

    @Override
    public void gameTerminated(Game game) {
        game.removeGameChangeListener(this);
    }

    @Override
    public void fieldChanged() {
        //ignore
    }

    @Override
    public void gameEnded(Game game) {
        this.botThread.interrupt();
    }

    @Override
    public void newChatMessage(String message) {
        //ignore
    }

    @Override
    public void parametersChanged(Game game) {
        //ignore
    }

    @Override
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

            LOG.info("Bot thread {} ended.", botThread);
        }
    }
}
