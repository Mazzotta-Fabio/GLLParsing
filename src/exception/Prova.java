package exception;

import gllflowchart.Statement;
import stack.ArrayStack;
import stack.Stack;

public class Prova {
	public static void main(String []args) {
		Stack<Statement>s=new ArrayStack<Statement>();
		s.push(new Statement("A","b","S","d"));
		s.push(new Statement("A","m","l","d"));
		Statement st=s.pop();
		System.out.println(s.top());
		s.top().setSecondoAttacco(s.pop().getType1(), s.pop().getType1());
		System.out.println(s.top());
	}
}
