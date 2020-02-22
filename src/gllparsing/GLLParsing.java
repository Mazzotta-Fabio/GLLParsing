package gllparsing;

import graph.*;
import gsscostruzione.*;
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
	private static ArrayList<TriplaGSS>r;
	//insieme p
	private static ArrayList<ElementoP>p;
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
					r=new ArrayList<TriplaGSS>();
					u=new ArrayList<ElementoU>();
					p=new ArrayList<ElementoP>();
					op=new ArrayList<Operazione>();
					op.add(Operazione.creaParsingInfo("parsing_info","GLLParsing",f.getName(),"19/02/2020"));
					op.add(Operazione.creaMessage("message","Initializing parser"));
					String esito=parse(buf);
					System.out.println(esito);
					System.out.println(u.size());
					
				}	
			}
			catch(Exception e){
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
				catch(IOException e) {
					e.printStackTrace();
				}
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
		op.add(Operazione.creaReadNextToken("read_next_token",i,""+buf[i],1,"-1","TY"));
		op.add(Operazione.creaInizializzazione("stack_inizialization"));
		op.add(Operazione.creaInizializzazione("stack_removeAllElements"));
		op.add(Operazione.creaGoto("goto", etichetta));
		while(true){
			//op.add(Operazione.creaOperazione8("goto",etichetta));
			switch(etichetta){
			//non terminale LL1
			//S
			case "LS":
				if(test(buf[i],"S","ASd")){
					add("LS1",cu,i);
				}
				if(test(buf[i],"S","BS")){
					add("LS2",cu,i);
				}
				if((buf[i]=='d')||(buf[i]=='$')){
					add("LS3",cu,i);
				}
				etichetta="L0";
				break;
			//terminale LL1 A
			case "LA":
				if(test(buf[i],"A","a")){
					etichetta="La";
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,".a"));
				}
				if(test(buf[i],"A","c")){
					etichetta="Lc";
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,".c"));
				}
				break;
			//terminale LL1 B
			case "LB":
				if(test(buf[i],"B","a")){
					etichetta="La";
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,".a"));
				}
				if(test(buf[i],"B","b")){
					etichetta="Lb";
					op.add(Operazione.creaShift("shift","Lb",i,""+buf[i],i,"La",".b"));
				}
				break;
			//.ASd
			case "LS1":
				if(test(buf[i],"S","ASd")){
					cu=create("L1",cu,i);
					etichetta="LA";
					op.add(Operazione.creaPushStack("stack_push",i,""+buf[i], 1,etichetta,""));
					op.add(Operazione.creaGoto("goto",etichetta));
				}
				else{
					etichetta="L0";
				}
				break;
			//A.Sd
			case "L1":
				if(test(buf[i],"S","Sd")){
					cu=create("L2",cu,i);
					etichetta="LS";
					op.add(Operazione.creaPushStack("stack_push",i,""+buf[i], 1,etichetta,""));
					op.add(Operazione.creaGoto("goto",etichetta));
				}
				else{
					etichetta="L0";
				}
				break;
			//AS.d
			case "L2":
				if(buf[i]=='d'){
					i++;
					etichetta="Ld";
					op.add(Operazione.creaReadNextToken("read_next_token",i,""+buf[i], 2, etichetta,"AS.d"));
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,"AS.d"));
				}
				else{
					etichetta="L0";
				}
				break;
			//ASd.
			case "Ld":
				pop(cu,i,u0);
				etichetta="L0";
				op.add(Operazione.creaReduce("reduce",1,1,1));
				op.add(Operazione.creaPopStack("stack_pop",i,""+buf[i],1, etichetta,""));
				op.add(Operazione.creaMessage("message", "Reduce rule"));
				break;
			//.BS
			case "LS2":
				if(test(buf[i],"S","BS")){
					cu=create("L3",cu,i);
					etichetta="LB";
					op.add(Operazione.creaPushStack("stack_push",i,""+buf[i], 1,etichetta,""));
					op.add(Operazione.creaGoto("goto",etichetta));
				}
				else{
					etichetta="L0";
				}
				break;
			//B.S
			case "L3":
				if(test(buf[i],"S","S")){
					cu=create("L4",cu,i);
					etichetta="LS";
					op.add(Operazione.creaPushStack("stack_push",i,""+buf[i], 1,etichetta,""));
					op.add(Operazione.creaGoto("goto",etichetta));
				}
				else{
					etichetta="L0";
				}
				break;
			//BS.
			case "L4":
				pop(cu,i,u0);
				etichetta="L0";
				op.add(Operazione.creaReduce("reduce",1,1,1));
				op.add(Operazione.creaPopStack("stack_pop",i,""+buf[i],1, etichetta,""));
				op.add(Operazione.creaMessage("message", "Reduce rule"));
				break;
			//epsilon
			case "LS3":
				pop(cu,i,u0);
				etichetta="L0";
				op.add(Operazione.creaReduce("reduce",1,1,1));
				op.add(Operazione.creaPopStack("stack_pop",i,""+buf[i],1, etichetta,""));
				op.add(Operazione.creaMessage("message", "Reduce rule"));
				break;
			//.a
			case "La":
				if(buf[i]=='a'){
					i++;
					etichetta="La1";
					op.add(Operazione.creaReadNextToken("read_next_token",i,""+buf[i], 2, etichetta,".a"));
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,".a"));
				}
				else{
					etichetta="L0";
				}
				break;
			//a.
			case "La1":
				pop(cu,i,u0);
				etichetta="L0";
				op.add(Operazione.creaReduce("reduce",1,1,1));
				op.add(Operazione.creaPopStack("stack_pop",i,""+buf[i],1, etichetta,""));
				op.add(Operazione.creaMessage("message", "Reduce rule"));
				break;
			//.b
			case "Lb":
				if(buf[i]=='b'){
					i++;
					etichetta="Lb1";
					op.add(Operazione.creaReadNextToken("read_next_token",i,""+buf[i], 2, etichetta,".b"));
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,".b"));
				}
				else{
					etichetta="L0";
				}
				break;
			//b.
			case "Lb1":
				pop(cu,i,u0);
				etichetta="L0";
				op.add(Operazione.creaReduce("reduce",1,1,1));
				op.add(Operazione.creaPopStack("stack_pop",i,""+buf[i],1, etichetta,""));
				op.add(Operazione.creaMessage("message", "Reduce rule"));
				break;
			//.c
			case "Lc":
				if(buf[i]=='c'){
					i++;
					etichetta="Lc1";
					op.add(Operazione.creaReadNextToken("read_next_token",i,""+buf[i], 2, etichetta,".c"));
					op.add(Operazione.creaShift("shift",etichetta,i,""+buf[i],i,etichetta,".c"));
				}
				else{
					etichetta="L0";
				}
				break;
			//c.
			case "Lc1":
				pop(cu,i,u0);
				etichetta="L0";
				op.add(Operazione.creaReduce("reduce",1,1,1));
				op.add(Operazione.creaPopStack("stack_pop",i,""+buf[i],1, etichetta,""));
				op.add(Operazione.creaMessage("message", "Reduce rule"));
				break;
			case "L0":
				if(r.size()>0){
					etichetta=r.get(0).getEtichetta();
					i=r.get(0).getI();
					cu=r.get(0).getU();
					System.out.println(r.get(0));
					r.remove(0);
				}
				else{
					if(u.size()==0){
						op.add(Operazione.creaMessage("message","No recovery state found on stack"));
						op.add(Operazione.creaParseEndFailure("error_recovery","fails"));
						return "NON SUCCESSO";
					}
					else{
						if((u.get(u.size()-1).getEtichetta().equals("L0"))&&(u.get(u.size()-1).getU().element().equals(u0.element()))){
							op.add(Operazione.creaParseEndSucccess("parse_end",true));
							return "SUCCESSO";
						}
						else{
							op.add(Operazione.creaMessage("message","No recovery state found on stack"));
							op.add(Operazione.creaParseEndFailure("error_recovery","fails"));
							return "NON SUCCESSO";
						}
					}
				}
				break;
			}
		}
	}
	
	//ok
	public static void add(String etichetta, Vertex<String> nu,int j){
		if((u.size()==0)&&(r.size()==0)){
			u.add(new ElementoU(etichetta,nu));
			r.add(new TriplaGSS(etichetta,nu,j));
		}
		else{
			ElementoU el=u.get(j);
			if(!((el.getEtichetta().equals(etichetta))&&(el.getU().element().equals(nu.element())))){
				u.add(new ElementoU(etichetta,nu));
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
	
	//controlla il simbolo buffer corrente di un non terminale 
	public static boolean test(char x,String nonTerm,String handle){
		if((first(x,handle))||(first('$',handle)&&(follow(x,nonTerm)))){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean first(char x,String handle){
		if((handle.equals("ASd"))&&((x=='a')||(x=='c'))){return true;}
		if((handle.equals("Sd"))&&((x=='b')||(x=='a')||(x=='d')||(x=='$'))){return true;}
        if((handle.equals("BS"))&&((x=='a')||(x=='b'))){return true;}
        if((handle.equals("S"))&&((x=='a')||(x=='c')||(x=='$'))){return true;}
        if(handle.equals("a")&&(x=='a')){return true;}
        if(handle.equals("b")&&(x=='b')){return true;}
        if(handle.equals("c")&&(x=='c')){return true;}
		return false;
        
	}
		
	public static boolean follow(char x,String nonTerm){
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
