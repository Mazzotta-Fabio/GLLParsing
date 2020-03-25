package gllparsing;

import graph.*;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import elementijson.*;

/*
 * GRAMMATICA
 * S->adb
 * S->aSb
 * S->d
 */
public class GLLParsingNew {
	//gss
	private static Graph<String> gss;
	//insieme r e u che sono gli insiemi usati per registrare le scelte del non determinismo
	private static ArrayList<DescrittoreR>r;
	private static ElementoU[] u;
	//insieme p
	private static ArrayList<ElementoP>p;
	//sppf
	private static Graph<IdNodoSppf> sppf;
	//insieme usato per le operazioni di logging
	private static List<Operazione> op;
	
	private static int i;
	 
	public static void main(String []args) {
		File f=new File("file2.txt");
		File fJson=new File("log.json");
		Scanner buffer;
		if(f.exists()){
			try{
				FileReader in=new FileReader(f);
				buffer=new Scanner(in);
				char[] buf;
				while(buffer.hasNextLine()){
					String line=buffer.nextLine();
					buf=line.toCharArray();
					gss=new Graph<String>();
					sppf=new Graph<IdNodoSppf>();
					r=new ArrayList<DescrittoreR>();
					u=new ElementoU[25000];
					p=new ArrayList<ElementoP>();
					op=new ArrayList<Operazione>();
					Date d=new Date();
					op.add(Operazione.creaParsingInfo("GLLParsing",f.getName(),d.toString()));
					String esito=parse(buf);
					System.out.println(esito);
					System.out.println(i);
				}	
			}
			catch(Exception e){
				op.add(Operazione.creaInformazione("error_parse"));
				e.printStackTrace();
			}
			finally {
				try {
					Gson gson=new Gson();
					FileWriter writer2=new FileWriter(fJson);
					String jsonString=gson.toJson(op);
					PrintWriter pw3=new PrintWriter(writer2,true);
					pw3.println(jsonString);
					pw3.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		else{
			System.out.println("File not Found");
		} 
	}
	
	private static String parse(char []buf){
		//dichirazione indici
    	int i=0;
		//inizializzo etichette
		String etichetta ="LS";
		//creiamo i nodi del gss inserendo un arco tra u0 e u1
		Vertex<String> u0=gss.insertVertex("$");
		gss.insertVertex("Ls0L0");
		gss.insertDirectedEdge(gss.getLastNode(),u0, "");
		Vertex<String> cu=gss.getLastNode();
		//creazione nodo sppf
		Vertex<IdNodoSppf> cn=sppf.insertVertex(new IdNodoSppf("S"));
		cn.element().setId(cn.hashCode());
		op.add(Operazione.creaGoto("$","$"));
		op.add(Operazione.creaInsertNodeGSS("$"));
		op.add(Operazione.creaInsertNodeGSS("Ls"+i+"L0"));
		op.add(Operazione.creaInsertEdgeGSS(gss.getLastNode().element(),u0.element()));
		op.add(Operazione.creaInsertNodeSppf(cn.toString()));
		op.add(Operazione.creaCurrentToken(i,""+buf[i]));
		while(true){
			switch(etichetta){
			//non terminale LL1
			//S
			case "LS":
				op.add(Operazione.creaGoto(etichetta,"S"));
				if(test(buf[i],"S","adb")){
					add("LS1",cu,i,cn);
				}
				if(test(buf[i],"S","aSb")){
					add("LS2",cu,i,cn);
				}
				if(test(buf[i],"S","d")){
					add("LS3",cu,i,cn);
				}
				etichetta="L0";
				break;
				
			//.adb
			case "LS1":
				op.add(Operazione.creaGoto(etichetta,"S->*adb"));
				if(buf[i]=='a'){
					i++;
					etichetta="Ld1";
					cn=getNodeT("a",cn);
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//a.db
			case "Ld1":
				op.add(Operazione.creaGoto(etichetta,"S->a*db"));
				if(buf[i]=='d'){
					i++;
					etichetta="Lb1";
					cn=getNodeP(cn.hashCode());
					cn=getNodeT("d",cn);
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//ad.b
			case "Lb1":
				op.add(Operazione.creaGoto(etichetta,"S->ad*b"));
				if(buf[i]=='b'){
					cn=getNodeP(cn.hashCode());
					cn=getNodeT("b",cn);
					i++;
					etichetta="Labd";
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//adb.
			case "Labd":
				op.add(Operazione.creaGoto(etichetta,"S->adb*"));
				cn=getNodeP(cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//.aSb
			case "LS2":
				op.add(Operazione.creaGoto(etichetta,"S->*aSb"));
				if(buf[i]=='a'){
					i++;
					etichetta="L1";
					cn=getNodeT("a",cn);
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//a.Sb
			case "L1":
				op.add(Operazione.creaGoto(etichetta,"S->a*Sb"));
				if(test(buf[i],"S","Sb")) {
					cu=create("L2",cu,i,cn);
					cn=getNodeP(cn.hashCode());
					cn=getNodeT("S",cn);
					etichetta="LS";
				}
				else {
					etichetta="L0";
				}
				break;
			//aS*b
			case "L2":
				op.add(Operazione.creaGoto(etichetta,"S->aS*b"));
				if(buf[i]=='b'){
					cn=getNodeP(cn.hashCode());
					cn=getNodeT("b",cn);
					i++;
					etichetta="L3";
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//aSb*
			case "L3":
				op.add(Operazione.creaGoto(etichetta,"S->aSb*"));
				cn=getNodeP(cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			
			//.d
			case "LS3":
				op.add(Operazione.creaGoto(etichetta,"S->*d"));
				if(buf[i]=='d'){
					i++;
					etichetta="Ld";
					cn=getNodeT("d",cn);
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//d.
			case "Ld":
				op.add(Operazione.creaGoto(etichetta,"S->d*"));
				cn=getNodeP(cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			case "L0":
				if(r.size()>0){
					op.add(Operazione.creaGoto(etichetta,"S0"));
					etichetta=r.get(0).getEtichetta();
					i=r.get(0).getI();
					cu=r.get(0).getU();
					cn=r.get(0).getW();
					System.out.println(r.get(0));
					op.add(Operazione.creaInformazione("remove_r_element"));
					r.remove(0);
				}
				else{
					if(u[i]==null){
						op.add(Operazione.creaInformazione("failure_parse"));
						return "NON SUCCESSO";
					}
					else{
						if((u[i].getEtichetta().equals("L0"))&&(u[i].getU().element().equals(u0.element()))){//if((u.get(u.size()-1).getEtichetta().equals("L0"))&&(u.get(u.size()-1).getU().element().equals(u0.element()))){
							op.add(Operazione.creaInformazione("success_parse"));
							return "SUCCESSO";
						}
						else{
							op.add(Operazione.creaInformazione("failure_parse"));
							return "NON SUCCESSO";
						}
					}
				}
				break;
			}
		}
	}
	
	private static Vertex<IdNodoSppf> getNodeT(String item,Vertex<IdNodoSppf>cn){
		Vertex<IdNodoSppf> v=sppf.insertVertex(new IdNodoSppf(item));
		v.element().setId(v.hashCode());
		op.add(Operazione.creaInsertNodeSppf(v.toString()));
		sppf.insertDirectedEdge(cn, v, null);
		op.add(Operazione.creaInsertEdgeSppf(cn.toString(),v.toString()));
		return v;
	}
	
	private static Vertex<IdNodoSppf> getNodeP(int i){
		Iterator<Edge<IdNodoSppf>>it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf>e=it.next();
			Vertex<IdNodoSppf> v1=e.getStartVertex();
			Vertex<IdNodoSppf> v2=e.getEndVertex();
			if(v2.element().getId()==i) {
				return v1;
			}
		}
		return null;
	}
	
	//ok
	private static void add(String etichetta, Vertex<String> nu,int j,Vertex<IdNodoSppf>cn){
		ElementoU el=u[j];
		if(el==null) {
			u[j]=new ElementoU(etichetta,nu);
			i++;
			r.add(new DescrittoreR(etichetta,nu,j,cn));
			op.add(Operazione.creaInsertUelement(etichetta, nu.element()));
			op.add(Operazione.creaInsertRelement(etichetta,nu.element(), j,cn.toString()));
		}
		else {
			if(!((el.getEtichetta().equals(etichetta))&&(el.getU().element().equals(nu.element())))){
				u[j]=new ElementoU(etichetta,nu);
				i++;
				r.add(new DescrittoreR(etichetta,nu,j,cn));
				op.add(Operazione.creaInsertUelement(etichetta, nu.element()));
				op.add(Operazione.creaInsertRelement(etichetta,nu.element(), j,cn.toString()));
			}
		}
	}
	
	private static Vertex<String> create(String etichetta,Vertex<String> u,int j,Vertex<IdNodoSppf>cn){
		//creazione del nodo
		String nomeNodo="Ls"+j+etichetta;
		Vertex<String> v=null;
		Iterator<Vertex<String>> iteratorNodes=gss.vertices();
		while(iteratorNodes.hasNext()){
			Vertex<String> last=iteratorNodes.next();
			if(nomeNodo.equals(last.element())){
				v=last;
			}
		}
		if(v==null){
			v=gss.insertVertex(nomeNodo);
			op.add(Operazione.creaInsertNodeGSS(v.element()));
		}
		//controlliamo arco tra v ed u
		Iterator<Edge<String>> eset=gss.edges();
		boolean flag=true;
		while(eset.hasNext()){
			Edge<String> ed=eset.next();
			Vertex<String> u1=ed.getStartVertex();
			Vertex<String> u2=ed.getEndVertex();
			if((u1.element().equals(v.element()))&&(u2.element().equals(u.element()))){
				flag=false;
			}
		}
		if(flag){
			gss.insertDirectedEdge(v, u, "");
			op.add(Operazione.creaInsertEdgeGSS(v.element() ,u.element()));
			for(ElementoP elp:p){
				if(elp.getU().element().equals(v.element())){
					add(etichetta,u,elp.getK(),elp.getZ());
				}
			}
		}
		return v;
	}
	
	private static void pop(Vertex<String> u,int j,Vertex<String> u0,Vertex<IdNodoSppf>cn){
		//if u diverso da u0
		if(!(u.element().equals(u0.element()))){
			//mettiamo elemento u,j a p
			p.add(new ElementoP(u,j,cn));
			op.add(Operazione.creaInsertPelement(u.element(),j,cn.toString()));
			Iterator<Edge<String>> eset=gss.edges();
			//per ogni figlio v di aggiungi lu,v,j ad r e u
			while(eset.hasNext()){
				Edge<String> ed=eset.next();
				Vertex<String>u1=ed.getStartVertex();
				Vertex<String> v1=ed.getEndVertex();
				if(u.element().equals(u1.element())){
					add(u1.element().substring(3),v1,j,cn);
				}
			}
		}
	}
	
	//controlla il simbolo buffer corrente di un non terminale 
	private static boolean test(char x,String nonTerm,String handle){
		if((first(x,handle))||(first('$',handle)&&(follow(x,nonTerm)))){
			return true;
		}
		else{
			return false;
		}
	}
	
	private static boolean first(char x,String handle){
		if((handle.equals("adb"))&&((x=='a')||(x=='d'))){return true;}
		if(handle.equals("d")&&(x=='d')){return true;}
        if((handle.equals("aSb"))&&((x=='a')||(x=='d'))){return true;}
        if((handle.equals("Sb"))&&((x=='a')||(x=='d'))){return true;}
		return false;
        
	}
		
	private static boolean follow(char x,String nonTerm){
		if(nonTerm.equals("S")){
			switch(x){
			case '$':return true;
			case 'd':return true;
			}
		}
		return false;
	}
}
