var prototype = require('./lib');
Object.extend(global, prototype);

(3).times(function(x){
  console.log("Number Modification: "+x);
});

var Person = Class.create({
  initialize: function(name){
    this.name = name;
  },
  say: function(message){
    console.log(this.name+": "+message);
  }
});
var bob = new Person("bob");
bob.say("Imma Object.");