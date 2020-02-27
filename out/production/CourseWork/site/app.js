var graphics;
var graphPoints;
var text;
var graphWidth;
var graphHeight;
var minX =  99999999;
var minY =  99999999;
var maxX = -99999999;
var maxY = -99999999;
var graphs = new Map();
var context;
var expressionNumber = 0;
var vars = new Map();
var zoomText;
var gridStep = 1;
var manualZoom = -1;
var labelText;
var lastPointer = null;
var pointerdown = false;
var secondClick = false;
var firstClick = false;
var cameraMoved = false;
const spinner = document.getElementById("spinner");


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
        id = ""+expressionNumber;

    pk.colorChosen(function(col) {
        wrapperEl.style.backgroundColor = col;
         console.log(id);
         console.log(graphs.size)
        obj = graphs.get(id);
        if (!!obj) {
            obj.lineColor = ("0x" + col.replace("#", ""));
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
    transparent: true,
    //backgroundColor: 'rgba(0,0,0,0)',
    dom: {
        createContainer: true
    },
    scene: {
        create: create,
        update: update
    }
};

var game = new Phaser.Game(config);

addExpression();

function reload() {
    if (game) {
        /*context = game.scene.keys.default;
        context.registry.destroy();
        context.events.off();
        context.scene.restart();*/
        graphs = new Map();
        game.destroy(true);
        game = new Phaser.Game(config);
        updateAllExpressions();
    }
}

function create() {
    var settings_id = getCookie("settings_id");
    if (!!!settings_id) {
        settings_id = Math.random(Date.now()).toString().substr(2, 9);
        setCookie("settings_id", settings_id);
    }

    context = this;
    graphWidth = 100;
    graphHeight = 100;
    graphics = this.add.graphics();

    follower = {
        t: 0,
        vec: new Phaser.Math.Vector2()
    };

    //  Path starts at 100x100
   // path = new Phaser.Curves.Path(10, 10);

   // path.lineTo(300, 170);

    // cubicBezierTo: function (x, y, control1X, control1Y, control2X, control2Y)
    //path.cubicBezierTo(200, 20, 67, 43, 88, 10);

    getSettings(null);

    //addExpression();

    zoomText = this.add.text(5, 10, 'gridStep').setColor('#0000ff').setOrigin(0);
    labelText = this.add.text(0, 0, '').setOrigin(0, 1);

    updateMinMax();

/*    this.input.on('pointerover', function(pointer){
        var touchX = pointer.x;
        var touchY = pointer.y;
        alert(touchX + '; ' + touchY);
     });*/

/*     this.input.on('pointerup', function(pointer){
        //console.log(pointer.worldX + '; '+pointer.worldY);

     }, this);*/

     this.input.on('pointerdown', function(pointer){

         lastPointer = this.cameras.main.getWorldPoint(pointer.x, pointer.y);
         pointerdown = true;

      });
           this.input.on('pointerup', function(pointer){

               pointerdown = false;

               if (secondClick) {
               firstClick = false;
                                                                                   secondClick = false;
                                                                                   cameraMoved = false;
                                                                                   manualZoom = -1;
                                                                                   document.getElementById("zoomRange").value = -1;
               } else if (firstClick) {
                 setTimeout(function() { secondClick = false; }, 300);
                secondClick = true;
               } else {
                firstClick = true;
                 setTimeout(function() { firstClick = false; if (secondClick) {
                 context.cameras.main.centerOn(0,0);
                 cameraMoved = false; }}, 300);
               }


           }, this);

               this.input.on('wheel', function (pointer, gameObjects, deltaX, deltaY, deltaZ) {

                   var rangeElement = document.getElementById("zoomRange");
                    rangeElement.value = Math.min(Math.max(parseInt(rangeElement.value) - parseInt(rangeElement.step) * (deltaY > 0 ? 1 : -1), rangeElement.min), rangeElement.max);
                    var localPointer = this.cameras.main.getWorldPoint(pointer.x, pointer.y);
                    //this.cameras.main.centerOn(localPointer.x, localPointer.y);
                    cameraMoved = true;
                    cellSizeChanged(rangeElement)
               });

}

function update() {
    graphics.clear();

        var zoomY = this.cameras.main.height / ((maxY - minY) * 1 + 1)
        var zoomX =   this.cameras.main.width / ((maxX - minX) * 1 + 1);
            if (manualZoom > 0) {
                zoomX = zoomY = manualZoom;
            }
        this.cameras.main.setZoom(Math.min(zoomX, zoomY));
        pointer = this.cameras.main.getWorldPoint(this.input.x, this.input.y);

        if (pointerdown) {
            var dx = pointer.x - lastPointer.x;
            var dy = pointer.y - lastPointer.y
            this.cameras.main.scrollX -= dx;
            this.cameras.main.scrollY -= dy;
            cameraMoved = true;
        } else if (!cameraMoved) {
               this.cameras.main.centerOn((minX + maxX) / 2, -(minY + maxY) / 2);
        }

    graphics.lineStyle(1 / this.cameras.main.zoom, 0xAAAAAA, 1);
/*    app.printLog(""+this.cameras.main.worldView.x+" "+this.cameras.main.worldView.y+"  "+this.cameras.main.worldView.width+" "+this.cameras.main.worldView.height);*/
    var cameraMinX = Math.floor(this.cameras.main.worldView.x -1);
    var cameraMaxX = Math.floor(this.cameras.main.worldView.x + this.cameras.main.worldView.width + 1);
    var cameraMinY = Math.floor(this.cameras.main.worldView.y - 1);
    var cameraMaxY = Math.floor(this.cameras.main.worldView.y + this.cameras.main.worldView.height + 1);
    var zoom1 = Math.abs(this.cameras.main.zoom) / (this.cameras.main.worldView.width /this.cameras.main.worldView.height) / 2;

        gridStep = 1;
        while  (zoom1 > 1) {
            gridStep /= 10;
            zoom1 /= 10;
        }
        while (zoom1 < 1) {
            gridStep *= 10;
            zoom1 *= 10;
        }


    for (var i = cameraMinX; i < cameraMaxX; i+=gridStep)
        graphics.strokeLineShape(new Phaser.Geom.Line(i, cameraMinY, i, cameraMaxY));
    for (var i = cameraMinY; i < cameraMaxY; i+=gridStep)
        graphics.strokeLineShape(new Phaser.Geom.Line(cameraMinX, i, cameraMaxX, i));

    graphics.lineStyle(2 / this.cameras.main.zoom, 0x000000, 1);
    graphics.strokeLineShape(new Phaser.Geom.Line(cameraMinX, 0, cameraMaxX, 0));
    graphics.strokeLineShape(new Phaser.Geom.Line(0, cameraMinY, 0, cameraMaxY));

    var drawingLabel = false;
    for (var [id, graph] of graphs) {
        graphics.fillStyle( graph.lineColor, 1);
        graph.path.getPoint(graph.follower.t, graph.follower.vec);
        graphics.lineStyle(3 / this.cameras.main.zoom, graph.lineColor, 1);

        graph.path.draw(graphics);
        pointer = this.cameras.main.getWorldPoint(this.input.x , this.input.y);
        if (!drawingLabel && this.input.isOver && !this.game.input.activePointer.isDown) {
                        if (graph.minX <= pointer.x && graph.maxX >= pointer.x) {
                            if (graph.minY <= -pointer.y && graph.maxY >= -pointer.y) {
                                for (var curve of graph.path.curves) {
                                    var start = curve.getStartPoint();
                                    var end = curve.getEndPoint();
                                    if (pointer.x >= start.x && pointer.x <= end.x
                                            && pointer.y >= Math.min(start.y, end.y) - gridStep && pointer.y <= Math.max(end.y, start.y) + gridStep) {
                                            console.log(pointer.x + '; '+pointer.y);
                                            var fooY = start.y + (end.y - start.y)*((pointer.x - start.x)/(end.x - start.x));
                                            labelText.setText(pointer.x.toFixed(3)+'\n'+ (-fooY).toFixed(4));
                                            labelText.setPosition(pointer.x, pointer.y);
                                            labelText.setColor(graph.lineColor.replace('0x', '#'));
                                            labelText.setScale(1/this.cameras.main.zoom);
                                            labelText.setTintFill(0x555555);
                                             drawingLabel = true;
                                           break;
                                    }
                                }
                            }
                        }
                       }
        graphics.fillCircle(graph.follower.vec.x, graph.follower.vec.y, 3 / this.cameras.main.zoom);
    }
    labelText.setVisible(drawingLabel);

    var gridStepY = this.cameras.main.worldView.y + Math.abs(this.cameras.main.worldView.height) * 0.05;
    var gridStepX = this.cameras.main.worldView.x + Math.abs(this.cameras.main.worldView.width) * 0.05;
   // graphics.strokeLineShape(new Phaser.Geom.Line(gridStepX, gridStepY, gridStepX+gridStep, gridStepY));
    zoomText.setScale(1/this.cameras.main.zoom).setText(gridStep).setPosition(gridStepX, gridStepY, 0);


}

function rgb2hex(rgb) {
    rgb = rgb.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+))?\)$/);

    function hex(x) {
        return ("0" + parseInt(x).toString(16)).slice(-2);
    }
    return "0x" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
}

function drawGraph(points, id) {

    if (points.length > 0) {
        follower = {
            t: 0,
            vec: new Phaser.Math.Vector2()
        };
        var minX = 99999999999;
        var maxX = -9999999999;
        var minY = 99999999999;
        var maxY = -9999999999;
        while (points.length > 0 && points[0].value == null) {
           points.shift();
        }

        if (points.length > 0) {

            path = new Phaser.Curves.Path(points[0].key, -points[0].value);
            if (points[0].key < minX)
                minX = points[0].key;
            if (points[0].key > maxX)
                maxX = points[0].key;
            if (points[0].value < minY)
                minY = points[0].value;
            if (points[0].value > maxY)
                maxY = points[0].value;
            var textToSet = '';
            textToSet += '' + points[0].key + ',' + (points[0].value) + '; ';
            var gotNaN = false;
            for (var i = 1; i < points.length; i++) {
            if (points[i].value == null) {
                gotNaN = true;
                continue;
            } else if (gotNaN) {
                path.moveTo(points[i].key, -points[i].value);
                gotNaN = false;
            } else {
                gotNaN = false;
                path.lineTo(points[i].key, -points[i].value);
                }
                if (points[i].key < minX)
                    minX = points[i].key;
                if (points[i].key > maxX)
                    maxX = points[i].key;
                if (points[i].value < minY)
                    minY = points[i].value;
                if (points[i].value > maxY)
                    maxY = points[i].value;
                // textToSet += ''+points.get(i).getKey()+','+(points.get(i).getValue())+'; ';
            }
        } else {
            path = new Phaser.Curves.Path(0, 0);
            maxX = maxY = minX = minY = 0;
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
         console.log(obj);
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
         console.log(name+ " "+ value.value+" "+value.chosen);
    }
     console.log(varValues);
    readIt(getCookie("settings_id"), element.id.replace("textInput", ""), element.value,  varValues);
}

function updateAllExpressions() {
    var varValues = "";
    for (var [name, value] of vars) {
        varValues += name + ( value.chosen ? "="+value.value : "" ) + ";"
         console.log(name+ " "+ value.value+" "+value.chosen);
    }
    var allVars = [];
    var id = getCookie("settings_id");
    if (graphs && graphs.size > 0) {
        for (var [id] of graphs) {
            var responseVars = [];
            readIt(id, id, document.getElementById("textInput"+id).value ,varValues)
            allVars.concat(responseVars);
        }
    } else if (expressionNumber > 0) {
        for (var i = 0; i < expressionNumber; i++) {
            var elem = document.getElementById("textInput"+i)
            if (elem) {
                var responseVars = [];
                readIt(id, i, elem.value , varValues, responseVars)
                allVars.concat(responseVars);
            }
        }
    }
    for (var [varName] of vars) {
         var elem = document.getElementById("varContainer_"+varName);
         if (elem)
            elem.parentElement.removeChild(elem);
    }
    vars = new Map();
    for (var varName of allVars)
        addVar(varName, id, "");

}

function readIt(id, graphId, element, varValues, responseVars) {
    var url = window.location.href;
    var options = {id : id, element : element, varValues : varValues}
    showSpinner();
    fetch(url, {
                 method: 'POST',
                 headers: {
                   'Content-Type': 'application/json;charset=utf-8'
                 },
                 body: encodeURIComponent(JSON.stringify(options))
                 })
      .then(response => response.json())
      .then(response =>
        {
            console.log(response)
            hideSpinner();
            document.getElementById("answer"+graphId).innerText  = response.message;
            drawGraph(response.points, graphId);
            for (var i = 0; i < response.vars.length; i++)
                addVar(response.vars[i], id, null);
            responseVars = response.vars;
        }
      );
}

function remove(id) {
    var child = document.getElementById("expression" + id);
    child.parentNode.removeChild(child);
    child = document.getElementById("color-picker" + id);
        child.parentNode.removeChild(child);
    if (graphs.delete(id)) {
        updateMinMax();
        if (graphs.size == 1) {
            var elem = document.getElementById("remove-button"+graphs.values()[0].id);
            if (elem) {
                elem.disabled = true;
             }
        }
    }
}

function updateMinMax() {

    if (graphs.size) {
        this.minX = this.minY =  99999999;
        this.maxX = this.maxY = -99999999;
        for (var [key, obj] of graphs) {
            this.minX = Math.min(this.minX, obj.minX);
            this.minY = Math.min(this.minY, obj.minY);
            this.maxX = Math.max(this.maxX, obj.maxX);
            this.maxY = Math.max(this.maxY, obj.maxY);
        }
    } else {
            this.minX = this.minY =  -10;
            this.maxX = this.maxY = 10;
    }

}


function addExpression() {

    var textName = "textInput" + expressionNumber;
    var buttonName = "buttonSubmit" + expressionNumber;
    let div = document.createElement('div');
    div.className = "expressionClass";
    div.innerHTML = "<expression id = \"expression" + expressionNumber + "\" style='height:2em; white-space: nowrap;'><input type=\"text\" size=\"30\" id = \"" + textName + "\"> <input type=\"submit\" id = \"" + buttonName + "\" value=\"Ок\" onclick=\"clicked(" + textName + ")\" style=\"display: none;\">" +
        "<class=\"picker-wrapper\" id = \"picker-wrapper" + expressionNumber + "\"><button class=\"color-button\" id = \"color-button" + expressionNumber + "\" style = 'display: inline-block'></button>" +
        "<input type='image' id = \"remove-button" + expressionNumber + "\" onclick=\"remove(\'" + (expressionNumber) + "\')\" src= 'trash.png' style='height:2em; vertical-align: middle; display: inline-block'>"+
        "<p><span id = 'answer"+expressionNumber+"' style = 'white-space: normal'></span></p></expression>"+
        "<div class=\"color-picker\" id = \"color-picker" + expressionNumber + "\"></div>";
    expressions.append(div);

    document.getElementById(textName).addEventListener("keyup", function(event) {
        if (event.keyCode === 13) {
        event.preventDefault();
        document.getElementById(buttonName).click(textName);
         }
    });
        document.getElementById(textName).addEventListener("blur", function(event) {

           // event.preventDefault();
          //  document.getElementById(buttonName).click(textName);
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
}

function varChosen(name){

            var obj = vars.get(name);
            vars.set(name, {
                value : obj.value,
                chosen: document.getElementById("checkBox_"+name).checked
            })
             console.log(document.getElementById("checkBox_"+name).checked);
}


function addVar(name, id, title) {

    if (!!!title) {
         title = name;
    }

    if (document.getElementById('var_'+name) == null) {
            let div = document.createElement('div');
            div.className = "varClass";
            div.innerHTML = "<div id = 'varContainer_"+ name +"'><input class='checkBox' type='checkbox' id=checkBox_"+name+" onchange = varChosen('"+name+"') >"+title+" = <input type=\"text\" size=\"10\" name = " + name + " id = \"var_" + name + "\"></div>";
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

function saveSettings() {
 var url = window.location.href;
   var defaultExpressions = document.getElementById("defaultExpressions").value;
   var fromX = document.getElementById("fromX").value;
   var toX = document.getElementById("toX").value;

   var settings_id = getCookie("settings_id");
   if (!!!settings_id) {
        settings_id = Math.random(Date.now()).toString().substr(2, 9);
        setCookie("settings_id", settings_id);
   }
      var options = {fromX : fromX, toX : toX, defaultExpressions : defaultExpressions, settings_id : settings_id};
       fetch(url, {
                    method: 'POST',
                    headers: {
                      'Content-Type': 'application/json;charset=utf-8'
                    },
                    body: encodeURIComponent(JSON.stringify(options))
                    })
         .then(response => response.text())
         .then(response =>
           {
            alert("Saved");
           })
}


function cellSizeChanged(elem) {
    var val = parseFloat(elem.value);
    manualZoom = isNaN(val) ? 1 : val == 0 ? 1 : val
}

function getDefaultSettings() {
    getSettings(-1);
}


function getSettings(settings_id) {
   var url = window.location.href;
      var options = {settings_id : settings_id == null ? getCookie("settings_id") : settings_id };
       fetch(url, {
                    method: 'POST',
                    headers: {
                      'Content-Type': 'application/json;charset=utf-8'
                    },
                    body: encodeURIComponent(JSON.stringify(options))
                    })
         .then(response => response.json())
         .then(response =>
           {
            document.getElementById("defaultExpressions").value = response.defaultExpressions;
            document.getElementById("fromX").value = response.fromX;
            document.getElementById("toX").value = response.toX;
           })
}

function getCookie(name) {
  let matches = document.cookie.match(new RegExp(
    "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
  ));
  return matches ? decodeURIComponent(matches[1]) : undefined;
}

function setCookie(name, value, options = {}) {

  options = {
    path: '/'
  };

  let updatedCookie = encodeURIComponent(name) + "=" + encodeURIComponent(value);

  for (let optionKey in options) {
    updatedCookie += "; " + optionKey;
    let optionValue = options[optionKey];
    if (optionValue !== true) {
      updatedCookie += "=" + optionValue;
    }
  }
  document.cookie = updatedCookie;
}

function showSpinner() {
  spinner.className = "show";
  setTimeout(() => {
    spinner.className = spinner.className.replace("show", "");
  }, 5000);
}

 function hideSpinner() {
   spinner.className = spinner.className.replace("show", "");
 }