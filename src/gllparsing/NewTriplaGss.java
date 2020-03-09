package gllparsing;

import graph.Vertex;

public class NewTriplaGss {
	private String etichetta;
	private Vertex<String> u;
	private Vertex<IdNodoSppf> w;
	private int i;
	public NewTriplaGss(String etichetta, Vertex<String> u, int i,Vertex<IdNodoSppf> w) {
		this.etichetta = etichetta;
		this.u=u;
		this.i = i;
		this.w=w;
	}
	
	public String getEtichetta() {
		return etichetta;
	}
	
	public Vertex<IdNodoSppf> getW() {
		return w;
	}
	public Vertex<String> getU() {
		return u;
	}
	
	public int getI() {
		return i;
	}
	public String toString(){
		return "<" + etichetta + " , " + u.element() + " , " +i + " , " + w.element() +">";
	}

}
