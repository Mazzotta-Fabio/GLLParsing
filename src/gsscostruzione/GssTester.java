package gsscostruzione;


import graph.*;

import java.io.*;
import java.util.*;

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
public class GssTester {
	//gss
	private static Graph<String> gss;
	//insieme r e u che sono gli insiemi usati per registrare le scelte del non determinismo
	private static ArrayList<ElementiU> u;
	private static ArrayList<TriplaGSS>r;
	//insieme p
	private static ArrayList<ElementoP>p;
	
	public static void main(String []args){
		File f=new File("file.txt");
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
					r=new ArrayList<TriplaGSS>();
					u=new ArrayList<ElementiU>();
					p=new ArrayList<ElementoP>();
					String esito=parse(buf);
					if(esito.equals("SUCCESSO")){
						File f1=new File("grafo.dot");
						FileWriter writer=new FileWriter(f1);
						PrintWriter pw=new PrintWriter(writer,true);
						pw.println("diGraph G {");
						Iterator<Edge<String>> iterator=gss.edges();
						while(iterator.hasNext()){
							Edge<String> e=iterator.next();
							Vertex<String> u=e.getStartVertex();
							Vertex<String> v=e.getEndVertex();
							if(!(v.element().equals("$"))){
								pw.println(u.element()+"->"+v.element());
							}
						}
						pw.println("}");
						pw.close();
					}
					else{
						System.out.println(esito);
					}
					
				}	
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			System.out.println("File not Found");
		} 
	}
	
	public static String parse(char []buf){
		//dichirazione indici
    	int i=0;
		//inizializzo etichette
		String etichetta ="LS";
		//creiamo i nodi del gss inserendo un arco tra u0 e u1
		Vertex<String> u0=gss.insertVertex("$");
		gss.insertVertex("Ls0L0");
		gss.insertDirectedEdge(gss.getLastNode(),u0, "");
		Vertex<String> cu=gss.getLastNode();
		while(true){
			switch(etichetta){
			//OK
			case "LS":
				if((buf[i]=='a')||(buf[i]=='c')){
					add("LS1",cu,i);
				}
				if((buf[i]=='a')||(buf[i]=='b')){
					add("LS2",cu,i);
				}
				if((buf[i]=='d')||(buf[i]=='$')){
					add("LS3",cu,i);
				}
				etichetta="L0";
				break;
			//OK
			case "L0":
				if(r.size()>0){
					etichetta=r.get(0).getEtichetta();
					i=r.get(0).getI();
					cu=r.get(0).getU();
					System.out.println(r.get(0));
					r.remove(0);
				}
				else{
					if((u.get(u.size()-1).getEtichetta().equals("L0"))&&(u.get(u.size()-1).getU().element().equals(u0.element()))){
						return "SUCCESSO";
					}
					else{
						return "NON SUCCESSO";
					}
				}
				break;
			//ok
			case "LS1":
				cu=create("L1",cu,i);
				etichetta="La";
				break;
			//ok
			case "LS2":
				cu=create("L3",cu,i);
				etichetta="Lb";
				break;
			case "LS3":
				pop(cu,i,u0);
				etichetta="L0";
				break;
			case "L1":
				cu=create("L2",cu,i);
				etichetta="LS";
				break;
			case "L2":
				if(buf[i]=='d'){
					i++;
					pop(cu,i,u0);
				}
				etichetta="L0";
				break;
			case "L3":
				cu=create("L4",cu,i);
				etichetta="LS";
				break;
			case "L4":
				pop(cu,i,u0);
				etichetta="L0";
				break;
			case "La":
				if(buf[i]=='a'){
					i++;
					pop(cu,i,u0);
					etichetta="L0";
				}
				else{
					if(buf[i]=='c'){
						i++;
						pop(cu,i,u0);
					}
					etichetta="L0";
				}
				break;
			case "Lb":
				if(buf[i]=='a'){
					i++;
					pop(cu,i,u0);
					etichetta="L0";
				}
				else{
					if(buf[i]=='b'){
						i++;
						pop(cu,i,u0);
					}
					etichetta="L0";
				}
				break;
			}
		}
	}
	
	//ok
	public static void add(String etichetta, Vertex<String> nu,int j){
		if((u.size()==0)&&(r.size()==0)){
			u.add(new ElementiU(etichetta,nu));
			r.add(new TriplaGSS(etichetta,nu,j));
		}
		else{
			ElementiU el=u.get(j);
			if(!((el.getEtichetta().equals(etichetta))&&(el.getU().element().equals(nu.element())))){
				u.add(new ElementiU(etichetta,nu));
				r.add(new TriplaGSS(etichetta,nu,j));
			}
		}
	}
	
	public static Vertex<String> create(String etichetta,Vertex<String> u,int j){
		//creazione del nodo
		Vertex<String> v=null;
		Iterator<Vertex<String>> iteratorNodes=gss.vertices();
		while(iteratorNodes.hasNext()){
			Vertex<String> last=iteratorNodes.next();
			if(etichetta.equals(last.element())){
				v=last;
			}
		}
		if(v==null){
			v=gss.insertVertex("Ls"+j+etichetta);
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
			for(ElementoP elp:p){
				if(elp.getU().element().equals(v.element())){
					add(etichetta,u,elp.getK());
				}
			}
		}
		
		return v;
	}
	
	public static void pop(Vertex<String> u,int j,Vertex<String> u0){
		//if u diverso da u0
		if(!(u.element().equals(u0.element()))){
			//mettiamo elemento u,j a p
			p.add(new ElementoP(u,j));
			Iterator<Edge<String>> eset=gss.edges();
			//per ogni figlio v di aggiungi lu,v,j ad r e u
			while(eset.hasNext()){
				Edge<String> ed=eset.next();
				Vertex<String>u1=ed.getStartVertex();
				Vertex<String> v1=ed.getEndVertex();
				if(u.element().equals(u1.element())){
					add(u1.element().substring(3),v1,j);
				}
			}
		}
	}
}
