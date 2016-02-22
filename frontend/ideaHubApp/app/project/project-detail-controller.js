(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$q', '$scope','Restangular','$routeParams', '$location', '$cookies'];

    function ProjectDetailController($q, $scope, Restangular, $routeParams, $location, $cookies) {

    	console.log("ProjectDetailController");

        var tookenCookie = $cookies.get('ideahub_token');
        if(angular.isUndefined(tookenCookie) || tookenCookie == ""){
            $location.path('/login');
        }

    	$scope.project = {};

        $scope.projectId = $routeParams.projectId;

        console.log($scope.projectId);

        getProjectDetail();
    	
        //      this.getDefinition = function () {
        //          var definition = Restangular.all('/idea/definition')
        // return definition.getList().then(
        //           function(response) {
        //            //console.log(response);
        //               //return parseResponseDataToArray(response);
        //               return response;
        //           }, function(error) {
        //         return null;
        //           }
        //       );  
        //      }


        
    	
        //Get Project
        function getProjectDetail (){

            var projectDeatilRequest = Restangular.all("idea");
             projectDeatilRequest.get($scope.projectId).then(
                function(response) {

                    $scope.project = response;    
                    console.log(response);

                },
                function(error) {
                    
                }
            );
        }




    }

})();
