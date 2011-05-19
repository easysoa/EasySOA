$(function() {
	
	/**
	 * NavbarModel
	 * Contains the navigation context.
	 */
	window.NavbarModel = Backbone.Model.extend({
	
		// NavbarModel Attributes
		//   url = Navigation URL
		
		DEFAULT_URL: "/client/default.html",
		HTTP: "http://",
	
		initialize: function() {
			this.set({"url": this.DEFAULT_URL});
		},
		
		getURL: function(withHTTP) {
			var url = this.get("url");
			if (url.indexOf(this.HTTP) == -1)
				return (withHTTP) ? this.HTTP+url : url;
  		else
  		  return url;
		},
		
		update: function(newUrl) {
			if (newUrl == '')
				newUrl = this.DEFAULT_URL;
			this.set({"url": newUrl});
			Frame.setSource(this.getURL(true));
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
			"click #navButtonClear": "navigationReset",
			"keypress #navUrl": "navigationGoKeyboard"
		},
		
		initialize: function() {
			_.bindAll(this, 'render', 'navigationGo', 'navigationReset');
			Navbar.view = this;
			Navbar.bind('change', this.render);
			this.render();
		},
		
		// Render page
		render: function() {
			
			var url = Navbar.getURL();
			if (!this.navUrl === document.activeElement)
				this.navUrl.attr("value", url);

			// Request proxy server found links
			// (Note: could probably use the Backbone.js server sync features instead)
			$.ajax({
				url: 'http://127.0.0.1:8081',
				crossDomain: true,
				dataType: 'jsonp',
				success: function(data, textStatus, jqXHR) {
						if (data.error != null) {
							console.log(data.error);
							SubmitForm.failure(data.error); // TODO: Display somewhere else
						}
						else {
							var i = Descriptors.size();
							var objs = Descriptors.first(i);
							Descriptors.remove(objs);
							$.each(data.foundLinks, function(descName, descContent) {
								Descriptors.add(descContent);
							});
						}
					},
				error: function(jqXHR, textStatus, errorThrown) {
						alert('error : ' + errorThrown);
					}
				});
			
			setTimeout(this.render, 2000);
		},
		
		// Change URL through keyboard
		navigationGoKeyboard: function(e) {
			if (e.keyCode == 13) {
				this.navigationGo();
			}
		},

		// Change URL
		navigationGo: function() {
			Navbar.update($.trim(this.navUrl.attr("value")));
		},
		
		// Restore default URL
		navigationReset: function() {
			Navbar.update('');
		}
		
	});
	
});
