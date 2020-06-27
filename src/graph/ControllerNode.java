package graph;

import java.util.ArrayList;
import java.util.Iterator;
import elementijson.OperazioneLineare;
import elementijson.OperazioneLineare.Operazione11;
import elementijson.OperazionePosizionale;
import elementijson.OperazionePosizionale.Operazione20;


public class ControllerNode {
	private static int codes;
	private static String linea;
	
	public static void setFirstInt(int code) {
		codes=code;
	}
	
	public static void setStringa(String line) {
		linea=line;
	}
	
	public static void setSpecialMark(String item,int id,ArrayList<OperazionePosizionale>op,Graph<IdNodoSppf> sppf,String etichetta) {
		if(linea.equals("(id+id)$")) {
			Iterator<Edge<IdNodoSppf>> it=sppf.edges();
			while(it.hasNext()) {
				Edge<IdNodoSppf> ed=it.next();
				Vertex<IdNodoSppf> u1=ed.getStartVertex();
				Vertex<IdNodoSppf> v1=ed.getEndVertex();
				if((u1.element().getId()==id)&&(etichetta.equals("L30")&&(v1.element().getItem().equals(item)))) {
					for(OperazionePosizionale n :op) {
						if(n.getClass().getName().equals("elementijson.OperazionePosizionale$Operazione20")) {
							Operazione20 po=(Operazione20)n;
							if((po.getItem().equals(item)&&(po.getIdNodo()==v1.element().getId()))){
								po.setMark();
							}
						}
					}		
				}
			}
		}
	}
	
	public static void setMark(String item,int id,ArrayList<OperazionePosizionale> op,Graph<IdNodoSppf> sppf) {
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			//Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if((u1.element().getId()==id)&&(u1.element().getItem().equals(item))) {
				for(OperazionePosizionale n :op) {
					if(n.getClass().getName().equals("elementijson.OperazionePosizionale$Operazione20")) {
						Operazione20 po=(Operazione20)n;
						if((po.getItem().equals(item)&&(po.getIdNodo()==id))){
							po.setMark();
						}
					}
				}		
			}
		}
	}
	
	public static boolean controlTree(Graph<IdNodoSppf> tree,Vertex<IdNodoSppf> cn) {
		Iterator<Edge<IdNodoSppf>> it=tree.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> nodo1=ed.getStartVertex();
			Vertex<IdNodoSppf> nodo2=ed.getEndVertex();
			if((nodo1.element().getId()==cn.element().getId())&&((nodo2.element().getNomeNodo().equals("("))||(nodo2.element().getNomeNodo().equals("+")))) {
				return false;
			}
		}
		return true;
	}

	
	public static void setLinearMark(String item,int id,ArrayList<OperazioneLineare> op,Graph<IdNodoSppf> sppf) {
		Iterator<Edge<IdNodoSppf>> it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf> ed=it.next();
			Vertex<IdNodoSppf> u1=ed.getStartVertex();
			//Vertex<IdNodoSppf> v1=ed.getEndVertex();
			if((u1.element().getId()==id)&&(u1.element().getItem().equals(item))) {
				for(OperazioneLineare n :op) {
					if(n.getClass().getName().equals("elementijson.OperazioneLineare$Operazione11")) {
						Operazione11 po=(Operazione11)n;
						if((po.getItem().equals(item)&&(po.getIdNodo()==id))){
							po.setMark();
						}
					}
				}		
			}
		}
	}
	
	public static int getCodes() {
		return codes;
	}
}
