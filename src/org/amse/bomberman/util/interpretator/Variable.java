package org.amse.bomberman.util.interpretator;

/**
 * Representation of Variable 
 * which implements Expression
 */
import java.util.Iterator;
import java.util.Map;

public class Variable implements Expression {
    // Name of variable
    private final String name;

    // constructor
    public Variable(String name) {
	this.name = name;
    }
    // function to get name of Variable
    public String getName() {
	return name;
    }

    // Iterator realization. Iterator of one element!
    private static class VarIterator implements Iterator<Variable> {

	private Variable v;

	// Конструктор запоминает итерируемую переменную
	public VarIterator(Variable v) {
	    this.v = v;
	}

	// Реализация методов итератора
        @Override
	public boolean hasNext() {
	    return v != null;
	}

        @Override
	public Variable next() {
	    Variable saveV = v;
	    v = null;
	    return saveV;
	}

        @Override
	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
    //  Object class methods
    //variables are equal if names are the same
    @Override
    public boolean equals(Object o) {
	return (o instanceof Variable) && (name.equals(((Variable) o).name));
    }

    @Override
    public int hashCode() {
	return name.hashCode();
    }

    @Override
    public String toString() {
	return name;
    }

    // Iterator interface method
    @Override
    public Iterator<Variable> iterator() {
	return new VarIterator(this);
    }

    // Expression interface method
    @Override
    public Expression dash(Variable v) {
	return v.equals(this) ? Constant.ONE : Constant.ZERO;
    }

    @Override
    public Double evaluate(Map<Variable, Constant> context) {
	try{
	    Double bn = context.get(this).getValue();
	    return bn;
	} catch(NullPointerException ex){
	    throw new IllegalArgumentException(name + ":unknown Variable ");
	}
    }
}