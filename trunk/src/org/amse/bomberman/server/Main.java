/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

import java.io.IOException;
import java.net.BindException;
import org.amse.bomberman.server.net.Net;

/**
 *
 * @author chibis
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        new serverFrame();
    }
}
