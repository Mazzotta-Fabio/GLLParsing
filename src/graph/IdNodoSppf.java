package graph;

public class IdNodoSppf {
	private String nomeNodo;
	private String item;
	private int id;
	
	public IdNodoSppf(String nomeNodo,String item) {
		this.nomeNodo = nomeNodo;
		this.item = item;
	}
		
	public String getItem() {
		return item;
	}
	
	public String getNomeNodo() {
		return nomeNodo;
	}
		
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		String idNodo=""+id;
		String i=idNodo.substring(0,4);
		return (nomeNodo+i);
	}
}
