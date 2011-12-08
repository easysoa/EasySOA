$(function(){

window.ValidatorName = Backbone.Model.extend({});
window.Validators = Backbone.Collection.extend({
    model: ValidatorName
});
    
window.AppView = Backbone.View.extend({
    
    /** Backbone data: view location **/
    el: $('#container'),

    events:  {
        'click .serviceEntryLocalServiceName' : 'selectLocalItem',
        'click .serviceEntryReferencedServiceName' : 'selectReferencedItem',
        'click .defineReferenceLink' : 'defineReferenceLink'
    },

    /** Tags */
    entriesTableTag: $('#serviceentries'),
    entriesHeaderTag: $('#serviceentriesheader'),
    placeHolderTag: $('#placeholder'),
    errorTag: $('#error'),
    
    /** Templates */
    errorTemplate: _.template($('#error-template').html()),
    entriesheadervalidatorTemplate: _.template($('#serviceentriescolumn-template').html()),
    
    initialize: function() {
        // Makes sure the bound functions are called with the app as 'this'
        _.bindAll(this, 'add', 'hidePlaceholder', 'loadServices',
                'selectLocalItem', 'selectReferencedItem', 'defineReferenceLink',
                'loadValidators', 'addValidatorColumn'); 
        
        // Create the model and bind it to the app functions
        this.serviceEntries = new ServiceEntries();
        this.validatorNames = new Validators();
        this.services = new Services();
        this.selectedLocal = null;
        this.selectedReference = null;
        
        this.serviceEntries.bind('add', this.add);
        this.validatorNames.bind('add', this.addValidatorColumn);
        
        // Load the models
        this.loadValidators();
        this.loadServices();
    },

    loadValidators: function() {
        if (window.username != undefined) {
            var app = this;
            $.ajax({
                url: '/dashboard/validators',
                success: function(data, textStatus, jqXHR) {
                    var names = $.parseJSON(jqXHR.responseText);
                    for (i in names) {
                        app.validatorNames.add(new ValidatorName({name: names[i]}));
                    }
                },
                error: function(data) {
                    app.showError("Failed to query validators");
                }
            });
        }
        else { 
            // Wait for username to be retrieved
            setTimeout(this.loadValidators, 100);  
        }
    },
    
    loadServices: function() {
        if (window.username != undefined) {
            var app = this;
            $.ajax({
                url: '/dashboard/servicesstate?username=' + window.username,
                success: function(data, textStatus, jqXHR) {
                    app.hidePlaceholder();
                    var result = $.parseJSON(jqXHR.responseText);
                    if (result.error == undefined) {
                        for (var i in result) {
                            var newLocalService = new Service(result[i].localService);
                            var newReferencedService = new Service(result[i].referencedService);
                            var newServiceEntry = new ServiceEntry({
                                localService: newLocalService,
                                referencedService: newReferencedService
                            });
                            app.serviceEntries.add(newServiceEntry);
                            app.services.add(newLocalService);
                            app.services.add(newReferencedService);
                        }
                    }
                    else {
                        app.showError(result.error);
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

    selectLocalItem: function(event) {
        var target = $(event.target);
        var targetWasSelected = target.hasClass("selectedLocal");
        $('.serviceEntryLocalServiceName').removeClass("selectedLocal");
        if (!targetWasSelected) {
            target.addClass("selectedLocal");
            this.selectedLocal = $(".selectedLocal .id").html();
        }
        else {
            this.selectedLocal = null;
        }
    },
    
    selectReferencedItem: function(event) {
        var target = $(event.target);
        var targetWasSelected = target.hasClass("selectedReference");
        $('.serviceEntryReferencedServiceName').removeClass("selectedReference");
        if (!targetWasSelected) {
            target.addClass("selectedReference");
            this.selectedReference = $(".selectedReference .id").html();
        }
        else {
            this.selectedReference = null;
        }
    },
    
    defineReferenceLink: function(event) {
        if (this.selectedReference != null && this.selectedLocal != null) {
            var fromId = this.services.getByCid(this.selectedLocal).get('id');
            var toId = this.services.getByCid(this.selectedReference).get('id');
            var app = this;
            $.ajax({
                url: '/dashboard/linkservices?fromid=' + fromId + '&toid=' + toId,
                success: function(data, textStatus, jqXHR) {
                    location.reload();
                },
                error: function(data) {
                    app.showError("Failed to link services");
                }
            });
        }
        else {
            alert("You must select both a service and a reference to match.");
        }
    },
    
    add: function(serviceEntry) {
        var serviceEntryView = new ServiceEntryView(serviceEntry);
        this.hidePlaceholder();
        this.entriesTableTag.append(serviceEntryView.render().el);
    },
    
    addValidatorColumn: function(validatorName) {
        this.entriesHeaderTag.append(this.entriesheadervalidatorTemplate(validatorName));
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