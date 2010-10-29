package org.amse.bomberman.client.control.impl;

import java.util.HashMap;
import java.util.Map;
import org.amse.bomberman.client.control.protocolhandlers.ProtocolHandler;
import org.amse.bomberman.client.control.protocolhandlers.impl.*;
import org.amse.bomberman.protocol.impl.ProtocolConstants;

/**
 * Class that corresponds for giving protocol handlers to hancle protocol
 * messages.
 *
 * @author Kirilchuk V.E.
 */
public class ProtocolHandlersFactory {

    private final Map<Integer, ProtocolHandler> handlers = new HashMap<Integer, ProtocolHandler>();

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

    /**
     * Returnes protocol message handler for specified message id, or
     * null if no handler for such message.
     *
     * @param protocolMessageId id of message to get handler for.
     * @return protocol message handler for specified message id, or
     * null if no handler for such message.
     */
    public ProtocolHandler getCommand(int protocolMessageId) {
        return handlers.get(protocolMessageId);
    }//TODO CLIENT when server send disconnect message
}
