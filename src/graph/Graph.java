package graph;

import java.util.*;

public class Graph<E> {
	private int numVer;//numero di vertici
	private int numEdg;//numero di archi
	private PositionList<Vertex<E>> VList; //lista dei vertici
	private PositionList<Edge<E>> EList; //lista degli archi
	
	
	public Graph() {
		numVer=0;
		numEdg=0;
		VList=new NodePositionList<Vertex<E>>();
		EList=new NodePositionList<Edge<E>>();
		
	}
	
	public Edge<E> insertEdge(Vertex<E> u,Vertex<E>v,E info) {
		numEdg++;
		Edge <E>e = new Edge<E>(u,v,info);
		EList.addLast(e);
		//arco non orientato da "u" a "v"
		u.insertAdjacent(v);
		v.insertAdjacent(u);
		return e;
	}
	public Vertex<E> insertVertex(E info) {
		numVer++;
		Vertex<E> v = new Vertex<E>(info);
		VList.addLast(v);
		return v;
	}
	
	public Edge<E> insertDirectedEdge(Vertex <E>u, Vertex <E>v,E info) {
		numEdg++;
		Edge<E>e = new Edge<E>(u,v,info);	
		EList.addLast(e);
		u.insertAdjacent(v);
		return e;
	}	
	public Iterator<Vertex<E>> vertices(){
		return VList.iterator();
	}
	
	public Iterator<Edge<E>> edges(){
		return EList.iterator();
	}
	
	public Iterator<Vertex<E>> adjacentVertex(Vertex<E> v){
		return v.getAdjList().iterator();
	}
	
	public Vertex<E> getFirstNode(){
		return VList.first().element();
	}
	public Vertex<E>getLastNode(){
		return VList.last().element();
	}
	
	public int getNumeVer() {
		return numVer;
	}
	
	public String toString(){
		String frase=" ";
		Iterator<Edge<E>> it=edges();
		while(it.hasNext()){
			Edge<E>e=it.next();
			Vertex<E>u=e.getStartVertex();
			Vertex<E>v=e.getEndVertex();
			frase=frase+u.element() +" -> "+"("+e.element()+")"+v.element()+" ";
		}
		return frase;
	}
}