$(function() {

	/**
	 * NavbarModel
	 * Contains the navigation context.
	 */
	window.NavbarModel = Backbone.Model.extend({
	
		// NavbarModel Attributes
		//   url = Navigation URL
		
		DEFAULT_URL: "default.html",
		HTTP: "http://",
	
		initialize: function() {
			this.set({"url": this.DEFAULT_URL});
		},
		
		getURL: function(withoutHTTP) {
			var url = this.get("url");
			if (url.indexOf(this.HTTP) != -1)
				return (withoutHTTP) ? url.substr(7) : url;
			else {
				if (url == this.DEFAULT_URL)
					return url;
				else
					return (withoutHTTP) ? url : this.HTTP+url;
			}
		},
		
		update: function(newUrl) {
			this.set({"url": newUrl});
		}
	
	});
	
	window.Navbar = new NavbarModel;
	
	/**
	 * NavbarView
	 * Navigation view, as a browser bar
	 */
	window.NavbarView = Backbone.View.extend({

	    el: $("#navBar"),
	    navUrl: $("#navUrl"),
	
		events: {
			"click #navButtonGo": "navigationGo",
			"keypress #navUrl": "navigationGoKeyboard"
		},
		
		initialize: function() {
			_.bindAll(this, 'render', 'navigationGo');
			Navbar.view = this;
			Navbar.bind('change', this.render);
			this.render();
		},
		
		// Render page
		render: function() {
			
			var url = Navbar.getURL();
			Frame.setSource(url);
			
			// Nuxeo request
			if (url != Navbar.DEFAULT_URL) {
				
				this.navUrl.attr("value", url);
				
				$.ajax({
					url: 'http://localhost:8080/nuxeo/restAPI/wsdlscraper/'+Navbar.getURL(true),
					beforeSend: function(xhr) {
							//xhr.setRequestHeader("Authorization", 
							//base64.encode("Administrator:Administrator")); // Doesn't work
						},
					crossDomain: true,
					dataType: 'jsonp',
					success: function(data, textStatus, jqXHR) {
							
							Frame.setSource(data.url);
							//Frame.view.html(data.html); // Transclusion
							
							var items = [];
							$.each(data.foundLinks, function(descName, descUrl) {
								Descriptors.add({
							        name: descName,
							        url: descUrl
								});
							});
							
						},
					error: function(jqXHR, textStatus, errorThrown) {
							alert('error : ' + errorThrown);
						}
					});
					
				// Nuxeo request (simulation)
				/*foundLinks = {
					"link1": "http://www.google.com",
					"link2": "http://www.amazon.com"
				};
				$.each(foundLinks, function(descName, descUrl) {
					Descriptors.add({
						name: descName,
						url: descUrl
					});
				});*/
				
			}
			
		    return this;
		},
		
		// Change URL through keyboard
		navigationGoKeyboard: function(e) {
			if (e.keyCode == 13) {
				this.navigationGo();
			}
		},

		// Change URL
		navigationGo: function() {
			Navbar.update(this.navUrl.attr("value"));
		}
		
		
		
	});
	
});
