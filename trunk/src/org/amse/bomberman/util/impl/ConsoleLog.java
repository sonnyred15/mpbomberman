/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.amse.bomberman.util.ILog;

/**
 * Note that information in console will be lost after closing console.
 *
 * @author Kirilchuck V.E.
 */
public class ConsoleLog implements ILog {
    private final List<String> temporaryLog = new ArrayList<String>();

    public void println(String message) {
        if(temporaryLog.size()==1024){
            temporaryLog.clear();
        }
        temporaryLog.add(message);
        System.out.println(message);
    }

    public void close() {
        ; // do_nothing;
    }

    /**
     * Return ONLY last 1024 lines of log.
     * @return last 1024 lines of log.
     */
    public List<String> getLog(){
        return Collections.unmodifiableList(temporaryLog);
    }
}
