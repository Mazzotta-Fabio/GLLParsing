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

const lineColumnNumbersStyles = {"none":1, "onlyStart":2, "startEnd":3};

// options
var stopOnErrorRecovery = true;
var linesNumbersTextStyle = true ? "gray" : null;
var lineColumnNumbersStyleText = lineColumnNumbersStyles.onlyStart;
var lineColumnNumbersStyleNodes = lineColumnNumbersStyles.none;
var lineColumnNumbersStyleTooltip = lineColumnNumbersStyles.none;
var showTreeNodesAttributesInTooltip = true; // false = attributes (e.g. variant) in node text
var defaultTextStyle = "header";
var inputTextSyleRead = "headerblue";
var inputTextSylePush = "headerred";
var inputTextSyleLookaheadCur = "headerorange";
var inputTextSyleLookahead = "headergreen";
var cupdumpTextStyleCurrentLalr = "headerblue";
var terminalNodeColor = Color.YELLOW;
var nonterminalNodeColor = Color.decode("#FFD700");
var virtualStackNodeColor = Color.ORANGE;
var otherNodeColor = Color.LIGHT_GRAY;
var edgeColor = Color.BLACK;

var stackNodes = [];
var virtualStackNodes = [];
var stack = new Graph("Stack", TreeLayouter.NAME);
var state = new Text("Current status");
var nodeValue = new Graph("Stack element parse tree", TreeLayouter.NAME);
var nodeValueNodes = [];
var cur_token;
var cur_token_textentry;
var cur_token_clickconsumer;
var cur_state;
var cur_state_textentry;
var inputTextEntries = [];
var scanTokens = [];
var scanEvents = [];
var lookahead = null;
var inputTextStyles = [];
var textinput = new Text("Input tokens");
var textinput_selected = [];
var textinputfile = new Text("Input");
var inputFileTextEntries = [];
var inputFileTextStyles = [];
var textinputfile_selected = [];
var cupdump = new Text("CUP dump");
var cupdump_selected = null;
var productions = [];
var lalr_states = [];
var cur_lalr_state_Select;

var lastStep = null;
var lastStepActStart = null;
var lastErrorRecovery = null;

var stdLabel = new NodeLabelType("Standard node label");
var fineLevel = new DetailLevel("Fine");
var coarseLevel = new DetailLevel("Coarse");

var initializer = null;

function newCompositeEvent(logText, granularity, parent, autoop) {
	return autoop ? CompositeEvent.makeAuto(logText, granularity, parent) : new CompositeEvent(logText, granularity, parent);
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

function newActionCompositeEvent(op, autoop) {
	return lastStepActStart == null ? newCompositeEvent(opToString(op), lastErrorRecovery == null ? 3 : 2, lastStep, autoop) : newCompositeEvent(opToString(op), lastErrorRecovery == null ? 2 : 1, lastStep, autoop);
}

function lastStepActStart() {
	return lastStep && lastStep.getLogText().contains("act:\"start\"");
}

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

function opToString(obj) {
	var res = obj.op;
	for (var key in obj) {
		if(obj[key].id != undefined && obj[key].sym != undefined) {
			res += " " + key + ":{" + tokenToString(obj[key], lineColumnNumbersStyleText) + "}";
		} else if(key != "op"){
			res += " " + key + ":" + JSON.stringify(obj[key]);
		}
	}
	return res;
}

function tokenId(obj) {
	return "i" + obj.id;
}

function tokenToString(obj, lineColumnNumStyle, newline) {
	var res = (obj.parse_state != -1 ? "state:" + obj.parse_state + (newline ? "\n" : " ") : "") + (obj.name != null ? obj.name : "sym:" + obj.sym);
	return res + lineColumnToString(lineColumnNumStyle, obj.line, obj.column, obj.endline, obj.endcolumn, " (", ")");
}

function lineColumnToString(style, line, column, endline, endcolumn, startText, endText) {
	return style != lineColumnNumbersStyles.none ? (startText != undefined ? startText : "") + "l" + line + " c" + column + (style == lineColumnNumbersStyles.onlyStart ? "" : (line == endline ? (column == endcolumn ? "" : "-" + endcolumn) : " - l" + endline + " c" + endcolumn)) + (endText != undefined ? endText : ""): "";
}

function showSubTree(xmlnode, parent, gView) {
	var children = xmlnode.children();
	var tagChildren = [];
	var text = "";
    for (var i = 0; i < children.length(); i++) {
        var child = children[i];
        if(child.localName() == null) {
        	text += child + "\n";
        } else {
        	tagChildren.push(child);
        }
    }
    
	var regex = /.*\:(\d+)\/(\d+)\((\d+)\).*/;
	var left = xmlnode.@left.toString().match(regex);
	var right = xmlnode.@right.toString().match(regex);

	var descr = "" + xmlnode.@id;
	var lcs = "";
	if(left != null) {
		descr += lineColumnToString(lineColumnNumbersStyleNodes, left[1], left[2], right[1], right[2], "\n(", ")");
		lcs = lineColumnToString(lineColumnNumbersStyleTooltip, left[1], left[2], right[1], right[2], "<i>", "</i>");
	}

    var attributes = xmlnode.attributes();
    for (var i = 0; i < attributes.length(); i++) {
    	var name = attributes[i].name();
    	if(name != "id" && name != "left" && name != "right") {
    		var at = "\n" + attributes[i].name() + ":" + attributes[i].toString();
    		if(showTreeNodesAttributesInTooltip) {
    			lcs += at;
    		} else {
    			descr += at;
    		}
    	}
    }
    if(text.length > 0) {
    	descr += "\nvalue:" + text;
    }

	var n = new Node(descr, nodeValue, initialPos(stack, []), null);
	if(lcs.length > 0) {
	    n.addDecoration(new TooltipDecoration(n, fineLevel, "<html>" + lcs.trim().replace("\n", "<br>") + "</html>", null));
	}
	if(left != null && right != null) {
		n.addDecoration(new ClickDecoration(n, new Consumer({
	        accept: function (e) {
	        	selectInputFile(left[1], left[2], right[1], right[2]);
	        	selectInput(left[1], left[2], right[1], right[2]);
	        }
	    }), null));
	} else {
		n.addDecoration(new ClickDecoration(n, new Consumer({
	        accept: function (e) {
	        	removeSelectionsInputAndInputFile();
	        }
	    }), null));
	}
    gView.notify(n, true);
	nodeValueNodes.push(n);
	if(xmlnode.name() == "nonterminal") {
		gView.notify(new NodeColor(n, nonterminalNodeColor, null), true);
		gView.notify(new NodeLabel(fineLevel, n, descr, stdLabel, null), true);
	} else if(xmlnode.name() == "terminal") {
		gView.notify(new NodeColor(n, terminalNodeColor, null), true);
		gView.notify(new NodeLabel(fineLevel, n, descr, stdLabel, null), true);
	} else  {
		gView.notify(new NodeColor(n, otherNodeColor, null), true);
		gView.notify(new NodeLabel(fineLevel, n, xmlnode.name() + "\n" + descr, stdLabel, null), true);
	}
    
	if(parent != null) {
    	var e = new Edge(parent, n, nodeValue, null);
    	gView.notify(e, true);
    	gView.notify(new EdgeStyle(e, edgeColor, null), true);
	}

    for (var i = 0; i < tagChildren.length; i++) {
        showSubTree(tagChildren[i], n, gView);
    }
}

var donothing = new Runnable({
    run: function () {
    }
});

function showValue(token) {
	var gView = app.getGraphFrame(nodeValue).getView();
	while(nodeValueNodes.length > 0) {
		gView.notify(nodeValueNodes.pop(), false);
	}
	nodeValueNodesEdges = [];
	var value = token.value;
	if(("" + value).startsWith("<nonterminal")) {
		showSubTree(new XML(value), null, gView);
	} else {
		var termxml = <terminal id={token.name != null ? token.name : "sym:" + token.sym} left={ "unknown:" + token.line + "/" + token.column + "(0)" } right={ "unknown:" + token.endline + "/" + token.endcolumn + "(0)" }>{ value != undefined ? value : "" }</terminal>;
		showSubTree(termxml, null, gView);
	}
	gView.animatedLayoutFromScratch(donothing);
}

function newInputTextStyle(token, style, compositeevent) {
	var tokenid = tokenId(token);
	if(inputTextStyles[tokenid]) {
		new Delete(inputTextStyles[tokenid], compositeevent);
	}
	inputTextStyles[tokenid] = new TextStyle(inputTextEntries[tokenid], style, compositeevent);
	
	for(var l = token.line - 1; l < Math.min(token.endline, inputFileTextEntries.length); l++) {
		var end = l == token.endline - 1 ? Math.min(token.endcolumn, inputFileTextEntries[l].length) : inputFileTextEntries[l].length;
    	for(var c = token.column - 1; c < end; c++) {
        	if(inputFileTextStyles[tokenid]) {
        		new Delete(inputFileTextEntries[l][c], compositeevent);
        	}
        	inputFileTextStyles[l][c] = new TextStyle(inputFileTextEntries[l][c], style, compositeevent);
    	}
	}
	
	return inputTextStyles[tokenid];
}

function inputFileClickSelector(e, ln, cn) {
    return new Consumer({
        accept: function (e) {
        	var textFrame = app.getTextFrame(textinput);
        	
        	for(var k = 0; k < textinput_selected.length; k++) {
        		textFrame.notify(textinput_selected[k], false);
        	}
        	textinput_selected = [];
        	
        	var selected = null;
        	for (var key in scanTokens) {
        		var token = scanTokens[key];
                if(token.line == ln) {
                    if(token.column > cn) {
                    	break;
                    }
                } else if(token.line > ln) {
                	break;
                }
            	selected = key;
        	}
        	if(SwingUtilities.isRightMouseButton(e)) {
	        	var sel = new Select(inputTextEntries[selected], null);
	        	textFrame.notify(sel, true);
	        	textinput_selected.push(sel);
        	} else {
        		var ce = scanEvents[selected];
        		removeSelectionsInputAndInputFile();
                app.goTo(ce, ce.getChildren().size());
        	}
        }
    });
}

function loadtextinputfile(inputfile) {
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
}

function loadcupdump(file) {
    var dump = new java.util.Scanner(file).useDelimiter("\\A").next();
    var sp = dump.split("lalr_state");
    new TextEntry(0, sp[0], defaultTextStyle, cupdump);
    
	var regex = /\[(\d+)\] (.* ::= .*)\n/g;

	for(var m; m = regex.exec(sp[0]);) {
		if(productions.length != m[1]) throw "Missing productions?";
		productions.push(m[2].trim());
	}
	
    for (var i = 1; i < sp.length - 1; i++) {
        var spi = "lalr_state" + sp[i];
        lalr_states[i - 1] = new TextEntry(0, spi.replace(/\n$/, ''), defaultTextStyle, cupdump);
    }
    if(sp.length > 1) {
    	var last = "lalr_state" + sp[sp.length - 1];
    	var sep = "-------------------";
    	var pos = last.indexOf(sep);
        lalr_states[sp.length - 2] = new TextEntry(0, pos != -1 ? last.substring(0, pos + sep.length) : last, defaultTextStyle, cupdump);
    	if(pos != -1) {
    		new TextEntry(0, last.substring(pos + sep.length), defaultTextStyle, cupdump);
    	}
    }
}

function selectInput(line, column, endline, endcolumn) {
	var textFrame = app.getTextFrame(textinput);
	
	while(textinput_selected.length > 0) {
		textFrame.notify(textinput_selected.pop(), false);
	}
	
	for (var key in scanTokens) {
		var token = scanTokens[key];
		if((line < token.line || (line == token.line && column <= token.column)) &&
				(endline > token.endline || (endline == token.endline && endcolumn >= token.endcolumn))) {
    		var sel = new Select(inputTextEntries[key], null);
    		textFrame.notify(sel, true);
        	textinput_selected.push(sel);
		}
	}
}

function selectInputFile(line, column, endline, endcolumn) {
	var textFrame = app.getTextFrame(textinputfile);
	while(textinputfile_selected.length > 0) {
		textFrame.notify(textinputfile_selected.pop(), false);
	}
	
	for(var l = Math.max(0, line - 1); l < Math.min(endline, inputFileTextEntries.length); l++) {
		var end = l == endline - 1 ? Math.min(endcolumn, inputFileTextEntries[l].length) : inputFileTextEntries[l].length;
    	for(var c = Math.max(0, column - 1); c < end; c++) {
    		var sel = new Select(inputFileTextEntries[l][c], null);
        	textFrame.notify(sel, true);
        	textinputfile_selected.push(sel);
    	}
	}
}

function removeSelectionsInputAndInputFile(line, column, endline, endcolumn) {
	var textFrameI = app.getTextFrame(textinput);
	while(textinput_selected.length > 0) {
		textFrameI.notify(textinput_selected.pop(), false);
	}
	
	var textFrameIF = app.getTextFrame(textinputfile);
	while(textinputfile_selected.length > 0) {
		textFrameIF.notify(textinputfile_selected.pop(), false);
	}
}

function read_next_token(obj) {
	var ce = newStepCompositeEvent(obj);
	var te = new TextEntry(0, lineColumnToString(lineColumnNumbersStyleText, obj.returns.line, obj.returns.column, obj.returns.endline, obj.returns.endcolumn, "", "\t") + (obj.returns.name != null ? obj.returns.name : obj.returns.sym) + (obj.returns.value != null ? " (value:\"" + obj.returns.value + "\")" : ""), defaultTextStyle, textinput);
    new ClickDecoration(te, new Consumer({
        accept: function (e) {
        	if(SwingUtilities.isRightMouseButton(e)) {
        		selectInputFile(obj.returns.line, obj.returns.column, obj.returns.endline, obj.returns.endcolumn);
        	} else {
        		removeSelectionsInputAndInputFile();
                app.goTo(ce, ce.getChildren().size());
        	}
        }
    }), initializer);
    var tokenid = tokenId(obj.returns);
    scanTokens[tokenid] = obj.returns;
    scanEvents[tokenid] = ce;
	inputTextEntries[tokenid] = te;
	newInputTextStyle(obj.returns, inputTextSyleRead, ce);
	cur_token = obj.returns;
	cur_token_clickconsumer = new Consumer({
        accept: function (e) {
            selectInputFile(obj.returns.line, obj.returns.column, obj.returns.endline, obj.returns.endcolumn);
            selectInput(obj.returns.line, obj.returns.column, obj.returns.endline, obj.returns.endcolumn);
        }
    });
	updateStateText(ce);
}

function stack_push(obj) {
    var ec = newActionCompositeEvent(obj);
    var descr = tokenToString(obj.element, lineColumnNumbersStyleNodes, true);
    var n = new Node(descr, stack, initialPos(stack, stackNodes.length > 0 ? [stackNodes[stackNodes.length - 1]] : []), ec);
    var tokenid = tokenId(obj.element);
    new NodeColor(n, obj.element.parse_state == 0 ? otherNodeColor : (scanEvents[tokenid] == undefined ? nonterminalNodeColor : terminalNodeColor), ec);
    new NodeLabel(fineLevel, n, descr, stdLabel, ec);
    var productions = lalr_states[obj.element.parse_state].getLogText();
    if(obj.element.parse_state != 0) {
    	productions = productions.replaceAll(".* ::= \\(\\*\\) .*\n", "");
    }
    var i = productions.indexOf("\n}\n");
    if(i != -1) {
    	productions = productions.substring(0, i);
    }
    productions = productions.replaceAll("^lalr_state \\[\\d+\\]: \\{\n?", "");
    new TooltipDecoration(n, fineLevel, "<html>" + lineColumnToString(lineColumnNumbersStyleTooltip, obj.element.line, obj.element.column, obj.element.endline, obj.element.endcolumn, "<i>", "</i><br>") + productions.replace("\n", "<br>") + "</html>", ec);
    stackNodes.push(n);
    if(stackNodes.length > 1) {
    	var e = new Edge(n, stackNodes[stackNodes.length - 2], stack, ec);
        new EdgeStyle(e, edgeColor, ec);
    }
    if(inputTextStyles[tokenid]) {
    	newInputTextStyle(obj.element, inputTextSylePush, ec);
    }
    new ClickDecoration(n, new Consumer({
        accept: function (e) {
        	var textFrame = app.getTextFrame(cupdump);
        	if(cupdump_selected != null) {
        		textFrame.notify(cupdump_selected, false);
        	}
        	textFrame.notify(cupdump_selected = new Select(lalr_states[obj.element.parse_state], true, null), true);
        	showValue(obj.element);
        	selectInputFile(obj.element.line, obj.element.column, obj.element.endline, obj.element.endcolumn);
        	selectInput(obj.element.line, obj.element.column, obj.element.endline, obj.element.endcolumn);
        }
    }), initializer);
}

function stack_pop(obj) {
    var ec = newActionCompositeEvent(obj);
    new Delete(stackNodes.pop(), ec);
}

function stack_removeAllElements(obj) {
	if(stackNodes.length > 0) {
	    var ec = newActionCompositeEvent(obj);
		while(stackNodes.length > 0) {
		    new Delete(stackNodes.pop(), ec);
		}
	}
}

function virtual_stack_creation(obj) {
    var ec = newActionCompositeEvent(obj);
	var real_next_i = stackNodes.length - 1 - obj.real_next;
	var n = new Node(obj.top, stack, initialPos(stack, real_next_i >= 0 ? [stackNodes[real_next_i]]: []), ec);
    new NodeColor(n, virtualStackNodeColor, ec);
    new NodeLabel(fineLevel, n, obj.top, stdLabel, ec);
    if(real_next_i >= 0) {
	    var e = new Edge(n, stackNodes[real_next_i], stack, ec);
	    new EdgeStyle(e, edgeColor, ec);
    }
    virtualStackNodes.push(n);
}

function virtual_stack_push(obj) {
    var ec = newActionCompositeEvent(obj);
    var n = new Node(obj.top, stack, initialPos(stack, virtualStackNodes.length > 0 ? [virtualStackNodes[virtualStackNodes.length - 1]]: []), ec);
    new NodeColor(n, virtualStackNodeColor, ec);
    new NodeLabel(fineLevel, n, obj.top, stdLabel, ec);
    var e = new Edge(n, virtualStackNodes[virtualStackNodes.length - 1], stack, ec);
    new EdgeStyle(e, edgeColor, ec);
    virtualStackNodes.push(n);
}

function virtual_stack_pop(obj) {
    var ec = newActionCompositeEvent(obj);
    new Delete(virtualStackNodes.pop(), ec);
    if(virtualStackNodes.length == 0) {
        var real_next_i = stackNodes.length - 1 - obj.real_next;
        var n = new Node(obj.top, stack, initialPos(stack, real_next_i >= 0 ? [stackNodes[real_next_i]]: []), ec);
        new NodeColor(n, virtualStackNodeColor, ec);
        new NodeLabel(fineLevel, n, obj.top, stdLabel, ec);
        if(real_next_i >= 0) {
    	    var e = new Edge(n, stackNodes[real_next_i], stack, ec);
    	    new EdgeStyle(e, edgeColor, ec);
        }
        virtualStackNodes.push(n);
    }
}

function stack_initialization(obj) {
	newStepCompositeEvent(obj);
}

function shift(obj) {
	var shift = Object.assign({}, obj);
	shift.symbol = Object.assign({}, obj.element);
	shift.symbol.parse_state = -1;
	delete shift.element;
	var ce = newStepCompositeEvent(shift);
	cur_state = obj.to_state;
	updateStateText(ce);
    new Delete(cur_lalr_state_Select, ce);
    cur_lalr_state_Select = new TextStyle(lalr_states[obj.to_state], cupdumpTextStyleCurrentLalr, true, ce)
}

function reduce(obj) {
	var reduce = new Object();
	reduce.op = obj.op;
	reduce.production = productions[obj.prod_num];
	newStepCompositeEvent(reduce);
}

function goto(obj) {
	var ce = newActionCompositeEvent(obj, true);
	cur_state = obj.to_state;
	updateStateText(ce);
    new Delete(cur_lalr_state_Select, ce);
    cur_lalr_state_Select = new TextStyle(lalr_states[obj.to_state], cupdumpTextStyleCurrentLalr, true, ce)
}

function updateStateText(ce) {
	new TextStyle(cur_state_textentry, "invisible", ce);
	cur_state_textentry = new TextEntry(0, "current_state: " + cur_state, "invisible", state);
	new TextStyle(cur_state_textentry, defaultTextStyle, ce);
	new TextStyle(cur_token_textentry, "invisible", ce);
	cur_token_textentry = new TextEntry(0, "\cur_token: {" + tokenToString(cur_token, lineColumnNumbersStyleText) + "}", "invisible", state);
	new TextStyle(cur_token_textentry, defaultTextStyle, ce);
    new ClickDecoration(cur_token_textentry, cur_token_clickconsumer, ce);
}

function error_recovery(obj) {
	lastErrorRecovery = newStepCompositeEvent(obj);
	if(obj.result != null) {
		lastStep = lastErrorRecovery = null;
	}
}

function try_parse_ahead(obj) {
	var ec = newStepCompositeEvent(obj);
	if(obj.act == "end") {
		while(virtualStackNodes.length > 0) {
		    new Delete(virtualStackNodes.pop(), ec);
		}
	}
}

function parse_ahead(obj) {
	newStepCompositeEvent(obj);
}

function read_lookahead(obj) {
	var ce = newStepCompositeEvent(obj);
	if(obj.act == "start") {
		lookahead = [];
	} else if(obj.act == "add") {
		newInputTextStyle(obj.token, lookahead.length == 0 ? inputTextSyleLookaheadCur : inputTextSyleLookahead, ce);
		lookahead.push(obj.token);
	}
}

function advance_lookahead(obj) {
	var ce = newActionCompositeEvent(obj);
	if(obj.result) {
		newInputTextStyle(lookahead[obj.lookahead_pos], inputTextSyleLookaheadCur, ce);
	}
}

function restart_lookahead(obj) {
	// TODO visualizzazione lista lookahead
	newStepCompositeEvent(obj);
}

function parse_lookahead(obj) {
	newStepCompositeEvent(obj);
}

function message(obj) {
	if(lastStep) {
		newActionCompositeEvent(obj);
	} else {
		newStepCompositeEvent(obj);
		lastStep = null;
	}
}

function parsing_info(obj) {
    events = new Log(null, ["SubSubAction", "SubAction", "Action", "Step"]);
    events.addAttribute(new Attribute("title", obj.inputfile));
    events.addAttribute(new Attribute("Input file", obj.inputfile));
    events.addAttribute(new Attribute("Parser", obj.parser));
    events.addAttribute(new Attribute("Timestamp", obj.date));
    events.addGraph(stack);
    events.addGraph(nodeValue);
    events.addText(state);
    events.addText(textinput);
    events.addText(textinputfile);
    events.addText(cupdump);
    events.addConfig(stdLabel);
    events.addConfig(fineLevel);
    events.addConfig(coarseLevel);

    initializer = CompositeEvent.makeAuto("Init", 1, events.getRoot());
    loadtextinputfile(new java.io.File(currentFile.getParentFile(), obj.inputfile));
    loadcupdump(new java.io.File(currentFile.getParentFile(), "cupdump.txt"));
    
    cur_state_textentry = new TextEntry(0, "current_state: 0", "invisible", state);
    new TextStyle(cur_state_textentry, defaultTextStyle, initializer);
    cur_token_textentry = new TextEntry(0, "cur_token: null", "invisible", state);
    new TextStyle(cur_token_textentry, defaultTextStyle, initializer);
    cur_token_clickconsumer = new Consumer({
    	accept: function (e) {
    		removeSelectionsInputAndInputFile();
    	}
    });
    new ClickDecoration(cur_token_textentry, cur_token_clickconsumer, initializer);
    
    cur_lalr_state_Select = new TextStyle(lalr_states[0], cupdumpTextStyleCurrentLalr, true, initializer);
}

function parse_end(obj) {
	lastStep = newStepCompositeEvent(obj);
    events.addAttribute(new Attribute("Success", obj.success));
}

var json_log_error = false;
var logArr;
try {
	logArr = JSON.parse(json_log);
} catch(e) {
	json_log_error = true;
	logArr = JSON.parse(json_log + "]");
}

for (var i = 0; i < logArr.length; i++) {
    var logCmd = logArr[i];
    eval(logCmd.op + "(logCmd)")
    if(stopOnErrorRecovery && logCmd.op == "error_recovery") {
    	break;
    }
}

if(json_log_error) {
	events.addAttribute(new Attribute("Success", "truncated json log"));
}
