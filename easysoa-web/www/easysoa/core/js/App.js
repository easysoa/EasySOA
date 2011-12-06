$(function() {

	window.socket = null;
	
	window.AppView = Backbone.View.extend({
	
	    error: function(msg) {
              console.error(msg);
              SubmitForm.view.failure(msg);
	    },
	    
		/**
		 * Application initialization
		 */
		initialize: function() {
		
			this.descriptorsView = new DescriptorsView;
			this.frameView = new FrameView;
			this.navBarView = new NavbarView;
			this.submitFormView = new SubmitFormView;
			this.serviceListView = new ServiceListView;
        
	        socket = io.connect();
	        
	        // Show proxy warning after a delay
            setTimeout(function() {
            	nothingProxiedDiv = $('#nothingProxied');
            	if (nothingProxiedDiv != null) {
            		nothingProxiedDiv.show(300);
            	}
            }, 2000);
	        
            socket.on('proxyack', function(data) {
                  $('#nothingProxied').remove();
            });
            socket.on('error', function(data) {
                  App.error(data);
            });
            socket.on('wsdl', function(data) {
                  try {
                      data = jQuery.parseJSON(data);
                      if (!Descriptors.contains(data)) {
                        Descriptors.add(data);
                      }
                  }
                  catch (error) {
                    App.error("Error while handling 'wsdl' message: "+error);
                  }
            });
	        socket.submitFormView = this.submitFormView;
            socket.on('ready', function(data) {
            	this.submitFormView.info('Ready.');
            });
             
         }
         
	});
	
	window.App = new AppView;
	
});
