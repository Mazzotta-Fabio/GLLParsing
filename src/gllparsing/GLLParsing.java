package gllparsing;

import graph.*;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import elementijson.*;

/*
 * GRAMMATICA
 * S->ASd
 * S->BS
 * S->epsilon
 * A->a
 * A->c
 * B->a
 * B->b
 */
public class GLLParsing {
	//gss
	private static Graph<String> gss;
	//insieme r e u che sono gli insiemi usati per registrare le scelte del non determinismo
	private static ArrayList<ElementoU> u;
	private static ArrayList<DescrittoreR>r;
	//insieme p
	private static ArrayList<ElementoP>p;
	//sppf
	private static Graph<IdNodoSppf> sppf;
	//insieme usato per le operazioni di logging
	private static List<Operazione> op;
	 
	public static void main(String []args) {
		File f=new File("file.txt");
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
					u=new ArrayList<ElementoU>();
					p=new ArrayList<ElementoP>();
					op=new ArrayList<Operazione>();
					Date d=new Date();
					op.add(Operazione.creaParsingInfo("GLLParsing",f.getName(),d.toString()));
					String esito=parse(buf);
					System.out.println(esito);
					System.out.println(u.size());
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
				if(test(buf[i],"S","ASd")){
					add("LS1",cu,i,cn);
				}
				if(test(buf[i],"S","BS")){
					add("LS2",cu,i,cn);
				}
				if((buf[i]=='d')||(buf[i]=='$')) {
					add("LS3",cu,i,cn);
				}
				etichetta="L0";
				break;
			//terminale LL1 A
			case "LA":
				op.add(Operazione.creaGoto(etichetta,"A"));
				if(test(buf[i],"A","a")){
					etichetta="La";
				}
				if(test(buf[i],"A","c")){
					etichetta="Lc";
				}
				break;
			//terminale LL1 B
			case "LB":
				op.add(Operazione.creaGoto(etichetta,"B"));
				if(test(buf[i],"B","a")){
					etichetta="Lab";
				}
				if(test(buf[i],"B","b")){
					etichetta="Lb";
				}
				break;
			//.ASd
			case "LS1":
				op.add(Operazione.creaGoto(etichetta,"S->*ASd"));
				if(test(buf[i],"S","ASd")){
					cu=create("L1",cu,i,cn);
					cn=getNodeT("ASd",cn);
					//cn=getNodeT("A",cn);
					etichetta="LA";
				}
				else{
					etichetta="L0";
				}
				break;
			//A.Sd
			case "L1":
				op.add(Operazione.creaGoto(etichetta,"S->A*Sd"));
				if(test(buf[i],"S","Sd")){
					cu=create("L2",cu,i,cn);
					/*
					if(i<buf.length-2) {
						cn=getNodeT("S",cn);
					}
					*/
					etichetta="LS";
				}
				else{
					etichetta="L0";
				}
				break;
			//AS.d
			case "L2":
				op.add(Operazione.creaGoto(etichetta,"S->AS*d"));
				if(buf[i]=='d'){
					i++;
					etichetta="Ld";
					//cn=getNodeT("d",cn);
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//ASd.
			case "Ld":
				op.add(Operazione.creaGoto(etichetta,"S->ASd*"));
				//cn=getNodeP("d",cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//.BS
			case "LS2":
				op.add(Operazione.creaGoto(etichetta,"S->*BS"));
				if(test(buf[i],"S","BS")){
					cu=create("L3",cu,i,cn);
					cn=getNodeT("BS",cn);
					//cn=getNodeT("B",cn);
					etichetta="LB";
				}
				else{
					etichetta="L0";
				}
				break;
			//B.S
			case "L3":
				op.add(Operazione.creaGoto(etichetta,"S->B*S"));
				if(test(buf[i],"S","S")){
					cu=create("L4",cu,i,cn);
					/*
					if(i<buf.length-2) {
						cn=getNodeT("S",cn);
					}
					*/
					etichetta="LS";
				}
				else{
					etichetta="L0";
				}
				break;
			//BS.
			case "L4":
				op.add(Operazione.creaGoto(etichetta,"S->BS*"));
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//epsilon
			case "LS3":
				op.add(Operazione.creaGoto(etichetta,"S->e*"));
				cn=getNodeT("e",cn);
				cn=getNodeP("e",cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//.a
			case "La":
				op.add(Operazione.creaGoto(etichetta,"A->*a"));
				if(buf[i]=='a'){
					cn=getNodeT("a",cn);
					i++;
					etichetta="La1";
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//a.
			case "La1":
				op.add(Operazione.creaGoto(etichetta,"A->a*"));
				cn=getNodeP("a",cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//.a
			case "Lab":
				op.add(Operazione.creaGoto(etichetta,"B->*a"));
				if(buf[i]=='a'){
					cn=getNodeT("a",cn);
					i++;
					etichetta="La1b";
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//a.
			case "La1b":
				op.add(Operazione.creaGoto(etichetta,"B->a*"));
				cn=getNodeP("a",cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//.b
			case "Lb":
				op.add(Operazione.creaGoto(etichetta,"B->*b"));
				if(buf[i]=='b'){
					cn=getNodeT("b",cn);
					i++;
					etichetta="Lb1";
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//b.
			case "Lb1":
				op.add(Operazione.creaGoto(etichetta,"B->b*"));
				cn=getNodeP("b",cn.hashCode());
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
			//.c
			case "Lc":
				op.add(Operazione.creaGoto(etichetta,"A->*c"));
				if(buf[i]=='c'){
					cn=getNodeT("c",cn);
					i++;
					etichetta="Lc1";
					op.add(Operazione.creaCurrentToken(i,""+buf[i]));
				}
				else{
					etichetta="L0";
				}
				break;
			//c.
			case "Lc1":
				op.add(Operazione.creaGoto(etichetta,"A->c*"));
				cn=getNodeP("c",cn.hashCode());
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
					if(u.size()==0){
						op.add(Operazione.creaInformazione("failure_parse"));
						return "NON SUCCESSO";
					}
					else{
						if((u.get(u.size()-1).getEtichetta().equals("L0"))&&(u.get(u.size()-1).getU().element().equals(u0.element()))){
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
	
	private static Vertex<IdNodoSppf> getNodeP(String item,int i){
		Iterator<Edge<IdNodoSppf>>it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf>e=it.next();
			Vertex<IdNodoSppf> v1=e.getStartVertex();
			Vertex<IdNodoSppf> v2=e.getEndVertex();
			if(v2.element().getId()==i) {
				/*extra*/
				/*
				Iterator<Edge<IdNodoSppf>>it2=sppf.edges();
				while(it2.hasNext()) {
					Edge<IdNodoSppf>e2=it2.next();
					Vertex<IdNodoSppf> v3=e2.getStartVertex();
					Vertex<IdNodoSppf> v4=e2.getEndVertex();
					if(v4.element().getId()==v1.element().getId()) {
						return v3;
					}
				}
				*/
				/*fine extra*/
				return v1;
			}
		}
		return null;
	}
	
	//ok
	private static void add(String etichetta, Vertex<String> nu,int j,Vertex<IdNodoSppf>cn){
		if((u.size()==0)&&(r.size()==0)){
			u.add(new ElementoU(etichetta,nu));
			r.add(new DescrittoreR(etichetta,nu,j,cn));
			op.add(Operazione.creaInsertUelement(etichetta, nu.element()));
			op.add(Operazione.creaInsertRelement(etichetta,nu.element(), j,cn.toString()));
		}
		else{
			ElementoU el=u.get(j);
			if(!((el.getEtichetta().equals(etichetta))&&(el.getU().element().equals(nu.element())))){
				u.add(new ElementoU(etichetta,nu));
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
		if((handle.equals("ASd"))&&((x=='a')||(x=='c'))){return true;}
		if((handle.equals("Sd"))&&((x=='b')||(x=='a')||(x=='d')||(x=='$'))){return true;}
        if((handle.equals("BS"))&&((x=='a')||(x=='b'))){return true;}
        if((handle.equals("S"))&&((x=='a')||(x=='c')||(x=='$'))){return true;}
        if(handle.equals("a")&&(x=='a')){return true;}
        if(handle.equals("b")&&(x=='b')){return true;}
        if(handle.equals("c")&&(x=='c')){return true;}
		return false;
        
	}
		
	private static boolean follow(char x,String nonTerm){
		if(nonTerm.equals("S")){
			switch(x){
			case '$':return true;
			case 'd':return true;
			}
		}
		if(nonTerm.equals("A")){
			switch(x){
			case 'a':return true;
			case 'b':return true;
			case 'c':return true;
			case 'd':return true;
			}
		}
		if(nonTerm.equals("B")){
			switch(x){
			case 'a':return true;
			case 'b':return true;
			case 'c':return true;
			case 'd':return true;
			case '$':return true;
			}
		}
		return false;
	}
}
