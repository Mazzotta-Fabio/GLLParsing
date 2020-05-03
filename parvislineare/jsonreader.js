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
var states = new Graph("Current State", TreeLayouter.NAME);
var inputfile= new Text("Input File");
var dumpfile = new Text("Dump File");
var inputfileElements=[];
var sppfNodes = [];
var sppf = new Graph("Sppf", TreeLayouter.NAME);

var stdLabel = new NodeLabelType("Standard node label");
var fineLevel = new DetailLevel("Fine");
var coarseLevel = new DetailLevel("Coarse");

var lastErrorRecovery = null;
var lastStep = null;
var lastStepActStart = null;

var initializer = null;

var defaultTextStyle = "header";
var inputFileElements = [];
var inputTextSyleRead = "headerred";
var statesGoto = [];

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
    
    events.addText(dumpfile);
    events.addText(inputfile);
    
    events.addConfig(stdLabel);
    events.addConfig(fineLevel);
    events.addConfig(coarseLevel); 
    
    initializer = CompositeEvent.makeAuto("Init", 1, events.getRoot());
    loadtextinputfile(new java.io.File(currentFile.getParentFile(), obj.inputfile));
    loadtextdumpfile(new java.io.File(currentFile.getParentFile(), obj.dumpfile));
}

function loadtextdumpfile(dumpfiletext){
	var lines = java.nio.file.Files.readAllLines(dumpfiletext.toPath());
	var index = 0;
	for(var i = 0; i < lines.size(); i++) {
		var line = lines.get(i) + "\n";
		var t= new TextEntry(-1, line,"header",dumpfile);
		if(line.includes("State")){
			statesGoto[index] = line;
			inputFileElements.push(t);
			index++;
		}
	}
}

function set_state(obj){
	var ce = newStepCompositeEvent(obj);
	var descr=obj.to_state+" "+obj.item;
	var n = new Node(descr, states, initialPos(states, stateElements.length > 0 ? [stateElements[stateElements.length - 1]] : []), ce);
	n.addDecoration(new TooltipDecoration(n, fineLevel, "<html>LabelState: "+obj.to_state+"<br>Item: "+obj.item+"<\html>",null));
	new NodeColor(n,Color.CYAN, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	if(stateElements.length>0){
		new Delete(stateElements.shift(), ce);
	}
	stateElements.push(n);
	change_state(obj.to_state,ce);
}

function insert_gss_node(obj){
	var ce = newActionCompositeEvent(obj);
	var descr = obj.parse_state;
	n = new Node(descr, gss, initialPos(gss, gssNodes.length > 0 ? [gssNodes[gssNodes.length - 1]] : []), ce);
	n.addDecoration(new TooltipDecoration(n, fineLevel, "GSSNodeState: "+obj.parse_state,null));
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
	var nameNodeSppf = (obj.nameNodeSppf).replace(/[0-9]/g,'');
	var descr = obj.label+" "+obj.nameNode+" "+obj.i+" "+nameNodeSppf;
	var n = new Node(descr, r, initialPos(r, rElements.length > 0 ? [rElements[rElements.length - 1]] : []), ce);
	n.addDecoration(new TooltipDecoration(n, fineLevel, "<html>LabelState: "+obj.label+"<br> NodeGSS: "+obj.nameNode+"<br>PositionInput: "+obj.i+"<br> NodeSppf: "+nameNodeSppf+"<\html>",null));
	new NodeColor(n,Color.ORANGE, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	rElements.push(n);
	if(rElements.length > 1) {
	    var e = new Edge(n, rElements[rElements.length - 2], r, ce);
	    new EdgeStyle(e, Color.BLACK, ce);
	}
	/*
	//non funziona
	new ClickDecoration(n,new Consumer({
		accept: function (e) {
			var textFrame = app.getTextFrame(r);
			var del = new Delete(rElements.shift(),ce);
			textFrame.notify(del,true);
		}
	}), initializer);
	*/
}

function remove_r_element(obj){
	var ce = newActionCompositeEvent(obj);
	new Delete(rElements.shift(), ce);
}

function insert_u_element(obj){
	var ce = newActionCompositeEvent(obj);
	var descr = obj.label+" "+obj.nameNode;
	var n = new Node(descr, u, initialPos(u, uElements.length > 0 ? [uElements[uElements.length - 1]] : []), ce);
	n.addDecoration(new TooltipDecoration(n, fineLevel, "<html>LabelState: "+obj.label+"<br> GSSNode: "+obj.nameNode+"<\html>",null));
	new NodeColor(n,Color.GREEN, ce);
	new NodeLabel(fineLevel, n, descr, stdLabel, ce);
	uElements.push(n);
	if(uElements.length > 1) {
	    var e = new Edge(n, uElements[uElements.length - 2], u, ce);
	    new EdgeStyle(e, Color.BLACK, ce);
	}
}

function getnodep(obj){
	var ce = newActionCompositeEvent(obj);
}

function insert_p_element(obj){
	var ce = newActionCompositeEvent(obj);
	var nameNodeSppf = obj.nameNodeSppf.replace(/[0-9]/g,'');
	var descr=obj.nameNode+" "+obj.i+" "+nameNodeSppf;
	var n = new Node(descr, p, initialPos(p, pElements.length > 0 ? [pElements[pElements.length - 1]] : []), ce);
	n.addDecoration(new TooltipDecoration(n, fineLevel, "<html> GSSNode: "+obj.nameNode+"<br>PositionInput: "+obj.i+"<br>SppfNode: "+nameNodeSppf+"<\html>",null));
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
}

function goto(obj){
	var ce = newActionCompositeEvent(obj);
}

function change_state(state,compositeEvent){
	for(var i = 0; i < inputFileElements.length; i++){
		var arr = statesGoto[i].split(" ");
		if(arr[2]==state){
			new TextStyle(inputFileElements[i], inputTextSyleRead ,compositeEvent);
		}
		else{
			new TextStyle(inputFileElements[i],defaultTextStyle,compositeEvent);
		}
	}
}

function insert_sppf_node(obj){
	var ce = newActionCompositeEvent(obj);
	var descr=(obj.parse_state).replace(/[0-9]/g,'')+" ["+obj.item+"]";
	n = new Node(descr, sppf, initialPos(sppf, sppfNodes.length > 0 ? [sppfNodes[sppfNodes.length - 1]] : []), ce);
	var expr = /^[a-z()+]/;
	if(expr.test(descr)){
		if(obj.mark){
			new NodeColor(n,Color.GRAY,ce);
			n.addDecoration(new TooltipDecoration(n, fineLevel, "<html> InvalidTerminalNode <br> NameNode: "+(obj.parse_state).replace(/[0-9]/g,'')+"<br> Item: "+obj.item+" <\html>",null));
		}
		else{
			new NodeColor(n,Color.ORANGE, ce);
			n.addDecoration(new TooltipDecoration(n, fineLevel, "<html> TerminalNode <br> NameNode: "+(obj.parse_state).replace(/[0-9]/g,'')+"<br> Item: "+obj.item+" <\html>",null));
		}
	}
	else{
		if(obj.mark){
			new NodeColor(n,Color.GRAY,ce);
			n.addDecoration(new TooltipDecoration(n, fineLevel, "<html> InvalidNonTerminalNode <br> NameNode: "+(obj.parse_state).replace(/[0-9]/g,'')+"<br> Item: "+obj.item+" <\html>",null));
		}
		else{
			new NodeColor(n,Color.GREEN, ce);
			n.addDecoration(new TooltipDecoration(n, fineLevel, "<html> NonTerminalNode <br> NameNode: "+(obj.parse_state).replace(/[0-9]/g,'')+"<br> Item: "+obj.item+" <\html>",null));
		}
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

function set_current_token(obj){
	var ce = newActionCompositeEvent(obj);
	if(obj.id>0){
		new TextStyle(inputfileElements[obj.id-1], "headerblue", ce);
	}
	new TextStyle(inputfileElements[obj.id], "headerred", ce);
}

function failure_parse(obj){
	var ce = newActionCompositeEvent(obj);
	events.addAttribute(new Attribute("Result", obj.op));
}

function success_parse(obj){
	var ce = newActionCompositeEvent(obj);
	events.addAttribute(new Attribute("Result", obj.op));
}

function error_parse(obj){
	var ce= newActionCompositeEvent(obj);
	events.addAttribute(new Attribute("Result", obj.op));
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
	var res;
	if(obj.op == "set_state"){
		res = "state";
	}
	else{
		if((obj.callFunction != undefined)&&(obj.id != undefined)){
			res = obj.callFunction + obj.id + ": " + obj.op;
		} 
		else{
			if(obj.callFunction != undefined){
				res = obj.callFunction + ": " + obj.op;
			}
			else{
				if(obj.op == "remove_r_element"){
					res = obj.op;
				}
				else{
					res = obj.op + ":";
				}
			}
		}
	}
	for (var key in obj) {
		if((key != "op")&&(key != "callFunction")&&(key != "id")&&(key != "mark")&&(key != "idNodo")){
			res += " " + JSON.stringify(obj[key]);
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
