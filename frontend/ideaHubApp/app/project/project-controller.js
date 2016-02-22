(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('ProjectController', ProjectController);

    ProjectController.$inject = ['$q', '$scope','Restangular','$routeParams', '$location', '$cookies'];

    function ProjectController($q, $scope, Restangular, $routeParams, $location, $cookies) {

    	console.log("ProjectController");

        
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

    				var ideaId = response.id;

    				console.log(ideaId);

                    $location.path('/dashboard/'+ideaId);

    			},
    			function(error){
    				console.log(error);
    			}
    		);


    	}



    }

})();
