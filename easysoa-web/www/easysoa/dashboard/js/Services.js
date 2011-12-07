$(function(){
    
window.Service = Backbone.Model.extend({

    /** Backbone function: default values */
    defaults: function() {
        return {
            name: null,
            url: null,
            isValidated: null,
            validationLog: null
        };
    }
    
});

window.ServiceEntry = Backbone.Model.extend({

    /** Backbone function: default values */
    defaults: function() {
        return {
          localService: new Service(),
          referencedService: new Service()
        };
    }
    
});

window.ServiceEntries = Backbone.Collection.extend({

    /** Backbone data: contents model */
    model: ServiceEntry,

    /** Backbone function: sorting comparator */
    comparator: function(model) {
        return (model.localService != null) ? model.localService.name : null;
    }
    
});

window.ServiceEntryView = Backbone.View.extend({

    /** Backbone data: view tag */
    tagName: "tr",

    template: _.template($('#serviceentry-template').html()),
    
    initialize: function(model) {
        this.model = model;
    },
    
    render: function() {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
    
});

});