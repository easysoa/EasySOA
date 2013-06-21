var AJAX = createXMLHttpRequest();
var AJAX2 = createXMLHttpRequest();
var AJAX3 = createXMLHttpRequest();
var AJAX4 = createXMLHttpRequest();
var editor;
var intentEditor;
var tree;
var $dialogCreateImplem;
var $dialogImplemCreation;
var $dialogUpload;
var $dialogBindingCreation;
var $dialogCreateInterf;
var $dialogInterfaceCreationUse;
var $dialogInterfaceCreationNew;
var $dialogUploadInterface;
var $dialogBindService;
var menu;
var catalog;
var urlCatalogService;
var bindableElements;
var rowIndex;

String.prototype.startsWith = function(str) {
	return (this.match("^" + str) == str)
}

function createXMLHttpRequest() {
	if (typeof XMLHttpRequest == "undefined")
		XMLHttpRequest = function() {
			try {
				return new ActiveXObject("Msxml2.XMLHTTP.6.0")
			} catch (e) {
			}
			try {
				return new ActiveXObject("Msxml2.XMLHTTP.3.0")
			} catch (e) {
			}
			try {
				return new ActiveXObject("Msxml2.XMLHTTP")
			} catch (e) {
			}
			try {
				return new ActiveXObject("Microsoft.XMLHTTP")
			} catch (e) {
			}
			throw new Error("This browser does not support XMLHttpRequest.")
		};
	return new XMLHttpRequest();
}

function handler() {
	if (AJAX.readyState == 4 && AJAX.status == 200) {

	}
}

function show() {
	var userId = $("#userId").val();
	$.get("/studioGUIRest/tree", {
		userId : userId
	}, function(data) {
		var json = eval("(" + data + ")");
		tree = new dhtmlXTreeObject("serviceView", "100%", "100%", 0);
		tree.setDataMode("json");
		tree.setImagePath("images/sca/");
		tree.attachEvent("onSelect", onTreeClick);
		tree.enableKeyboardNavigation(true);
		tree.loadJSONObject(json, function() {
		});
	});
	$.get("/catalogServices/catalogServices", {
		socialNetwork : "Facebook"
	}, function(data) {
		catalog = eval("(" + data + ")");
		for (i = 0; i < catalog.publications.length; i++) {
			for (j = 0; j < catalog.publications[i].urls.length; j++) {
				$("#catalogServices table").append(
						"<tr class=\"catalogService\"><td>"
								+ catalog.publications[i].urls[j].url
								+ "</td></tr>");
			}
		}
		$('.catalogService').click(function(e) {
			urlCatalogService = $(this).find("td").html();
			rowindex = $(this).find("td").parent().parent().children().index($(this).find("td").parent());
			$('#catalogServicesMenu').css({
				'top' : e.pageY + 'px',
				'left' : e.pageX - 100 + 'px',
				'display' : 'block'
			});
		});
		//    	
		// $(document).click(function() {
		// $('#catalogServicesMenu').css('display',"none");
		// });
	});
};

function getComponentContent() {
	if (AJAX.readyState == 4 && AJAX.status == 200) {
		var res = AJAX.responseText;
		document.getElementById("component_frame_content").innerHTML = res;
	}
}

function getActionMenu() {
	load();
}

function getGlobalId() {
	console.log("getGlobalId");
	var selectId = tree.getSelectedItemId();
	var globalId = "";
	console.log("selectId : "+ selectId);
	if(selectId != "composite+"){
		while (selectId != "composite+") {
			//console.log(selectId);
			globalId = selectId + globalId;
			selectId = tree.getParentId(selectId);
		}
	}
	else{
		globalId="composite+";
	}
	return globalId;
}

function onTreeClick() {
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	AJAX.onreadystatechange = function() {
		if (AJAX.readyState == 4 && AJAX.status == 200) {
			var res = AJAX.responseText;
			document.getElementById("component_frame_content").innerHTML = res;
			if (globalId.indexOf("implementation") != -1) {
				initEditor("implementation");
			} else if (globalId.indexOf("interface") != -1) {
				initEditor("interface");
			}

		}
	};
	AJAX.open("GET", "/studioGUIRest/componentContent?userId=" + userId
			+ "&elementId=" + globalId);
	AJAX.send("");
	getActionMenu();

}

function action(action) {
	var id = tree.getSelectedItemId();
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	$.post("/rest/addElement", {
		userId : userId,
		elementId : globalId,
		action : action
	}, function(data) {
		if (action == "addBinding") {
			tree.insertNewChild(id, "+binding+name", "name", 0, "Binding.gif",
					"Binding.gif", "Binding.gif", "SELECT,CALL");
		} else if (action == "deleteBinding") {
			tree.deleteItem(id, true);
		} else if (action == "addComponentService") {
			tree.insertNewChild(id, "+service+name", "name", 0,
					"ComponentService.gif", "ComponentService.gif",
					"ComponentService.gif", "SELECT,CALL");
		} else if (action == "addComponentReference") {
			tree.insertNewChild(id, "+reference+name", "name", 0,
					"ComponentReference.gif", "ComponentReference.gif",
					"ComponentReference.gif", "SELECT,CALL");
		} else if (action == "addComponentProperty") {
			tree.insertNewChild(id, "+property+name", "name", 0,
					"Property.gif", "Property.gif", "Property.gif",
					"SELECT,CALL");
		} else if (action == "deleteComponent") {
			tree.deleteItem(id, true);
		} else if (action == "deleteComponentService") {
			tree.deleteItem(id, true);
		} else if (action == "deleteComponentReference") {
			tree.deleteItem(id, true);
		} else if (action == "deleteComponentProperty") {
			tree.deleteItem(id, true);
		} else if (action == "addComponent") {
			tree.insertNewChild(id, "component+name", "name", 0,
					"Component.gif", "Component.gif", "Component.gif",
					"SELECT,CALL");
		} else if (action == "addService") {
			tree.insertNewChild(id, "service+name", "name", 0, "Service.gif",
					"Service.gif", "Service.gif", "SELECT,CALL");
		} else if (action == "addReference") {
			tree.insertNewChild(id, "reference+name", "name", 0,
					"Reference.gif", "Reference.gif", "Reference.gif",
					"SELECT,CALL");
		} else if (action == "deleteService") {
			tree.deleteItem(id, true);
		} else if (action == "deleteReference") {
			tree.deleteItem(id, true);
		} else if (action == "deleteImplementation") {
			tree.deleteItem(id, true);
		} else if (action == "deleteInterface") {
			tree.deleteItem(id, true);
		}
		onTreeClick();
	});

}

function changeImplementation() {
	var id = $("select[name='implementation-type'] option:selected").val();
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	AJAX.onreadystatechange = function() {
		if (AJAX.readyState == 4 && AJAX.status == 200) {
			var res = AJAX.responseText;
			document.getElementById("implementation-panel").innerHTML = res;
			initEditor();
		}
	};
	AJAX.open("GET", "/rest/implementationContent?userId=" + userId
			+ "modelId=" + globalId + "&elementId=" + id);
	AJAX.send("");
}

function changeInterface() {
	var id = $("select[name='interface-type'] option:selected").val();
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	AJAX.onreadystatechange = function() {
		if (AJAX.readyState == 4 && AJAX.status == 200) {
			var res = AJAX.responseText;
			document.getElementById("interface-panel").innerHTML = res;
		}
	};
	AJAX.open("GET", "/rest/interfaceContent?userId=" + userId + "modelId="
			+ globalId + "&elementId=" + id);
	AJAX.send("");
}

function changeBinding() {
	var id = $("select[name='binding-type'] option:selected").val();
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	AJAX.onreadystatechange = function() {
		if (AJAX.readyState == 4 && AJAX.status == 200) {
			var res = AJAX.responseText;
			document.getElementById("component_frame_content").innerHTML = res;
		}
	};
	AJAX.open("GET", "/rest/bindingContent?userId=" + userId + "modelId="
			+ globalId + "&elementId=" + id);
	AJAX.send("");
}

function getTemplateForm() {
	var templateName = $("select[name='template'] option:selected").val();
	AJAX.onreadystatechange = function() {
		if (AJAX.readyState == 4 && AJAX.status == 200) {
			var res = AJAX.responseText;
			document.getElementById("templateForm").innerHTML = res;
			if (templateName == "BasicService") {
				var editorInterf = ace.edit("temp-editor");
				var JavaMode = require("ace/mode/java").Mode;
				editorInterf.getSession().setMode(new JavaMode());
				var textarea = $('textarea[name="temp-textarea"]');
				textarea.hide();
				editorInterf.getSession().on('change', function() {
					textarea.html(editorInterf.getSession().getValue());
				});
			}
		}
	};
	AJAX.open("GET", "/templateRest/templateForm?templateName=" + templateName);
	AJAX.send("");
}

function initEditor(type) {
	editor = ace.edit("editor");
	AJAX4.onreadystatechange = function() {
		if (AJAX4.readyState == 4 && AJAX4.status == 200) {
			var res = AJAX4.responseText;
			editor.getSession().setValue(res);
			AJAX3.onreadystatechange = function() {
				if (AJAX3.readyState == 4 && AJAX3.status == 200) {
					var res = AJAX3.responseText;
					if (res == "javascript") {
						var JavaScriptMode = require("ace/mode/javascript").Mode;
						editor.getSession().setMode(new JavaScriptMode());
					} else if (res == "java") {
						var JavaMode = require("ace/mode/java").Mode;
						editor.getSession().setMode(new JavaMode());
					}else if (res == "html") {
						var HtmlMode = require("ace/mode/html").Mode;
						editor.getSession().setMode(new HtmlMode());
					}
				}
			};
			AJAX3.open("GET", "/rest/editorMode?type=" + type);
			AJAX3.send("");
		}
	};
	AJAX4.open("GET", "/rest/fileContent?type=" + type);
	AJAX4.send("");
}

function saveContentFile(contentText, type) {
	$.post("/rest/saveFileContent", {
		content : contentText,
		type : type
	});
}

function getEditorValue() {
	return editor.getSession().getValue();
}

function addImplementation() {
	$("#dialog-form").dialog("open");
}

function createImplementation(choice) {
	if (choice == "upload") {
		$.fx.speeds._default = 1000;
		$dialogUpload = $("#dialog-upload").dialog({
			autoOpen : false,
			show : "blind",
			hide : "explode",
		});
		$("#dialog-upload").css("display", "block");
		$("#dialog-upload").dialog("option", "width", "auto");
		$dialogUpload.dialog("open");
		return false;
	} else if (choice == "create") {
		$.fx.speeds._default = 1000;
		$dialogImplemCreation = $("#dialog-creation")
				.dialog(
						{
							autoOpen : false,
							show : "blind",
							hide : "explode",
							buttons : {

								"OK" : function() {
									var className = $("#className").val();
									var implemType = $("#implementation-type-dialog-value :selected").text();
									createNewImplementation(className,implemType);
									$(this).dialog("close");
								},
								Cancel : function() {
									$(this).dialog("close");
								}
							}
						});
		$("#dialog-creation").css("display", "block");
		$("#dialog-creation").dialog("option", "width", "auto");
		$dialogImplemCreation.dialog("open");
		return false;
	}
}

function createImplementationUpload(file, implementationType) {
	var globalId = getGlobalId();
	var list = document.getElementById("implementation-type-dialog-value");
	var implemType = list.options[list.options.selectedIndex].value;
	var file = document.getElementById("filename").value;
	var millisecondsToWait = 500;
	var userId = $("#userId").val();
	setTimeout(function() {
		$.post("/rest/createNewImplementation", {
			userId : userId,
			elementId : globalId,
			className : file,
			implemType : implementationType,
			createFile : false
		}, function(data) {
			tree.insertNewChild(tree.getSelectedItemId(), "+implementation",
					file, 0, "Implementation.gif", "Implementation.gif",
					"Implementation.gif", "SELECT,CALL");
		});
	}, millisecondsToWait);

}

function createNewImplementation(className, implemType) {
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	$.post("/rest/createNewImplementation", {
		userId : userId,
		elementId : globalId,
		className : className,
		implemType : implemType,
		createFile : true
	}, function(data) {
		tree.insertNewChild(tree.getSelectedItemId(), "+implementation",
				className, 0, "Implementation.gif", "Implementation.gif",
				"Implementation.gif", "SELECT,CALL");
	});
}

function createBinding() {
	$.fx.speeds._default = 1000;
	$dialogBindingCreation = $("#dialog-creation-binding")
			.dialog(
					{
						autoOpen : false,
						show : "blind",
						hide : "explode",
						buttons : {

							"OK" : function() {
								var list = document
										.getElementById("binding-type");
								var bindingType = list.options[list.options.selectedIndex].value;
								createNewBinding(bindingType);
								$(this).dialog("close");
							},
							Cancel : function() {
								$(this).dialog("close");
							}
						}
					});
	$("#dialog-creation-binding").css("display", "block");
	$("#dialog-creation-binding").dialog("option", "width", "auto");
	$dialogBindingCreation.dialog("open");
}

function createInterface(choice) {
	if (choice == "upload") {
		$.fx.speeds._default = 1000;
		$dialogUploadInterface = $("#dialog-upload-interface").dialog({
			autoOpen : false,
			show : "blind",
			hide : "explode"
		});
		$("#dialog-upload-interface").css("display", "block");
		$("#dialog-upload-interface").dialog("option", "width", "auto");
		$dialogUploadInterface.dialog("open");
		return false;
	} else if (choice == "create") {
		$.fx.speeds._default = 1000;
		$dialogInterfaceCreationNew = $("#dialog-creation-interface")
				.dialog(
						{
							autoOpen : false,
							show : "blind",
							hide : "explode",
							buttons : {

								"OK" : function() {
									var className = document
											.getElementById("classNameNewInterface").value;
									var list = document
											.getElementById("interface-type-creation-interface");
									var implemType = list.options[list.options.selectedIndex].value;
									createNewInterfaceNew(className, implemType);
									$(this).dialog("close");
								},
								Cancel : function() {
									$(this).dialog("close");
								}
							}
						});
		$("#dialog-creation-interface").css("display", "block");
		$("#dialog-creation-interface").dialog("option", "width", "auto");
		$dialogInterfaceCreationNew.dialog("open");
		return false;
	} else if (choice == "use") {
		$.fx.speeds._default = 1000;
		$dialogInterfaceCreationUse = $("#dialog-interface-use")
				.dialog(
						{
							autoOpen : false,
							show : "blind",
							hide : "explode",
							buttons : {

								"OK" : function() {
									var className = document
											.getElementById("classNameInterface").value;
									createNewInterfaceUse(className);
									$(this).dialog("close");
								},
								Cancel : function() {
									$(this).dialog("close");
								}
							}
						});
		$("#dialog-interface-use").css("display", "block");
		$("#dialog-interface-use").dialog("option", "width", "auto");
		$dialogInterfaceCreationUse.dialog("open");
		return false;
	}
}

function createNewInterfaceUse(className) {
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	$.post("/rest/createNewInterface", {
		userId : userId,
		elementId : globalId,
		className : className,
		interfaceType : 'Java',
		createFile : false,
		choice : "Use"
	}, function(data) {
		tree.insertNewChild(tree.getSelectedItemId(), "+interface", className,
				0, "JavaInterface.gif", "JavaInterface.gif",
				"JavaInterface.gif", "SELECT,CALL");
	});
}

function createNewBinding(bindingType) {
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	$.post("/rest/createNewBinding", {
		userId : userId,
		elementId : globalId,
		bindingType : bindingType,
		uri : ""
	}, function(data) {
		tree.insertNewChild(tree.getSelectedItemId(), "+binding+binding",
				"binding", 0, "Binding.gif", "Binding.gif", "Binding.gif",
				"SELECT,CALL");
	});
}

function createNewInterfaceNew(className, interfaceType) {
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	$.post("/rest/createNewInterface", {
		userId : userId,
		elementId : globalId,
		className : className,
		interfaceType : interfaceType,
		createFile : true,
		choice : "New"
	}, function(data) {
		tree.insertNewChild(tree.getSelectedItemId(), "+interface", className,
				0, "Interface.gif", "Interface.gif", "Interface.gif",
				"SELECT,CALL");
	});
}

function createInterfaceUpload(file, interfaceType) {
	var globalId = getGlobalId();
	var list = document.getElementById("interface-type-upload");
	var interfaceType = list.options[list.options.selectedIndex].value;
	var file = document.getElementById("filenameUploadInterface").value;
	var millisecondsToWait = 500;
	var userId = $("#userId").val();
	setTimeout(function() {
		$.post("/rest/createNewInterface", {
			userId : userId,
			elementId : globalId,
			className : file,
			interfaceType : interfaceType,
			createFile : false,
			choice : "Upload"
		}, function(data) {
			tree.insertNewChild(tree.getSelectedItemId(), "+interface", file,
					0, "Interface.gif", "Interface.gif", "Interface.gif",
					"SELECT,CALL");
		});
	}, millisecondsToWait);

}

function deploy() {
	var val = [];
	var share = false;
	var counter = 0; 
	$(":checkbox:checked").each(function(i) {
		if($(this).val() == "share"){
			share = true;
		}else{
			val[counter] = $(this).val();
			counter++;
		}
	});
	var string = val.join("<>");
	var userId = $("#userId").val();
	$("#loading-text").html("Deploying...");
	$("#loading").css("display", "block");
	$.get("/deploymentRest/deploy", {
		userId : userId,
		ids : string
	}, function(data) {
		$("#loading").css("display", "none");
		$("#loading-text").html("");
		if (data.indexOf("successfully") != -1) {
			$.get("/deploymentRest/webExplorerUrls", {
				ids : string
			}, function(result) {

				result = eval('(' + result + ')');
				var message = data;
				message = message + "\n\n";
				message = message + "Deployed services are visible here : "
						+ "\n";
				for (i = 0; i < result.urls.length; i++) {
					message = message + "<a target=\"_blank\" href=\""
							+ result.urls[i].webExplorer + "\">"
							+ result.urls[i].webExplorer + "</a>" + "\n";
				}
				jAlert(message);
				if(share){
					$.post("/catalogServices/share", {
						userId : userId,
						socialNetwork : "Facebook",
						urls : string
					}, function(result) {

					});
				}
			});
		} else {
			jAlert(data);
		}

	});
}

function undeploy() {
	var val = [];
	$(":checkbox:checked").each(function(i) {
		val[i] = $(this).val();
	});
	var string = val.join("<>");
	var userId = $("#userId").val();
	$("#loading-text").html("Undeploying...");
	$("#loading").css("display", "block");
	$.get("/deploymentRest/undeploy", {
		userId : userId,
		ids : string
	}, function(data) {
		$("#loading").css("display", "none");
		$("#loading-text").html("");

		jAlert(data);
	});

}

function uploadResource() {
	$.fx.speeds._default = 1000;
	$dialogUploadResource = $("#dialog-upload-resource").dialog({
		autoOpen : false,
		show : "blind",
		hide : "explode",
	});
	$("#dialog-upload-resource").css("display", "block");
	$("#dialog-upload-resource").dialog("option", "width", "auto");
	$dialogUploadResource.dialog("open");
	return false;
}

/*-----------------------------------------*/
/* Menu */
/*-----------------------------------------*/

function initMenu() {
	menu = new dhtmlXMenuObject("menuObj");
	menu.setIconsPath("images/sca/");
	menu.attachEvent("onClick", menuClick);
	menu.loadFromHTML("menuContent", true, function() {

	});
}

function load() {
	if (menu != null) {
		unload();
	}
	var globalId = getGlobalId();
	var userId = $("#userId").val();
	AJAX2.onreadystatechange = function() {
		if (AJAX2.readyState == 4 && AJAX2.status == 200) {
			var res = AJAX2.responseText;
			var middle = document.getElementById("middle");
			var menuContent = document.createElement("div");
			menuContent.setAttribute("id", "menuContent");
			middle.appendChild(menuContent);
			document.getElementById("menuContent").innerHTML = res;
			menu = new dhtmlXMenuObject("menuObj");
			menu.setIconsPath("images/sca/");
			menu.attachEvent("onClick", menuClick);
			menu.loadFromHTML("menuContent", true, function() {
			});
		}
	};
	AJAX2.open("GET", "/studioGUIRest/componentMenu?elementId=" + globalId
			+ "&userId=" + userId);
	AJAX2.send("");

}

function unload() {
	menu.unload();
	menu = null;
}

function menuClick(id) {
	if (id == "deleteBinding") {
		action(id);
	} else if (id == "addComponentService") {
		action(id);
	} else if (id == "addComponentReference") {
		action(id);
	} else if (id == "addComponentProperty") {
		action(id);
	} else if (id == "deleteComponent") {
		action(id);
	} else if (id == "deleteComponentService") {
		action(id);
	} else if (id == "deleteComponentReference") {
		action(id);
	} else if (id == "deleteComponentProperty") {
		action(id);
	} else if (id == "addComponent") {
		action(id);
	} else if (id == "addBinding") {
		createBinding();
	} else if (id == "addService") {
		action(id);
	} else if (id == "addReference") {
		action(id);
	} else if (id == "deleteService") {
		action(id);
	} else if (id == "deleteReference") {
		action(id);
	} else if (id == "deleteImplementation") {
		action(id);
	} else if (id == "deleteInterface") {
		action(id);
	} else if (id == "deploy") {
		window.location.href = "deploy.html";
	} else if (id == "undeploy") {
		window.location.href = "undeploy.html";
	} else if (id == "upload") {
		uploadResource();
	} else if (id == "save") {
		save();
	} else if (id == "addImplementation") {
		var userId = $("#userId").val();
		var elementId = getGlobalId();
		$
				.get(
						"/rest/hasExistingImplementation",
						{
							userId : userId,
							elementId : elementId
						},
						function(data) {
							if (data == "true") {
								showMessage("error", "Error",
										"An implementation exists, remove the existing before creating a new.");
							} else {
								$dialogCreateImplem = $("#dialog")
										.dialog(
												{
													autoOpen : false,
													show : "blind",
													hide : "explode",
													buttons : {

														"OK" : function() {
															var list = document
																	.getElementById("list");
															var choice = list.options[list.options.selectedIndex].value;
															createImplementation(choice);
															$(this).dialog(
																	"close");
														},
														Cancel : function() {
															$(this).dialog(
																	"close");
														}
													}
												});

								$("#dialog").css("display", "block");
								$dialogCreateImplem.dialog("open");
								return false;
							}
						});
	} else if (id == "addInterface") {
		var userId = $("#userId").val();
		var elementId = getGlobalId();
		$
				.get(
						"/rest/hasExistingInterface",
						{
							userId : userId,
							elementId : elementId
						},
						function(data) {
							if (data == "true") {
								showMessage("error", "Error",
										"An interface exists, remove the existing before creating a new.");
							} else {
								$dialogCreateInterf = $("#dialog-interface")
										.dialog(
												{
													autoOpen : false,
													show : "blind",
													hide : "explode",
													buttons : {

														"OK" : function() {
															var list = document
																	.getElementById("listInterf");
															var choice = list.options[list.options.selectedIndex].value;
															createInterface(choice);
															$(this).dialog(
																	"close");
														},
														Cancel : function() {
															$(this).dialog(
																	"close");
														}
													}
												});

								$("#dialog-interface").css("display", "block");
								$dialogCreateInterf.dialog("open");
								return false;
							}
						});
	} else if (id == "seeIntent"){
		seeIntent("logging");
	} else if (id == "saveIntent"){
		saveIntent("logging");
	}
}

function loadWSDL(wsdlPath) {
    if(wsdlPath == "" || wsdlPath == undefined){
    	return;
    }
	try {
		$.get(
				"/templateRest/loadWSDL",
				{
					wsdlPath : wsdlPath
				},
				function(data) {
					var jsonWsdl = eval('(' + data + ')');
					$("#target").val(jsonWsdl.targetNameSpace);
					$('#servicePort').find('option').remove();
					for (i = 0; i < jsonWsdl.servicesPorts.length; i++) {
						$("#servicePort").append(
								'<option value=\"'
										+ jsonWsdl.servicesPorts[i].value
										+ '\">'
										+ jsonWsdl.servicesPorts[i].value
										+ '</option>');
					}
					document.getElementById('service').value = document
							.getElementById('servicePort').options[document
							.getElementById('servicePort').selectedIndex].value
							.split('/')[0];
					document.getElementById('port').value = document
							.getElementById('servicePort').options[document
							.getElementById('servicePort').selectedIndex].value
							.split('/')[1];
					$("#serviceTR").removeAttr("style");
				}).error(function() {
			$("#servicePort").empty();
			$("#serviceTR").attr("style", "display:none");
			alert("Cannot access to the wsdl.")
		});
	} catch (err) {
		alert(err);
	}
}

$(document).ajaxError(function(event, xhr, request, settings) {
	jAlert(xhr.responseText);
});

function hideCatalogMenu() {
	$('#catalogServicesMenu').css({
		'display' : 'none'
	});
}

function bindOnMenu() {
	hideCatalogMenu();
	var userId = $("#userId").val();
	$.post("/rest/createNewBinding", {
		userId : userId,
		elementId : getGlobalId(),
		bindingType : "Rest",
		uri : urlCatalogService
	}, function(data) {
		tree.insertNewChild(tree.getSelectedItemId(), "+binding+binding",
				"binding", 0, "Binding.gif", "Binding.gif", "Binding.gif",
				"SELECT,CALL");
	});
};

function bindingDetails(){
	var publis = catalog.publications;
	var publi = publis[parseInt(rowindex)];
	var description = publi.description;
	jAlert("Retrieve current weather informations from a city","Description");
}

function seeIntent(name){
	$("#intent-editor").css("display","block");
	intentEditor = ace.edit("intent-editor");
	var userId = $("#userId").val();
	$.get("/rest/intentImplementation", {
		name : name,
		userId : userId
	}, function(data){
		intentEditor.getSession().setValue(data);
		var JavaMode = require("ace/mode/java").Mode;
		intentEditor.getSession().setMode(new JavaMode());
	});
}

function saveIntent(name){
	var userId = $("#userId").val();
	var content = intentEditor.getSession().getValue();
	$.post("/rest/saveIntent", {
		name : name,
		userId : userId,
		content : content
	}, function(data){
		
	});
}