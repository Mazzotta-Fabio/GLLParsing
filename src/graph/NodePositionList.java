package graph;

import java.util.Iterator;

import exception.*;

public class NodePositionList<E> implements PositionList<E>,Cloneable{

	protected int numElts;
	protected DNode<E> header, trailer;
	
	public NodePositionList(){
		numElts = 0;
		header = new DNode<E>(null, null, null);
		trailer = new DNode<E>(header, null, null);
		header.setNext(trailer);
	}
	
	public DNode<E> checkPosition(Position<E> p) throws InvalidPositionException{
		
		if(p == null) throw new InvalidPositionException("Posizione nulla passata a NodeList");
		if(p == header) throw new InvalidPositionException("Header non è una posizione valida");
		if(p == trailer) throw new InvalidPositionException("Trailer non è una posizione valida");
		
		try{
			DNode<E> temp = (DNode<E>) p;
			if((temp.getPrev() == null) || (temp.getNext() == null))
				throw new InvalidPositionException("Posizione non appartenente ad una valida NodeList");
			return temp;
		}catch(ClassCastException e){
			throw new InvalidPositionException("Posizione di tipo sbagliato per questo contenitore");
		}
		
	}
	@Override
	public int size() {
		return numElts;
	}
	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}
	@Override
	public void addBefore(Position<E> p, E e)throws InvalidPositionException {
		DNode<E> v = checkPosition(p);
		numElts++;
		DNode<E> newNode = new DNode<E>(v.getPrev(),v,e);
		v.getPrev().setNext(newNode);
		v.setPrev(newNode);
	}
	@Override
	public void addAfter(Position<E> p, E e)
			throws InvalidPositionException {
		DNode<E> v = checkPosition(p);
		numElts++;
		DNode<E> newNode = new DNode<E>(v,v.getNext(),e);
		v.getNext().setPrev(newNode);
		v.setNext(newNode);
	}
	@Override
	public void addFirst(E e) {
		numElts++;
		DNode<E> newNode = new DNode<E>(header, header.getNext(), e);
		header.getNext().setPrev(newNode);
		header.setNext(newNode);
		
	}
	@Override
	public void addLast(E e) {
		numElts++;
		DNode<E> newNode = new DNode<E>(trailer.getPrev(),trailer, e);
		trailer.getPrev().setNext(newNode);
		trailer.setPrev(newNode);
		
	}
	@Override
	public E remove(Position<E> p) throws InvalidPositionException {
		DNode<E> v = checkPosition(p);
		numElts--;
		E el = v.element();
		DNode<E> vPrev = v.getPrev();
		DNode<E> vNext = v.getNext();
		vPrev.setNext(vNext);
		vNext.setPrev(vPrev);
		v.setNext(null);
		v.setPrev(null);
	    return el;
	}
	@Override
	public Position<E> first() throws EmptyListException {
		if(isEmpty())
			throw new EmptyListException("La lista è vuota");
		return header.getNext();
	}
	@Override
	public Position<E> last() throws EmptyListException {
		if(isEmpty())
			throw new EmptyListException("La lista è vuota");
		return trailer.getPrev();
	}
	@Override
	public Position<E> prev(Position<E> p) throws InvalidPositionException,
			BoundaryViolationException {
		DNode<E> v = checkPosition(p);
		DNode<E> prev = v.getPrev();
		if(prev == header)
			throw new BoundaryViolationException("Non posso retrocedere oltre l'inizio della lista");
		return prev;
	}
	
	@Override
	public Position<E> next(Position<E> p) throws InvalidPositionException,
			BoundaryViolationException {
		DNode<E> v = checkPosition(p);
		DNode<E> next = v.getNext();
		if(next == trailer)
			throw new BoundaryViolationException("Non posso avanzare oltre la fine della lista");
		return next;
	}

	@Override
	public Iterator<E> iterator() {
		return new ElementIterator<E>(this);
	}

	@Override
	public Iterable<Position<E>> positions() {
		  PositionList <Position<E>> toReturn = new NodePositionList <Position<E>>();
		  if(!isEmpty()){ 
		   Position <E> current=first();
		  for(int i=0;i<size()-1;i++){
		
		      toReturn.addLast(current);
		      current=next(current);
		   } 
		  toReturn.addLast(last()); }   
		  return (toReturn);
	}
	
}

