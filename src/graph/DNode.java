package graph;

import exception.InvalidPositionException;

//nodo singolo di una lista
public class DNode<E> implements Position<E>{
	private DNode<E> prev,next;//puntatore al nodo successivo e precedente
	private E element;
	
	public DNode(DNode<E> newPrev,DNode<E> newNext, E e) {
		prev=newPrev;
		next=newNext;
		element=e;
	}
	@Override
	public E element() {
		if ((prev == null) && (next == null)) {
			 throw new InvalidPositionException("La posizione non e` in una lista!");
		}
		return element;
	}
	public DNode<E> getPrev() {
		return prev;
	}
	public void setPrev(DNode<E> prev) {
		this.prev = prev;
	}
	public DNode<E> getNext() {
		return next;
	}
	public void setNext(DNode<E> next) {
		this.next = next;
	}
	public void setElement(E element) {
		this.element = element;
	}

}