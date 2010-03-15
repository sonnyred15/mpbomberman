/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.bot.Bot;
import org.amse.bomberman.server.net.ISession;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.Stringalize;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSession extends Session {

    public AsynchroSession(Server server, Socket clientSocket, int sessionID, ILog log) {
        super(server, clientSocket, sessionID, log);
    }

    @Override
    public void gameMapChanged() {
        if (this.game != null) {
            List<String> messages = new ArrayList<String>();
            List<ISession> sessionsToNotify = this.game.getSessions();
            messages.add(ProtocolConstants.UPDATE_GAME_MAP);
            this.server.notifySomeClients(sessionsToNotify, messages);
        } else {
            writeToLog("Session: GAME MAP CHANGED CALLED. BUT GAME=NULL. FIX PLZ.");
        }
    }

    @Override
    protected void sendGames() {
        List<String> linesToSend = Stringalize.unstartedGames(this.server.getGamesList());
        linesToSend.add(0, ProtocolConstants.CAPTION_GAMES_LIST);

        if (linesToSend.size() == 1) {//only "Games list." phraze
            linesToSend.add("No unstarted games finded.");
            this.sendAnswer(linesToSend);
            writeToLog("Session: client tryed to get games list. No unstarted games finded.");
            return;
        } else {
            this.sendAnswer(linesToSend);
            writeToLog("Session: sended games list to client.");
            return;
        }
    }

    @Override
    protected void createGame(String[] queryArgs) {
        //Example queryArgs = "1" "gameName" "mapName" "maxpl"        
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_CREATE_GAME);

        if (this.game != null) {
            messages.add("Leave another game first.");
            sendAnswer(messages);
            writeToLog("Session: createGame warning. Client tryed to join game, canceled. Already in other game.");
            return;
        }

        String gameName = null;
        String mapName = null;
        int maxPlayers = -1;//-1 for: defines by GameMap.

        if (queryArgs.length == 4) {//if we getted command in full syntax
            gameName = queryArgs[1];
            mapName = queryArgs[2];
            try {
                maxPlayers = Integer.parseInt(queryArgs[3]);
            } catch (NumberFormatException ex) {
                messages.add("Wrong query parameters. Error on client side.");
                writeToLog("Session: createGame error. Client tryed to create game, canceled. " + "Wrong command parameters. Error on client side. " + ex.getMessage());
                sendAnswer(messages);
                return;
            }
        } else { // if command have more or less arguments than must have.
            messages.add("Wrong query parameters. Error on client side.");
            writeToLog("Session: createGame error. Client tryed to create game, canceled. " + "Wrong command parameters. Error on client side.");
            sendAnswer(messages);
            return;
        }

        try {
            Game newGame = Creator.createGame(this.server, mapName, gameName, maxPlayers);
            this.server.addGame(newGame);

            this.game = newGame;
            this.player = this.game.join("HOST", this);
            this.game.setOwner(this.player);//TODO owner is session NOT PLAYER!!

            sendAnswer(messages);

            messages.clear();
            messages.add(ProtocolConstants.UPDATE_GAMES_LIST + " New game was created.");
            this.server.notifyAllClients(messages);

            writeToLog("Session: client created game." + " Map=" + mapName + " gameName=" + gameName + " maxPlayers=" + maxPlayers);
            return;
        } catch (FileNotFoundException ex) {
            sendAnswer("No such map on server.");
            writeToLog("Session: createGame warning. Client tryed to create game, canceled. " + "Map wasn`t founded on server." + " Map=" + mapName);
            return;
        } catch (IOException ex) {
            sendAnswer("Error on server side, while loading map.");
            writeToLog("Session: createGame error while loadimg map. " + " Map=" + mapName + " " + ex.getMessage());
            return;
        }
    }

    @Override
    protected void joinGame(String[] queryArgs) {
        //"2" "gameID" "playerName"
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_JOIN_GAME);

        if (this.game != null) {
            messages.add("Leave another game first.");
            sendAnswer(messages);
            writeToLog("Session: joinGame warning. Client tryed to join game, canceled. Already in other game.");
            return;
        }

        int gameID = 0;
        String playerName = null;

        if (queryArgs.length == 3) { //if we getted command in full syntax

            try {
                gameID = Integer.parseInt(queryArgs[1]);
            } catch (NumberFormatException ex) {
                messages.add("Wrong command parameters. Error on client side." + " gameID must be int.");
                sendAnswer(messages);
                writeToLog("Session: joinGame error. " + " Wrong command parameters. " + "Error on client side. gameID must be int. " + ex.getMessage());
                return;
            }
            playerName = queryArgs[2];

        } else { //wrong syntax
            messages.add("Wrong query. Error on client side.");
            sendAnswer(messages);
            writeToLog("Session: joinGame error. Wrong command parameters. Error on client side.");
            return;
        }

        //all is ok
        Game gameToJoin = server.getGame(gameID);
        if (gameToJoin != null) {
            if (!gameToJoin.isStarted()) {
                this.player = gameToJoin.join(playerName, this);//TODO join must return int. Player not part of Session!!!
                if (this.player == null) {//if game is full
                    messages.add("Game is full. Try to join later.");
                    sendAnswer(messages);
                    writeToLog("Session: joinGame warning. Client tryed to join to full game, canceled.");
                    return;
                } else {
                    this.game = gameToJoin;
                    messages.add("Joined.");
                    sendAnswer(messages);

                    List<ISession> sessionsToNotify = this.game.getSessions();
                    messages = new ArrayList<String>(1);
                    messages.add(0, ProtocolConstants.UPDATE_GAME_INFO);
                    this.server.notifySomeClients(sessionsToNotify, messages);

                    List<String> messages2 = new ArrayList<String>(1);
                    messages2.add(0, ProtocolConstants.UPDATE_GAMES_LIST);
                    this.server.notifyAllClients(messages2);

                    writeToLog("Session: client joined to game." + " GameID=" + gameID + " Player=" + playerName);
                    return;
                }
            } else { //if game.isStarted() true
                messages.add("Game was already started.");
                sendAnswer(messages);
                writeToLog("Session: joinGame warning. Client tryed to join gameID=" + gameID + " ,canceled." + " Game is already started. ");
                return;
            }
        } else { //if gameToJoin==null true
            messages.add("No such game.");
            sendAnswer(messages);
            writeToLog("Session: client tryed to join gameID=" + gameID + " ,canceled." + " No such game on server.");
            return;
        }
    }

    @Override
    protected void doMove(String[] queryArgs) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_DO_MOVE);

        if (this.game != null) {
            if (timer.getDiff() > Constants.GAME_STEP_TIME) {
                int dir = 0;
                boolean moved = false;
                try {
                    dir = Integer.parseInt(queryArgs[1]);//throws NumberFormatException
                    Direction direction = Direction.fromInt(dir); //throws IllegalArgumentException
                    moved =
                            this.game.doMove(player, direction);
                } catch (NumberFormatException ex) {
                    messages.add("Wrong move value.");
                    sendAnswer(messages);
                    writeToLog("Session: doMove error. Unsupported direction(not int). Error on client side." + " direction=" + queryArgs[1]);
                    return;
                } catch (IllegalArgumentException ex) {
                    sendAnswer("Wrong move value.");
                    writeToLog("Session: doMove error. Unsupported direction. Error on client side." + " direction=" + dir);
                    return;
                }

                if (moved) {
                    timer.setStartTime(System.currentTimeMillis());
                }

                messages.add("" + moved);
                sendAnswer(messages);

                if (moved) {
                    this.timer.setStartTime(System.currentTimeMillis());
                }
                return;

            } else { //timer.getDiff < gameStep true
                sendAnswer("false");
                writeToLog("Session: doMove warning. Client tryed to move, canceled. Moves allowed only every " + Constants.GAME_STEP_TIME + "ms.");
                return;
            }
        } else { //game == null true
            sendAnswer("Not joined to any game.");
            writeToLog(
                    "Session: doMove warning. Client tryed to move, canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void sendGameMapArray() {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_GAME_MAP_INFO);

        if (this.game != null) {
            List<String> linesToSend =
                    Stringalize.mapAndExplosionsAndPlayerInfo(this.game, this.player);
            messages.addAll(linesToSend);
            sendAnswer(messages);
            writeToLog("Session: sended mapArray+explosions+playerInfo to client.");
            return;
        } else {
            messages.add("Not joined to any game.");
            sendAnswer(messages);
            writeToLog("Session: sendMapArray warning. Canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void startGame() {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_START_GAME_INFO);

        if (this.game != null) {
            if (!this.game.isStarted()) {
                if (this.player == this.game.getOwner()) { //ONLY HOST(CREATER) CAN START GAME!!!
                    this.game.startGame();

                    List<ISession> sessionsToNotify = this.game.getSessions();
                    messages.add("Game started.");
                    this.server.notifySomeClients(sessionsToNotify, messages);

                    writeToLog("Session: started game. " + "(gameName=" + this.game.getName() + ")");
                    return;
                } else { //if player not owner of game
                    sendAnswer("Not owner of game.");
                    writeToLog(
                            "Session: startGame warning. Client tryed to start game, canceled. Not an owner.");
                }
            } else { //if game.isStarted() true
                sendAnswer("Game is already started.");
                writeToLog(
                        "Session: startGame warning. Client tryed to start started game. Canceled.");
                return;
            }
        } else { // game == null true
            sendAnswer("Not joined to any game.");
            writeToLog("Session: client tryed to start game, canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void leaveGame() {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_LEAVE_GAME_INFO);

        if (this.game != null) {
            Game temp = this.game;

            this.game.disconnectFromGame(this.player);//TODO must disconnect session NOT PLAYER
            this.game = null;                         //but first game call model to remove player
            this.player = null;
            messages.add("Disconnected.");
            sendAnswer(messages);

            List<ISession> sessionsToNotify = temp.getSessions();

            if (temp.isStarted()) {
                ;// do nothing
            } else {
                messages.clear();
                messages.add(ProtocolConstants.UPDATE_GAME_INFO);
                this.server.notifySomeClients(sessionsToNotify, messages);
            }

            messages.clear();
            messages.add(ProtocolConstants.UPDATE_GAMES_LIST);
            this.server.notifyAllClients(messages);

            writeToLog("Session: player has been disconnected from the game.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog(
                    "Session: leaveGame warning. Disconnect from game canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void placeBomb() {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_PLACE_BOMB_INFO);

        if (this.game != null) { // Always if game!=null player is not null too!
            if (this.game.isStarted()) {
                this.game.placeBomb(this.player); //is player alive checking in model
                messages.add("Ok.");
                sendAnswer(messages);

                writeToLog("Session: tryed to plant bomb. " + "playerID=" + this.player.getID() + " " + this.player.getPosition().toString());
                return;
            }else{
                messages.add("Game is not started. Can`t place bomb.");
                writeToLog("Session: placew bomb warning. Cancelled, Game is not started.");
            }
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: place bomb warning. Canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void sendDownloadingGameMap(String[] queryArgs) {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_DOWNLOAD_GAME_MAP);

        int[][] ret = null;
        String mapFileName = queryArgs[1] + ".map";

        if (queryArgs.length == 2) {
            try {
                ret = Creator.createMapAndGetArray(mapFileName);
            } catch (FileNotFoundException ex) {
                sendAnswer("No such map on server.");
                writeToLog("Session: sendMap warning. Client tryed to download map, canceled. " + "Map wasn`t founded on server." + " Map=" + mapFileName + " " + ex.getMessage());
                return;
            } catch (IOException ex) {
                sendAnswer("Error on server side, while loading map.");
                writeToLog("Session: sendMap error. Client tryed to download map, canceled. " + "Error on server side while loading map." + " Map=" + mapFileName + " " + ex.getMessage());
                return;
            }
        } else { //if arguments!=2
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: sendMap error. Client tryed to download map, canceled. Wrong query.");
            return;
        }

        //if all is OK.
        messages.addAll(Stringalize.map(ret));
        sendAnswer(messages);
        writeToLog("Session: client downloaded map." + " Map=" + mapFileName);
    }

    @Override
    protected void sendGameStatus() {
        List<String> messages = new ArrayList<String>();
        messages.add(0, ProtocolConstants.CAPTION_GAME_STATUS_INFO);

        if (this.game != null) {
            String ret = Stringalize.gameStatus(this.game);

            messages.add(ret);
            sendAnswer(messages);

            writeToLog("Session: sended game status to client.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameStatus warning. Canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void sendGameMapsList() {//TODO if no maps NPE FIX
        List<String> messages = Stringalize.mapsList(Creator.createMapsList());
        messages.add(0, ProtocolConstants.CAPTION_GAME_MAPS_LIST);

        if (messages.size() > 1) {
            sendAnswer(messages);
            writeToLog("Session: sended maps list to client. Maps count=" + (messages.size()-1));
            return;
        } else {
            messages.add("No maps on server was founded.");
            sendAnswer(messages);
            writeToLog("Session: sendMapsList error. No maps founded on server.");
            return;
        }
    }

    @Override
    protected void addBot(String[] queryArgs) {//TODO write for asynchro
        //"11" "gameID" "botName"

        int gameID = 0;
        String botName = "defaultBot";
        switch (queryArgs.length) {
            case 3: { //if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." + " gameID must be int.");
                    writeToLog("Session: addBot error. " + " Wrong command parameters. " + "Error on client side. gameID must be int. " + ex.getMessage());
                }
                botName = queryArgs[2];
                break;
            }
            default: { //wrong syntax
                sendAnswer("Wrong query. Error on client side.");
                writeToLog("Session: addBot error. Wrong command parameters. Error on client side.");
                break;
            }
        }

        Game gameToJoin = server.getGame(gameID);
        if (gameToJoin != null) {
            if (!gameToJoin.isStarted()) {
                Bot bot = gameToJoin.joinBot(botName);
                if (bot == null) { //if game is full
                    sendAnswer("Game is full. Try to add bot later.");
                    writeToLog("Session: addBot warning. Tryed to add bot, canceled. Game is full.");
                    return;
                } else {
                    //this.game = gameToJoin;
                    sendAnswer("Bot added.");

                    List<ISession> sessionsToNotify = this.game.getSessions();
                    List<String> messages = new ArrayList<String>(1);
                    messages.add(0, "Update game info.");
                    this.server.notifySomeClients(sessionsToNotify, messages);

                    messages.clear();
                    messages.add("Update games list.");
                    this.server.notifyAllClients(messages);

                    writeToLog("Session: added bot to game." + " GameID=" + gameID + " Player=" + botName);
                    return;
                }
            } else { //if game.isStarted() true
                sendAnswer("Game was already started.");
                writeToLog("Session: addbot warning. Tryed to add bot to game(gameID=" + gameID + ") ,canceled." + " Game is already started.");
                return;
            }
        } else { //if game==null true
            sendAnswer("No such game.");
            writeToLog("Session: addBot warning. Tryed to add bot to game(gameID=" + gameID + ") ,canceled." + " No such game.");
            return;
        }
    }

    @Override
    protected void sendGameInfo() {
        List<String> info = new ArrayList<String>();
        info.add(0, ProtocolConstants.CAPTION_GAME_INFO);

        if (this.game != null) {
            info.addAll(Stringalize.gameInfo(this.game, this.player));
            sendAnswer(info);
            writeToLog("Session: sended gameInfo to client.");
            return;
        } else {
            info.add("Not joined to any game.");
            sendAnswer(info);
            writeToLog("Session: sendGameInfo warning. Client tryed to get game info, canceled. Not joined to any game.");
            return;
        }
    }

    @Override//TODO write for asynchro
    protected void addMessageToChat(String[] queryArgs) {
        if (queryArgs.length >= 2) {
            if (this.game != null) {
                StringBuilder message = new StringBuilder();
                for (int i = 1; i <
                        queryArgs.length; ++i) {
                    message.append(queryArgs[i] + " ");
                }
                this.game.addMessageToChat(this.player, message.toString());
                List<String> toSend = this.game.getNewMessagesFromChat(this.player);
                sendAnswer(toSend);
                writeToLog("Session: client added message to game chat. message=" + queryArgs[1]);
                return;
            } else {
                sendAnswer("Not joined to any game.");
                writeToLog("Session: addMessageToChat warning. Client tryed to add message, canceled. Not joined to any game.");
                return;
            }
        } else {
            sendAnswer("Wrong query. Not enough arguments");
            writeToLog("Session: addMessageToChat error. Client tryed to add message, canceled. Wrong query.");
            return;
        }
    }

    @Override//TODO write for asynchro
    protected void getNewMessagesFromChat() {
        if (this.game != null) {
            List<String> toSend = this.game.getNewMessagesFromChat(this.player);
            sendAnswer(toSend);
            writeToLog("Session: client getted new messages from chat. count=" + toSend.size());
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: getNewMessagesFromChat warning. Client tryed to get messages, canceled. Not joined to any game.");
            return;
        }
    }
}
