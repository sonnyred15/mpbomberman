package org.amse.bomberman.util.interpretator;

/**
 * Class for representation of constant
 * which implements Expression.
 * Only integer constants are supported
 */
import java.util.Iterator;
import java.util.Map;

public class Constant implements Expression, Comparable<Constant> {

    private final Double value;    // More often used constants
    public final static Constant ZERO = new Constant(new Double(0));
    public final static Constant ONE = new Constant(new Double(1));

    // Constructor of constant
    public Constant(Double value) {
	this.value = value;
    }
    // function to get value of constant
    public Double getValue() {
	return value;
    }

    // Iterator of constant is empty iterator
    private static class ConstIterator implements Iterator<Variable> {

	public boolean hasNext() {
	    return false;
	}

	public Variable next() {
	    return null;
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    // Object class methods
    @Override
    public boolean equals(Object o) {
	return (o instanceof Constant) && (value == ((Constant) o).value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
	return (value.toString());
    }

    // Comparable interface methods
    public int compareTo(Constant c) {
	return value.compareTo(c.value);
    }

    //  Iterable interface methods
    public Iterator<Variable> iterator() {
	return new ConstIterator();
    }

    //  Expression interface methods
    public Expression dash(Variable v) {
	return ZERO;
    }

    public Double evaluate(Map<Variable, Constant> context) {
	return value;
    }
}