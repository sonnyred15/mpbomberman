package org.amse.bomberman.util.interpretator;


/**
 * Number lexema.
 * An int value.
 */
public class NumLexema extends Lexema {
    // the meaning of number
    public final Double value;

    // constructor
    public NumLexema(String value) {
	super(Lexema.Type.NUMBER);
	this.value = new Double(value);
    }
}
