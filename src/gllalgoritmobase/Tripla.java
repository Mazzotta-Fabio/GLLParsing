package gllalgoritmobase;

import stack.Stack;

public class Tripla {
	private String etichetta;
	private Stack<String> s;
	private int i;
	public Tripla(String etichetta, Stack<String> s, int i) {
		this.etichetta = etichetta;
		this.s = s;
		this.i = i;
	}
	
	public String getEtichetta() {
		return etichetta;
	}
	
	public Stack<String> getS() {
		return s;
	}
	
	public int getI() {
		return i;
	}
	public String toString(){
		return "<" + etichetta + " , " + s.toString() + " , " +i + ">";
	}
}
