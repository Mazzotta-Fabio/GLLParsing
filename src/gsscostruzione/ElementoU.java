package gsscostruzione;

import graph.Vertex;

public class ElementoU {
	private String etichetta;
	private Vertex<String> u;
	public ElementoU(String etichetta, Vertex<String> u) {
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
