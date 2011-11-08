var $R, $H, $A, $w, Enumerable, Prototype, Class, Template, Try;
Prototype =   require('./Prototype');
              require('./Object');
Class =       require('./Class');
              require('./Function');
              require('./Date');
              require('./RegExp');
              require('./String');
Template =    require('./Template');
Enumerable =  require('./Enumerable');
$A =          require('./Array').A;
$w =          require('./Array').w;
$H =          require('./Hash');
              require('./Number');
$R =          require('./Range');

module.exports = Try = {
  these: function() {
    var returnValue;

    for (var i = 0, length = arguments.length; i < length; i++) {
      var lambda = arguments[i];
      try {
        returnValue = lambda();
        break;
      } catch (e) { }
    }

    return returnValue;
  }
};