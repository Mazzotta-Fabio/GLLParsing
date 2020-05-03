package gllflowchart;

import java.util.*;
import elementijson.OperazioneFlowChart;
import elementijson.OperazioneFlowChart.Operazione11;
import graph.*;

public class ControllerNodeFlowChart {
	
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
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if((v1.element().getNomeNodo().equals("predicate"))&&(v1.element().getId()==id)&&(item.equals(v1.element().getItem()))) {
				for(OperazioneFlowChart n :op) {
					if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
						Operazione11 po=(Operazione11)n;
						if((po.getItem().equals(item))&&(po.getIdNodo()==id)) {
							po.setMark();
						}
					}
				}
			}
			if((u1.element().getId()==id)&&(u1.element().getItem().equals(item))) {
				for(OperazioneFlowChart n :op) {
					if(n.getClass().getName().equals("elementijson.OperazioneFlowChart$Operazione11")) {
						Operazione11 po=(Operazione11)n;
						if((po.getItem().equals(item)&&(po.getIdNodo()==id))){
							po.setMark();
						}
					}
				}		
			}
		}
	}
	
	public static void setLastStatement(Vertex<IdNodoSppf> cn,Graph<IdNodoSppf>sppf,Statement s,ArrayList<OperazioneFlowChart> op,InputHandler p) {
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if((u1.element().getNomeNodo().equals(cn.element().getNomeNodo())) &&(v1.element().getNomeNodo().equals("STATEMENTS"))){
				p.setStatementNode(s, op, v1);
				return;
			}
		}
	}
	
	/*incerta*/
	public static boolean controlCycle(Graph<String>gss,Vertex<String> u,Vertex<String>v) {
		Iterator<Edge<String>> it=gss.edges();
		while(it.hasNext()) {
			Edge<String> ed=it.next();
			Vertex<String> u1=ed.getStartVertex();
			Vertex<String> v1=ed.getEndVertex();
			if(v1.element().equals(u.element())&&(u1.element().equals(v.element()))){
				return false;
			}
		}
		return true;
	}
	
}
