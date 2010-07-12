
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net;

/**
 *
 * @author Kirilchuk V.E
 */
public enum ServerState {
    STARTED() {
        @Override
        public void start() {
            System.err.println("Server: start error. Already in started state.");
            throw new IllegalStateException("Server: start error. " +
                                            "Already in started state.");

        }
        @Override
        public void shutdown() {
            
        }
    },
    SHUTDOWNED() {
        @Override
        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public void shutdown() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public abstract void start() throws IllegalStateException;

    public abstract void shutdown() throws IllegalStateException;
}
