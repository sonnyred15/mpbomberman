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
    protected void sendGames() {
        List<String> linesToSend = Stringalize.unstartedGames(this.server.getGamesList());

        if (linesToSend.size() == 0) {
            linesToSend.add("Games list.");
            linesToSend.add("No unstarted games finded.");
            this.sendAnswer(linesToSend);
            writeToLog("Session: client tryed to get games list. No unstarted games finded.");
            return;
        } else {
            linesToSend.add(0, "Games list.");
            this.sendAnswer(linesToSend);
            writeToLog("Session: sended games list to client.");
            return;
        }
    }

    @Override
    protected void createGame(String[] queryArgs) {
        //Example queryArgs = "1" "gameName" "mapName" "maxpl"
        String gameName = "defaultGameName";
        String mapName = "1";
        int maxPlayers = -1;//-1 for: defines by GameMap.

        if (queryArgs.length == 4) {//if we getted command in full syntax
            gameName = queryArgs[1];
            mapName = queryArgs[2];
            try {
                maxPlayers = Integer.parseInt(queryArgs[3]);
            } catch (NumberFormatException ex) {
                sendAnswer("Wrong query parameters for creating game.");
                writeToLog("Session: createGame error. Client tryed to create game, canceled. " + "Wrong command parameters. Error on client side. " + ex.getMessage());
                return;
            }
        }

        try {
            Game newGame = Creator.createGame(this.server, mapName, gameName, maxPlayers);
            int index = this.server.addGame(newGame);

            if (this.game != null) { //if not correct client can create multiple games
                if (this.player != null) {//we just disconnect him from first game!
                    this.game.disconnectFromGame(this.player);
                }
            }

            this.game = newGame;
            //TODO
            this.player = this.game.join("HOST", this);
            this.game.setOwner(this.player);
            sendAnswer("Created game.");

            List<String> messages = new ArrayList<String>();
            messages.add("New game was created.");
            messages.add(Stringalize.game(newGame, index));
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

        int gameID = 0;
        String playerName = "defaultPlayer";
        switch (queryArgs.length) {
            case 3: { //if we getted command in full syntax
                try {
                    gameID = Integer.parseInt(queryArgs[1]);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong command parameters. Error on client side." + " gameID must be int.");
                    writeToLog("Session: joinGame error. " + " Wrong command parameters. " + "Error on client side. gameID must be int. " + ex.getMessage());
                }
                playerName = queryArgs[2];
                break;
            }
            default: { //wrong syntax
                sendAnswer("Wrong query. Error on client side.");
                writeToLog(
                        "Session: joinGame error. Wrong command parameters. Error on client side.");
                break;
            }
        }

        if (this.game != null) {
            sendAnswer("Leave another game first.");
            writeToLog("Session: joinGame warning. Client tryed to join game, canceled. Already in other game.");
            return;
        }

        //all is ok
        Game gameToJoin = server.getGame(gameID);
        if (gameToJoin != null) {
            if (!gameToJoin.isStarted()) {
                this.player = gameToJoin.join(playerName, this);
                if (this.player == null) {//if game is full
                    sendAnswer("Game is full. Try to join later.");
                    writeToLog(
                            "Session: joinGame warning. Client tryed to join to full game, canceled.");
                    return;
                } else {
                    this.game = gameToJoin;
                    sendAnswer("Joined.");

                    List<ISession> sessionsToNotify = this.game.getSessions();
                    List<String> messages = new ArrayList<String>(1);
                    messages.add(0, "Update game info.");
                    this.server.notifySomeClients(sessionsToNotify, messages);

                    List<String> messages2 = new ArrayList<String>(1);
                    messages.add(0, "Update games info.");
                    this.server.notifyAllClients(messages2);

                    writeToLog("Session: client joined to game." + " GameID=" + gameID + " Player=" + playerName);
                    return;
                }
            } else { //if game.isStarted() true
                sendAnswer("Game was already started.");
                writeToLog("Session: joinGame warning. Client tryed to join gameID=" + gameID + " ,canceled." + " Game is already started. ");
                return;
            }
        } else { //if gameToJoin==null true
            sendAnswer("No such game.");
            writeToLog("Session: client tryed to join gameID=" + gameID + " ,canceled." + " No such game on server.");
            return;
        }
    }

    @Override
    protected void doMove(String[] queryArgs) {
        if (this.game != null) {
            if (timer.getDiff() > Constants.GAME_STEP_TIME) {
                int dir = 0;
                boolean moved = false;
                try {
                    dir = Integer.parseInt(queryArgs[1]);//throws NumberFormatException
                    Direction direction = Direction.fromInt(dir); //throws IllegalArgumentException
                    moved = this.game.doMove(player, direction);
                } catch (NumberFormatException ex) {
                    sendAnswer("Wrong move value.");
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

                sendAnswer("" + moved);
                if (moved) {
                    List<ISession> sessionsToNotify = this.game.getSessions();
                    List<String> messages = new ArrayList<String>(1);
                    messages.add(0, "Update map.");
                    this.server.notifySomeClients(sessionsToNotify, messages);
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
    protected void sendMapArray() {
        if (this.game != null) {
            List<String> linesToSend =
                    Stringalize.mapAndExplosionsAndPlayerInfo(this.game, this.player);
            linesToSend.add(0, "Map array.");
            sendAnswer(linesToSend);
            writeToLog("Session: sended mapArray+explosions+playerInfo to client.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendMapArray warning. Canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void startGame() {
        if (this.game != null) {
            if (!this.game.isStarted()) {
                if (this.player == this.game.getOwner()) { //ONLY HOST(CREATER) CAN START GAME!!!
                    this.game.startGame();

                    List<ISession> sessionsToNotify = this.game.getSessions();
                    List<String> messages = new ArrayList<String>(1);
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
        if (this.game != null) {
            this.game.disconnectFromGame(this.player);
            this.game = null;
            this.player = null;
            sendAnswer("Disconnected from game.");

            List<ISession> sessionsToNotify = this.game.getSessions();
            sessionsToNotify.remove(this);
            List<String> messages = new ArrayList<String>(1);
            messages.add(0, "Another player disconnected from game.");
            this.server.notifySomeClients(sessionsToNotify, messages);


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
        if (this.game != null) { // Always if game!=null player is not null too!
            if (this.game.isStarted()) {
                this.game.placeBomb(this.player); //is player alive checking in model

                List<ISession> sessionsToNotify = this.game.getSessions();
                List<String> messages = new ArrayList<String>(1);
                messages.add(0, "Update map.");
                this.server.notifySomeClients(sessionsToNotify, messages);

                writeToLog("Session: tryed to plant bomb. " + "playerID=" + this.player.getID() + " " + this.player.getPosition().toString());
            }
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: place bomb warning. Canceled. Not joined to any game.");
            return;
        }
    }

    @Override
    protected void sendMap(String[] queryArgs) {
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
        List<String> lst = Stringalize.map(ret);
        lst.add(0, "Downloaded map.");
        sendAnswer(lst);
        writeToLog("Session: client downloaded map." + " Map=" + mapFileName);
    }

    @Override
    protected void sendGameStatus() {
        if (this.game != null) {
            String ret = Stringalize.gameStatus(this.game);

            List<String> messages = new ArrayList<String>(2);
            messages.add(0, "Game status.");
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
    protected void sendMapsList() {
        List<String> maps = Stringalize.mapsList(Creator.createMapsList());
        if (maps != null || maps.size() > 0) {
            maps.add(0, "Maps list.");
            sendAnswer(maps);
            writeToLog("Session: sended maps list to client. Maps count=" + maps.size());
            return;
        } else {
            sendAnswer("No maps on server was founded.");
            writeToLog("Session: sendMapsList error. No maps founded on server.");
            return;
        }
    }

    @Override
    protected void addBot(String[] queryArgs) {
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
        if (this.game != null) {
            List<String> info = Stringalize.gameInfo(this.game, this.player);
            info.add(0,"Game info.");
            sendAnswer(info);
            writeToLog("Session: sended gameInfo to client.");
            return;
        } else {
            sendAnswer("Not joined to any game.");
            writeToLog("Session: sendGameInfo warning. Client tryed to get game info, canceled. Not joined to any game.");
            return;
        }
    }
}
