/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util;

import java.io.IOException;

/**
 *
 * @author chibis
 */
public interface ILog {

    void println(String message);

    void close() throws IOException;
}
