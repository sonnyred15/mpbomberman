/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.imodel.impl;

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.Player;
import org.amse.bomberman.server.net.tcpimpl.Server;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author chibis
 */
public class ModelTest {

    public ModelTest() {
    }

    private static final int[][] GAME_MAP = {{1,0,0,0,0,2},
                                             {0,0,0,0,0,0},
                                             {0,0,0,0,0,0},
                                             {0,0,0,0,0,0},
                                             {0,0,0,0,0,0},
                                             {3,0,0,0,0,4}};

    private final GameMap map = new GameMap(GAME_MAP);
    private final Game game = new Game(new Server(), map, "testModelGame", -1);
    private Model model = new Model(map, game);
    private Player player1 = new Player("player1", 1);
    private Player player2 = new Player("player2", 2);
    private Player player3 = new Player("player3", 3);
    private Player player4 = new Player("player4", 4);
    {
        player1.setX(0);
        player1.setY(0);

        player2.setX(0);
        player2.setY(5);

        player3.setX(5);
        player3.setY(0);

        player4.setX(5);
        player4.setY(5);
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

    private static final int MOVE_ITER = 1000000;

    @Test
    public void testDoMove() throws InterruptedException {
        System.out.println();
        System.out.println("Testing concurrent call to doMove");
        //seting position of two players for test
        model.doMove(player2, Direction.LEFT);
        model.doMove(player2, Direction.LEFT);
        model.doMove(player2, Direction.LEFT); // now position in 0 row is |1 0 2 0 0 0 |

        Thread t1 = new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < MOVE_ITER; i++) {
                    if(player1.getY()>1){
                        fail("Syncronization problem. Player moved through other player");
                    }
                    model.doMove(player1, Direction.RIGHT);  //trying to go right
                    if(map.isEmpty(0, 0) && map.isEmpty(0, 2)){ //if two players in one square
                        fail("Syncronization problem. Two players moved in same square.");
                    }
                    model.doMove(player1, Direction.LEFT);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < MOVE_ITER; i++) {
                    if(player2.getY()<1){
                        fail("Syncronization problem. Player moved through other player");
                    }
                    model.doMove(player2, Direction.LEFT);  //trying to go left
                    if(map.isEmpty(0, 0) && map.isEmpty(0, 2)){ //if two players in one square
                        fail("Syncronization problem. Two players moved in same square.");
                    }
                    if(map.isEmpty(0, 2)){
                        model.doMove(player2, Direction.RIGHT);
                    }
                }
            }
        });

        t1.start();
        t2.start();

        int deadThreadsCounter = 0;
        while (deadThreadsCounter != 2) {
            deadThreadsCounter = 0;

            if (!t1.isAlive()) {
                deadThreadsCounter++;
            }
            if (!t2.isAlive()) {
                deadThreadsCounter++;
            }
            Thread.sleep(5000);
        }

        model.printToConsole();
    }

    //@Test
    public void testPlaceBomb() {
    }

    //@Test
    public void testGetMapName() {
    }

}