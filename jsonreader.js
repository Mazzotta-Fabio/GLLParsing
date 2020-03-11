importPackage(Packages.de.unibwm.inf2.parvis.base.log);
importPackage(Packages.de.unibwm.inf2.parvis.base.log.graph);
importPackage(Packages.de.unibwm.inf2.parvis.base.log.input);
importPackage(Packages.de.unibwm.inf2.parvis.base.log.text);
importPackage(Packages.de.unibwm.inf2.parvis.graph);
importPackage(Packages.de.unibwm.inf2.parvis.graph.layout);
importClass(java.lang.Runnable);
importClass(java.util.function.Consumer)
importClass(java.util.function.Supplier);
importClass(java.awt.Color);
importClass(java.awt.geom.Point2D);
importClass(javax.swing.SwingUtilities);

var gssNodes = [];
var gss = new Graph("Gss", HierarchicGraphLayouter.NAME);
var n;
var rElements = [];
var r = new Graph("R", TreeLayouter.NAME);
var uElements = [];
var u = new Graph("U", TreeLayouter.NAME);
var pElements = [];
var p = new Graph("P", TreeLayouter.NAME);
var stateElements = [];
var states = new Graph("States", TreeLayouter.NAME);
var inputfile= new Text("Input File");
var inputfileElements=[];
var sppfNodes = [];
var sppf = new Graph("Sppf", TreeLayouter.NAME);

var stdLabel = new NodeLabelType("Standard node label");
var fineLevel = new DetailLevel("Fine");
var coarseLevel = new DetailLevel("Coarse");

var lastErrorRecovery = null;
var lastStep = null;
var lastStepActStart = null;

//creazione nodo
function initialPos(graph, refNodes) {
    return new Supplier({
        get: function () {
            var frame = app.getGraphFrame(graph);
            var positions = refNodes.map(n => frame.getView().getPos(n)).filter(p => p != null);
            if (positions.length === 0) return null;
            var sum = positions[0].getX();
            var y = positions[0].getY();
            for (var i = 1; i < positions.length; i++) {
                var p = positions[i];
                sum += p.getX();
                y = Math.min(y, p.getY());
            }
            var x = sum / positions.length;
            return new Point2D.Double(x, y);
        }
    })
}

function parsing_info(obj){
	events = new Log(null, ["SubSubAction", "SubAction", "Action", "Step"]);
    events.addAttribute(new Attribute("Input file", obj.inputfile));
    events.addAttribute(new Attribute("Parser", obj.parser));
    events.addAttribute(new Attribute("Timestamp", obj.date));
    
    events.addGraph(gss);
    events.addGraph(r);
    events.addGraph(u);
    events.addGraph(p);
    events.addGraph(states);
    events.addGraph(sppf);
    events.addText(inputfile);
    
    events.addConfig(stdLabel);
    events.addConfig(fineLevel);
    events.addConfig(coarseLevel); 
    
    loadtextinputfile(new java.io.File(currentFile.getParentFile(), obj.inputfile));
}

function goto(obj){
	var ce = newStepCompositeEvent(obj);
	var descr=obj.to_state+" "+obj.item;
	var n = new Node(descr, states, initialPos(states, stateElements.length > 0 ? [stateElements[stateElements.length - 1]] : []), ce);
	new NodeColor(n,Color.CYAN, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	if(stateElements.length>0){
		new Delete(stateElements.shift(), ce);
	}
	stateElements.push(n);
}

function insert_gss_node(obj){
	var ce = newActionCompositeEvent(obj);
	var descr = obj.parse_state;
	n = new Node(descr, gss, initialPos(gss, gssNodes.length > 0 ? [gssNodes[gssNodes.length - 1]] : []), ce);
	new NodeColor(n,Color.YELLOW, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	gssNodes[obj.parse_state]=n;
}

function insert_gss_edge(obj){
	var ce = newActionCompositeEvent(obj);
	var descr = obj.u+" "+obj.v;
	var e = new Edge(gssNodes[obj.u], gssNodes[obj.v], gss, ce);
	new EdgeStyle(e, Color.BLACK, ce);
}

function insert_r_element(obj){
	var ce = newActionCompositeEvent(obj);
	var descr = obj.label+" "+obj.nameNode+" "+obj.i+" "+(obj.nameNodeSppf).replace(/[0-9]/g,'');;
	var n = new Node(descr, r, initialPos(r, rElements.length > 0 ? [rElements[rElements.length - 1]] : []), ce);
	new NodeColor(n,Color.ORANGE, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	rElements.push(n);
	if(rElements.length > 1) {
	    var e = new Edge(n, rElements[rElements.length - 2], r, ce);
	    new EdgeStyle(e, Color.BLACK, ce);
	}
}
function remove_r_element(obj){
	var ce = newActionCompositeEvent(obj);
	new Delete(rElements.shift(), ce);
}

function insert_u_element(obj){
	var ce = newActionCompositeEvent(obj);
	var descr = obj.label+" "+obj.nameNode;
	var n = new Node(descr, u, initialPos(u, uElements.length > 0 ? [uElements[uElements.length - 1]] : []), ce);
	new NodeColor(n,Color.GREEN, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	uElements.push(n);
	if(uElements.length > 1) {
	    var e = new Edge(n, uElements[uElements.length - 2], u, ce);
	    new EdgeStyle(e, Color.BLACK, ce);
	}
}

function insert_p_element(obj){
	var ce = newActionCompositeEvent(obj);
	var descr=obj.nameNode+" "+obj.i+" "+obj.nameNodeSppf.replace(/[0-9]/g,'');
	var n = new Node(descr, p, initialPos(p, pElements.length > 0 ? [pElements[pElements.length - 1]] : []), ce);
	new NodeColor(n,Color.ORANGE, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	pElements.push(n);
	if(pElements.length > 1) {
	    var e = new Edge(n, pElements[pElements.length - 2], p, ce);
	    new EdgeStyle(e, Color.BLACK, ce);
	}
}

function loadtextinputfile(fileinput) {
	var source = new java.util.Scanner(fileinput).useDelimiter("\\A").next();
	for(var i=0;i<source.length();i++){
		var carattere = source.substring(i,i+1);
		var lnte = new TextEntry(-1, carattere,"header",inputfile);
		inputfileElements.push(lnte);
	}
	/*
	var lines = java.nio.file.Files.readAllLines(inputfile.toPath());
	inputFileTextEntries = new Array(lines.size());
	inputFileTextStyles = new Array(lines.size());
	var nlines = "" + lines.size();
    for(var i = 0; i < lines.size(); i++) {
    	var ln = "" + (i + 1);
    	if(linesNumbersTextStyle) {
	    	var lnte = new TextEntry(-1, "\u2007".repeat((nlines.length - ln.length)) + ln + "\u2003", linesNumbersTextStyle, textinputfile);
	        new ClickDecoration(lnte, inputFileClickSelector(lnte, i + 1, 1), initializer);
    	}

    	var line = lines.get(i) + (ln == nlines ? "" : "\n");
    	inputFileTextEntries[i] = new Array(line.lenght);
    	inputFileTextStyles[i] = new Array(line.lenght);
        for(var j = 0; j < line.length; j++) {
        	inputFileTextEntries[i][j] = new TextEntry(-1, line.substring(j, j + 1), defaultTextStyle, textinputfile);
            new ClickDecoration(inputFileTextEntries[i][j], inputFileClickSelector(inputFileTextEntries[i][j], i + 1, j + 1), initializer);
        }
    }
    */
}

function insert_sppf_node(obj){
	var ce = newActionCompositeEvent(obj);
	var descr=(obj.parse_state).replace(/[0-9]/g,'');
	n = new Node(descr, sppf, initialPos(sppf, sppfNodes.length > 0 ? [sppfNodes[sppfNodes.length - 1]] : []), ce);
	var expr = /^[a-z()+]/;
	if(expr.test(descr)){
		new NodeColor(n,Color.ORANGE, ce);
	}
	else{
		new NodeColor(n,Color.GREEN, ce);
	}
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	sppfNodes[obj.parse_state]=n;
}

function insert_sppf_edge(obj){
	var ce = newActionCompositeEvent(obj);
	var descr=obj.u+" "+obj.v;
	var e = new Edge(sppfNodes[obj.u], sppfNodes[obj.v], sppf, ce);
	new EdgeStyle(e, Color.BLACK, ce);
}

function current_token(obj){
	var descr=obj.value+" "+obj.id;
	var ce=new CompositeEvent("current_token "+descr, 1, events.getRoot());
	var sel = new Select(inputfileElements[obj.id], ce);
}

function failure_parse(obj){
	var ce=new CompositeEvent(obj.op, 1, events.getRoot());
}

function success_parse(obj){
	var ce=new CompositeEvent(obj.op, 1, events.getRoot());
}

function error_parse(obj){
	var ce=new CompositeEvent(obj.op, 1, events.getRoot());
}

function newActionCompositeEvent(op, autoop) {
	return lastStepActStart == null ? newCompositeEvent(opToString(op), lastErrorRecovery == null ? 3 : 2, lastStep, autoop) : newCompositeEvent(opToString(op), lastErrorRecovery == null ? 2 : 1, lastStep, autoop);
}

function lastStepActStart() {
	return lastStep && lastStep.getLogText().contains("act:\"start\"");
}

function newCompositeEvent(logText, granularity, parent, autoop) {
	return autoop ? CompositeEvent.makeAuto(logText, granularity, parent) : new CompositeEvent(logText, granularity, parent);
}

function opToString(obj) {
	var res = obj.op;
	for (var key in obj) {
		if(((obj.op == "insert_p_element")||(obj.op == "insert_sppf_edge")||(obj.op == "insert_sppf_node")||(obj.op == "insert_r_element"))&&((key=="parse_state")||(key == "nameNodeSppf")||(key == "u")||(key == "v"))){
			res += " " + ((JSON.stringify(obj[key])).replace(/[0-9]/g,''));
		}
		else{
			if(key != "op"){
				res += " " + JSON.stringify(obj[key]);
			}
		}
	}
	return res.replace(/"/g,'');
}

function newStepCompositeEvent(op, autoop) {
	if(lastStepActStart) {
		var ec = lastStep = lastErrorRecovery == null ? newCompositeEvent(opToString(op), 3, lastStepActStart, autoop) : newCompositeEvent(opToString(op), 2, lastStepActStart, autoop);
		if(op.act == "end") {
			lastStep = lastStepActStart.getParent();
			lastStepActStart = null;
		}
		return ec;
	} else {
		lastStep = lastErrorRecovery == null ? newCompositeEvent(opToString(op), 4, events.getRoot(), autoop) : newCompositeEvent(opToString(op), 3, lastErrorRecovery, autoop);
		if(op.act == "start") {
			lastStepActStart = lastStep;
		}
		return lastStep;
	}
}

logArr = JSON.parse(json_log);

for (var i = 0; i < logArr.length; i++) {
    var logCmd = logArr[i];
    eval(logCmd.op + "(logCmd)")
}
