$(function() {

	window.ServiceListView = Backbone.View.extend({
		
	    el: $("#menuFoundList"),
	    
		trash: $('#trash'),
		
		events: {
			"click #trash": "clear"
		},
		
		initialize: function() {
			_.bindAll(this, 'clear');
		},
		
		render: function() {
			return this;
		},
  
		clear: function() {
			if (confirm("Clear all found services?")) {
				// TODO Use socket.io (doesn't work through the proxy atm)
				jQuery.ajax({
					url: '/clear',
				    success: function () {
				        Descriptors.view.clearAll();
		            },
		            error: function (data) {
		                SubmitForm.view.failure(data);
		            }
				});
			}
		}
		
	});
			
});
