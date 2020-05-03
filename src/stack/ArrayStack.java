package stack;

import exception.EmptyStackException;
import exception.FullStackException;

public class ArrayStack<E> implements Stack<E> {
	
	private int capacity;
	private E S[];
	private int top = -1;
	
	public static final int CAPACITY = 500000;
	
	public ArrayStack(){
		this(CAPACITY);
	}

	public ArrayStack(int cap) {
		capacity = cap;
		S = (E[]) new Object[CAPACITY];
	}

	@Override
	public int size() {
		
		return top + 1;
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	@Override
	public E top() throws EmptyStackException {
		if(isEmpty())
			throw new EmptyStackException("Lo stack è vuoto!");
		return S[top];
	}
     /*è stato modificato per fare in modo di non lanciare la FullExceptionStack incrementando 
      * la capacità e dichiarando un nuovo metodo
      */
	
	@Override
	public void push(E element){
		if(size() == S.length){
			capacity = capacity * 2;
			E[] newS = (E[]) new Object[CAPACITY];
			int size = size();
			for (int i = 0; i<size; i++)
				newS[i] = S[i];
			S=newS;
		}
		S[++top] = element;

	}

	@Override
	public E pop() throws EmptyStackException {
		E element;
		if(isEmpty())
			throw new EmptyStackException("Lo stack è vuoto!");
		element = S[top];
		S[top--] = null;
		return element;
	}
	
	public String toString(){
		String s;
		s = "[";
		if(size() > 0) s+= S[0];
		if(size() > 1)
			for(int i =  1; i <= size()-1; i++){
				s += ", " + S[i];
			}
		return s + "]";
	}
	
	/*
	 * questo metodo è come all'esame va inserito secondo le direttive della professoressa
	 */
	public void union(Stack<E> s){
		ArrayStack<E> temp = new ArrayStack<>();
		
		int sz = s.size(); 
		
		for(int i = 0; i < sz; i++){
			temp.push(s.pop());
		}
		
		int sz2 = this.size();
		
		for(int i=0; i < sz2; i++){
			temp.push(this.pop());
		}
		
		int sz3 = temp.size();
		
		for(int i=0; i < sz3; i++){
			this.push(temp.pop());
		}
	}
	
	public Stack<E> duplica(){
		Stack<E> newS=new ArrayStack<E>();
		int i=0;
		while(S[i]!=null){
			newS.push(S[i]);
			i++;
		}
		return newS;
	}
}
