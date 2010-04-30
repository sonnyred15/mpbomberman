package org.amse.bomberman.util.interpretator;

/**
 * Class for representation of binary operation
 * which is implements Expression
 * Three operations are supported + - *
 */
import java.util.Iterator;
import java.util.Map;

public class Binary implements Expression {

    private final Expression left,  right;
    private final String operator;  // "+", "-" or "*"

    // Constructor
    public Binary(Expression left, String operator, Expression right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
    }
    // Get Functions
    public Expression getLeft() {
	return left;
    }

    public Expression getRight() {
	return right;
    }

    public String getOperator() {
	return operator;
    }

    // override of standart Object methods
    @Override
    public boolean equals(Object o) {
	// two binary are equals if
	// operands and operations are equal.
	if (!(o instanceof Binary)) {
	    return false;
	//class cast object to Binary
	}
	Binary b = (Binary) o;

	return operator.equals(b.operator) &&
		left.equals(b.left) && right.equals(b.right);
    }

    @Override
    public int hashCode() {
	return 19 * left.hashCode() + 23 * right.hashCode() + 37 * operator.hashCode();
    }

    @Override
    public String toString() {
	return "(" + left + operator + right + ")";
    }

    // realization of iterator from Iterable interface
    public Iterator<Variable> iterator() {
	return new PairIterator<Variable>(left.iterator(), right.iterator());
    }

    // realization of Expression interface methods
    public Expression dash(Variable v) {
	// 
	Expression leftDash = left.dash(v);
	Expression rightDash = right.dash(v);

	if (operator.equals("+") || operator.equals("-")) {
	    return new Binary(leftDash, operator, rightDash);
	} else if (operator.equals("*")) {
	    return new Binary(
		    new Binary(leftDash, "*", right),
		    "+",
		    new Binary(left, "*", rightDash));
	}
	// only +, -, * operations are supported
	throw new IllegalArgumentException();
    }

    public Double evaluate(Map<Variable, Constant> context) {
	// evaluating meanings of left and right operands
	Double leftVal = left.evaluate(context);
	Double rightVal = right.evaluate(context);

	if (operator.equals("+")) {
	    return leftVal + rightVal;
	} else if (operator.equals("-")) {
	    return leftVal - rightVal;
	} else if (operator.equals("*")) {
	    return leftVal * rightVal;
	} else if (operator.equals("/")) {	    
	    try{
	        Double bn = leftVal / rightVal;
		return bn;
	    } catch(Exception ex) {
		throw new ArithmeticException("Divide by zero exception");
	    }
	}

	throw new IllegalArgumentException();
    }
}
