
$(document).ready( function() { 
	
	checkIEAjax();
	
    $('.dropdown-toggle').click(function() {
	$('.dropdown-menu').toggle();
    });
    $('.more_btn').click( function() {
       	var section = $(this).parent();
        $(section).children('.more_section').slideDown();
        // $(more_section).slideDown();
        $(this).hide();
	return false;
    });
    $('.less_btn').click( function() {
        var section = $(this).parent();
        $(section).slideUp( function() {
            var listing = $(this).parent();
            $(listing).children('.more_btn').show();
	});
	return false;
    });
    
    $(document).on('mouseenter','.toptext', function(event){
    	handleTopTextMouseEnter(event);
    });
    
    $(document).on('mouseleave','.toptext', function(event){
    	handleTopTextMouseLeave(event);
    });
    
    $(document).on('mouseenter','.footeranchor', function(event){
    	handleFooterMouseEnter(event);
    });
    
    $(document).on('mouseleave','.footeranchor', function(event){
    	handleFooterMouseLeave(event);
    });
    
    $(document).on('mouseenter','.basement', function(event){
    	handleBasementMouseEnter(event);
    });
    
    $(document).on('mouseleave','.basement', function(event){
    	handleBasementMouseLeave(event);
    });    
    
    $('.toptext')
    .mouseenter(function(event){
    	handleFooterMouseEnter(event);
    })
    .mouseleave(function(event){
    	handleFooterMouseLeave(event);
    });
    
    $('.footeranchor')
    .mouseenter(function(event){
    	handleFooterMouseEnter(event);
    })
    .mouseleave(function(event){
    	handleFooterMouseLeave(event);
    });
    
    $('.basement')
    .mouseenter(function(event){
    	handleBasementMouseEnter(event);
    })
    .mouseleave(function(event){
    	handleBasementMouseLeave(event);
    });
});

function checkIEAjax(){

	if (!$.support.cors && $.ajaxTransport && window.XDomainRequest) {
	  var httpRegEx = /^https?:\/\//i;
	  var getOrPostRegEx = /^get|post$/i;
	  var sameSchemeRegEx = new RegExp('^'+location.protocol, 'i');
	  var htmlRegEx = /text\/html/i;
	  var jsonRegEx = /\/json/i;
	  var xmlRegEx = /\/xml/i;
	  
	  // ajaxTransport exists in jQuery 1.5+
	  $.ajaxTransport('* text html xml json', function(options, userOptions, jqXHR){
	    // XDomainRequests must be: asynchronous, GET or POST methods, HTTP or HTTPS protocol, and same scheme as calling page
	    if (options.crossDomain && options.async && getOrPostRegEx.test(options.type) && httpRegEx.test(options.url) && sameSchemeRegEx.test(options.url)) {
	      var xdr = null;
	      var userType = (userOptions.dataType||'').toLowerCase();
	      return {
	        send: function(headers, complete){
	          xdr = new XDomainRequest();
	          if (/^\d+$/.test(userOptions.timeout)) {
	            xdr.timeout = userOptions.timeout;
	          }
	          xdr.ontimeout = function(){
	            complete(500, 'timeout');
	          };
	          xdr.onload = function(){
	            var allResponseHeaders = 'Content-Length: ' + xdr.responseText.length + '\r\nContent-Type: ' + xdr.contentType;
	            var status = {
	              code: 200,
	              message: 'success'
	            };
	            var responses = {
	              text: xdr.responseText
	            };
	            try {
	              if (userType === 'html' || htmlRegEx.test(xdr.contentType)) {
	                responses.html = xdr.responseText;
	              } else if (userType === 'json' || (userType !== 'text' && jsonRegEx.test(xdr.contentType))) {
	                try {
	                  responses.json = $.parseJSON(xdr.responseText);
	                } catch(e) {
	                  status.code = 500;
	                  status.message = 'parseerror';
	                  //throw 'Invalid JSON: ' + xdr.responseText;
	                }
	              } else if (userType === 'xml' || (userType !== 'text' && xmlRegEx.test(xdr.contentType))) {
	                var doc = new ActiveXObject('Microsoft.XMLDOM');
	                doc.async = false;
	                try {
	                  doc.loadXML(xdr.responseText);
	                } catch(e) {
	                  doc = undefined;
	                }
	                if (!doc || !doc.documentElement || doc.getElementsByTagName('parsererror').length) {
	                  status.code = 500;
	                  status.message = 'parseerror';
	                  throw 'Invalid XML: ' + xdr.responseText;
	                }
	                responses.xml = doc;
	              }
	            } catch(parseMessage) {
	              throw parseMessage;
	            } finally {
	              complete(status.code, status.message, responses, allResponseHeaders);
	            }
	          };
	          // set an empty handler for 'onprogress' so requests don't get aborted
	          xdr.onprogress = function(){};
	          xdr.onerror = function(){
	            complete(500, 'error', {
	              text: xdr.responseText
	            });
	          };
	          var postData = '';
	          if (userOptions.data) {
	            postData = ($.type(userOptions.data) === 'string') ? userOptions.data : $.param(userOptions.data);
	          }
	          xdr.open(options.type, options.url);
	          xdr.send(postData);
	        },
	        abort: function(){
	          if (xdr) {
	            xdr.abort();
	          }
	        }
	      };
	    }
	  });
	}

		
}

function handleTopTextMouseEnter(event){
	var theTarget=event.target;
	$(theTarget).removeAttr('style');
	$(theTarget).attr('style','color: #fff;cursor: pointer;text-decoration: underline;');
}

function handleTopTextMouseLeave(event){
	var theTarget=event.target;
	$(theTarget).removeAttr('style');
	$(theTarget).attr('style','color: #fff;cursor: pointer;text-decoration: none;');
}

function handleBasementMouseEnter(event){
	var theTarget=event.target;
	$(theTarget).removeAttr('style');
	$(theTarget).attr('style','color: #CCC !important;text-underline: none !important;cursor: pointer;text-align: left;');
}

function handleBasementMouseLeave(event){
	var theTarget=event.target;
	$(theTarget).removeAttr('style');
	$(theTarget).attr('style','color: #CCC !important;text-decoration: none !important;cursor: pointer;text-align: left;');
}

function handleFooterMouseEnter(event){
	var theTarget=event.target;
	$(theTarget).removeAttr('style');
	$(theTarget).attr('style','color: #AAA;text-decoration: underline;cursor: auto;font-size: 12px;text-transform: capitalize;white-space: nowrap;text-align: -webkit-match-parent;list-style-type: none;line-height: 14px;');
}

function handleFooterMouseLeave(event){
	var theTarget=event.target;
	$(theTarget).removeAttr('style');
	$(theTarget).attr('style','color: #AAA;text-decoration: none;cursor: auto;font-size: 12px;text-transform: capitalize;white-space: nowrap;text-align: -webkit-match-parent;list-style-type: none;line-height: 14px;');	
}

function getFooter(team){
	$.support.cors = true; 
	var theUrl="http://docs.rackspace.com/rax-headerservice/rest/service/getheader?footer=true&team="+team;
	$.ajax({
		url:theUrl,
	    type:"GET",
	    crossDomain:true,
	    dataType:"json",
	    data:{"team":team,"footer":"true"}
	})
	  .done(function(data,textStatus,jqXHR){
		  $("#footer").html(data.html); 
	  })
	  .fail(function(data,textStatus,errorThrown){
	      var failure="true";
	      failure+="asdf";	    	
	  });
	
	/*
	
    $.getJSON("http://docs.rackspace.com/rax-headerservice/rest/service/getheader?footer=true&team="+team,{"team":team,"footer":"true"})
    .done(function(data){
        $("#footer").html(data.html);           	
    })
    .fail(function(jqxhr, textStatus, error){
    	var failure="true";
    	failure+="asdf";
    });	
    */
}
