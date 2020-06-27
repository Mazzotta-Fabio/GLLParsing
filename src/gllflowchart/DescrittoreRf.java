package gllflowchart;

import java.util.ArrayList;

import dataset.Statement;
import graph.*;
import stack.Stack;

public class DescrittoreRf {

	private String etichetta;
	private Vertex<String> u;
	private int i;
	private Vertex<IdNodoSppf> v;
	private Stack<Statement> statements;
	private ArrayList<Integer> tokenViews;

	public DescrittoreRf(String etichetta, Vertex<String> u, int i,Vertex<IdNodoSppf> v, Stack<Statement> statements, ArrayList<Integer>tokenViews) {
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

	public ArrayList<Integer> getTokenViews() {
		return tokenViews;
	}

	public String toString(){
		return "<" + etichetta + " , " + u.element() + " , " + i + " , " +v.element()+ " , " + statements + " , " + tokenViews + ">";
	}

}
