package gllparsing;

import graph.Vertex;

public class NewElementoP {
	private Vertex<String> u;
	private int k;
	private Vertex<IdNodoSppf> z;
	public NewElementoP(Vertex<String> u, int k,Vertex<IdNodoSppf>z) {
		this.u = u;
		this.k = k;
		this.z=z;
	}
	
	public Vertex<String> getU() {
		return u;
	}
	
	public int getK() {
		return k;
	}
	
	public Vertex<IdNodoSppf>getZ(){
		return z;
	}
	
	public String toString(){
		return "< " + u.element() + " , " + k + " , " + z.element() + " >";
	}
}
