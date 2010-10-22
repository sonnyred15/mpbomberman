package org.amse.bomberman.client.control.impl;

import java.util.HashMap;
import java.util.Map;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.BotAddResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.CreateGameResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.DoMoveResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.EndResultsHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.GameInfoMessageHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.GameMapsListHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.GameStartedMessageHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.GameStatusMessageHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.GameTerminatedMessageHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.GamesListHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.JoinGameResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.KickResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.LeaveResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.NewChatMessagesHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.PlaceBombResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.PlayersStatsHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.SetGameMapHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.SetNameHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.StartGameResultHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.UpdateGameFieldCommand;
import org.amse.bomberman.client.control.protocolhandlers.impl.UpdateGameInfoCommand;
import org.amse.bomberman.client.control.protocolhandlers.impl.UpdateGamesListCommand;
import org.amse.bomberman.protocol.ProtocolConstants;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ProtocolHandlersFactory {

    Map<Integer, ProtocolHandler> handlers
            = new HashMap<Integer, ProtocolHandler>();
    {
        handlers.put(ProtocolConstants.GAMES_LIST_NOTIFY_ID, new UpdateGamesListCommand());
        handlers.put(ProtocolConstants.GAME_INFO_NOTIFY_ID, new UpdateGameInfoCommand());
        handlers.put(ProtocolConstants.GAME_FIELD_CHANGED_NOTIFY_ID, new UpdateGameFieldCommand());
        handlers.put(ProtocolConstants.DO_MOVE_MESSAGE_ID, new DoMoveResultHandler());
        handlers.put(ProtocolConstants.PLACE_BOMB_MESSAGE_ID, new PlaceBombResultHandler());
        handlers.put(ProtocolConstants.END_RESULTS_MESSAGE_ID, new EndResultsHandler());
        handlers.put(ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID, new SetGameMapHandler());
        handlers.put(ProtocolConstants.SET_NAME_MESSAGE_ID, new SetNameHandler());
        handlers.put(ProtocolConstants.LEAVE_MESSAGE_ID, new LeaveResultHandler());
        handlers.put(ProtocolConstants.CHAT_GET_MESSAGE_ID, new NewChatMessagesHandler());
        handlers.put(ProtocolConstants.CREATE_GAME_MESSAGE_ID, new CreateGameResultHandler());
        handlers.put(ProtocolConstants.JOIN_GAME_MESSAGE_ID, new JoinGameResultHandler());
        handlers.put(ProtocolConstants.START_GAME_MESSAGE_ID, new StartGameResultHandler());
        handlers.put(ProtocolConstants.GAME_STARTED_NOTIFY_ID, new GameStartedMessageHandler());
        handlers.put(ProtocolConstants.GAME_STATUS_MESSAGE_ID, new GameStatusMessageHandler());
        handlers.put(ProtocolConstants.GAME_TERMINATED_NOTIFY_ID, new GameTerminatedMessageHandler());
        handlers.put(ProtocolConstants.BOT_ADD_MESSAGE_ID, new BotAddResultHandler());
        handlers.put(ProtocolConstants.KICK_PLAYER_MESSAGE_ID, new KickResultHandler());
        handlers.put(ProtocolConstants.GAME_INFO_MESSAGE_ID, new GameInfoMessageHandler());
        handlers.put(ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID, new GameMapsListHandler());
        handlers.put(ProtocolConstants.GAMES_LIST_MESSAGE_ID, new GamesListHandler());
        handlers.put(ProtocolConstants.PLAYERS_STATS_MESSAGE_ID, new PlayersStatsHandler());
    }

    public ProtocolHandler getCommand(int protocolMessageId) {
        return handlers.get(protocolMessageId);
    }//TODO CLIENT when server send disconnect message
}
