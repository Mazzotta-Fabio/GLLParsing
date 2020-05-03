package gllparsinglineare;

import graph.*;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import elementijson.*;

/*
 * GRAMMATICA
 * S->ad
 * S->Ad
 * A->a
 * A->epsilon
 */
public class GLLParsingB {
	//gss
	private static Graph<String> gss;
	//insieme r e u che sono gli insiemi usati per registrare le scelte del non determinismo
	private static ArrayList<DescrittoreR>r;
	private static ArrayList<ElementoU>u;
	//insieme p
	private static ArrayList<ElementoP>p;
	//sppf
	private static Graph<IdNodoSppf> sppf;
	//insieme usato per le operazioni di logging
	private static List<OperazioneLineare> op;
	private static int contadd;
	private static int contacreate;
	private static int contagetnodet;
	
	public static void main(String []args) {
		File f=new File("parvislineare/file3.txt");
		File fJson=new File("parvislineare/log.json");
		File fdump=new File("parvislineare/dumplin2.txt");
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
					op=new ArrayList<OperazioneLineare>();
					Date d=new Date();
					op.add(OperazioneLineare.creaParsingInfo("GLLParsingLineare",f.getName(),d.toString(),fdump.getName()));
					contadd=0;
					contacreate=0;
					contagetnodet=0;
					String esito=parse(buf);
					System.out.println(esito);
					System.out.println(u.size());
				}	
			}
			catch(Exception e){
				op.add(OperazioneLineare.creaEsitoParsing("error_parse"));
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
		Vertex<IdNodoSppf> cn=sppf.insertVertex(new IdNodoSppf("S","S"));
		cn.element().setId(cn.hashCode());
		op.add(OperazioneLineare.creaStato("$","$"));
		op.add(OperazioneLineare.creaInsertNodeGSS("$",contacreate));
		op.add(OperazioneLineare.creaInsertNodeGSS("Ls"+i+"L0",contacreate));
		op.add(OperazioneLineare.creaInsertEdgeGSS(gss.getLastNode().element(),u0.element(),contacreate));
		op.add(OperazioneLineare.creaInsertNodeSppf(cn.toString(),"S",contagetnodet,cn.element().getId()));
		op.add(OperazioneLineare.creaCurrentToken(i,""+buf[i]));
		op.add(OperazioneLineare.creaGoto(etichetta));
		contacreate++;
		contagetnodet++;
		while(true){
			switch(etichetta){
			//non terminale non LL1
			//S
			case "LS":
				op.add(OperazioneLineare.creaStato(etichetta,"S"));
				if(test(buf[i],"S","ad")){
					add("LS1",cu,i,cn);
				}
				if(test(buf[i],"S","Ad")){
					add("LS2",cu,i,cn);
				}
				etichetta="L0";
				op.add(OperazioneLineare.creaGoto(etichetta));
				break;
			//non terminale LL1 A
			case "LA":
				op.add(OperazioneLineare.creaStato(etichetta,"A"));
				if(test(buf[i],"A","a")){
					add("LA1",cu,i,cn);
				}
				if(test(buf[i],"A","epsilon")){
					add("LA2",cu,i,cn);
				}
				etichetta="L0";
				op.add(OperazioneLineare.creaGoto(etichetta));
				break;
			//.ad
			case "LS1":
				op.add(OperazioneLineare.creaStato(etichetta,"S->*ad"));
				if(buf[i]=='a'){
					cn=getNodeT("a","S->*ad",cn);
					i++;
					op.add(OperazioneLineare.creaCurrentToken(i,""+buf[i]));
					etichetta="La1";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				else{
					etichetta="L0";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				break;
			//a.d
			case "La1":
				op.add(OperazioneLineare.creaStato(etichetta,"S->a*d"));
				if(buf[i]=='d'){
					cn=getNodeP(cn);
					cn=getNodeT("d","S->a*d",cn);
					i++;
					op.add(OperazioneLineare.creaCurrentToken(i,""+buf[i]));
					etichetta="Lad";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				else{
					etichetta="L0";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				break;
			//ad.
			case "Lad":
				op.add(OperazioneLineare.creaStato(etichetta,"S->ad*"));
				cn=getNodeP(cn);
				pop(cu,i,u0,cn);
				etichetta="L0";
				break;
				
			//.Ad
			case "LS2":
				op.add(OperazioneLineare.creaStato(etichetta,"S->*Ad"));
				if(test(buf[i],"S","Ad")){
					cu=create("L1",cu,i,cn);
					cn=getNodeT("A","S->*Ad",cn);
					etichetta="LA";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				else{
					etichetta="L0";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				break;
			//A.d
			case "L1":
				op.add(OperazioneLineare.creaStato(etichetta,"S->A*d"));
				if(buf[i]=='d'){
					cn=getNodeP(cn);
					cn=getNodeT("d","A->A*d",cn);
					i++;
					op.add(OperazioneLineare.creaCurrentToken(i,""+buf[i]));
					etichetta="LAd";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				else{
					etichetta="L0";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				break;
			//Ad.
			case "LAd":
				op.add(OperazioneLineare.creaStato(etichetta,"S->Ad*"));
				cn=getNodeP(cn);
				pop(cu,i,u0,cn);
				etichetta="L0";
				op.add(OperazioneLineare.creaGoto(etichetta));
				break;
			
			//epsilon
			case "LA2":
				op.add(OperazioneLineare.creaStato(etichetta,"A->e*"));
				cn=getNodeT("e","A->*eps",cn);
				cn=getNodeP(cn);
				pop(cu,i,u0,cn);
				etichetta="L0";
				op.add(OperazioneLineare.creaGoto(etichetta));
				break;
			
			//.a
			case "LA1":
				op.add(OperazioneLineare.creaStato(etichetta,"A->*a"));
				if(test(buf[i],"A","a")){
					cn=getNodeT("a","A->*a",cn);
					i++;
					op.add(OperazioneLineare.creaCurrentToken(i, ""+buf[i]));
					etichetta="La2";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				else{
					etichetta="L0";
					op.add(OperazioneLineare.creaGoto(etichetta));
				}
				break;
			//a.
			case "La2":
				op.add(OperazioneLineare.creaStato(etichetta,"A->a*"));
				cn=getNodeP(cn);
				pop(cu,i,u0,cn);
				etichetta="L0";
				op.add(OperazioneLineare.creaGoto(etichetta));
				break;
			case "L0":
				op.add(OperazioneLineare.creaStato(etichetta,"S0"));
				if(r.size()>0){
					etichetta=r.get(0).getEtichetta();
					i=r.get(0).getI();
					cu=r.get(0).getU();
					cn=r.get(0).getW();
					System.out.println(r.get(0));
					op.add(OperazioneLineare.creaInformazione("remove_r_element"));
					op.add(OperazioneLineare.creaGoto(etichetta));
					r.remove(0);
				}
				else{
					if(u.size()==0) {
						op.add(OperazioneLineare.creaEsitoParsing("failure_parse"));
						return "NON SUCCESSO";
					}
					else{
						if((u.get(u.size()-1).getEtichetta().equals("L0"))&&(u.get(u.size()-1).getU().element().equals(u0.element()))){
							op.add(OperazioneLineare.creaEsitoParsing("success_parse"));
							return "SUCCESSO";
						}
						else{
							op.add(OperazioneLineare.creaEsitoParsing("failure_parse"));
							return "NON SUCCESSO";
						}
					}
				}
				break;
			}
		}
	}
	
	private static Vertex<IdNodoSppf> getNodeT(String simbolo,String item,Vertex<IdNodoSppf>cn){
		Iterator<Edge<IdNodoSppf>>it=sppf.edges();
		boolean flag=true;
		while(it.hasNext()) {
			Edge<IdNodoSppf>e=it.next();
			Vertex<IdNodoSppf> v1=e.getStartVertex();
			Vertex<IdNodoSppf> v2=e.getEndVertex();
			if( (cn.element().getId()==v1.element().getId())&&(item.equals(v2.element().getItem())) ) {
				flag=false;
			}
		}
		if(flag) {
			Vertex<IdNodoSppf> v=sppf.insertVertex(new IdNodoSppf(simbolo,item));
			v.element().setId(v.hashCode());
			op.add(OperazioneLineare.creaInsertNodeSppf(v.toString(),item,contagetnodet,v.element().getId()));
			sppf.insertDirectedEdge(cn, v, null);
			op.add(OperazioneLineare.creaInsertEdgeSppf(cn.toString(),v.toString(),contagetnodet));
			contagetnodet++;
			return v;
		}
		return cn;
	}

	private static Vertex<IdNodoSppf> getNodeP(Vertex<IdNodoSppf>cn){
		Iterator<Edge<IdNodoSppf>>it=sppf.edges();
		while(it.hasNext()) {
			Edge<IdNodoSppf>e=it.next();
			Vertex<IdNodoSppf> v1=e.getStartVertex();
			Vertex<IdNodoSppf> v2=e.getEndVertex();
			if(v2.element().getId()==cn.element().getId()) {
				op.add(OperazioneLineare.creaOpGetNodeP(v1.element().toString()));
				return v1;
			}
		}
		op.add(OperazioneLineare.creaOpGetNodeP(cn.element().toString()));
		return cn;
	}
	
	//ok
	private static void add(String etichetta, Vertex<String> nu,int j,Vertex<IdNodoSppf>cn){
		u.add(new ElementoU(etichetta,nu));
		r.add(new DescrittoreR(etichetta,nu,j,cn));
		op.add(OperazioneLineare.creaInsertUelement(etichetta, nu.element(),contadd));
		op.add(OperazioneLineare.creaInsertRelement(etichetta,nu.element(), j,cn.toString(),contadd));
		contadd++;
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
			op.add(OperazioneLineare.creaInsertNodeGSS(v.element(),contacreate));
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
		if((flag)&&(!(v.element().equals(u.element())))){
			gss.insertDirectedEdge(v, u, "");
			op.add(OperazioneLineare.creaInsertEdgeGSS(v.element() ,u.element(),contacreate));
			for(ElementoP elp:p){
				if(elp.getU().element().equals(v.element())){
					add(etichetta,u,elp.getK(),elp.getZ());
				}
			}
		}
		contacreate++;
		return v;
	}
	
	private static void pop(Vertex<String> u,int j,Vertex<String> u0,Vertex<IdNodoSppf>cn){
		//if u diverso da u0
		if(!(u.element().equals(u0.element()))){
			//mettiamo elemento u,j a p
			p.add(new ElementoP(u,j,cn));
			op.add(OperazioneLineare.creaInsertPelement(u.element(),j,cn.toString()));
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
		if((handle.equals("Ad"))&&((x=='a')||(x=='d'))){return true;}
		if(handle.equals("ad")&&(x=='a')){return true;}
        if(handle.equals("a")&&(x=='a')){return true;}
        if(handle.equals("epsilon")&&(x=='d')){return true;}
		return false;
        
	}
		
	private static boolean follow(char x,String nonTerm){
		if(nonTerm.equals("S")){
			switch(x){
			case '$':return true;
			}
		}
		if(nonTerm.equals("A")){
			switch(x){
			case 'd':return true;
			case 'a':return true;
			}
		}
		return false;
	}
}
