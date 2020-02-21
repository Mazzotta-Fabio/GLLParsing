package elementijson;

import elementijson.ElementBase.*;
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
		public Operazione2(String op, String parser, String inputfile, String date) {
			super(op);
			this.parser = parser;
			this.inputfile = inputfile;
			this.date = date;
		}
	}
	
	public static class Operazione3 extends Operazione{
		private String text;
		public Operazione3(String op,String text) {
			super(op);
			this.text=text;
		}
	}
	
	public static class Operazione4 extends Operazione{
		private Returns returns;
		public Operazione4(String op,int id, String name, int sym, String parse_state,String value) {
			super(op);
			this.returns=ElementBase.creaReturns(id, name, sym, parse_state,value);
		}
	}
	
	public static class Operazione5 extends Operazione{
		private String to_state;
		private Element element;
		public Operazione5(String op,String to_state,int id, String name, int sym, String parse_state,String value) {
			super(op);
			this.to_state=to_state;
			this.element=ElementBase.creaElement(id, name, sym, parse_state,value);
		}
	}
	
	public static class Operazione6 extends Operazione{
		private int prod_num;
		private int nt_num;
		private int rhs_size;
		public Operazione6(String op,int prod_num,int nt_num,int rhs_size) {
			super(op);
			this.prod_num=prod_num;
			this.nt_num=nt_num;
			this.rhs_size=rhs_size;
		}
	}
	
	public static class Operazione7 extends Operazione{
		private Popped popped;
		public Operazione7(String op,int id, String name, int sym, String parse_state,String value) {
			super(op);
			this.popped=ElementBase.creaPopped(id, name, sym, parse_state,value);
		}
	}
	
	public static class Operazione8 extends Operazione{
		private String to_state;
		public Operazione8(String op,String to_state) {
			super(op);
			this.to_state=to_state;
		}
	}
	
	public static class Operazione9 extends Operazione{
		private boolean success;
		public Operazione9(String op,boolean success) {
			super(op);
			this.success=success;
		}
	}
	
	public static class Operazione10 extends Operazione{
		private String result;
		public Operazione10(String op,String result) {
			super(op);
			this.result=result;
		}
	}
	
	public static class Operazione11 extends Operazione{
		private Element element;
		public Operazione11(String op,int id, String name, int sym, String parse_state,String value) {
			super(op);
			this.element=ElementBase.creaElement(id, name, sym, parse_state,value);
		}
		
	}
	public static Operazione1 creaInizializzazione(String op) {
		return new Operazione1(op);
	}
	public static Operazione2 creaParsingInfo(String op, String parser, String inputfile, String date) {
		return new Operazione2(op, parser, inputfile, date);
	}
	public static Operazione3 creaMessage(String op, String text) {
		return new Operazione3(op, text);
	}
	public static Operazione4 creaReadNextToken(String op,int id, String name, int sym, String parse_state,String value) {
		return new Operazione4(op,id,name,sym,parse_state,value);
	}
	public static Operazione5 creaShift(String op,String to_state,int id, String name, int sym, String parse_state,String value) {
		return new Operazione5(op,to_state,id,name,sym,parse_state,value);
	}
	public static Operazione6 creaReduce(String op,int prod_num,int nt_num,int rhs_size) {
		return new Operazione6(op,prod_num,nt_num,rhs_size);
	}
	public static Operazione7 creaPopStack(String op,int id, String name, int sym, String parse_state,String value) {
		return new Operazione7(op,id,name,sym,parse_state,value);
	}
	public static Operazione8 creaGoto(String op, String to_state) {
		return new Operazione8(op,to_state);
	}
	public static Operazione9 creaParseEndSucccess(String op, boolean success) {
		return new Operazione9(op,success);
	}
	public static Operazione10 creaParseEndFailure(String op, String result) {
		return new Operazione10(op,result);
	}
	public static Operazione11 creaPushStack(String op,int id, String name, int sym, String parse_state,String value) {
		return new Operazione11(op,id,name,sym,parse_state,value);
	}
}