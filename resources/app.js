var graphics;
var graphPoints;
var text;
var graphWidth;
var graphHeight;
var minX = 99999999;
var minY = 99999999;
var maxX = -9999999;
var maxY = -9999999;
var graphs;
var context;
var expressionNumber;
var vars;



function addColorPicker(expressionNumber) {
    var pk = new Piklor("#color-picker" + expressionNumber, [
           "#DC143C",
           "#228B22",
           "#1E90FF",
           "#FF00FF",
           "#FFD700",
           "#7FFFD4",
           "#8A2BE2",
           "#556B2F",
           "#808080"

        ], {
            open: "#picker-wrapper" + expressionNumber + " #color-button" + expressionNumber
        }),
        wrapperEl = pk.getElm("#color-button" + expressionNumber),
        id = expressionNumber;;

    pk.colorChosen(function(col) {
        wrapperEl.style.backgroundColor = col;
        app.printLog(id);
        app.printLog(graphs.size)
        obj = graphs.get(id);
        if (!!obj) {
            obj.lineColor = Number("0x" + col.replace("#", ""));
            graphs.set(id, obj)
        }

    });

    for (let i = 0; i < pk.colors.length; i++) {
        var colorChosen = true;
        for (var [key, obj] of graphs) {
            if (obj.lineColor == "0x" + pk.colors[i].replace("#", "")) {
                colorChosen = false;
                break;
            }
        }
        if (colorChosen) {
            pk.set(pk.colors[i], true);
            break;
        }
    }
    if (!colorChosen) {
        pk.set(pk.colors[0], true);
    }
}


var config = {
    type: Phaser.AUTO,
    parent: 'graph_place',
    width: 480,
    height: 320,
    scale: {
        mode: Phaser.Scale.NONE,
        autoCenter: Phaser.Scale.NO_CENTER
    },
    backgroundColor: '#ffffff',
    dom: {
        createContainer: true
    },
    scene: {
        create: create,
        update: update
    }
};

var game = new Phaser.Game(config);


function create() {
    expressionNumber = 0;
    context = this;
    graphWidth = 100;
    graphHeight = 100;
    graphs = new Map();
    vars = new Map();
    text = this.add.text(0, 0, 'text123');
    graphics = this.add.graphics();

    follower = {
        t: 0,
        vec: new Phaser.Math.Vector2()
    };

    //  Path starts at 100x100
    path = new Phaser.Curves.Path(10, 10);

    path.lineTo(300, 170);

    // cubicBezierTo: function (x, y, control1X, control1Y, control2X, control2Y)
    path.cubicBezierTo(200, 20, 67, 43, 88, 10);




    addExpression();

}

function update() {
    graphics.clear();

    this.cameras.main.setScroll((minX + maxX - this.cameras.main.width) / 2, (-(minY + maxY) - this.cameras.main.height) / 2);
    var zoomY = this.cameras.main.height / ((maxY - minY) * 1.1 + 5)
    var zoomX = this.cameras.main.width / ((maxX - minX) * 1.1 + 5);
    this.cameras.main.setZoom(Math.min(zoomX, zoomY));

    graphics.lineStyle(2 / this.cameras.main.zoom, 0x111111, 0.5);
/*    app.printLog(""+this.cameras.main.worldView.x+" "+this.cameras.main.worldView.y+"  "+this.cameras.main.worldView.width+" "+this.cameras.main.worldView.height);*/
    graphics.strokeLineShape(new Phaser.Geom.Line(this.cameras.main.worldView.x, 0, minX+this.cameras.main.worldView.width, 0));
    graphics.strokeLineShape(new Phaser.Geom.Line(0, this.cameras.main.worldView.y, 0, this.cameras.main.worldView.y+this.cameras.main.worldView.height));
    graphics.strokeLineShape(new Phaser.Geom.Line(1, 1, 1, -1));
    graphics.strokeLineShape(new Phaser.Geom.Line(1, -1, -1, -1));

    for (var [id, graph] of graphs) {
        graphics.fillStyle( graph.lineColor, 1);
        graph.path.getPoint(graph.follower.t, graph.follower.vec);
        graphics.lineStyle(3 / this.cameras.main.zoom, graph.lineColor, 1);
        graph.path.draw(graphics);
        graphics.fillCircle(graph.follower.vec.x, graph.follower.vec.y, 3 / this.cameras.main.zoom);
    };
    setText("derivativeExpressionValue", '' + minX + ' ' + minY + '   ' + '\n' + (maxX) + ' ' + (maxY) + '\n cam: ' + Math.min(zoomX, zoomY) + '  ' + (-(minY + maxY) - this.cameras.main.height) / 2);
}

function rgb2hex(rgb) {
    rgb = rgb.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+))?\)$/);

    function hex(x) {
        return ("0" + parseInt(x).toString(16)).slice(-2);
    }
    return "0x" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
}

function drawGraph(points, id) {

    if (points.size() > 0) {
        follower = {
            t: 0,
            vec: new Phaser.Math.Vector2()
        };
        var minX = 0;
        var maxX = 0;
        var minY = 0;
        var maxY = 0;
        while (points.size() > 0 && Number.isNaN(points.get(0).value)) {
           points.remove(0);
        }

        path = new Phaser.Curves.Path(points.get(0).key, -points.get(0).value);
        if (points.get(0).key < minX)
            minX = points.get(0).key;
        if (points.get(0).key > maxX)
            maxX = points.get(0).key;
        if (points.get(0).value < minY)
            minY = points.get(0).value;
        if (points.get(0).value > maxY)
            maxY = points.get(0).value;
        var textToSet = '';
        textToSet += '' + points.get(0).key + ',' + (points.get(0).value) + '; ';
        var gotNaN = false;
        for (var i = 1; i < points.size(); i++) {
        if (Number.isNaN(points.get(i).value)) {
            gotNaN = true;
            continue;
        } else if (gotNaN) {
            path.moveTo(points.get(i).key, -points.get(i).value);
            gotNaN = false;
        } else {
            gotNaN = false;
            path.lineTo(points.get(i).key, -points.get(i).value);
            }
            if (points.get(i).key < minX)
                minX = points.get(i).key;
            if (points.get(i).key > maxX)
                maxX = points.get(i).key;
            if (points.get(i).value < minY)
                minY = points.get(i).value;
            if (points.get(i).value > maxY)
                maxY = points.get(i).value;
            // textToSet += ''+points.get(i).getKey()+','+(points.get(i).getValue())+'; ';
        }



        context.tweens.add({
            targets: follower,
            t: 1,
            ease: 'Sine.easeInOut',
            duration: 4000,
            yoyo: true,
            repeat: -1
        }, this);

        this.minX = this.minY = 99999999;
        this.maxX = this.maxY = -9999999;
        var obj = graphs.get(id);
        var lineColor = 0x000000;
        if (!!obj) {
        app.printLog(obj);
            lineColor = obj.lineColor;
        }
        else {
            lineColor = rgb2hex(document.getElementById("color-button"+id).style.backgroundColor)
        }
        graphs.set(id,
        {
                path: path,
                follower: follower,
                minX: minX,
                minY: minY,
                maxX: maxX,
                maxY: maxY,
                lineColor: lineColor
        })

        updateMinMax();
    }
}

function clicked(element) {
    var varValues = "";
    for (var [name, value] of vars) {
        varValues += name + ( value.chosen ? "="+value.value : "" ) + ";"
        app.printLog(name+ " "+ value.value+" "+value.chosen);
    }
    app.printLog(varValues);
    app.readIt(element.id.replace("textInput", ""), element.value,  varValues);
}

function remove(id) {
    var child = document.getElementById("expression" + id);
    child.parentNode.removeChild(child);
    if (graphs.delete(id)) {
        updateMinMax();
    }
}

function updateMinMax() {
    this.minX = this.minY = 99999999;
    this.maxX = this.maxY = -9999999;
    for (var [key, obj] of graphs) {
        this.minX = Math.min(this.minX, obj.minX);
        this.minY = Math.min(this.minY, obj.minY);
        this.maxX = Math.max(this.maxX, obj.maxX);
        this.maxY = Math.max(this.maxY, obj.maxY);
    }
}

function addExpression() {


    var textName = "textInput" + expressionNumber;
    var buttonName = "buttonSubmit" + expressionNumber;
    let div = document.createElement('div');
    div.className = "expressionClass";
    div.innerHTML = "<expression id = \"expression" + expressionNumber + "\"><tr><input type=\"text\" size=\"30\" id = \"" + textName + "\" style = 'font-size: 16px'> <input type=\"submit\" id = \"" + buttonName + "\" value=\"Ок\" onclick=\"clicked(" + textName + ")\" style=\"display: none;\">" +
        "<class=\"picker-wrapper\" id = \"picker-wrapper" + expressionNumber + "\"><button class=\"color-button\" id = \"color-button" + expressionNumber + "\"></button>" +
        "<button class=\"remove-button\" id = \"remove-button" + expressionNumber + "\" onclick=\"remove(" + (expressionNumber) + ")\"></button></tr></expression>"+
        "<div class=\"color-picker\" id = \"color-picker" + expressionNumber + "\"></div>";
    expressions.append(div);

    document.getElementById(textName).addEventListener("keyup", function(event) {
        if (event.keyCode === 13) {
        event.preventDefault();
        document.getElementById(buttonName).click(textName);
         }
    });

    addColorPicker(expressionNumber);

    expressionNumber++;
}

function updateVars(id, expression) {
/*    if (expression == null) {
        expression = document.getElementById("textInput"+id).value;
    }
    var varsFromId = app.getVars(expression);
    var graph = graphs.get(id);
    for (var [key, obj] of graph.vars) {

    }*/
}

function varChanged(name) {
    var value = document.getElementById("var_"+name).value;
        vars.set(name, {
        value: value,
        chosen: value != ""
        });
            document.getElementById("checkBox_"+name).checked = value != "";
            app.printLog(""+i+"/"+vars.length+") "+value);
}

function varChosen(name){

            var obj = vars.get(name);
            vars.set(name, {
                value : obj.value,
                chosen: document.getElementById("checkBox_"+name).checked
            })
            app.printLog(document.getElementById("checkBox_"+name).checked);
}


function addVar(name, id) {

    if (document.getElementById('var_'+name) == null) {
            let div = document.createElement('div');
            div.className = "varClass";
            div.innerHTML = "<input class='checkBox' type='checkbox' id=checkBox_"+name+" onchange = varChosen('"+name+"') >"+name+" = <input type=\"text\" size=\"10\" name = " + name + " id = \"var_" + name + "\">";
            vars_place.append(div);

            document.getElementById("var_"+name).addEventListener("keyup", function(event) {
                event.preventDefault();
                varChanged(name);
            });

            vars.set(name, {value: 0, chosen: false, ids: new Map([ [id, 1] ] ) });
    } else {
        var obj = vars.get(name);
        if (!!obj) {
            vars.set(name, {
                value : obj.value,
                chosen : obj.chosen,
                ids : obj.ids.set(id, 1)
            })
        } else {
           vars.set(name, {value: 0, chosen: false, ids: new Map([ [id, 1] ])});
        }
    }

}