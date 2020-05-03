package gllflowchart;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import elementijson.OperazioneFlowChart;
import elementijson.OperazioneFlowChart.Operazione11;
import graph.*;

public class InputHandler {
	
	/*inizio zona perfetta*/
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
	}
	
	public int getTokenDriver(int pos, int attaccoSinistro, int attaccoDestro) {
		ArrayList<String> attacchi=buf.get(pos).getAttachPoints();
		for(int i=0;i<buf.size();i++) {
			Token t1=buf.get(i);
			ArrayList<String> attacchiEsterni=t1.getAttachPoints();
			if(i!=pos) {
				if(attacchiEsterni.size()>=attaccoDestro) {
					if(attacchi.get(attaccoSinistro-1).equals(attacchiEsterni.get(attaccoDestro-1))) {
						return i;
					}	
				}
			}
		}
		return -1;
	}
	
	/*fine zona perfetta*/
	
	/*inizio zona rischiosa */
	public int getTokenTester(Statement s,int attaccoStatement,int attaccoToken) {
		String at=null;
		if(attaccoStatement==2) {
			at=s.getSecondoAttacco();
		}
		if(attaccoStatement==1) {
			at=s.getPrimoAttacco();
		}
		for(int i=0;i<buf.size();i++) {
			Token ex=buf.get(i);
			if(ex.getAttachPoints().size()>=attaccoToken){
				if(ex.getAttachPoints().get(attaccoToken-1).equals(at)) {
					return i;	
				}
			}
		}
		return -1;
	}
	
	public void writeNewStatement(Statement s,ArrayList<OperazioneFlowChart> op) {
		op.add(OperazioneFlowChart.creaNewStatement("Statement.1",s.getType1(),s.getPrimoAttacco()));
		op.add(OperazioneFlowChart.creaNewStatement("Statement.2",s.getType2(),s.getSecondoAttacco()));
	}
	
	public void setStatementNode(Statement s,ArrayList<OperazioneFlowChart> op,Vertex<IdNodoSppf> cn) {
		if(!(cn.element().getNomeNodo().equals("PROGRAM"))) {
			String	statement="{ $.1 = "+s.getPrimoAttacco()+" , $.2 = "+s.getSecondoAttacco() +" }";	
			for(OperazioneFlowChart sp:op) {
				if(sp.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
					Operazione11 po=(Operazione11)sp;
					if(po.getIdNodo()==cn.element().getId()) {
						po.setStatement(statement);
					}
				}
			}
		}
	}
	
	public void setLastStatement(Vertex<IdNodoSppf> cn,Graph<IdNodoSppf>sppf,Statement s,ArrayList<OperazioneFlowChart> op,InputHandler p) {
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if((u1.element().getNomeNodo().equals(cn.element().getNomeNodo())) &&(v1.element().getNomeNodo().equals("STATEMENTS"))){
				p.setStatementNode(s, op, v1);
			}
		}
	}
	/*fine zona rischiosa */ 
}
