$(function(){
   
window.DeployablesModel = Backbone.Model.extend({

  initialize: function() {
    this.set({
      myEnv: null,
      refEnv: null
    });
  },
  
  setMyEnv: function(envName) {
    var model = this;
    this.fetchEnvDeployables(envName, function(data) {
       model.set({'myEnv': data});
       if (model.get('refEnv') && model.view) {
          model.view.render();
       }
    });
  },
  
  setRefEnv: function(envName) {
    var model = this;
    this.fetchEnvDeployables(envName, function(data) {
       model.set({'refEnv': data});
       if (model.get('myEnv') && model.view) {
          model.view.render();
       }
    });
  },
  
  fetchEnvDeployables: function(envName, callback) {
    var model = this;
    $.ajax({
        url: '/nuxeo/dashboard/deployables/' + envName,
        type: 'GET',
        success: function(data, textStatus, jqXHR) {
          callback($.parseJSON(jqXHR.responseText));
        },
        error: function(data) {
          console.log(data);
        }
    });
  }

});


window.DeployablesView = Backbone.View.extend({

  el: '#deployablesContents',
  
  envTemplate: _.template($('#envDeployablesTemplate').html()),
  
  envs: {
    myEnv : null,
    refEnv : null
  },
  
  initialize: function(model) {
    model.view = this;
    this.model = model;
  },
  
  render: function() {
    this.clear();
    this.renderEnv(this.model.get('myEnv'));
    this.renderEnv(this.model.get('refEnv'));
  },
  
  renderEnv: function(data) {
     $(this.el).append(this.envTemplate({'data': data}));
  },
  
  clear: function() {
    $(this.el).html("");
  }

});

});
