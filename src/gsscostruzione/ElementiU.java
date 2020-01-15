package gsscostruzione;

import graph.Vertex;

public class ElementiU {
	private String etichetta;
	private Vertex<String> u;
	public ElementiU(String etichetta, Vertex<String> u) {
		this.etichetta = etichetta;
		this.u = u;
	}
	
	public String getEtichetta() {
		return etichetta;
	}
	
	public Vertex<String> getU() {
		return u;
	}
	
	public String toString(){
		return "< "+ etichetta + " , " + u.element() + " >";
	}
	

}
