package gllparsing;

public class IdNodoSppf {
	private String nomeNodo;
	private int id;
	
	public IdNodoSppf(String nomeNodo) {
		this.nomeNodo = nomeNodo;
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
		return (nomeNodo+id).substring(0,5);
	}
}
