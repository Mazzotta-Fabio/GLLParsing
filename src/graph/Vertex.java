package graph;

public class Vertex<E> implements Position<E>{
	
	private E info;
	private NodePositionList<Vertex<E>> adjList;
	
	public Vertex(E info) {
		this.info=info;
		adjList=new NodePositionList<Vertex<E>>();
	}
	@Override
	public E element() {
		return info;
	}
	
	public void insertAdjacent(Vertex<E> v) {
		adjList.addLast(v);
	}
	public NodePositionList<Vertex<E>> getAdjList() {
		return adjList;
	}
	public String toString(){
		return ""+info;
	}
}
