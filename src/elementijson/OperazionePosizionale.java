package elementijson;

import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class OperazionePosizionale {
	
	private String op;
	private String callFunction;

	public OperazionePosizionale(String op, String callFunction) {
		this.op = op;
		this.callFunction = callFunction;
	}
	public static class Operazione0 extends OperazionePosizionale {
		public Operazione0(String op) {
			super(op,null);
		}
	}
	
	public static class Operazione1 extends OperazionePosizionale {
		public Operazione1(String op,String callFunction) {
			super(op,callFunction);
		}
	}
	
	public static class Operazione2 extends OperazionePosizionale{
		private String parser;
		private String inputfile;
		private String date;
		private String dumpfile;
		private String picturefile;
		public Operazione2(String op,String parser, String inputfile, String date,String dumpfile) {
			super(op, null);
			this.parser = parser;
			this.inputfile = inputfile;
			this.date = date;
			this.dumpfile = dumpfile;
		}
	}
	
	public static class Operazione11 extends OperazionePosizionale{
		private String parse_state;
		private int id;
		public Operazione11(String op,String callFunction,String parse_state,int id) {
			super(op,callFunction);
			this.parse_state=parse_state;
			this.id=id;
		}
	}
	
	public static class Operazione7 extends OperazionePosizionale{
		private String u;
		private String v;
		private int id;
		public Operazione7(String op,String callFunction,String u,String v,int id) {
			super(op,callFunction);
			this.u=u;
			this.v=v;
			this.id=id;
		}
	}
	
	public static class Operazione12 extends OperazionePosizionale{
		private String label;
		private String nameNode;
		private int i;
		private String nameNodeSppf;
		private String tokenViews;
		private int id;
		public Operazione12(String op,String callFunction,String label, String nameNode,int i,String nameNodeSppf,String tokenViews,int id) {
			super(op,callFunction);
			this.label=label;
			this.nameNode=nameNode;
			this.i=i;
			this.nameNodeSppf=nameNodeSppf;
			this.tokenViews=tokenViews;
			this.id=id;
		}
	}
	public static class Operazione13 extends OperazionePosizionale{
		private String label;
		private String nameNode;
		private int id;
		public Operazione13(String op,String callFunction,String label, String nameNode,int id) {
			super(op,callFunction);
			this.label=label;
			this.nameNode=nameNode;
			this.id=id;
		}
	}
	public static class Operazione14 extends OperazionePosizionale{
		private String nameNode;
		private int i;
		private String nameNodeSppf;
		private String tokenViews;
		public Operazione14(String op,String callFunction,String nameNode,int i,String nameNodeSppf,String tokenViews) {
			super(op,callFunction);
			this.nameNode=nameNode;
			this.i=i;
			this.nameNodeSppf=nameNodeSppf;
			this.tokenViews=tokenViews;
		}
	}	
	
	public static class Operazione20 extends OperazionePosizionale{
		private String parse_state;
		private String item;
		private boolean mark;
		private int id;
		private int idNodo;
		private int position;
		public Operazione20(String op,String callFunction,String parse_state,String item,int id,int idNodo,int position) {
			super(op,callFunction);
			this.parse_state=parse_state;
			this.item=item;
			this.mark=false;
			this.id=id;
			this.idNodo=idNodo;
			if(position>=0) {
				this.position=position;
			}
		}
		public int getIdNodo() {
			return idNodo;
		}
		public String getItem() {
			return item;
		}
		public void setMark() {
			mark=true;
		}
	}	
	public static class Operazione25 extends OperazionePosizionale{
		private int token;
		private int [] tokenViews;
		public Operazione25(String op,int token,int [] tokenViews) {
			super(op,null);
			this.token=token;
			this.tokenViews=tokenViews;
		}
	}
	
	public static class Operazione26 extends OperazionePosizionale{
		private int [] tokenViews;
		public Operazione26(String op,int [] tokenViews) {
			super(op,null);
			this.tokenViews=tokenViews;
		}
	}
	
	public static class Operazione31 extends OperazionePosizionale {
		private String nameNode;
		public Operazione31(String op, String nameNode) {
			super(op, null);
			this.nameNode = nameNode;
		}
	}
	
	public static class Operazione32 extends OperazionePosizionale {
		private String label;
		public Operazione32(String op,String label) {
			super(op, null);
			this.label = label;
		}
	}
	
	public static class Operazione8 extends OperazionePosizionale{
		private String to_state;
		private String item;
		public Operazione8(String op,String to_state,String item) {
			super(op,null);
			this.to_state=to_state;
			this.item=item;
		}
	}
	
	//metodi
	private static int [] getNumArray(ArrayList<String> t) {
		int [] tokenViews=new int [t.size()];
		for(int i=0;i<t.size();i++) {
			int num=Integer.parseInt(t.get(i));
			tokenViews[i]=num;
		}
		return tokenViews;
	}
	public static Operazione26 creaSetTokenFound(ArrayList<String> t) {
		int [] tokenViews=getNumArray(t);
		return new Operazione26("set_token_found",tokenViews);
	}
	public static Operazione25 creaGetNextToken(int newToken,ArrayList<String> t) {
		int [] tokenViews=getNumArray(t);
		return new Operazione25("get_next_token",newToken,tokenViews);
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
	public static Operazione11 creaInsertNodeGSS(String parse_state,int id) {
		return new Operazione11("insert_gss_node","create",parse_state,id);
	}
	public static Operazione7 creaInsertEdgeGSS(String u, String v,int id) {
		return new Operazione7("insert_gss_edge","create",u,v, id);
	}
	public static Operazione12 creaInsertRelement(String label,String nameNode,int i,String nameNodeSppf,String tokenViews,int id) {
		return new Operazione12("insert_r_element","add",label,nameNode,i,nameNodeSppf,tokenViews,id);
	}
	public static Operazione13 creaInsertUelement(String label,String nameNode,int id) {
		return new Operazione13("insert_u_element","add",label,nameNode,id);
	}
	public static Operazione14 creaInsertPelement(String nameNode,int i,String nameNodeSppf,String tokenViews) {
		return new Operazione14("insert_p_element","pop",nameNode,i,nameNodeSppf,tokenViews);
	}
	public static Operazione20 creaInsertNodeSppf(String parse_state,String item,int id,int idNodo,int position) {
		return new Operazione20("insert_sppf_node","getnodet",parse_state,item,id,idNodo,position);
	}
	public static Operazione7 creaInsertEdgeSppf(String u, String v,int id) {
		return new Operazione7("insert_sppf_edge","getnodet",u,v,id);
	}
	public static Operazione32 creaGoto(String etichetta) {
		return new Operazione32("goto",etichetta);
	}
	public static Operazione31 creaOpGetNodeP(String nameNode) {
		return new Operazione31("getnodep", nameNode);
	}
	public static Operazione8 creaStato(String to_state, String item) {
		return new Operazione8("set_state", to_state, item);
	}
}