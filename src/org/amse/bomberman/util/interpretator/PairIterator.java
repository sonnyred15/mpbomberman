package org.amse.bomberman.util.interpretator;

/**
 * class that merges two iterators
 */
import java.util.Iterator;

public class PairIterator<E> implements Iterator<E> {

    private Iterator<E> it1,  it2;

    public PairIterator(Iterator<E> it1, Iterator<E> it2) {
	this.it1 = it1;
	this.it2 = it2;
    }

    public boolean hasNext() {
	return it1.hasNext() || it2.hasNext();
    }

    public E next() {
	if (it1.hasNext()) {
	    return it1.next();
	} else {
	    return it2.next();
	}
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }
}
