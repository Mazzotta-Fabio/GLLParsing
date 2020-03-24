package gsscostruzione;

import graph.Vertex;

public class ElementoBaseP {
	private Vertex<String> u;
	private int k;
	public ElementoBaseP(Vertex<String> u, int k) {
		this.u = u;
		this.k = k;
	}
	
	public Vertex<String> getU() {
		return u;
	}
	
	public int getK() {
		return k;
	}
	
	public String toString(){
		return "< " + u.element() + " , " + k + " >";
	}
}
