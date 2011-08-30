$(function() {

	window.socket = null;
	
	window.AppView = Backbone.View.extend({
	
		/**
		 * Application initialization
		 */
		initialize: function() {
			this.descriptorsView = new DescriptorsView;
			this.frameView = new FrameView;
			this.navBarView = new NavbarView;
			this.submitFormView = new SubmitFormView;

	    socket = new io.Socket(null, {port: 8081, rememberTransport: false});
      socket.connect();
      socket.on('message', function(obj){
        if (obj.indexOf("ERROR") == 0) {
          SubmitForm.failure(obj.substring(7, obj.length-1));
        }
        else {
          try {
            obj = jQuery.parseJSON(obj);
            if (obj.messageType == 'wsdl') {
              obj.messageType = undefined;
              if (!Descriptors.contains(obj)) {
                Descriptors.add(obj);
              }
            }
            else if (obj.messageType == 'upload') {
              if (obj.result == 'ok') {
                SubmitForm.success();
              }
              else {
                SubmitForm.failure(obj.result);
              }
            }
            else if (obj.messageType == 'ready') {
			        $('#messageSuccess').html('Ready.');
			        $('#message').fadeOut(200, function() {
			          $('#messageSuccess').fadeIn(500, function() {
					          setTimeout(function() {
						          $('#messageSuccess').fadeOut(500);
					          }, 1000);
				          });
				      });
            }
          }
          catch (error) {
            console.log(error);
          }
        }
      });
      
		}
	
	});
	
	window.App = new AppView;
	
});
