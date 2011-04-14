$(function() {

	window.FrameModel = Backbone.Model.extend({
	
		initialize: function() {
		},
		
		setSource: function(newSource) {
			this.set({"source": newSource});
		},
	
		getSource: function() {
			return this.get("source");
		}
		
	});
	
	window.Frame = new FrameModel;
	
	window.FrameView = Backbone.View.extend({
	
		el: $("#frame"),
	
		initialize: function() {
			_.bindAll(this, 'render');
			Frame.view = this;
			Frame.bind('change', this.render);
		},
		
		render: function() {
			this.el.attr("src", Frame.getSource());
		}
	
	});
	
});