package graph;

import exception.*;

public interface PositionList <E> extends Iterable<E>{
	public int size();//serve per vedere quante posizioni ci sono
	public boolean isEmpty();//serve per vedere se è vuoto
	public Position<E> last()throws EmptyListException;//ottiene ultimo elemento
	public Position<E> first() throws EmptyListException;//ottiene il primo elemento
	public Position<E> next(Position<E> p)throws InvalidPositionException, BoundaryViolationException;//serve per accedere ad un elemento successivo a p
	public Position<E> prev(Position<E> p)throws InvalidPositionException, BoundaryViolationException;//serve per accedere ad un elemento precedente a p
	public void addLast(E e);//aggiunge elemento alla fine
	public void addFirst(E e);//aggiunge elemento all'inizio
	public void addAfter(Position<E> p,E e)throws InvalidPositionException;//aggiungi elemento e dopo p
	public void addBefore(Position<E> p,E e)throws InvalidPositionException;//aggiungi elemento e dopo p
	public E remove(Position<E> p)throws InvalidPositionException;//rimuovi p dalla lista
	public Iterable <Position<E>> positions();
}
