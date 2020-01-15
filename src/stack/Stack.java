package stack;

import exception.EmptyStackException;
/*
 * questa è la prima cosa da fare sempre quando si implementa un adt:
 * inserire i metodi che l'adt supporta, dichiarandoli e definisce come usarli(API)
 */
public interface Stack<E> {
	
	public int size();
	public boolean isEmpty();
	public E top() throws EmptyStackException;
	public void push(E element);
	public E pop() throws EmptyStackException;
	public Stack<E> duplica();
}