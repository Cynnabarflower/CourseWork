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
           "#808080",
           "#000000"


        ], {
            open: "#picker-wrapper" + expressionNumber + " #color-button" + expressionNumber
        }),
        wrapperEl = pk.getElm("#color-button" + expressionNumber),
        id = expressionNumber;;

    pk.colorChosen(function(col) {
        wrapperEl.style.backgroundColor = col;
        graphs.forEach(function(graph, index, array) {
            if (graph.id == id) {
                graph.lineColor = Number("0x" + col.replace("#", ""));
            }

        }, this);
    });

    for (let i = 0; i < pk.colors.length; i++) {
        var colorChosen = true;
        for (let j = 0; j < graphs.length; j++) {
            if (graphs[j].lineColor == "0x" + pk.colors[i].replace("#", "")) {
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
    width: 640,
    height: 480,
    scale: {
        mode: Phaser.Scale.WIDTH_CONTROLS_HEIGHT,
        autoCenter: Phaser.Scale.CENTER_HORIZONTALLY
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
    graphs = [];
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
    graphs.forEach(function(graph, index, array) {
        graphics.fillStyle( graph.lineColor, 1);
        graph.path.getPoint(graph.follower.t, graph.follower.vec);
        graphics.lineStyle(3 / this.cameras.main.zoom, graph.lineColor, 1);
        graph.path.draw(graphics);
        graphics.fillCircle(graph.follower.vec.x, graph.follower.vec.y, 3 / this.cameras.main.zoom);
    }, this);
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
        path = new Phaser.Curves.Path(points.get(0).getKey(), -points.get(0).getValue());
        if (points.get(0).getKey() < minX)
            minX = points.get(0).getKey();
        if (points.get(0).getKey() > maxX)
            maxX = points.get(0).getKey();
        if (points.get(0).getValue() < minY)
            minY = points.get(0).getValue();
        if (points.get(0).getValue() > maxY)
            maxY = points.get(0).getValue();
        var textToSet = '';
        textToSet += '' + points.get(0).getKey() + ',' + (points.get(0).getValue()) + '; ';
        for (var i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).getKey(), -points.get(i).getValue());
            if (points.get(i).getKey() < minX)
                minX = points.get(i).getKey();
            if (points.get(i).getKey() > maxX)
                maxX = points.get(i).getKey();
            if (points.get(i).getValue() < minY)
                minY = points.get(i).getValue();
            if (points.get(i).getValue() > maxY)
                maxY = points.get(i).getValue();
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
        var graphFound = false;
        graphs.forEach(function(graph, index, array) {
            if (graph.id == id) {
                graph.path = path;
                graph.follower = follower;
                graph.minX = minX;
                graph.minY = minY;
                graph.maxX = maxX;
                graph.maxY = maxY;
                graphFound = true;
            }
            this.minX = Math.min(graph.minX, this.minX);
            this.minY = Math.min(graph.minY, this.minY);
            this.maxX = Math.max(graph.maxX, this.maxX);
            this.maxY = Math.max(graph.maxY, this.maxY);
        }, this);
        if (!graphFound) {
            graphs.push({
                lineColor: rgb2hex(document.getElementById("color-button" + id).style.backgroundColor),
                path: path,
                follower: follower,
                id: id,
                minX: minX,
                minY: minY,
                maxX: maxX,
                maxY: maxY
            });
            this.minX = Math.min(minX, this.minX);
            this.minY = Math.min(minY, this.minY);
            this.maxX = Math.max(maxX, this.maxX);
            this.maxY = Math.max(maxY, this.maxY);
        }
        // setText('derivativeExpression', textToSet);
    }
}

function clicked(element) {
    app.readIt(+element.id.replace("textInput", ""), element.value);
}

function remove(id) {
    var child = document.getElementById("expression" + id);
    child.parentNode.removeChild(child);
    this.minX = this.minY = 99999999;
    this.maxX = this.maxY = -9999999;
    for (let j = 0; j < graphs.length; j++) {
        if (graphs[j].id == id) {
            graphs.splice(j, 1);
            j--;
            continue;
        }
        this.minX = Math.min(graphs[j].minX, this.minX);
        this.minY = Math.min(graphs[j].minY, this.minY);
        this.maxX = Math.max(graphs[j].maxX, this.maxX);
        this.maxY = Math.max(graphs[j].maxY, this.maxY);
    }

}

function addExpression() {


    var textName = "textInput" + expressionNumber;
    var buttonName = "buttonSubmit" + expressionNumber;
    let div = document.createElement('div');
    div.className = "expressionClass";
    div.innerHTML = "<expression id = \"expression" + expressionNumber + "\"><tr><input type=\"text\" size=\"40\" id = \"" + textName + "\"> <input type=\"submit\" id = \"" + buttonName + "\" value=\"Ок\" onclick=\"clicked(" + textName + ")\" style=\"display: none;\">" +
        "<class=\"picker-wrapper\" id = \"picker-wrapper" + expressionNumber + "\"><button class=\"color-button\" id = \"color-button" + expressionNumber + "\"></button><div class=\"color-picker\" id = \"color-picker" + expressionNumber + "\"></div>" +
        "<button class=\"remove-button\" id = \"remove-button" + expressionNumber + "\" onclick=\"remove(" + (expressionNumber) + ")\"></button></tr></expression>";
    expressions.append(div);

    document.getElementById(textName).addEventListener("keyup", function(event) {
        //if (event.keyCode === 13) {
        event.preventDefault();
        document.getElementById(buttonName).click(textName);
        // }
    });


    addColorPicker(expressionNumber);



    expressionNumber++;
}