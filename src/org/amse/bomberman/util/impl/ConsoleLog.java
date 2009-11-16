/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util.impl;

import org.amse.bomberman.util.ILog;

/**
 *
 * @author Kirilchuck V.E.
 */
public class ConsoleLog implements ILog {

    public void println(String message) {
        System.out.println(message);
    }

    public void close() {
        ; // do_nothing;
    }
}
