/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.net;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NetException extends Exception {

    private final static long serialVersionUID = 1L;

    public NetException() {
        super("NetException!!!\nServer is inaccessible now.\nPlease reconnect!");
    }
}
