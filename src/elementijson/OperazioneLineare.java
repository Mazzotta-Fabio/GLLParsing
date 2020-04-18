package elementijson;

@SuppressWarnings("unused")
public abstract class OperazioneLineare {

	private String op;
	private String callFunction;

	public OperazioneLineare(String op, String callFunction) {
		this.op = op;
		this.callFunction = callFunction;
	}
	public static class Operazione0 extends OperazioneLineare {
		public Operazione0(String op) {
			super(op,null);
		}
	}

	public static class Operazione1 extends OperazioneLineare {
		public Operazione1(String op,String callFunction) {
			super(op,callFunction);
		}
	}
	
	public static class Operazione2 extends OperazioneLineare {
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

	public static class Operazione4 extends OperazioneLineare {
		private int id;
		private String value;
		public Operazione4(String op, int id, String value) {
			super(op, null);
			this.id = id;
			this.value = value;
		}
	}

	public static class Operazione7 extends OperazioneLineare {
		private String u;
		private String v;

		public Operazione7(String op, String callFunction, String u, String v) {
			super(op, callFunction);
			this.u = u;
			this.v = v;
		}
	}

	public static class Operazione8 extends OperazioneLineare {
		private String to_state;
		private String item;

		public Operazione8(String op, String to_state, String item) {
			super(op, null);
			this.to_state = to_state;
			this.item = item;
		}
	}

	public static class Operazione11 extends OperazioneLineare {
		private String parse_state;
		private String item;

		public Operazione11(String op, String callFunction, String parse_state, String item) {
			super(op, callFunction);
			this.parse_state = parse_state;
			this.item = item;
		}
	}

	public static class Operazione27 extends OperazioneLineare {
		private String parse_state;

		public Operazione27(String op, String callFunction, String parse_state) {
			super(op, callFunction);
			this.parse_state = parse_state;
		}
	}

	public static class Operazione12 extends OperazioneLineare {
		private String label;
		private String nameNode;
		private int i;
		private String nameNodeSppf;

		public Operazione12(String op, String callFunction, String label, String nameNode, int i, String nameNodeSppf) {
			super(op, callFunction);
			this.label = label;
			this.nameNode = nameNode;
			this.i = i;
			this.nameNodeSppf = nameNodeSppf;
		}
	}

	public static class Operazione13 extends OperazioneLineare {
		private String label;
		private String nameNode;
		public Operazione13(String op, String callFunction, String label, String nameNode) {
			super(op, callFunction);
			this.label = label;
			this.nameNode = nameNode;
		}
	}

	public static class Operazione14 extends OperazioneLineare {
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

	public static class Operazione31 extends OperazioneLineare {
		private String nameNode;
		public Operazione31(String op, String nameNode) {
			super(op, null);
			this.nameNode = nameNode;
		}
	}
	
	public static class Operazione32 extends OperazioneLineare {
		private String label;
		public Operazione32(String op,String label) {
			super(op, null);
			this.label = label;
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

	public static Operazione4 creaCurrentToken(int id, String value) {
		return new Operazione4("set_current_token", id, value);
	}

	public static Operazione7 creaInsertEdgeGSS(String u, String v) {
		return new Operazione7("insert_gss_edge", "create", u, v);
	}

	public static Operazione8 creaStato(String to_state, String item) {
		return new Operazione8("set_state", to_state, item);
	}

	public static Operazione27 creaInsertNodeGSS(String parse_state) {
		return new Operazione27("insert_gss_node", "create", parse_state);
	}

	public static Operazione12 creaInsertRelement(String label, String nameNode, int i, String nameNodeSppf) {
		return new Operazione12("insert_r_element", "add", label, nameNode, i, nameNodeSppf);
	}

	public static Operazione13 creaInsertUelement(String label, String nameNode) {
		return new Operazione13("insert_u_element", "add", label, nameNode);
	}

	public static Operazione14 creaInsertPelement(String nameNode, int i, String nameNodeSppf) {
		return new Operazione14("insert_p_element", "pop", nameNode, i, nameNodeSppf);
	}

	public static Operazione11 creaInsertNodeSppf(String parse_state, String item) {
		return new Operazione11("insert_sppf_node", "getnodet", parse_state, item);
	}

	public static Operazione7 creaInsertEdgeSppf(String u, String v) {
		return new Operazione7("insert_sppf_edge", "getnodet", u, v);
	}
}