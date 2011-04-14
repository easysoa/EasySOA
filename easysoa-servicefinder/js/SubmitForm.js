$(function() {

	window.SubmitFormModel = Backbone.Model.extend({

		HTTP: "http://",
		url: "",
		
		initialize: function() {
			this.setURL("");
		},
		
		getURL: function(withoutHTTP) {
			var url = this.get("url");
			if (url.indexOf(this.HTTP) != -1)
				return (withoutHTTP) ? url.substr(7) : url;
			else
				return (withoutHTTP) ? url : this.HTTP+url;
		},
		
		setURL: function(newURL) {
			this.set({"url": newURL});
		},
		
		submit: function() {
			var url = this.getURL(true);
			if (url != '') {
				console.log('http://localhost:8080/nuxeo/restAPI/wsdlupload/'+url);
				$.ajax({
					url: 'http://localhost:8080/nuxeo/restAPI/wsdlupload/'+url,
					beforeSend: function(xhr) {
							//xhr.setRequestHeader("Authorization", 
							//base64.encode("Administrator:Administrator")); // Doesn't work
						},
					crossDomain: true,
					dataType: 'jsonp',
					success: function(data, textStatus, jqXHR) {
							SubmitForm.success();
						},
					error: function(jqXHR, textStatus, errorThrown) {
							alert('error : ' + errorThrown);
						}
					});
			}
		},
		
		select: function(descriptor) {
			this.setURL(descriptor.url);
		},
		
		success: function() {
			this.view.success(); // TODO : Find a better way?
		}
		
	});

	window.SubmitForm = new SubmitFormModel;
		
	window.SubmitFormView = Backbone.View.extend({
		
		el: $('#menuSubmitForm'),

		selectedWSDL: $('#submitSelectedWSDL'),
		submitSuccess: $('#submitSuccess'),
		
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
		}
		
	});
	
});