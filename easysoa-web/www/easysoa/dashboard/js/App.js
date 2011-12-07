$(function(){
    
window.AppView = Backbone.View.extend({
    
    /** Backbone data: view location **/
    el: $('#servicelist'),
    
    placeHolderTag: $('#placeholder'),
    errorTag: $('#error'),
    
    /** Template for error messages */
    errorTemplate: _.template($('#error-template').html()),
    
    initialize: function() {
        // Makes sure the bound functions are called with the app as 'this'
        _.bindAll(this, 'add', 'hidePlaceholder', 'loadServices'); 
        
        // Create the model and bind it to the app functions
        this.model = new ServiceEntries();
        this.model.bind('add', this.add);
        
        // Load the models
        this.loadServices();
    },
    
    // Controller
    
    loadServices: function() {
        if (window.username != undefined) {
            var app = this;
            $.ajax({
                url: '/servicesstate?username=' + window.username,
                success: function(data, textStatus, jqXHR) {
                    app.hidePlaceholder();
                    var serviceEntryList = $.parseJSON(jqXHR.responseText);
                    for (var i in serviceEntryList) {
                        var newServiceEntry = new ServiceEntry({
                            localService: new Service(serviceEntryList[i].localService),
                            referencedService: new Service(serviceEntryList[i].referencedService)
                        });
                        app.model.add(newServiceEntry);
                    }
                },
                error: function(data) {
                    app.showError("Failed to query services");
                }
            });
        }
        else { 
            // Wait for username to be retrieved
            setTimeout(this.loadServices, 100);  
        }
    },
    
    // View
    
    add: function(serviceEntry) {
        var serviceEntryView = new ServiceEntryView(serviceEntry);
        this.hidePlaceholder();
        this.el.append(serviceEntryView.render().el);
    },
    
    hidePlaceholder: function() {
        this.placeHolderTag.hide();
    },
    
    showError: function(error) {
        console.log("Error: " + error);
        var app = this;
        this.hidePlaceholder();
        this.errorTag.html(app.errorTemplate({error: error}));
        this.errorTag.show('fast');
    }
    
});

window.TheApp = new AppView();

});