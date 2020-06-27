package elementijson;

import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class OperazioneFlowChart {

	private String op;
	private String callFunction;

	public OperazioneFlowChart(String op, String callFunction) {
		this.op = op;
		this.callFunction = callFunction;
	}
	
	public static class Operazione0 extends OperazioneFlowChart {
		public Operazione0(String op) {
			super(op,null);
		}
	}

	public static class Operazione1 extends OperazioneFlowChart {
		public Operazione1(String op,String callFunction) {
			super(op,callFunction);
		}
	}
	
	public static class Operazione2 extends OperazioneFlowChart {
		private String parser;
		private String inputfile;
		private String date;
		private String dumpfile;

		public Operazione2(String op, String parser, String inputfile, String date, String dumpfile) {
			super(op, null);
			this.parser = parser;
			this.inputfile = inputfile;
			this.date = date;
			this.dumpfile = dumpfile;
		}
	}

	public static class Operazione7 extends OperazioneFlowChart {
		private String u;
		private String v;
		private int id;
		public Operazione7(String op, String callFunction, String u, String v,int id) {
			super(op, callFunction);
			this.u = u;
			this.v = v;
			if(id>=0) {
				this.id = id;
			}
		}
	}

	public static class Operazione8 extends OperazioneFlowChart {
		private String to_state;
		private String item;

		public Operazione8(String op, String to_state, String item) {
			super(op, null);
			this.to_state = to_state;
			this.item = item;
		}
	}

	public static class Operazione11 extends OperazioneFlowChart {
		private String parse_state;
		private String item;
		private boolean mark;
		private int id;
		private int idNodo;
		private int position;
		private String statement;
		public Operazione11(String op, String callFunction, String parse_state, String item,int id,int idNodo,int position) {
			super(op, callFunction);
			this.parse_state = parse_state;
			this.item = item;
			this.id=id;
			this.idNodo=idNodo;
			mark=false;
			if(position>0) {
				this.position=position;
			}
			statement=null;
		}
		
		public boolean isMarked() {
			return mark;
		}
		
		public String getStatement() {
			return statement;
		}
		public void setMark(boolean mark) {
			this.mark=mark;
		}
		public String getItem() {
			return item;
		}
		public int  getIdNodo() {
			return idNodo;
		}
		public void setStatement(String statement) {
			this.statement=statement;
		}
		public int getPosition() {
			return position;
		}
	}

	public static class Operazione27 extends OperazioneFlowChart {
		private String parse_state;
		private int id;
		public Operazione27(String op, String callFunction, String parse_state, int id) {
			super(op, callFunction);
			this.parse_state = parse_state;
			this.id=id;
		}
	}

	public static class Operazione12 extends OperazioneFlowChart {
		private String label;
		private String nameNode;
		private int i;
		private String nameNodeSppf;
		private int id;
		
		public Operazione12(String op, String callFunction, String label, String nameNode, int i, String nameNodeSppf,int id) {
			super(op, callFunction);
			this.label = label;
			this.nameNode = nameNode;
			this.i = i;
			this.nameNodeSppf = nameNodeSppf;
			this.id=id;
		}
	}

	public static class Operazione13 extends OperazioneFlowChart {
		private String label;
		private String nameNode;
		private int id;
		public Operazione13(String op, String callFunction, String label, String nameNode,int id) {
			super(op, callFunction);
			this.label = label;
			this.nameNode = nameNode;
			this.id=id;
		}
	}

	public static class Operazione14 extends OperazioneFlowChart {
		private String nameNode;
		private int i;
		private String nameNodeSppf;

		public Operazione14(String op, String callFunction, String nameNode, int i, String nameNodeSppf) {
			super(op, callFunction);
			this.nameNode = nameNode;
			this.i = i;
			this.nameNodeSppf = nameNodeSppf;
		}
	}

	public static class Operazione31 extends OperazioneFlowChart {
		private String nameNode;
		public Operazione31(String op, String nameNode) {
			super(op, null);
			this.nameNode = nameNode;
		}
	}
	
	public static class Operazione32 extends OperazioneFlowChart {
		private String label;
		public Operazione32(String op,String label) {
			super(op, null);
			this.label = label;
		}
	}
	
	public static class Operazione50 extends OperazioneFlowChart {
		private int i;
		private String type;
		public Operazione50(String op,int i,String type) {
			super(op, null);
			this.i=i;
			this.type=type;
		}
	}
	public static class Operazione51 extends OperazioneFlowChart {
		private int i;
		private String type;
		public Operazione51(String op,int i,String type) {
			super(op, null);
			this.i=i;
			this.type=type;
		}
	}
	
	public static class Operazione90 extends OperazioneFlowChart{
		private String u;
		private String v;
		private String typeStatement;
		public Operazione90(String op, String callFunction, String u, String v, String typeStatement) {
			super(op, callFunction);
			this.u = u;
			this.v = v;
			this.typeStatement = typeStatement;
		}
		
	}
	public static Operazione31 creaOpGetNodeP(String nameNode) {
		return new Operazione31("getnodep", nameNode);
	}

	public static Operazione32 creaGoto(String etichetta) {
		return new Operazione32("goto",etichetta);
	}
	public static Operazione1 creaEsitoParsing(String info) {
		return new Operazione1(info,"parse_end");
	}
	public static Operazione0 creaInformazione(String info) {
		return new Operazione0(info);
	}
	public static Operazione2 creaParsingInfo(String parser, String inputfile, String date, String dumpfile) {
		return new Operazione2("parsing_info", parser, inputfile, date, dumpfile);
	}

	public static Operazione7 creaInsertEdgeGSS(String u, String v,int id) {
		return new Operazione7("insert_gss_edge", "create", u, v,id);
	}
	
	public static Operazione90 creaNewStatement(String numStatement,String type,String link) {
		return new Operazione90("set_statement",null,type,link,numStatement);
	}
	
	public static Operazione8 creaStato(String to_state, String item) {
		return new Operazione8("set_state", to_state, item);
	}

	public static Operazione27 creaInsertNodeGSS(String parse_state,int id) {
		return new Operazione27("insert_gss_node", "create", parse_state,id);
	}
	
	public static Operazione12 creaInsertRelement(String label, String nameNode, int i, String nameNodeSppf,int id) {
		return new Operazione12("insert_r_element", "add", label, nameNode, i, nameNodeSppf,id);
	}

	public static Operazione13 creaInsertUelement(String label, String nameNode,int id) {
		return new Operazione13("insert_u_element", "add", label, nameNode,id);
	}

	public static Operazione14 creaInsertPelement(String nameNode, int i, String nameNodeSppf) {
		return new Operazione14("insert_p_element", "pop", nameNode, i, nameNodeSppf);
	}

	public static Operazione11 creaInsertNodeSppf(String parse_state, String item,int id,int idNodo,int position) {
		return new Operazione11("insert_sppf_node", "getnodet", parse_state, item,id,idNodo,position);
	}

	public static Operazione7 creaInsertEdgeSppf(String u, String v,int id) {
		return new Operazione7("insert_sppf_edge", "getnodet", u, v, id);
	}
	
	public static Operazione50 creaGetCurrentToken(int i,String type) {
		return new Operazione50 ("get_current_token",i,type);
	}
	
	public static Operazione51 creaSetCurrentToken(int i,String type) {
		return new Operazione51 ("set_token_view",i,type);
	}
}