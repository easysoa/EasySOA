var focusedDiv = null;

chooseDiv = function(div) {
  focusedDiv = div;
};

applyTemplate = function() {

  var append = false;
  if (arguments.length == 1) {
    append = true;
    arguments[1] = arguments[0];
    arguments[0] = focusedDiv;
  }
  
  var contractTemplate = _.template($('#contractTemplate').html());
  var result = contractTemplate({data: arguments[1]});
  if (append) {
    $('#'+arguments[0]).append(result);
  }
  else {
    $('#'+arguments[0]).html(result);
  }
  focusedDiv = arguments[0];
}
  
$(document).ready(function() {

    /*
    Example:
     environment: 'Master',
     endpoint: 'Content Automation',
     connector: 'Java client',
     exists: false,
     authorized: false,
     directAccess: true
    */

    applyTemplate('directAccessContract', {
      environment: 'Master',
      endpoint: 'Content Automation',
      directAccess: true,
      authorized: false
    });
    
    chooseDiv('recommended');
    applyTemplate({
      environment: 'Master',
      endpoint: 'Content Automation'
    });
    applyTemplate({
      environment: 'Master',
      endpoint: 'Content Automation',
      connector: 'EasySOA Light scripting'
    });
    
    chooseDiv('other');
    applyTemplate({
      environment: 'Master',
      endpoint: 'SOAP endpoint'
    });
    applyTemplate({
      environment: 'Master',
      endpoint: 'SOAP endpoint',
      connector: 'Java connector'
    });
    
    chooseDiv('create');
    applyTemplate({
      environment: 'Sophie',
      endpoint: 'REST endpoint',
      exists: false
    });
    applyTemplate({
      environment: 'Sophie',
      endpoint: 'REST endpoint',
      connector: 'Javascript connector',
      exists: false
    });
});
