package org.amse.bomberman.util.interpretator;

import java.text.ParseException;
import java.util.Stack;

/**
 * Class for building expression
 * by simple Lexical analyzator
 *
 */
public abstract class ExprBuilder {

    /**
     * enumerator for making flag of what we are waiting
     */
    private static class Status {

	private Status() {
	}
	public static final Status WAIT_OPERAND = new Status();
	public static final Status WAIT_OPERATOR = new Status();
    }

    private static class Prio {

	private Prio() {
	}
	public static final int PAR = 0;
	public static final int PLUS = 1;
	public static final int MUL = 2;
	public static final int UNARY = 3;
    }
    // Generating Expression Tree from String
    public static Expression generate(String s) throws ParseException {
	// creating lexical analyzator
	LexAnalyzer la = new LexAnalyzer(s);
	// Stack of operands - ready parts of expression
	Stack<Expression> operands = new Stack<Expression>();
	// Stack of operators - operations and open brackets
	Stack<OpLexema> operators = new Stack<OpLexema>();
	// flag of waiting. First we are waiting operator!
	Status status = Status.WAIT_OPERAND;

	// Getting next lexema
	for (Lexema lex = la.nextLex(); lex != Lexema.EOTEXT; lex = la.nextLex()) {
	    if (status == Status.WAIT_OPERAND) {
		if (lex.type == Lexema.Type.NUMBER) {
		    // Константа вталкивается в стек операндов
		    operands.push(new Constant(((NumLexema) lex).value));
		    status = Status.WAIT_OPERATOR;
		} else if (lex.type == Lexema.Type.IDENT) {
		    // Идентификатор вталкивается в стек операндов
		    operands.push(new Variable(((IdLexema) lex).name));
		    status = Status.WAIT_OPERATOR;
		} else if (lex == Lexema.LEFTPAR) {
		    // Скобка вталкивается в стек операций с приоритетом 0
		    operators.push((OpLexema) (lex));
		} else if (lex.type == Lexema.Type.OPERATOR) {
		    if (((OpLexema) lex).getOperator().equals("-")) {
			// "Исполняются" все унарные операции,
			// а затем унарный минус запоминается с приоритетом 3
			doExpressions(operands, operators, Prio.UNARY);
			operators.push(new OpLexema(((OpLexema) lex).getOperator(), Prio.UNARY));
		    } else {
			throw new ParseException("Unsupported unary operation", 0);
		    }
		} else {
		    throw new ParseException("Unknown operand", 0);
		}
	    } else if (status == Status.WAIT_OPERATOR) {
		if (lex == Lexema.RIGHTPAR) {
		    // "Исполняются" все операции,
		    // а затем из стека выталкивается открывающая скобка
		    doExpressions(operands, operators, Prio.PLUS);

		    if (operators.empty() || operators.peek() != Lexema.LEFTPAR) {
			throw new ParseException("Wrong balance of brackets", 0);
		    } else {
			operators.pop();
		    }
		} else if (lex.type == Lexema.Type.OPERATOR) {
		    // "Исполняются" все операции приоритета не меньше найденного,
		    // а затем встретившаяся операция запоминается в стеке операций
		    int prio = 100500;
		    String op = ((OpLexema) lex).getOperator();
		    char ch = op.charAt(0);
		    switch (ch) {
			case '+':
			case '-': {
			    prio = Prio.PLUS;
			    break;
			}
			case '*':
			case '/': {
			    prio = Prio.MUL;
			    break;
			}

		    }

		    doExpressions(operands, operators, prio);
		    operators.push(new OpLexema(op, prio));

		    status = Status.WAIT_OPERAND;
		} else {
		    throw new ParseException("Unknown operator", 0);
		}
	    }
	}

	if (status != Status.WAIT_OPERATOR) {
	    throw new ParseException("operand was waited", 0);
	}
	// doing all unddid operations 
	doExpressions(operands, operators, 1);
	// checking brackets
	if (!operators.empty()) {
	    throw new ParseException("Wrong balance of brackets or wrong end of expression", 0);
	}
	// in stack must be operand which we have made
	if (operands.empty()) {
	    throw new ParseException("Error. Not enough operands", 0);
	}
	//result of our expression
	Expression result = operands.pop();
	// the stack must be empty now
	if (!operands.empty()) {
	    throw new ParseException("Expression not correct", 0);
	}
	return result;
    }

    private static void doExpressions(
	    Stack<Expression> operands,
	    Stack<OpLexema> operators, int prio) throws ParseException {

	while (!operators.empty() && operators.peek().prio >= prio) {
	    //taking operator
	    OpLexema nextOp = operators.pop();

	    if (operands.empty()) {
		throw new ParseException("Not enough operands", 0);
	    }
	    //taking second operand
	    Expression op2 = operands.pop();

	    if (nextOp.prio == Prio.UNARY) {
		operands.push(new Unary("-", op2));
	    } else {
		if (operands.empty()) {
		    throw new ParseException("Not enough operands", 0);
		}
		Expression op1 = operands.pop();
		operands.push(new Binary(op1, nextOp.operator, op2));
	    }
	}
    }
}
