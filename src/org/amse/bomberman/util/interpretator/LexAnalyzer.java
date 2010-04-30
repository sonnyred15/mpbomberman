package org.amse.bomberman.util.interpretator;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

/**
 * Lexical analizator.
 */
public class LexAnalyzer {
    // input stream of symbols
    private Reader is;
    // next symbol
    private char nextChar;

    // constructor from stream.
    public LexAnalyzer(Reader is) {
	this.is = is;
	getNext();
    }

    // constructor from String
    public LexAnalyzer(String s) {
	this(new StringReader(s));
    }

    // reading next symbol
    private void getNext() {
	try {
	    int nextSymbol = is.read();
	    if (nextSymbol == -1) {
		// stream is ended.
		//char 0 ???
		this.nextChar = 0;
	    } else {
		this.nextChar = (char) nextSymbol;
	    }
	} catch (IOException e) {
	    // Error while trying to read from stream
	    this.nextChar = 0;
	}
    }

    // skipping whitespaces
    private void skip() {
	while (Character.isWhitespace(nextChar)) {
	    getNext();
	}
    }

    /**
     * Reading and returning of next lexema.
     * Functions is think that first symbol of
     * lexema had been already readed!
     * 
     * Functions stops when EOTEXT reached or 
     * other lexema is readed;
     * 
     * return lexema or EOTEXT,
     */
    public Lexema nextLex() {
	// finding first non zero symbol
	skip();
	Lexema l;
	switch (nextChar) {
	    case 0:  // end of stream or error while reading from the stream
		return Lexema.EOTEXT;
	    case '(':
		getNext();
		return Lexema.LEFTPAR;//priority is zero
	    case ')':
		getNext();
		return Lexema.RIGHTPAR;
	    case '+':
	    case '-':
		l = new OpLexema(Character.toString(nextChar), 1);
		getNext();		
		return l;
	    case '*':
	    case '/':
		l = new OpLexema(Character.toString(nextChar), 2);
		getNext();
		return l;
	    default:
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if (Character.isDigit(nextChar)) {
		    // analyzing the number           
		    StringBuilder n = new StringBuilder();
		    do {//while character is digit
			n.append(nextChar);//getting digit
			getNext();//changing nextChar
		    } while (Character.isDigit(nextChar));
		    //return numberLexema
		    return new NumLexema(n.toString());
		} else if (Character.isLetter(nextChar)) {
		    // analyzing identificator
		    StringBuffer sb = new StringBuffer();
		    do {//while character is leter or digit e.g. value124
			sb.append(Character.toString(nextChar));
			getNext();//changing nextChar
		    } while (Character.isLetterOrDigit(nextChar));
		    //return new identificatorLexema
		    return new IdLexema(sb.toString());
		} else {
		    return Lexema.UNKNOWN;
		}
	}
    }
}
