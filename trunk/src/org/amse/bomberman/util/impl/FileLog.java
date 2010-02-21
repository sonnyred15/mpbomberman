/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ILog;

/**
 *
 * @author Kirilchuck V.E.
 */
public class FileLog implements ILog {
   
    private final String fileName = Constants.DEFAULT_FILE_LOG_NAME;
    private PrintWriter writer = null;
    private boolean closed = true;
    
    private FileLog() {        
    }
    
    public FileLog(String fileName) throws IOException {
        this.writer = new PrintWriter(new FileWriter(fileName));
        this.closed = false;

    }
    
    public void println(String message) {
        this.writer.println(message);        
    }

    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
    }

    /**
     * Not supported yet. 
     * Return unmodifiable List with
     * one element: "FileLog doesnot support this operation";
     * @return
     */
    public List<String> getLog() {
        List<String> list= new ArrayList<String>();
        list.add("FileLog doesnot support this operation");
        return Collections.unmodifiableList(list);
    }

    public boolean isClosed() {
        return closed;
    }
}
