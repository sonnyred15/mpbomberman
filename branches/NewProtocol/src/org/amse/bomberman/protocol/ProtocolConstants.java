package org.amse.bomberman.protocol;

/**
 * Utility class for storing some constants for protocol.
 * @author Kirilchuk V.E.
 * @author Mikhail Korovkin
 */
public class ProtocolConstants {

    /** Message for client about kick from game. */
    public static final String MESSAGE_GAME_KICK = "You were kicked from the game.";

    /** Message for client about start of game. */
    public static final String MESSAGE_GAME_START = "Game started.";

    /** Notification for client to update game field. */
    public static final String UPDATE_GAME_MAP = "Update game map.";

    /** Notification for client to update list of games. */
    public static final String UPDATE_GAMES_LIST = "Update games list.";

    /** Notification for client to update game info(about number of players and so on). */
    public static final String UPDATE_GAME_INFO = "Update game info.";

    /** Notification for client to update chat messages. */
    public static final String UPDATE_CHAT_MSGS = "Update chat messages.";

    /** Split symbol that must be used in protocol between args. */
    public static final String SPLIT_SYMBOL = "/";



    private ProtocolConstants() {}

    public static final int CREATE_GAME_MESSAGE_ID = 1;

    public static final int GAMES_LIST_MESSAGE_ID = 2;

    public static final int JOIN_GAME_MESSAGE_ID = 3;

    public static final int CHAT_ADD_MESSAGE_ID = 4;

    public static final int DO_MOVE_MESSAGE_ID = 5;

    public static final int GAME_MAP_INFO_MESSAGE_ID = 6;

    public static final int START_GAME_MESSAGE_ID = 7;

    public static final int LEAVE_MESSAGE_ID = 8;

    public static final int PLACE_BOMB_MESSAGE_ID = 9;

    public static final int DOWNLOAD_GAME_MAP_MESSAGE_ID = 10;

    public static final int GAME_STATUS_MESSAGE_ID = 11;

    public static final int GAME_MAPS_LIST_MESSAGE_ID = 12;

    public static final int BOT_ADD_MESSAGE_ID = 13;

    public static final int GAME_INFO_MESSAGE_ID = 14;
    
    public static final int CHAT_ADD_RESULT_MESSAGE_ID = 15;

    public static final int PLAYERS_STATS_MESSAGE_ID = 16;

    public static final int SET_NAME_MESSAGE_ID = 17;

    public static final int NOTIFICATION_MESSAGE_ID = 18;

    public static final int DISCONNECT_MESSAGE_ID = 100500;

    public static final int INVALID_REQUEST_MESSAGE_ID = 1000;
    
    public static final int BOT_REMOVE_MESSAGE_ID = 2000;
    
    public static final int CHAT_GET_MESSAGE_ID = 3000;

    public static int END_RESULTS_MESSAGE_ID = 4000;

}
