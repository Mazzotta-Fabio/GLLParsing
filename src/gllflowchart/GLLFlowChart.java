package gllflowchart;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.google.gson.Gson;
import elementijson.OperazioneFlowChart;
import gllparsinglineare.ElementoU;
import graph.Edge;
import graph.Graph;
import graph.IdNodoSppf;
import graph.Vertex;
import stack.ArrayStack;
import stack.Stack;

public class GLLFlowChart {
	//gss
	private static Graph<String> gss;
	//insieme r e u che sono gli insiemi usati per registrare le scelte del non determinismo
	private static ArrayList<ElementoU> u;
	private static ArrayList<DescrittoreRf>r;
	//insieme p
	private static ArrayList<ElementoPf>p;
	//sppf
	private static Graph<IdNodoSppf>sppf;
	//insieme per operazioni di logging
	private static ArrayList<OperazioneFlowChart> op;
	private static int contadd;
	private static int contacreate;
	private static int contagetnodet;
	
	public static void main(String []args){
		Gson gson=new Gson();
		File f=new File("parvisflowchart/test2.json");
		File fJson=new File("parvisflowchart/log.json");
		File fdump=new File("parvisflowchart/dumppos1.txt");
		if(f.exists()){
			try{
				FileReader f1=new FileReader(f);
				gss=new Graph<String>();
				sppf=new Graph<IdNodoSppf>();
				r=new ArrayList<DescrittoreRf>();
				u=new ArrayList<ElementoU>();
				p=new ArrayList<ElementoPf>();
				op=new ArrayList<OperazioneFlowChart>();
				Date d=new Date();
				op.add(OperazioneFlowChart.creaParsingInfo("GLLParsingFlowChart",f.getName(),d.toString(),fdump.getName()));
				//settaggio dataset
				InputHandler inputhandler=new InputHandler();
				inputhandler.loadInput(gson,f1);
				contadd=1;
				contacreate=1;
				contagetnodet=1;
				String esito=parse(inputhandler);
				System.out.println(esito);
				System.out.println(u.size());
			}
			catch(Exception e){
				op.add(OperazioneFlowChart.creaEsitoParsing("error_parse"));
				e.printStackTrace();
			}
			finally {
				try {
					FileWriter writer2=new FileWriter(fJson);
					String jsonString=gson.toJson(op);
					PrintWriter pw3=new PrintWriter(writer2,true);
					pw3.println(jsonString);
					pw3.close();
				}
				catch(Exception e) {
					op.add(OperazioneFlowChart.creaEsitoParsing("error_parse"));
					e.printStackTrace();
				}
			}
		}
		else{
			System.out.println("File not Found");
		} 
	}
	
	public static String parse(InputHandler buf){
		//dichirazione indici
    	int i=buf.getFirstToken();
    	//inizializzo link calcolati
    	Stack<Statement> s=new ArrayStack<Statement>();
		//inizializzo etichette
		String etichetta ="LPROGRAM";
		//creiamo i nodi del gss inserendo un arco tra u0 e u1
		Vertex<String> u0=gss.insertVertex("$");
		gss.insertVertex("Ls0L0");
		gss.insertDirectedEdge(gss.getLastNode(),u0, "");
		Vertex<String> cu=gss.getLastNode();
		//creazione nodo sppf
		Vertex<IdNodoSppf> cn=sppf.insertVertex(new IdNodoSppf("PROGRAM","PROGRAM"));
		cn.element().setId(cn.hashCode());
		op.add(OperazioneFlowChart.creaStato("$","$"));
		op.add(OperazioneFlowChart.creaInsertNodeGSS("$",contacreate));
		op.add(OperazioneFlowChart.creaInsertNodeGSS("Ls"+i+"L0",contacreate));
		op.add(OperazioneFlowChart.creaInsertEdgeGSS(gss.getLastNode().element(),u0.element(),contacreate));
		op.add(OperazioneFlowChart.creaInsertNodeSppf(cn.toString(),"PROGRAM",contagetnodet,cn.element().getId(),-1));
		op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
		op.add(OperazioneFlowChart.creaGoto(etichetta));
		contagetnodet++;
		contacreate++;
		//int cod=cn.element().getId();
		while(true){
			switch(etichetta){
			//.START link(1,1) Statements link(2,1) END
			case "LPROGRAM":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Program-> *START link(1,1) Statements link(2,1) END"));
				if((buf.getToken(i).isStart())&&(buf.getToken(i).getType().equals("START"))) {
					op.add(OperazioneFlowChart.creaSetCurrentToken(i, buf.getToken(i).getType()));
					cn=getNodeT("start","Program-> *START link(1,1) Statements link(2,1) END",cn,i);
					etichetta="L1";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//START .link(1.1) Statements link(2.1) END
			case "L1":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Program-> START *link(1,1) Statements link(2,1) END"));
				i=buf.getTokenDriver(i,1,1);
				if(i>0) {
					op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
					etichetta="LSTATS";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//START link(1.1) .Statements link(2.1) END
			case "LSTATS":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Program-> START link(1,1) *Statements link(2,1) END"));
				if(test(buf.getToken(i).getType(),"Program","Statements link(2,1) END")){
					cu=create("L2",cu,i,cn);
					cn=getNodeP(cn);
					cn=getNodeT("STATEMENTS","Program-> START link(1,1) *Statements link(2,1) END",cn,-1);
					etichetta="LSTATEMENTS";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//START link(1.1) Statements *link(2.1) END
			case "L2":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Program-> START link(1,1) Statements *link(2,1) END"));
				i=buf.getTokenTester(s.top(), 2, 1);
				if(i>0) {
					op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
					etichetta="L3";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//START link(1.1) Statements link(2.1) *END
			case "L3":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Program-> START link(1,1) Statements link(2,1) *END"));
				if(buf.getToken(i).getType().equals("END")){
					op.add(OperazioneFlowChart.creaSetCurrentToken(i, buf.getToken(i).getType()));
					cn=getNodeP(cn);
					cn=getNodeT("end","Program-> START link(1,1) Statements link(2,1) *END",cn,i);
					etichetta="L4";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Program->START link(1,1) *Statements link(2,1) END",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//START link(1.1) Statements link(2.1) END*
			case "L4":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Program-> START link(1,1) Statements link(2,1) END*"));
				ControllerNodeFlowChart.setLastStatement(cn, sppf, s.top(), op, buf);
				cn=getNodeP(cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//Statement
			case "LSTATEMENTS":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements"));
				if(test(buf.getToken(i).getType(),"Statements","Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }")){
					add("LS1",cu,i,cn,s.duplica());
				}
				if(test(buf.getToken(i).getType(),"Statements","Statement { $$.1 = $1.1; $$.2 = $1.2; }")){
					add("LS2",cu,i,cn,s.duplica());
				}
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//*Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }
			case "LS1":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements-> *Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if((test(buf.getToken(i).getType(),"Statements","Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }"))){
					cu=create("L5",cu,i,cn);
					cn=getNodeT("STATEMENT","Statements-> *Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,-1);
					etichetta="LSTAT";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
				//Statements *link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }
			case "L5":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements-> Statement *link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				i=buf.getTokenTester(s.top(),2,1);
				if(i>0) {
					op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
					etichetta="L6";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Statements-> *Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//Statements link(2,1) *Statements { $$.1 = $1.1; $$.2 = $2.2; }	
			case "L6":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements-> Statement link(2,1) *Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if(test(buf.getToken(i).getType(),"Statements","Statements { $$.1 = $1.1; $$.2 = $2.2; }")){
					cu=create("L8",cu,i,cn);
					cn=getNodeP(cn);
					cn=getNodeT("STATEMENTS","Statements-> Statement link(2,1) *Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,-1);
					etichetta="LSTATEMENTS";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Statements-> *Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//Statements link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }*
			case "L8":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements-> Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }*"));
				if(s.size()>1) {
					Statement cv=s.pop();
					s.top().setSecondoAttacco(cv.getType2(), cv.getSecondoAttacco());
				}
				cn=getNodeP(cn);
				buf.setStatementNode(s.top(), op, cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//*Statement { $$.1 = $1.1; $$.2 = $1.2; }
			case "LS2":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements-> *Statement { $$.1 = $1.1; $$.2 = $1.2; }"));
				if(test(buf.getToken(i).getType(),"Statements","Statement { $$.1 = $1.1; $$.2 = $1.2; }")){
					cu=create("L10",cu,i,cn);
					cn=getNodeT("STATEMENT","Statements-> *Statement { $$.1 = $1.1; $$.2 = $1.2; }",cn,-1);
					etichetta="LSTAT";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//Statements-> Statement { $$.1 = $1.1; $$.2 = $1.2; }*
			case "L10":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Statements-> Statement { $$.1 = $1.1; $$.2 = $1.2; }*"));
				cn=getNodeP(cn);
				buf.setStatementNode(s.top(), op, cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//Statement
			case "LSTAT":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Statement"));
				if(test(buf.getToken(i).getType(),"Statement","INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }")) {
					add("LSTAT1",cu,i,cn,s.duplica());
				}
				if(test(buf.getToken(i).getType(),"Statement","PREDICATE link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }")) {
					add("LSTAT2",cu,i,cn,s.duplica());
				}
				if(test(buf.getToken(i).getType(),"Statement","PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }")) {
					add("LSTAT3",cu,i,cn,s.duplica());
				}
				if(test(buf.getToken(i).getType(),"Statement","PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$2 = $1.3; }")) {
					add("LSTAT4",cu,i,cn,s.duplica());
				}
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//Statement->*INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }
			case "LSTAT1":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Statement-> *INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }"));
				if((buf.getToken(i).getType().equals("INSTRUCTION"))&&(ControllerNodeFlowChart.controlTree(sppf, cn))) {
					op.add(OperazioneFlowChart.creaSetCurrentToken(i, buf.getToken(i).getType()));
					cn=getNodeT("instruction","Statement-> *INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }",cn,i);
					etichetta="L11";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//Statement -> INSTRUCTION *{ $$.1 = $1.1; $$.2 = $1.2; }
			case "L11":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> INSTRUCTION *{ $$.1 = $1.1; $$.2 = $1.2; }"));
				s.push(new Statement(buf.getToken(i).getType(),buf.getToken(i).getAttachPoints().get(0),buf.getToken(i).getType(),buf.getToken(i).getAttachPoints().get(1)));
				buf.writeNewStatement(s.top(), op);
				etichetta="L12";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//Statement -> INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }*
			case "L12":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Statement-> INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }*"));
				cn=getNodeP(cn);
				buf.setStatementNode(s.top(), op,cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			case "LSTAT4":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> *PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$.2 = $1.3; }"));
				if(buf.getToken(i).getType().equals("PREDICATE")) {
					op.add(OperazioneFlowChart.creaSetCurrentToken(i, buf.getToken(i).getType()));
					cn=getNodeT("predicate","Statement-> *PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$.2 = $1.3; }",cn,i);
					etichetta="L23";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE *link(2,1) ^ link(1,2) Statements   { $$.1 = $1.1; $$.2 = $1.3; }
			case "L23":
			op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE *link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$.2 = $1.3; }"));
			if((buf.getTokenDriver(i,2,1)>0)&&(buf.getTokenDriver(i,1,2)>0)) {
					i=buf.getTokenDriver(i, 2, 1);
					op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
					etichetta="L24";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Statement-> *PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$.2 = $1.3; }",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2,1) ^ link(1,2) *Statements { $$.1 = $1.1; $$.2 = $1.3; }	
			case "L24":
			op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ link(1,2) *Statements { $$.1 = $1.1; $$.2 = $1.3; }"));
			if(test(buf.getToken(i).getType(),"Statement","Statements { $$.1 = $1.1; $$.2 = $1.3; }")) {
				cu=create("L25",cu,i,cn);
				cn=getNodeP(cn);
				cn=getNodeT("STATEMENTS","Statement-> PREDICATE link(2,1) ^ link(1,2) *Statements { $$.1 = $1.1; $$.2 = $1.3; }",cn,-1);
				etichetta="LSTATEMENTS";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
			}
			else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2,1) ^ link(1,2) Statements *{ $$.1 = $1.1; $$.2 = $1.3; }
			case "L25":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ link(1,2) Statements *{ $$.1 = $1.1; $$.2 = $1.3; }"));
				i=buf.getTokenTester(s.top(),1,2);
				if(i>0) {
					if(buf.getToken(i).getType().equals("PREDICATE")) {
						s.pop();
						s.push(new Statement(buf.getToken(i).getType(),buf.getToken(i).getAttachPoints().get(0),buf.getToken(i).getType(),buf.getToken(i).getAttachPoints().get(2)));
						buf.writeNewStatement(s.top(), op);
						etichetta="L26";
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
					else {
						etichetta="L0";
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$.2 = $1.3; }*
			case "L26":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$.2 = $1.3; }*"));
				cn=getNodeP(cn);
				buf.setStatementNode(s.top(), op, cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//*PREDICATE link(2, 1) ^ link(3,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "LSTAT2":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> *PREDICATE link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if(buf.getToken(i).getType().equals("PREDICATE")) {
					op.add(OperazioneFlowChart.creaSetCurrentToken(i, buf.getToken(i).getType()));
					cn=getNodeT("predicate","Statement-> *PREDICATE link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,i);
					etichetta="L13";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE *link(2, 1) ^ link(3,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "L13":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE *link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if((buf.getTokenDriver(i, 2, 1)>0)&&(buf.getTokenDriver(i, 3, 2)>0)) {
					i=buf.getTokenDriver(i, 2, 1);
					op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
					etichetta="L14";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Statement-> *PREDICATE link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ link(3,2) *Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "L14":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ link(3,2) *Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if(test(buf.getToken(i).getType(),"Statement", "Statements { $$.1 = $1.1; $$.2 = $2.2; }")) {
					cu=create("L15",cu,i,cn);
					cn=getNodeP(cn);
					cn=getNodeT("STATEMENTS","Statement-> PREDICATE link(2,1) ^ link(3,2) *Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,-1);
					etichetta="LSTATEMENTS";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ link(3,2) Statements  *{ $$.1 = $1.1; $$.2 = $2.2; }	
			case "L15":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ link(3,2) Statements *{ $$.1 = $1.1; $$.2 = $2.2; }"));
				i=buf.getTokenTester(s.top(),2,3);
				if(i>0) {
					if(buf.getToken(i).getType().equals("PREDICATE")) {
						Statement st=s.pop();
						s.push(new Statement(buf.getToken(i).getType(),buf.getToken(i).getAttachPoints().get(0),st.getType2(),st.getSecondoAttacco()));
						buf.writeNewStatement(s.top(), op);
						etichetta="L16";
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
					else {
						etichetta="L0";
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ link(3,2) Statements   *{ $$.1 = $1.1; $$.2 = $2.2; }	
			case "L16":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }*"));
				cn=getNodeP(cn);
				buf.setStatementNode(s.top(), op, cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			//*PREDICATE link(2, 1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "LSTAT3":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> *PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if(buf.getToken(i).getType().equals("PREDICATE")) {
					op.add(OperazioneFlowChart.creaSetCurrentToken(i, buf.getToken(i).getType()));
					cn=getNodeT("predicate","Statement-> *PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,i);
					etichetta="L17";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE *link(2, 1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "L17":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE *link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if((buf.getTokenDriver(i,2,1)>0)&&(!(buf.getTokenDriver(i,1,2)>0))) {
					i=buf.getTokenDriver(i,2,1);
					op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
					etichetta="L18";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Statement-> *PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ nolink(1,2) *Statements link(3,1)(-1) ^ link(2,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "L18":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ nolink(1,2) *Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if(test(buf.getToken(i).getType(),"Statement","Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }")) {
					cu=create("L19",cu,i,cn);
					cn=getNodeP(cn);
					cn=getNodeT("STATEMENTS","Statement-> PREDICATE link(2,1) ^ nolink(1,2) *Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,-1);
					etichetta="LSTATEMENTS";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ nolink(1,2) Statements *link(3,1)(-1) ^ link(2,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "L19":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ nolink(1,2) Statements *link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				i=buf.getTokenTester(s.top(), 1, 2);
				if(i>0) {
					if(buf.getToken(i).getType().equals("PREDICATE")) {
						i=buf.getTokenDriver(i,3,1);
						//op.add(OperazioneFlowChart.creaGetCurrentToken(i,buf.getToken(i).getType()));
						etichetta="L20";
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
					else {
						etichetta="L0";
						//setMark("Statement-> PREDICATE link(2,1) ^ nolink(1,2) *Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
				}
				else {
					etichetta="L0";
					//setMark("Statement-> PREDICATE link(2,1) ^ nolink(1,2) *Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) *Statements   { $$.1 = $1.1; $$.2 = $2.2; }
			case "L20":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) *Statements { $$.1 = $1.1; $$.2 = $2.2; }"));
				if(test(buf.getToken(i).getType(),"Statement","Statements { $$.1 = $1.1; $$.2 = $2.2; }")) {
					cu=create("L21",cu,i,cn);
					cn=getNodeP(cn);
					cn=getNodeT("STATEMENTS","Statement-> PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) *Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn,-1);
					etichetta="LSTATEMENTS";
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				else {
					etichetta="L0";
					//setMark("Statement-> PREDICATE link(2,1) ^ nolink(1,2) *Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",cn.element().getId());
					//setMark("Statement-> *PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }",0);
					op.add(OperazioneFlowChart.creaGoto(etichetta));
				}
				break;
			//PREDICATE link(2, 1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements   *{ $$.1 = $1.1; $$.2 = $2.2; }
			case "L21":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statement-> PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements *{ $$.1 = $1.1; $$.2 = $2.2; }"));
				i=buf.getTokenTester(s.top(), 1, 3);
				s.pop();
				if(i>0) {
					if(buf.getToken(i).getType().equals("PREDICATE")){
						Statement sp=s.pop();
						s.push(new Statement(buf.getToken(i).getType(),buf.getToken(i).getAttachPoints().get(0),sp.getType2(),sp.getSecondoAttacco()));
						buf.writeNewStatement(s.top(), op);
						etichetta="L22";
						op.add(OperazioneFlowChart.creaGoto(etichetta));	
					}
					else {
						etichetta="L0";
						op.add(OperazioneFlowChart.creaGoto(etichetta));
					}
				}
				else {
					etichetta="L0";
					op.add(OperazioneFlowChart.creaGoto(etichetta));	
				}
				break;
			//PREDICATE link(2, 1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements   { $$.1 = $1.1; $$.2 = $2.2; }*
			case "L22":
				op.add(OperazioneFlowChart.creaStato(etichetta, "Statements-> PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }*"));
				cn=getNodeP(cn);
				buf.setStatementNode(s.top(), op, cn);
				pop(cu,i,u0,cn,s.duplica());
				etichetta="L0";
				op.add(OperazioneFlowChart.creaGoto(etichetta));
				break;
			case "L0":
				op.add(OperazioneFlowChart.creaStato(etichetta,"Program0"));
				if(r.size()>0){
					etichetta=r.get(0).getEtichetta();
					i=r.get(0).getI();
					cu=r.get(0).getU();
					cn=r.get(0).getV();
					s=r.get(0).getStatements();
					System.out.println(etichetta+" STATEMENTS: "+s);
					op.add(OperazioneFlowChart.creaInformazione("remove_r_element"));
					op.add(OperazioneFlowChart.creaGoto(etichetta));
					r.remove(0);
				}
				else{
					if(u.size()==0){
						op.add(OperazioneFlowChart.creaEsitoParsing("failure_parse"));
						return "NON SUCCESSO";
					}
					else{
						if((u.get(u.size()-1).getEtichetta().equals("L0"))&&(u.get(u.size()-1).getU().element().equals(u0.element()))){
							op.add(OperazioneFlowChart.creaEsitoParsing("success_parse"));
							return "SUCCESSO";
						}
						else{
							op.add(OperazioneFlowChart.creaEsitoParsing("failure_parse"));
							//setMark("PROGRAM",cod);
							return "NON SUCCESSO";
						}
					}
				}
				break;
			}
		}
	}
	
	//ok
	private static void add(String etichetta, Vertex<String> nu,int j,Vertex<IdNodoSppf> cn,Stack<Statement>statement){
		u.add(new ElementoU(etichetta,nu));
		op.add(OperazioneFlowChart.creaInsertUelement(etichetta, nu.element(),contadd));
		r.add(new DescrittoreRf(etichetta,nu,j,cn,statement));
		op.add(OperazioneFlowChart.creaInsertRelement(etichetta,nu.element(), j,cn.toString(),contadd));
		contadd++;
	}
	
	private static Vertex<IdNodoSppf> getNodeT(String simbolo,String item,Vertex<IdNodoSppf>cn,int i){
		Iterator<Edge<IdNodoSppf>>it=sppf.edges();
		boolean flag=true;
		String nonTerm=null;
		if(item.contains("Program->")) {
			nonTerm="PROGRAM";
		}
		if(item.contains("Statements->")) {
			nonTerm="STATEMENTS";
		}
		if(item.contains("Statement->")) {
			nonTerm="STATEMENT";
		}
		while(it.hasNext()) {
			Edge<IdNodoSppf>e=it.next();
			Vertex<IdNodoSppf> v1=e.getStartVertex();
			Vertex<IdNodoSppf> v2=e.getEndVertex();
			if( (cn.element().getId()==v1.element().getId())&&(item.equals(v2.element().getItem())) ) {
				flag=false;
			}
		}
		if((flag)&&(cn.element().getNomeNodo().equals(nonTerm))) {
			Vertex<IdNodoSppf> v=sppf.insertVertex(new IdNodoSppf(simbolo,item));
			v.element().setId(v.hashCode());
			op.add(OperazioneFlowChart.creaInsertNodeSppf(v.toString(), item,contagetnodet,v.element().getId(),i));
			sppf.insertDirectedEdge(cn, v, null);
			op.add(OperazioneFlowChart.creaInsertEdgeSppf(cn.toString(),v.toString(),contagetnodet));
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
				op.add(OperazioneFlowChart.creaOpGetNodeP(v1.element().toString()));
				return v1;
			}
		}
		op.add(OperazioneFlowChart.creaOpGetNodeP(cn.element().toString()));
		return cn;
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
			op.add(OperazioneFlowChart.creaInsertNodeGSS(v.element(),contacreate));
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
		if( (flag)&&(!(v.element().equals(u.element()))) /*&& (ControllerNodeFlowChart.controlCycle(gss,v,u))*/){
			gss.insertDirectedEdge(v, u, "");
			op.add(OperazioneFlowChart.creaInsertEdgeGSS(v.element(),u.element(),contacreate));
			for(ElementoPf elp:p){
				if(elp.getU().element().equals(v.element())){
					add(etichetta,u,elp.getK(),cn,elp.getStatements().duplica());
				}
			}
		}
		contacreate++;
		return v;
	}
	
	private static void pop(Vertex<String> u,int j,Vertex<String> u0,Vertex<IdNodoSppf>cn,Stack<Statement> statement) {
		//if u diverso da u0
		if(!(u.element().equals(u0.element()))){
			//mettiamo elemento u,j a p
			p.add(new ElementoPf(u,j,cn,statement));
			op.add(OperazioneFlowChart.creaInsertPelement(u.element(), j, cn.toString()));
			Iterator<Edge<String>> eset=gss.edges();
			//per ogni figlio v di aggiungi lu,v,j ad r e u
			while(eset.hasNext()){
				Edge<String> ed=eset.next();
				Vertex<String>u1=ed.getStartVertex();
				Vertex<String> v1=ed.getEndVertex();
				if(u.element().equals(u1.element())){
					String etichetta="L";
					int i=1;
					while(!(u1.element().substring(i,i+1).equals("L"))) {
						etichetta=etichetta+u1.element().substring(i,i+1);
						i++;
					}
					add(u1.element().substring(etichetta.length()),v1,j,cn,statement.duplica());
				}
			}
		}
	}
	
	//controlla il simbolo buffer corrente per un item
	private static boolean test(String x,String nonTerm,String handle){
		if((first(x,handle))||(first("$",handle)&&(follow(x,nonTerm)))){
			return true;
		}
		else{
			return false;
		}
	}
	
	private static boolean first(String x,String handle){
		if((handle.equals("Statements link(2,1) END"))&&((x.equals("INSTRUCTION"))||(x.equals("PREDICATE")))){return true;}
		if((handle.equals("Statement link(2,1) Statements { $$.1 = $1.1; $$.2 = $2.2; }"))&&((x.equals("INSTRUCTION"))||(x.equals("PREDICATE")))){return true;}
		if((handle.equals("Statement { $$.1 = $1.1; $$.2 = $1.2; }"))&&((x.equals("INSTRUCTION"))||(x.equals("PREDICATE")))){return true;}
		if((handle.equals("Statements { $$.1 = $1.1; $$.2 = $2.2; }"))&&((x.equals("INSTRUCTION"))||(x.equals("PREDICATE")))){return true;}
		if(handle.equals("INSTRUCTION { $$.1 = $1.1; $$.2 = $1.2; }")&&(x.equals("INSTRUCTION"))){return true;}
		if(handle.equals("PREDICATE link(2,1) ^ link(3,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }")&&(x.equals("PREDICATE"))){return true;}
		if(handle.equals("PREDICATE link(2,1) ^ link(1,2) Statements { $$.1 = $1.1; $$2 = $1.3; }")&&(x.equals("PREDICATE"))){return true;}
        if(handle.equals("PREDICATE link(2,1) ^ nolink(1,2) Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }")&&(x.equals("PREDICATE"))){return true;}
        if((handle.equals("Statements link(3,1)(-1) ^ link(2,2) Statements { $$.1 = $1.1; $$.2 = $2.2; }"))&&((x.equals("INSTRUCTION"))||(x.equals("PREDICATE")))){return true;}
        if((handle.equals("Statements { $$.1 = $1.1; $$.2 = $1.3; }"))&&((x.equals("INSTRUCTION"))||(x.equals("PREDICATE")))){return true;}
		return false;
	}
		
	private static boolean follow(String x,String nonTerm){
		if(nonTerm.equals("Statements")){
			switch(x){
			case "INSTRUCTION":return true;
			case "PREDICATE":return true;
			}
		}
		if(nonTerm.equals("Statement")){
			switch(x){
			case "INSTRUCTION":return true;
			case "PREDICATE":return true;
			}
		}
		return false;
	}
	
}
