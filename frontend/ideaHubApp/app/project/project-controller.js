(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('ProjectController', ProjectController);

    ProjectController.$inject = ['$q', '$scope','Restangular', '$location', '$location', '$cookies'];

    function ProjectController($q, $scope, Restangular, $location,$cookies) {

    	console.log("ProfileController");

        var tookenCookie = $cookies.get('ideahub_token');
        if(angular.isUndefined(tookenCookie) || tookenCookie == ""){
            $location.path('/login');
        }

    	$scope.project = {};


    	/*
    	URI: /idea
		Method: POST
		Headers:
		    Accept: application/json;

		Input:
		    {
		        "name": "",
		        "elevator_pitch": "",
		        "private": ""
		    }
       */

    	$scope.saveProject = function(project){

    		$scope.project = angular.copy(project);
    		
    		var ideaRequest = Restangular.all('/idea');
    		ideaRequest.post($scope.project).then(
    			function(response){
    				console.log(response);

    				var projectId = response.id;

    				console.log(projectId);

                    $location.path('/project/'+projectId);

    			},
    			function(error){
    				console.log(error);
    			}
    		);


    	}



    }

})();
