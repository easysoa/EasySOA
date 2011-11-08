# Prototype in Node.js
Implementing Prototypejs in Node.js  
My main goal has been to implement the Ruby-like features in JS.

### Instalation: 
    npm install prototype

### Usage:
    var prototype = require('prototype');  
    Object.extend(global, prototype);  
    //Now you can do all the awesome things like:  
    (9).times(function(x){  
      console.log("Loop Advanced: "+x);  
    })  

### To Do:
 - Map require('http') to the api of Ajax