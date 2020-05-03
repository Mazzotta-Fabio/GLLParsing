package gllflowchart;

import java.util.ArrayList;

public class Token {
	private boolean start;
	private ArrayList<String> attachPoints;
	private String type;
	
	public Token(boolean start, ArrayList<String> attachPoints, String type) {
		this.start = start;
		this.attachPoints = attachPoints;
		this.type = type;
	}
	
	public Token(ArrayList<String> attachPoints, String type) {
		start=false;
		this.attachPoints = attachPoints;
		this.type = type;
	}

	public boolean isStart() {
		return start;
	}

	public ArrayList<String> getAttachPoints() {
		return attachPoints;
	}

	public String getType() {
		return type;
	}
	
	public String toString() {
		if(start) {
			return start + " " + type + " " + attachPoints;
		}
		else {
			return type + " " + attachPoints;
		}		
	}
	
}
