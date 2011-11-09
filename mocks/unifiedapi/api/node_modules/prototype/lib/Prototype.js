module.exports = {
  Version: '1.7',
  
  Browser: {
    IE:             false,
    Opera:          false,
    WebKit:         false,
    Gecko:          false,
    MobileSafari:   false
  },
  
  BrowserFeatures: {
    XPath: false,
    SelectorsAPI: false,
    ElementExtensions: false,
    SpecificElementExtensions: false
  },
  
  ScriptFragment: '<script[^>]*>([\\S\\s]*?)<\/script>',
  JSONFilter: /^\/\*-secure-([\s\S]*)\*\/\s*$/,

  emptyFunction: function(){},
  K: function(x){ x }
}