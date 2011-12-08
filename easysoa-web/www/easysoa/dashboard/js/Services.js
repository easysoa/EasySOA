$(function(){
    
window.Service = Backbone.Model.extend({

    /** Backbone function: default values */
    defaults: function() {
        return {
            id: null,
            name: null,
            url: null,
            isValidated: null,
            validationState: {}
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

window.Services = Backbone.Collection.extend({
    
    /** Backbone data: contents model */
    model: Service,

    /** Backbone function: sorting comparator */
    comparator: function(model) {
        return model.name;
    }
    
});

window.ServiceEntryView = Backbone.View.extend({

    tagName: 'tr',
    
    template: _.template($('#serviceentry-template').html()),
    
    initialize: function(model) {
        _.bindAll(this, 'remove');
        this.model = model;
        this.model.bind('destroy', this.remove, this);
    },
    
    render: function() {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    },
    
    remove: function() {
        $(this.el).remove();
    }
    
});

});