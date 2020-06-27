package graph;

import java.util.*;
import dataset.Statement;
import dataset.Token;
import elementijson.OperazioneFlowChart;
import elementijson.OperazioneFlowChart.Operazione11;

public class ControllerNodeFlowChart {
	
	private static ArrayList<Statement> st=new ArrayList<Statement>();
	private static ArrayList<Token> tokens;
	private static String nameFile;
	
	//ok
	public static boolean controlTree(Graph<IdNodoSppf> tree,Vertex<IdNodoSppf> cn) {
		Iterator<Edge<IdNodoSppf>> it=tree.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> nodo1=ed.getStartVertex();
			Vertex<IdNodoSppf> nodo2=ed.getEndVertex();
			if((nodo1.element().getId()==cn.element().getId())&&((nodo2.element().getNomeNodo().equals("predicate"))||(nodo2.element().getNomeNodo().equals("instruction")))) {
				return false;
			}
		}
		return true;
	}
	
	public static void setMark(String item,int id,Graph<IdNodoSppf>sppf,ArrayList<OperazioneFlowChart>op) {
		if(!(item.equals("Statements-> *Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }"))){
			Iterator<Edge<IdNodoSppf>> it=sppf.edges();
			while(it.hasNext()) {
				Edge<IdNodoSppf> ed=it.next();
				Vertex<IdNodoSppf> u1=ed.getStartVertex();
				Vertex<IdNodoSppf> v1=ed.getEndVertex();
				if((u1.element().getId()==id)&&(u1.element().getItem().equals(item))||(v1.element().getItem().equals(item))) {
					for(OperazioneFlowChart n :op) {
						if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
							Operazione11 po=(Operazione11)n;
							if((po.getItem().equals(item)&&(po.getIdNodo()==id))){
								po.setMark(true);
							}						
						}
					}		
				}
			}
		}
		else {
			trovaNodi(sppf,op);
		}
	}
	
	private static void trovaNodi(Graph<IdNodoSppf>sppf,ArrayList<OperazioneFlowChart> op) {
		Iterator<Vertex<IdNodoSppf>> it2=sppf.vertices();
		int contaS=0,contaSS=0,id=0;
		while(it2.hasNext()) {
			Vertex<IdNodoSppf> n1=it2.next();
			if(n1.element().getNomeNodo().equals("STATEMENTS")) {
				Iterator<Edge<IdNodoSppf>> it=sppf.edges();
				while(it.hasNext()) {
					Edge<IdNodoSppf> ed=it.next();
					Vertex<IdNodoSppf> u1=ed.getStartVertex();
					Vertex<IdNodoSppf> v1=ed.getEndVertex();
					if(u1.element().getId()==n1.element().getId()) {
						if(v1.element().getNomeNodo().equals("STATEMENTS")) {
							contaSS++;
						}
						if(v1.element().getNomeNodo().equals("STATEMENT")) {
							contaS++;
							if(contaS==1) {
								id=v1.element().getId();
							}
						}
					}
				}
				if(contaSS==0) {
					for(OperazioneFlowChart n :op) {
						if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
							Operazione11 po=(Operazione11)n;
							if(po.getIdNodo()==id){
								po.setMark(true);
							}						
						}
					}
				}
				else {
					for(OperazioneFlowChart n :op) {
						if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
							Operazione11 po=(Operazione11)n;
							if(po.getIdNodo()==id){
								po.setMark(false);
							}						
						}
					}
				}
				contaSS=0;
				contaS=0;
				id=0;
			}
		}	
	}
	
	public static void setMarkIf(Graph<IdNodoSppf>sppf,ArrayList<OperazioneFlowChart> op,String item) {
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if(u1.element().getNomeNodo().equals("STATEMENT")&&(v1.element().getItem().equals(item))) {
				for(OperazioneFlowChart n :op) {
					if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
						Operazione11 po=(Operazione11)n;
						if((po.getItem().equals(item)&&(po.getIdNodo()==v1.element().getId()))){
							po.setMark(true);
						}
					}
				}
			}
		}
	}
	
	public static void helpIf(Statement s,ArrayList<OperazioneFlowChart> op,Graph<IdNodoSppf>sppf) {
		String	statement="{ $.1 = "+s.getPrimoAttacco()+" , $.2 = "+s.getSecondoAttacco() +" }";
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		int code=0,conta=0;
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if((v1.element().getItem().equals("Statement-> *PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"))||(u1.element().getId()==code)) {
				code=u1.element().getId();
				for(OperazioneFlowChart n :op) {
					if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
						Operazione11 po=(Operazione11)n;
						if((po.getIdNodo()==u1.element().getId())){
							if(nameFile.equals("test4.json")) {
								if(po.getStatement()==null) {
									po.setStatement(statement);
								}
							}
							else {
								conta++;
								if(conta==4) {
									po.setStatement(statement);
								}
							}
						}
					}
				}
			}
		}
		Iterator<Edge<IdNodoSppf>> it2=sppf.edges();
		while(it2.hasNext()) {
			Edge<IdNodoSppf> ed=it2.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if(v1.element().getId()==code) {
				for(OperazioneFlowChart n :op) {
					if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
						Operazione11 po=(Operazione11)n;
						if((po.getIdNodo()==u1.element().getId())){
							if(nameFile.equals("test4.json")) {
								po.setStatement(statement);
							}
							else {
								conta++;
								if(conta<2) {
									po.setStatement(statement);
								}
							}
						}
					}
				}
			}
		}
	}
	
	//ok
	public static void writeNewStatement(Statement s,ArrayList<OperazioneFlowChart> op) {
		op.add(OperazioneFlowChart.creaNewStatement("Statement.1",s.getType1(),s.getPrimoAttacco()));
		op.add(OperazioneFlowChart.creaNewStatement("Statement.2",s.getType2(),s.getSecondoAttacco()));
	}
	
	public static void setStatementNode(Statement s) {
		boolean flag=true;
		for(Statement sp :st) {
			if(sp.toString().equals(s.toString())) {
				flag=false;
			}
		}
		if(flag) {
			st.add(s);
		}
	}
	
	public static void writeStatementNode(ArrayList<OperazioneFlowChart> op,Graph<IdNodoSppf>sppf) {
		System.out.println(st);
		int conta=0;
		Iterator<Vertex<IdNodoSppf>> it=sppf.vertices();
		while(it.hasNext()) {
			Vertex<IdNodoSppf> u1=it.next();
			if(u1.element().getNomeNodo().equals("STATEMENTS")) {
				conta++;
				for(OperazioneFlowChart sp:op) {
					if(sp.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
						Operazione11 po=(Operazione11)sp;
						if((u1.element().getId()==po.getIdNodo())&&(conta==1)) {
							String attach=getAttach(conta);
							if(!(po.getStatement().contains(attach))) {
								for(int l=0;l<st.size();l++) {
									Statement s=st.get(l);
									if(s.getPrimoAttacco().equals(attach)){
										String statement = "{ $.1 = " +s.getPrimoAttacco()+" , $.2 = "+s.getSecondoAttacco() + " }";
										po.setStatement(statement);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	//ok
	public static void setStatementNode(Statement s,ArrayList<OperazioneFlowChart> op,Vertex<IdNodoSppf> cn) {
		String	statement="{ $.1 = "+s.getPrimoAttacco()+" , $.2 = "+s.getSecondoAttacco() +" }";
		if(!(cn.element().getNomeNodo().equals("PROGRAM"))) {
			for(OperazioneFlowChart sp:op) {
				if(sp.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
					Operazione11 po=(Operazione11)sp;
					if((po.getIdNodo()==cn.element().getId())/*&&(po.getStatement()==null)*/) {
						po.setStatement(statement);
					}
				}
			}
		}
	}
	
	public static void setTokens(ArrayList<Token> token) {
		tokens=token;
	}
	
	private static String getAttach(int i) {
		for(int j=0;j<tokens.size();j++) {
			if(j==(i-1)) {
				return tokens.get(j).getAttachPoints().get(0);
			}
		}
		return null;
	}
	public static void setFile(String name) {
		nameFile=name;
	}
}
