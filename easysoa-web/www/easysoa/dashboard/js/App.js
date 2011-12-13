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
        'click #defineReferenceLink' : 'defineReferenceLink',
        'click .actionButton' : 'updateLifeCycleState'
    },

    /** Tags */
    entriesTableTag: $('#serviceentries'),
    entriesHeaderTag: $('#serviceentriesheader'),
    placeHolderTag: $('#placeholder'),
    errorHolder: $('#errorHolder'),
    
    /** Templates */
    errorTemplate: _.template($('#error-template').html()),
    entriesheadervalidatorTemplate: _.template($('#serviceentriescolumn-template').html()),
    correlationResultTemplate: _.template($('#correlationresult-template').html()),
    
    initialize: function() {
        // Makes sure the bound functions are called with the app as 'this'
        _.bindAll(this, 'add', 'hidePlaceholder', 'loadServices',
                'selectLocalItem', 'selectReferencedItem', 'defineReferenceLink',
                'loadValidators', 'addValidatorColumn', 'showError', 'updateLifeCycleState'); 
        
        // Create the model and bind it to the app functions
        this.serviceEntries = new Services();
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
                url: '/dashboard/services/' + window.username,
                success: function(data, textStatus, jqXHR) {
                    app.hidePlaceholder();
                    var result = $.parseJSON(jqXHR.responseText);
                    if (result.error == undefined) {
                        for (var i in result) {
                            var newService = new Service(result[i]);
                            app.serviceEntries.add(newService);
                            app.services.add(newService);
                            app.services.add(newService.get('referencedService'));
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
        // Select item
        var target = $(event.target);
        var targetWasSelected = target.hasClass("selectedLocal");
        $('.serviceEntryLocalServiceName').removeClass("selectedLocal");
        if (!targetWasSelected) {
            target.addClass("selectedLocal");
            this.selectedLocal = $(".selectedLocal").attr('cid');
        }
        else {
            this.selectedLocal = null;
        }
        
        // Show correlation results if service has no reference
        $('.correlationResult').remove();
        if (this.selectedLocal != null) {
            var localServiceModel = this.serviceEntries.getByCid(this.selectedLocal);
            if (localServiceModel.get('referencedService').cid == null) {
                var app = this;
                $.ajax({
                    url: '/dashboard/service/' + localServiceModel.get('id') + '/matches',
                    success: function(data, textStatus, jqXHR) {
                        // If the user didn't click somewhere before
                        if (app.selectedLocal == localServiceModel.cid) {
                            var matches = $.parseJSON(jqXHR.responseText);
                            for (var i in matches) {
                                $('#'+matches[i].id).append(app.correlationResultTemplate(matches[i]));
                            }
                        }
                    },
                    error: function(data) {
                        app.showError("Failed to query services");
                    }
                });
            }
        }
        
        this.updateEnabledButtons();
    },
    
    selectReferencedItem: function(event) {
        var target = $(event.target);
        if (!target.hasClass('correlationResult')) {
            var targetWasSelected = target.hasClass("selectedReference");
            $('.serviceEntryReferencedServiceName').removeClass("selectedReference");
            if (!targetWasSelected) {
                target.addClass("selectedReference");
                this.selectedReference = $(".selectedReference").attr('cid');
            }
            else {
                this.selectedReference = null;
            }
            this.updateEnabledButtons();
        }
    },
    
    updateEnabledButtons: function() {
        var service = this.services.getByCid(this.selectedLocal);
        var lifeCycleState = service.get('lifeCycleState');
        
        this.toggle($('#defineReferenceLink'), this.selectedReference != null && this.selectedLocal != null);
        this.toggle($('#breakReferenceLink'), this.selectedLocal != null && service.hasReference());
        this.toggle($('#approveService'), this.selectedLocal != null && 'project' == lifeCycleState);
        this.toggle($('#maskService'), this.selectedLocal != null && 'project' == lifeCycleState);
        this.toggle($('#removeService'), this.selectedLocal != null && 'project' == lifeCycleState);
        this.toggle($('#resetService'), this.selectedLocal != null && ('approved' == lifeCycleState || 'obsoloete' == lifeCycleState));
    },
    
    toggle: function(element, value) {
        if (value) {
            element.show(100);
        }
        else {
            element.hide(100);
        }
    },
    
    defineReferenceLink: function() {
        if (this.selectedReference != null && this.selectedLocal != null) {
            var fromId = this.services.getByCid(this.selectedLocal).get('id');
            var toId = this.services.getByCid(this.selectedReference).get('id');
            var app = this;
            $.ajax({
                url: '/dashboard/service/' + fromId + '/linkto/' + toId,
                type: 'POST',
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
    
    breakReferenceLink: function() {
        if (this.selectedLocal != null && this.services.getByCid(this.selectedLocal).hasReference()) {
            var fromId = this.services.getByCid(this.selectedLocal).get('id');
            $.ajax({
                url: '/dashboard/service/' + fromId + '/linkto/null',
                type: 'POST',
                success: function(data, textStatus, jqXHR) {
                    location.reload();
                },
                error: function(data) {
                    app.showError("Failed to unlink services");
                }
            });
        }
        else {
            alert("You must a service that has a reference.");
        }
    },
     
    updateLifeCycleState: function(event) {
        if (this.selectedLocal != null) {
            var buttonId = $(event.target).attr('id');
            var serviceId = this.services.getByCid(this.selectedLocal).get('id');
            var lifecycleTransition = null;
            switch (buttonId) {
            case 'approveService': lifecycleTransition = 'approve'; break;
            case 'maskService': lifecycleTransition = 'obsolete'; break;
            case 'removeService': lifecycleTransition = 'delete'; break;
            case 'resetService': lifecycleTransition = 'backToProject'; break;
            }
            $.ajax({
                url: '/dashboard/service/' + serviceId + '/lifecycle/' + lifecycleTransition,
                type: 'POST',
                success: function(data, textStatus, jqXHR) {
                    location.reload();
                },
                error: function(data) {
                    app.showError("Failed to update lifecycle state");
                }
            });
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
        this.hidePlaceholder();
        this.errorHolder.html(this.errorTemplate({error: error}));
        var error = $("#error");
        error.show('fast');
        setTimeout(function() {
            error.hide('slow');
        }, 3000);
    }
    
});

window.TheApp = new AppView();

});