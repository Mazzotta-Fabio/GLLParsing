package elementijson;

@SuppressWarnings("unused")
public abstract class Operazione {
	
	private String op;
	public Operazione(String op) {
		this.op=op;
	}
	
	public static class Operazione1 extends Operazione{
		public Operazione1(String op) {
			super(op);
		}
	}
	
	public static class Operazione2 extends Operazione{
		private String parser;
		private String inputfile;
		private String date;
		public Operazione2(String op, String parser, String inputfile,String date) {
			super(op);
			this.parser = parser;
			this.inputfile = inputfile;
			this.date=date;
		}
	}

	public static class Operazione4 extends Operazione{
		private int id;
		private String value;
		public Operazione4(String op,int id,String value) {
			super(op);
			this.id=id;
			this.value=value;
		}
	}
	
	public static class Operazione5 extends Operazione{
		private String to_state;
		private int id;
		private String value;
		public Operazione5(String op,String to_state,int id, String value) {
			super(op);
			this.to_state=to_state;
			this.id=id;
			this.value=value;
		}
	}
	
	public static class Operazione7 extends Operazione{
		private String u;
		private String v;
		public Operazione7(String op,String u,String v) {
			super(op);
			this.u=u;
			this.v=v;
		}
	}
	
	public static class Operazione8 extends Operazione{
		private String to_state;
		private String item;
		public Operazione8(String op,String to_state,String item) {
			super(op);
			this.to_state=to_state;
			this.item=item;
		}
	}
	
	public static class Operazione11 extends Operazione{
		private String parse_state;
		public Operazione11(String op,String parse_state) {
			super(op);
			this.parse_state=parse_state;
		}
	}
	
	public static class Operazione12 extends Operazione{
		private String label;
		private String nameNode;
		private int i;
		private String nameNodeSppf;
		public Operazione12(String op,String label, String nameNode,int i,String nameNodeSppf) {
			super(op);
			this.label=label;
			this.nameNode=nameNode;
			this.i=i;
			this.nameNodeSppf=nameNodeSppf;
		}
	}
	public static class Operazione13 extends Operazione{
		private String label;
		private String nameNode;
		public Operazione13(String op,String label, String nameNode) {
			super(op);
			this.label=label;
			this.nameNode=nameNode;
		}
	}
	public static class Operazione14 extends Operazione{
		private String nameNode;
		private int i;
		private String nameNodeSppf;
		public Operazione14(String op,String nameNode,int i,String nameNodeSppf) {
			super(op);
			this.nameNode=nameNode;
			this.i=i;
			this.nameNodeSppf=nameNodeSppf;
		}
	}
	
	public static Operazione1 creaInformazione(String op) {
		return new Operazione1(op);
	}
	
	public static Operazione2 creaParsingInfo(String parser, String inputfile,String date) {
		return new Operazione2("parsing_info", parser, inputfile,date);
	}
	
	public static Operazione4 creaCurrentToken(int id, String value) {
		return new Operazione4("current_token",id,value);
	}
	
	public static Operazione5 creaShift(String to_state,int id, String value) {
		return new Operazione5("goto",to_state,id,value);
	}
	
	public static Operazione7 creaInsertEdgeGSS(String u, String v) {
		return new Operazione7("insert_gss_edge",u,v);
	}
	
	public static Operazione8 creaGoto(String to_state,String item) {
		return new Operazione8("goto",to_state,item);
	}
	
	public static Operazione11 creaInsertNodeGSS(String parse_state) {
		return new Operazione11("insert_gss_node",parse_state);
	}
	
	public static Operazione12 creaInsertRelement(String label,String nameNode,int i,String nameNodeSppf) {
		return new Operazione12("insert_r_element",label,nameNode,i,nameNodeSppf);
	}
	
	public static Operazione13 creaInsertUelement(String label,String nameNode) {
		return new Operazione13("insert_u_element",label,nameNode);
	}
	
	public static Operazione14 creaInsertPelement(String nameNode,int i,String nameNodeSppf) {
		return new Operazione14("insert_p_element",nameNode,i,nameNodeSppf);
	}
	
	public static Operazione11 creaInsertNodeSppf(String parse_state) {
		return new Operazione11("insert_sppf_node",parse_state);
	}
	
	public static Operazione7 creaInsertEdgeSppf(String u, String v) {
		return new Operazione7("insert_sppf_edge",u,v);
	}
}