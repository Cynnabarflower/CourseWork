
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

function clicked(element) {
    graphs.forEach(function(graph, index, array) {

        if (graph.id == +element.id.replace("textInput", "")) {
            graphs.splice(index, 1);

           return;
        }
    }, this);
     app.readIt(+element.id.replace("textInput", ""), element.value);
}
function addExpression() {
               var textName = "textInput"+expressionNumber;
               var buttonName = "buttonSubmit"+expressionNumber;
              let div = document.createElement('div');
              div.className = "expressionClass";
              div.innerHTML = "<input type=\"text\" size=\"40\" id = \""+textName+"\"> <input type=\"submit\" id = \""+buttonName+"\" value=\"Ок\" onclick=\"clicked("+textName+")\">";
              expressions.append(div);

              document.getElementById(textName).addEventListener("keyup", function(event) {
               //if (event.keyCode === 13) {
                  event.preventDefault();
                  document.getElementById(buttonName).click(textName);
               // }
              });

              expressionNumber++;
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

  function create ()
  {
  expressionNumber = 0;
  addExpression();
    context = this;
        graphWidth = 100;
        graphHeight = 100;
        graphs = [];
      text = this.add.text(0,0, 'text123');
      graphics = this.add.graphics();

      follower = { t: 0, vec: new Phaser.Math.Vector2() };

      //  Path starts at 100x100
      path = new Phaser.Curves.Path(10, 10);

      path.lineTo(300, 170);

      // cubicBezierTo: function (x, y, control1X, control1Y, control2X, control2Y)
      path.cubicBezierTo(200, 20, 67, 43, 88, 10);

  }

  function update ()
  {
      graphics.clear();

      graphics.fillStyle(0xff0000, 1);

     this.cameras.main.setScroll((minX+maxX- this.cameras.main.width)/2, (-(minY+maxY)- this.cameras.main.height)/2 );
     var zoomY = this.cameras.main.height/((maxY-minY)*1.1+5)
     var zoomX = this.cameras.main.width/((maxX-minX)*1.1+5);
     this.cameras.main.setZoom(Math.min(zoomX, zoomY));
     graphs.forEach(function(graph, index, array) {
        graph.path.getPoint(graph.follower.t, graph.follower.vec);
        graphics.lineStyle( 1/this.cameras.main.zoom, graph.lineColor, 1);
        graph.path.draw(graphics);
        graphics.fillCircle(graph.follower.vec.x, graph.follower.vec.y, 10/this.cameras.main.zoom);

     }, this);
     setText("derivativeExpressionValue",''+minX+' '+minY+'   '+'\n'+(maxX)+' '+(maxY)+'\n cam: '+Math.min(zoomX, zoomY) +'  '+(-(minY+maxY)- this.cameras.main.height)/2);
  }

  function drawGraph(points, color, id) {

  if (points.size() > 0) {
    follower = { t: 0, vec: new Phaser.Math.Vector2() };
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
    textToSet += ''+points.get(0).getKey()+','+(points.get(0).getValue())+'; ';
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
              graphs.push({
                  lineColor: color,
                  path: path,
                  follower : follower,
                  id : id,
                  minX : minX,
                  minY : minY,
                  maxX : maxX,
                  maxY : maxY
              });
              this.minX = this.minY = 99999999;
              this.maxX = this.maxY = -9999999;
                      graphs.forEach(function(graph, index, array) {
                                  this.minX = Math.min(graph.minX, this.minX);
                                  this.minY = Math.min(graph.minY, this.minY);
                                  this.maxX = Math.max(graph.maxX, this.maxX);
                                  this.maxY = Math.max(graph.maxY, this.maxY);
                      }, this);
   // setText('derivativeExpression', textToSet);
    }
  }