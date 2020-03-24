package gsscostruzione;

import graph.Vertex;

public class TriplaBaseGSS {
	private String etichetta;
	private Vertex<String> u;
	private int i;
	public TriplaBaseGSS(String etichetta, Vertex<String> u, int i) {
		this.etichetta = etichetta;
		this.u=u;
		this.i = i;
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
	public String toString(){
		return "<" + etichetta + " , " + u.element() + " , " +i + ">";
	}
}
