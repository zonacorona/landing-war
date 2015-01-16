function TryitController($scope, $http){
	$scope.username;
	$scope.password;
	$scope.commands=[{}];
	$scope.catalogs=[];
	
	$scope.authenticate=function(){
		if(null!=$scope.uname && $scope.uname!=undefined && $scope.uname.trim()!=""){
			$($('#unametext')[0]).removeAttr('style');
			if(null!=$scope.passwd && $scope.passwd!=undefined && $scope.passwd.trim()!=""){
				$($('#passtext')[0]).removeAttr('style');
				authenUser($scope, $http);
				
			}
			else{
				$($('#passtext')[0]).css('border-color','#CC1100');
				alert('User Name field cannot be empty');					
			}
		}
		else{
			$($('#unametext')[0]).css('border-color','#CC1100');
			alert('Password field cannot be empty');			
		}
		
	};

	$scope.renderEndpointSelection=function(endpoints){
	    var retVal='';
	    if(endpoints.length==1){

	        retVal='<select class="endpointselectclass"><option selected value="'+endpoints[0].publicURL+'">default</option></select>\n';
	    }
	    else{
            retVal='<select class="endpointselectclass">';
            $.each(endpoints, function(index,endpoint){
                retVal+='<option value="';
                retVal+=endpoint.publicURL;
                retVal+='">';
                retVal+=endpoint.region;
                retVal+='</option>\n';
            });

            retVal+='</select>\n';
	    }
	    return retVal;
	}

}

function authenUser($scope, $http, user, passwd){
	var user=$scope.uname;
	var passwd=$scope.passwd;
	var theData='{"auth":{"passwordCredentials":{"username":"'+user+'", "password":"'+passwd+'"}}}';
	httpRequest=$http({method:'POST', url:"/rax-autodeploy/authenticate",
		headers: {'Accept':'application/json'}, data:theData}).
    success(function(data, status){
        var blah;

        getCatalogs($scope,data);

        $('.credentials').hide();
        $('#commandsectionid').show();
    }).
    error(function(data, status){
    	var blah;
		return false;
    });


}

function getCatalogs($scope, data){
    //Get the array of serviceCatalog
    var catalogs=data.access.serviceCatalog;
    $.each(catalogs, function(index, catalog){
        $scope.catalogs.push(catalog);

    });
}