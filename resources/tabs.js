(function() {

  'use strict';

  /**
   * tabs
   *
   * @description The Tabs component.
   * @param {Object} options The options hash
   */
  var tabs = function(options) {

    var el = document.querySelector(options.el);
    var tabNavigationLinks = el.querySelectorAll(options.tabNavigationLinks);
    var tabContentContainers = el.querySelectorAll(options.tabContentContainers);
    var activeIndex = 0;
    var initCalled = false;

    /**
     * init
     *
     * @description Initializes the component by removing the no-js class from
     *   the component, and attaching event listeners to each of the nav items.
     *   Returns nothing.
     */
    var init = function() {
      if (!initCalled) {
        initCalled = true;
        el.classList.remove('no-js');
        
        for (var i = 0; i < tabNavigationLinks.length; i++) {
          var link = tabNavigationLinks[i];
          handleClick(link, i);
        }
      }
    };

    /**
     * handleClick
     *
     * @description Handles click event listeners on each of the links in the
     *   tab navigation. Returns nothing.
     * @param {HTMLElement} link The link to listen for events on
     * @param {Number} index The index of that link
     */
    var handleClick = function(link, index) {
      link.addEventListener('click', function(e) {
        e.preventDefault();
        goToTab(index);
      });
    };

    /**
     * goToTab
     *
     * @description Goes to a specific tab based on index. Returns nothing.
     * @param {Number} index The index of the tab to go to
     */
    var goToTab = function(index) {
      if (index !== activeIndex && index >= 0 && index <= tabNavigationLinks.length) {
        tabNavigationLinks[activeIndex].classList.remove('is-active');
        tabNavigationLinks[index].classList.add('is-active');
        tabContentContainers[activeIndex].classList.remove('is-active');
        tabContentContainers[index].classList.add('is-active');
        activeIndex = index;
      }
    };

    /**
     * Returns init and goToTab
     */
    return {
      init: init,
      goToTab: goToTab
    };

  };

  /**
   * Attach to global namespace
   */
  window.tabs = tabs;


  var input = document.getElementById("textInput");

  input.addEventListener("keyup", function(event) {
   // if (event.keyCode === 13) {
      event.preventDefault();
      document.getElementById("buttonSubmit").click();
    //}
  });


//  var canvas = document.getElementById("graph_place");
/*  var ctx = canvas.getContext("2d");

  ctx.fillStyle = "green";
  ctx.fillRect(10, 10, 100, 100);*/

  var config = {
      type: Phaser.AUTO,
      parent: 'graph_place',
      width: 160,
      height: 90,
          scale: {
              mode: Phaser.Scale.WIDTH_CONTROLS_HEIGHT,
              autoCenter: Phaser.Scale.CENTER_HORIZONTALLY
          },
      backgroundColor: '#222288',
      dom: {
          createContainer: true
      },
  };

  var game = new Phaser.Game(config);

})();

  function setText(id, text) {
    document.getElementById(id).textContent = text;
  }