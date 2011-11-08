var Prototype, Class;
Prototype = require('./Prototype');
            require('./Object');
Class =     require('./Class');
            require('./Function');
            require('./Date');
RegExp.prototype.match = RegExp.prototype.test;

RegExp.escape = function(str) {
  return String(str).replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');
};