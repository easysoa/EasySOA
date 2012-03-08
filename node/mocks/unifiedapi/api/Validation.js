// Unified API Mocking
// Copyright (c) 2011 Open Wide and others
// 
// MIT licensed
// 
// Contact : easysoa-dev@googlegroups.com

Object.extend(global, require('prototype'));

//===================== Tests =====================

var AbstractValidationTest = new Class.create({
    initialize : function() {
    },
    run : function(target, data) {
        // To implement (return true if the test has passed, else provide an error message)
    }
});

var WSDLTest = new Class.create(AbstractValidationTest, {
    initialize : function($super, wsdl) {
        $super();
        this.wsdl = wsdl;
    },
    run : function(target, service) {
        // Check WSDL compliance
        return true;
    }
});

var ServiceCheckTest = new Class.create({
    initialize : function(serviceHandler /*function(env, endpoint)*/) {
        this.serviceHandler = serviceHandler;
    },
    run : function(environment, endpoint) {
        return serviceHandler(environment, endpoint);
    }
});

var NotificationTest = new Class.create({
    initialize : function(notificationHandler /*function(env, notification)*/) {
        this.notificationHandler = notificationHandler;
    },
    run : function(environment, notification) {
        return notificationHandler(environment, notification);
    }
});

//==================== Actions ====================

var AbstractValidationFailureAction = new Class.create({
    initialize : function() {
    },
    run : function(target, error, data) {
        // To implement
    }
});

var BlockAction = new Class.create({
    initialize : function() {
    },
    run : function(target, error, data) {
        return false;
    }
});

var SendMailAction = new Class.create({
    initialize : function(mail) {
        this.mail = mail;
    },
    run : function(target, error, data) {
        console.log("Sending mail to " + this.mail + ": " + error);
        return true;
    }
});

//===================== Rules =====================

var ValidationRule = new Class.create({
    initialize : function(test, action) {
        this.test = test;
        this.action = action;
    },
    handleEvent : function(target, data) {
        var result = this.test.run(target, data);
        if (result !== true) {
            return this.action.run(target, result, data);
        }
        return true; // Continue action (false = cancel action)
    }
});


module.exports = {
    WSDLTest : WSDLTest,
    ServiceCheckTest : ServiceCheckTest,
    NotificationTest : NotificationTest,
    BlockAction : BlockAction,
    SendMailAction : SendMailAction,
    ValidationRule : ValidationRule
};