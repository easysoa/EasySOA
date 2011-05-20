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
			
			var url = this.getURL();
			var serviceName = $('#submitService').attr('value');
			var appName = $('#submitApp').attr('value');
			
			if (url != '') {
			console.log('http://127.0.0.1:8080/nuxeo/site/easysoa/notification/' + appName + '/' + serviceName + '/' + url);
				$.ajax({
					url: 'http://127.0.0.1:8080/nuxeo/site/easysoa/notification/' + appName + '/' + serviceName + '/' + url,
					crossDomain: true,
					dataType: 'jsonp',
					success: function(data, textStatus, jqXHR) {
					console.log(data);
							if (data.result == "ok")
								SubmitForm.success();
							else
								SubmitForm.failure(data.result);
						},
					error: function(jqXHR, textStatus, errorThrown) {
							SubmitForm.failure("Request failed: "+errorThrown);
						}
					});
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
		submitSuccess: $('#submitSuccess'),
		submitFailure: $('#submitFailure'),
		
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
			this.submitSuccess.fadeIn(500, function() {
					setTimeout(function() {
						$('#submitSuccess').fadeOut(500);
					}, 3000);
				});
		},
		
		failure: function(error) {
			this.submitFailure.html(error);
			this.submitFailure.fadeIn(500, function() {
					setTimeout(function() {
						$('#submitFailure').fadeOut(500);
					}, 5000);
				});
		}
		
	});
	
});
