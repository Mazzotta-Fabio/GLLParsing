package gllflowchart;

public class Statement {
	private String type;
	private String type2;
	private String primoAttacco;
	private String secondoAttacco;
	
	
	public Statement(String type, String primoAttacco, String type2, String secondoAttacco) {
		this.type = type;
		this.primoAttacco = primoAttacco;
		this.secondoAttacco = secondoAttacco;
		this.type2 = type2;
	}
	
	public void setSecondoAttacco(String type2,String secondoAttacco) {
		this.secondoAttacco = secondoAttacco;
		this.type2 = type2;
	}
	
	public String getType1() {
		return type;
	}
	
	public String getType2() {
		return type2;
	}
	
	public String getPrimoAttacco() {
		return primoAttacco;
	}
	
	public String getSecondoAttacco() {
		return secondoAttacco;
	}
	
	public String toString() {
		return "< "+ type + ": " + primoAttacco + " ; " + type2 + ": " + secondoAttacco + " >";
	}
}
