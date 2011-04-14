$(function() {

	window.AppView = Backbone.View.extend({
	
		initialize: function() {
			this.descriptorsView = new DescriptorsView;
			this.frameView = new FrameView;
			this.navBarView = new NavbarView;
		}
	
	});
	
	window.App = new AppView;
	
});