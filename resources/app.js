

	var Main = new Phaser.Class ({


        Extends: Phaser.Scene,

		initialize:

		function Main() {
			Phaser.Scene.call(this);
		},




    preload: function()
    {
        this.cameras.main.zoom = 1;
    },

    create: function()
    {

    },
    clickListener(button) {
        console.log(button);
    },


    update: function(time, delta) {

    },

    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    },


    onClicked(pointer, object){
    	
    }
});

    var config = {
        type: Phaser.AUTO,
        parent: 'phaser-example',
        width: 400,
        height: 400,
        backgroundColor: '#111144',
            dom: {
        createContainer: true
        },
        canvas: 'canvas1',
        renderType: Phaser.WEBGL
        scene: [Main]
    };

    var game = new Phaser.Game(config);