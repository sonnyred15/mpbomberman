/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.imodel.impl;

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.Pair;
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
    private final Game game = new Game(map, "testModelGame", -1);
    private Model model = new Model(map, game);
    private Player player1 = new Player("player1");
    private Player player2 = new Player("player2");
    private Player player3 = new Player("player3");
    private Player player4 = new Player("player4");
    {
        player1.setPosition(new Pair(0, 0));

        player2.setPosition(new Pair(0, 5));

        player3.setPosition(new Pair(5, 0));

        player4.setPosition(new Pair(5, 5));
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
        model.tryDoMove(player2, Direction.LEFT);
        model.tryDoMove(player2, Direction.LEFT);
        model.tryDoMove(player2, Direction.LEFT); // now position in 0 row is |1 0 2 0 0 0 |

        Thread t1 = new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < MOVE_ITER; i++) {
                    if(player1.getPosition().getY()>1){
                        fail("Syncronization problem. Player moved through other player");
                    }
                    model.tryDoMove(player1, Direction.RIGHT);  //trying to go right
                    if(map.isEmpty(0, 0) && map.isEmpty(0, 2)){ //if two players in one square
                        fail("Syncronization problem. Two players moved in same square.");
                    }
                    model.tryDoMove(player1, Direction.LEFT);
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < MOVE_ITER; i++) {
                    if(player2.getPosition().getY()<1){
                        fail("Syncronization problem. Player moved through other player");
                    }
                    model.tryDoMove(player2, Direction.LEFT);  //trying to go left
                    if(map.isEmpty(0, 0) && map.isEmpty(0, 2)){ //if two players in one square
                        fail("Syncronization problem. Two players moved in same square.");
                    }
                    if(map.isEmpty(0, 2)){
                        model.tryDoMove(player2, Direction.RIGHT);
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