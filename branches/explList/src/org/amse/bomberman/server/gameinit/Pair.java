/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit;

/**
 *
 * @author Kirilchuck V.E.
 */
public class Pair {

        private int x;
        private int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX(){
            return this.x;
        }
        
        public int getY(){
            return this.y;
        }
}
