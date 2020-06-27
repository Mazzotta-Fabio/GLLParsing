package dataset;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

import graph.ControllerNodeFlowChart;


public class InputHandler {
	
	private ArrayList<Token> buf;
	
	public InputHandler() {
		buf=new ArrayList<Token>();
	}
	
	public int getFirstToken() {
		for(int i=0;i<buf.size();i++) {
			Token t=buf.get(i);
			if(t.isStart()) {
				return i;
			}
		}
		throw new IllegalArgumentException("ERRORE: Inserire start:true al token iniziale");
	}
	
	public Token getToken(int i) {
		return buf.get(i);
	}
	
	public void loadInput(Gson gson,FileReader f1){
		ArrayList<String> attacc = null;
		boolean start=false;
		String type = null;
		Map<?, ?> map = gson.fromJson(f1, Map.class);
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			ArrayList<StringMap> tokens=(ArrayList<StringMap>)entry.getValue();
		    for(StringMap<?> b: tokens) {
		    	Set<?>  set= b.entrySet(); 
		    	Iterator<?> it=set.iterator();
		    	while(it.hasNext()) {
		    		Entry<String,?> en=(Entry<String, ?>) it.next();
		    		switch(en.getValue().getClass().getName()) {
		    		case "java.lang.Boolean":
		    			start=(boolean) en.getValue();
		    			break;
		    		case "java.lang.String":
		    			type=(String) en.getValue();
		    			break;
		    		case "java.util.ArrayList":
		    			attacc=(ArrayList<String>)en.getValue();
		    		}
		    	}
		    	if(start) {
		    		buf.add(new Token(start,attacc,type));
		    		start=false;
		    	}
		    	else {
		    		buf.add(new Token(attacc,type));
		    	}
		    }
		}
		ControllerNodeFlowChart.setTokens(buf);
	}
	
	public int getTokenDriver(int pos, int attaccoSinistro, int attaccoDestro,ArrayList<Integer>tokenViews) {
		ArrayList<String> attacchi=buf.get(pos).getAttachPoints();
		for(int i=0;i<buf.size();i++) {
			Token t1=buf.get(i);
			ArrayList<String> attacchiEsterni=t1.getAttachPoints();
			if(isViewed(i,tokenViews)) {
				if(attacchiEsterni.size()>=attaccoDestro) {
					if(attacchi.get(attaccoSinistro-1).equals(attacchiEsterni.get(attaccoDestro-1))) {
						return i;
					}	
				}
			}
		}
		return -1;
	}
	
	//verifica se e' stato visto il token index
	private boolean isViewed(int index,ArrayList<Integer> tokenViews) {
		if(tokenViews==null) {
			return true;
		}
		else {
			for(Integer s:tokenViews) {
				if(s==index){
					return false;
				}
			}
			return true;
		}
	}
	
	public int getTokenTester(Statement s,int attaccoStatement,int attaccoToken,ArrayList<Integer> tokenViews/*,int pos*/) {
		String at=null;
		if(attaccoStatement==2) {
			at=s.getSecondoAttacco();
		}
		if(attaccoStatement==1) {
			at=s.getPrimoAttacco();
		}
		for(int i=0;i<buf.size();i++) {
			Token ex=buf.get(i);
			if(isViewed(i,tokenViews)) {
				if(ex.getAttachPoints().size()>=attaccoToken){
					if(ex.getAttachPoints().get(attaccoToken-1).equals(at)) {
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	//metodo per settare un valore trovato 
	public void setTokenFound(int index,ArrayList<Integer> tokenViews) {
		boolean flag=true;
		for(Integer t:tokenViews) {
			if(t==index) {
				flag=false;
			}
		}
		if(flag) {
			tokenViews.add(index);
		}
	}
}
