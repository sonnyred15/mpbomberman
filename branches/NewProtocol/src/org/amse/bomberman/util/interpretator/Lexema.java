package org.amse.bomberman.util.interpretator;

/**
 * Lexems to analyze Expression
 */
public class Lexema {
    // Types of Lexems. Imitates enum.
    public static class Type {

	public static final Type IDENT = new Type();
	public static final Type NUMBER = new Type();
	public static final Type OPERATOR = new Type();
	public static final Type EOTEXT = new Type();
	public static final Type UNKNOWN = new Type();

	private Type() {
	}
    }    // More common used lexems
    public static final Lexema LEFTPAR = new OpLexema("(", 0);
    public static final Lexema RIGHTPAR = new OpLexema(")", 0);
    public static final Lexema MINUS = new OpLexema("-", 1);
    public static final Lexema EOTEXT = new Lexema(Type.EOTEXT);
    public static final Lexema UNKNOWN = new Lexema(Type.EOTEXT);    // Type of THIS lexem.
    //public field!!!
    public final Type type;

    // constructor
    public Lexema(Type type) {
	this.type = type;
    }
}

