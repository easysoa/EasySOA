$(function() {

	window.AppView = Backbone.View.extend({
	
		/**
		 * Application initialization
		 */
		initialize: function() {
			this.descriptorsView = new DescriptorsView;
			this.frameView = new FrameView;
			this.navBarView = new NavbarView;
			this.submitFormView = new SubmitFormView;
		}
	
	});
	
	window.App = new AppView;
	
});