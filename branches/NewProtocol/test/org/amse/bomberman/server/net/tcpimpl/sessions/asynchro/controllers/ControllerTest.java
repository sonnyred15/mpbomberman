/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

import java.util.List;
import org.amse.bomberman.protocol.InvalidDataException;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.net.Session;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.ClientState;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ControllerTest {//TODO TEST HERE ONLY PROTOTYPES

    public ControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of sendGames method, of class Controller.
     */
    @Test(expected=InvalidDataException.class)
    public void testSendGames() {
        System.out.println("sendGames");
        Controller instance = null;
        instance.sendGames();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryCreateGame method, of class Controller.
     */
    @Test
    public void testTryCreateGame() throws Exception {
        System.out.println("tryCreateGame");
        List<String> args = null;
        Controller instance = null;
        instance.tryCreateGame(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryJoinGame method, of class Controller.
     */
    @Test
    public void testTryJoinGame() throws Exception {
        System.out.println("tryJoinGame");
        List<String> args = null;
        Controller instance = null;
        instance.tryJoinGame(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryDoMove method, of class Controller.
     */
    @Test
    public void testTryDoMove() throws Exception {
        System.out.println("tryDoMove");
        List<String> args = null;
        Controller instance = null;
        instance.tryDoMove(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendGameMapInfo method, of class Controller.
     */
    @Test
    public void testSendGameMapInfo() {
        System.out.println("sendGameMapInfo");
        Controller instance = null;
        instance.sendGameMapInfo();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryStartGame method, of class Controller.
     */
    @Test
    public void testTryStartGame() {
        System.out.println("tryStartGame");
        Controller instance = null;
        instance.tryStartGame();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryLeave method, of class Controller.
     */
    @Test
    public void testTryLeave() {
        System.out.println("tryLeave");
        Controller instance = null;
        instance.tryLeave();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryPlaceBomb method, of class Controller.
     */
    @Test
    public void testTryPlaceBomb() {
        System.out.println("tryPlaceBomb");
        Controller instance = null;
        instance.tryPlaceBomb();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendDownloadingGameMap method, of class Controller.
     */
    @Test
    public void testSendDownloadingGameMap() throws Exception {
        System.out.println("sendDownloadingGameMap");
        List<String> args = null;
        Controller instance = null;
        instance.sendDownloadingGameMap(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendGameStatus method, of class Controller.
     */
    @Test
    public void testSendGameStatus() {
        System.out.println("sendGameStatus");
        Controller instance = null;
        instance.sendGameStatus();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendGameMapsList method, of class Controller.
     */
    @Test
    public void testSendGameMapsList() {
        System.out.println("sendGameMapsList");
        Controller instance = null;
        instance.sendGameMapsList();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryAddBot method, of class Controller.
     */
    @Test
    public void testTryAddBot() throws Exception {
        System.out.println("tryAddBot");
        List<String> args = null;
        Controller instance = null;
        instance.tryAddBot(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendGameInfo method, of class Controller.
     */
    @Test
    public void testSendGameInfo() {
        System.out.println("sendGameInfo");
        Controller instance = null;
        instance.sendGameInfo();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addMessageToChat method, of class Controller.
     */
    @Test
    public void testAddMessageToChat() throws Exception {
        System.out.println("addMessageToChat");
        List<String> args = null;
        Controller instance = null;
        instance.addMessageToChat(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendNewMessagesFromChat method, of class Controller.
     */
    @Test
    public void testSendNewMessagesFromChat() {
        System.out.println("sendNewMessagesFromChat");
        Controller instance = null;
        instance.sendNewMessagesFromChat();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of tryRemoveBot method, of class Controller.
     */
    @Test
    public void testTryRemoveBot() {
        System.out.println("tryRemoveBot");
        Controller instance = null;
        instance.tryRemoveBot();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendGamePlayersStats method, of class Controller.
     */
    @Test
    public void testSendGamePlayersStats() {
        System.out.println("sendGamePlayersStats");
        Controller instance = null;
        instance.sendGamePlayersStats();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setClientNickName method, of class Controller.
     */
    @Test
    public void testSetClientNickName() throws Exception {
        System.out.println("setClientNickName");
        List<String> args = null;
        Controller instance = null;
        instance.setClientNickName(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSession method, of class Controller.
     */
    @Test
    public void testGetSession() {
        System.out.println("getSession");
        Controller instance = null;
        Session expResult = null;
        Session result = instance.getSession();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sendToClient method, of class Controller.
     */
    @Test
    public void testSendToClient() {
        System.out.println("sendToClient");
        ProtocolMessage<Integer, String> message = null;
        Controller instance = null;
        instance.sendToClient(message);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getState method, of class Controller.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        Controller instance = null;
        ClientState expResult = null;
        ClientState result = instance.getState();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setState method, of class Controller.
     */
    @Test
    public void testSetState() {
        System.out.println("setState");
        ClientState state = null;
        Controller instance = null;
        instance.setState(state);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGamePlayer method, of class Controller.
     */
    @Test
    public void testGetGamePlayer() {
        System.out.println("getGamePlayer");
        Controller instance = null;
        NetGamePlayer expResult = null;
        NetGamePlayer result = instance.getGamePlayer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}