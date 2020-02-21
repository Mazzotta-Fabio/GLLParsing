package elementijson;
@SuppressWarnings("unused")
public abstract class ElementBase {
	private int id;
	private String name;
	private String parse_state;
	private int sym;
	private int line;
	private int column;
	private int endline;
	private int endcolumn;
	private String value;
	
	public ElementBase(int id, String name, int sym, String parse_state,String value) {
		this.id = id;
		this.name = name;
		this.parse_state = parse_state;
		this.sym = sym;
		this.value=value;
	}
	
	public static class Returns extends ElementBase{
		public Returns(int id, String name, int sym, String parse_state,String value) {
			super(id, name,sym,parse_state,value);
		}
	}
	public static class Element extends ElementBase{
		public Element(int id, String name, int sym, String parse_state,String value) {
			super(id, name,sym,parse_state,value);
		}
	}
	
	public static class Popped extends ElementBase{
		public Popped(int id, String name, int sym, String parse_state,String value) {
			super(id, name,sym,parse_state,value);
		}
	}
	
	public static Element creaElement(int id, String name, int sym, String parse_state,String value) {
		return new Element (id, name,sym,parse_state,value);
	}
	public static Returns creaReturns(int id, String name, int sym, String parse_state,String value) {
		return new Returns (id, name,sym,parse_state,value);
	}
	public static Popped creaPopped(int id, String name, int sym, String parse_state,String value) {
		return new Popped (id, name,sym,parse_state,value);
	}
}
