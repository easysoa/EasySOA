$(function() {

	window.SubmitFormModel = Backbone.Model.extend({

		HTTP: "http://",
		
		initialize: function() {
			this.set({
				"url": "",
				"applicationName": "",
				"serviceName": ""
			});
		},
		
		getURL: function() {
			var url = this.get("url");
			return (url.indexOf(this.HTTP) != -1) ? url : this.HTTP+url;
		},
		
		getApplicationName: function() {
			return this.get("applicationName");
		},
		
		getServiceName: function() {
			return this.get("serviceName");
		},
		
		submit: function() {
			if (this.getURL() != '') {
			    jQuery.ajax({
			        url: '/send',
			        data: {
			            'url': this.getURL(),
			            'servicename': $('#submitService').attr('value'),
			            'applicationname': $('#submitApp').attr('value')
			          },
			        success: function (data) {
			            if (data == 'ok') {
                           SubmitForm.success();
                        }
                        else {
                           SubmitForm.failure(data);
                        }
                      },
                    error: function (data) {
                        SubmitForm.failure(data);
                      }
			      });
			    // Couldn't make it work through the proxy
			  /*window.socket.send(JSON.stringify({
			    'url': this.getURL(),
			    'servicename': $('#submitService').attr('value'),
			    'applicationname': $('#submitApp').attr('value'),
			    'authToken': window.authToken
			  }));*/
			}
		},
		
		select: function(descriptor) {
			this.set({
				"url": descriptor.url,
				"serviceName": descriptor.serviceName,
				"applicationName": descriptor.applicationName
			});
		},
		
		success: function() {
			this.view.success(); 
		},
		
		failure: function(error) {
			this.view.failure(error); 
		}
		
	});

	window.SubmitForm = new SubmitFormModel;
		
	window.SubmitFormView = Backbone.View.extend({
		
		el: $('#menuSubmitForm'),

		selectedWSDL: $('#submitSelectedWSDL'),
		submitService: $('#submitService'),
		submitApp: $('#submitApp'),
		messageSuccess: $('#messageSuccess'),
		messageFailure: $('#messageFailure'),
		
		events: {
			"click #submit": "submit"
		},
		
		initialize: function() {
			_.bindAll(this, 'render');
			SubmitForm.view = this;
			SubmitForm.bind('change', this.render);
		},
	
		render: function() {
			this.selectedWSDL.html(SubmitForm.getURL());
			this.submitApp.attr("value", SubmitForm.getApplicationName());
			this.submitService.attr("value", SubmitForm.getServiceName());
			return this;
		},

		submit: function() {
			SubmitForm.submit();
		},
		
		success: function() {
			this.messageSuccess.html('<img src="img/ok.png" height="15" /> WSDL registered');
			this.messageSuccess.fadeIn(500, function() {
					setTimeout(function() {
						$('#messageSuccess').fadeOut(500);
					}, 3000);
				});
		},
		
		failure: function(error) {
			this.messageFailure.html(error);
			this.messageFailure.fadeIn(500, function() {
					setTimeout(function() {
						$('#messageFailure').fadeOut(500);
					}, 5000);
				});
		}
		
	});
	
});
