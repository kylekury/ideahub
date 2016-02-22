(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['$q', '$scope','Restangular','$routeParams', '$location', '$cookies'];

    function DashboardController($q, $scope, Restangular, $routeParams, $location, $cookies) {

    	console.log("DashboardController");

        var tookenCookie = $cookies.get('ideahub_token');
        if(angular.isUndefined(tookenCookie) || tookenCookie == ""){
            $location.path('/login');
        }

    	
        $scope.ideaId = $routeParams.ideaId;

        $scope.idea = {};
        $scope.definitionList = [];
        $scope.myIdeaList = [];

        getIdeaDetail();
        getIdeaDefinition();
        getIdeas() ;

        //Get Project
        function getIdeaDetail (){

            var ideaDeatilRequest = Restangular.all("idea");
             ideaDeatilRequest.get($scope.ideaId).then(
                function(response) {

                    $scope.idea = response;    
                    console.log(response);

                },
                function(error) {
                    
                }
            );
        }


        function getIdeaDefinition () {
			var definition = Restangular.all('/idea/definition')
			definition.getList().then(
				function(response) {

				    $scope.definitionList = response;

				    console.log($scope.definitionList);

				},
				function(error) {
				}
			);	

        }


        function getIdeas () {

        	var myIdeasListRequest = Restangular.all('/idea')
			return myIdeasListRequest.getList().then(
	            function(response) {
	           		 $scope.myIdeaList = response;

	           		 console.log($scope.myIdeaList);
	            }, 
	            function(error) {
			        
	            }
        	);	

        }




    }

})();
