$(function() {

	window.socket = new io.Socket(null, {port: 8081, rememberTransport: false});
  
	window.AppView = Backbone.View.extend({
	
		/**
		 * Application initialization
		 */
		initialize: function() {
    
			this.descriptorsView = new DescriptorsView;
			this.frameView = new FrameView;
			this.navBarView = new NavbarView;
			this.submitFormView = new SubmitFormView;
      
      socket.connect();
      socket.on('message', function(obj){
        if (obj.indexOf("ERROR") == 0) {
          SubmitForm.failure(obj.substring(7, obj.length-1));
        }
        else {
          obj = jQuery.parseJSON(obj);
          console.log(obj);
          if (!Descriptors.contains(obj)) {
            Descriptors.add(obj);
          }
        }
      });
      socket.send('hi');
      
		}
	
	});
	
	window.App = new AppView;
	
});
