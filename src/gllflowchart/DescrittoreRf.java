package gllflowchart;

import java.util.ArrayList;
import graph.*;
import stack.Stack;

public class DescrittoreRf {

	private String etichetta;
	private Vertex<String> u;
	private int i;
	private Vertex<IdNodoSppf> v;
	private Stack<Statement> statements;

	public DescrittoreRf(String etichetta, Vertex<String> u, int i,
			Vertex<IdNodoSppf> v, Stack<Statement> statements) {
		this.etichetta = etichetta;
		this.u = u;
		this.i = i;
		this.v = v;
		this.statements = statements;
	}
	
	public Vertex<IdNodoSppf> getV() {
		return v;
	}

	public String getEtichetta() {
		return etichetta;
	}
	
	public Vertex<String> getU() {
		return u;
	}
	
	public int getI() {
		return i;
	}
	
	public Stack<Statement> getStatements() {
		return statements;
	}
	

	public String toString(){
		return "<" + etichetta + " , " + u.element() + " , " + i + " , " +v.element()+ " , " + statements +">";
	}

}
