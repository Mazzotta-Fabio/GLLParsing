package gllflowchart;

import java.util.ArrayList;
import graph.*;
import stack.Stack;

public class ElementoPf {
	
	private Vertex<String> u;
	private int k;
	private Vertex<IdNodoSppf> w;
	private Stack<Statement>statements;
	
	public ElementoPf(Vertex<String> u, int k, Vertex<IdNodoSppf> w,
			Stack<Statement> statements) {
		this.u = u;
		this.k = k;
		this.w = w;
		this.statements = statements;
	}

	public Stack<Statement> getStatements() {
		return statements;
	}

	public Vertex<String> getU() {
		return u;
	}
	
	public int getK() {
		return k;
	}

	public Vertex<IdNodoSppf> getW() {
		return w;
	}

	public String toString(){
		return "< " + u.element() + " , " + k + " , " + statements +" >";
	}
}
