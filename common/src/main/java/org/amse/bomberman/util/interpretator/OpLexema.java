package org.amse.bomberman.util.interpretator;

/**
 * Operation Lexema
 * It`s a sign! (+ - *)
 * or brackets.(look more often used lexems in Lexema)
 */
public class OpLexema extends Lexema {
    // operation sign
    public final String operator;
    // priority of operation
    public final int prio;

    // constructor
    public OpLexema(String operator, int priority) {
	super(Lexema.Type.OPERATOR);
	this.operator = operator;
	this.prio = priority;
    }
    
    public String getOperator(){
	return this.operator;
    }
}
