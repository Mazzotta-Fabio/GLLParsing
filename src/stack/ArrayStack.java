package stack;

import java.util.ArrayList;

import exception.EmptyStackException;
import exception.FullStackException;

public class ArrayStack<E> implements Stack<E>, Cloneable{

	private ArrayList<E> S;;
	
	public ArrayStack(){
		S = new ArrayList<E>();	
	}

	@Override
	public int size() {
		return S.size();
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	@Override
	public E top() throws EmptyStackException {
		if(isEmpty())
			throw new EmptyStackException("Lo stack è vuoto!");
		return S.get(S.size()-1);
	}
     /*è stato modificato per fare in modo di non lanciare la FullExceptionStack incrementando 
      * la capacità e dichiarando un nuovo metodo
      */
	
	@Override
	public void push(E element){
		S.add(element);
	}

	@Override
	public E pop() throws EmptyStackException {
		if(isEmpty())
			throw new EmptyStackException("Lo stack è vuoto!");
		return S.remove(S.size()-1);
	}
	
	public String toString(){
		return S.toString();
	}
	
	public Object clone(){
		try {
			ArrayStack<E> cloned=(ArrayStack<E>)super.clone();
			cloned.S=(ArrayList<E>)S.clone();
			return cloned;
		}
		catch(CloneNotSupportedException e) {
			return null;
		}
		
	}
	
	public Stack <E>duplica(){
		/*
		Stack<E> newS=new ArrayStack<E>();
		for(E e:this.S) {
			newS.push(e);
		}
		return newS;
		*/ 
		return (Stack<E>)this.clone();
	}
	
}
