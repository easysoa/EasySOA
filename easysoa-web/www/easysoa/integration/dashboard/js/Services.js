$(function(){
    
window.Service = Backbone.Model.extend({

    /** Backbone function: default values */
    defaults: function() {
        return {
            id: null,
            name: null,
            url: null,
            referencedService: {}, /* Service */
            isValidated: false,
            validationState: {},
            lifeCycleState: null
        };
    },
    
    initialize: function(data) {
        // Convert the referenceService field to a valid Service model
        if (this.get('referencedService').id != undefined) {
            this.set({'referencedService': new Service(this.get('referencedService'))});
        }
    },
    
    hasReference: function() {
        return (this.get('referencedService').cid != undefined);
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
    
    events: {
        "mouseover .serviceEntryValidationResult" : "showLabel",
        "mouseout .serviceEntryValidationResult" : "hideLabel",
    },
    
    template: _.template($('#serviceentry-template').html()),
    
    initialize: function(model) {
        _.bindAll(this, 'remove', 'showLabel', 'hideLabel');
        this.model = model;
        this.model.bind('destroy', this.remove, this);
    },
    
    render: function() {
        var json = this.model.toJSON();
        json.cid = this.model.cid;  // XXX Don't use cids in HTML, rather push the event from the service view to the app?
        if (this.model.get('referencedService') != undefined) {
            json.referencedService.cid = this.model.get('referencedService').cid;
        }
        else {
            json.referencedService = null;
        }
        $(this.el).html(this.template(json));
        return this;
    },
    
    remove: function() {
        $(this.el).remove();
    },
    
    showLabel: function(event) {
        var label = $('div', event.currentTarget);
        if (label.html().trim() != "") {
            $('div', event.currentTarget).addClass("visible");
        }
    },
    
    hideLabel: function(event) {
        $('div', event.currentTarget).removeClass("visible");
    }
    
});

});