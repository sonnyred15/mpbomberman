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

    /** Split symbol that must be used in protocol between args. */
    public static final String SPLIT_SYMBOL = "/";

    private ProtocolConstants() {}

    public static final int SET_NAME_MESSAGE_ID = 10;

    public static final int GAMES_LIST_MESSAGE_ID = 20;

    public static final int GAME_MAPS_LIST_MESSAGE_ID = 30;
    
    public static final int CREATE_GAME_MESSAGE_ID = 40;

    public static final int JOIN_GAME_MESSAGE_ID = 50;

    //
    public static final int GAME_INFO_MESSAGE_ID = 60;

    public static final int GAME_STATUS_MESSAGE_ID = 70;

    public static final int CHAT_ADD_MESSAGE_ID = 80;

    public static final int CHAT_ADD_RESULT_MESSAGE_ID = 90;

    public static final int CHAT_GET_MESSAGE_ID = 100;

    public static final int BOT_ADD_MESSAGE_ID = 110;    
    
    public static final int KICK_PLAYER_MESSAGE_ID = 120;
      
    //
    public static final int START_GAME_MESSAGE_ID = 130;

    public static final int GAME_MAP_INFO_MESSAGE_ID = 140;

    public static final int DO_MOVE_MESSAGE_ID = 150;

    public static final int PLACE_BOMB_MESSAGE_ID = 160;

    //
    public static final int PLAYERS_STATS_MESSAGE_ID = 170;

    public static int END_RESULTS_MESSAGE_ID = 180;
    
    //
    public static final int LEAVE_MESSAGE_ID = 190;

    //
    public static final int DOWNLOAD_GAME_MAP_MESSAGE_ID = 200;

    public static final int INVALID_REQUEST_MESSAGE_ID = 210;

    //
    public static final int GAME_INFO_NOTIFY_ID = 510;

    public static final int GAME_STARTED_NOTIFY_ID = 520;
    
    public static final int GAME_TERMINATED_NOTIFY_ID = 530;

    public static final int GAME_FIELD_CHANGED_NOTIFY_ID = 540;

    public static final int GAMES_LIST_NOTIFY_ID = 550;

    //
    public static final int DISCONNECT_MESSAGE_ID = 1000;
}
