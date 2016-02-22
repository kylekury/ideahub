(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('ExploreController', ExploreController);

    ExploreController.$inject = ['$q', '$scope','Restangular'];

    function ExploreController($q, $scope, Restangular) {

    	$scope.definition_list= [];  
        $scope.recentIdea_list= [];    
        $scope.popularIdea_list= [];
        $scope.idea_list= [];
        $scope.display = {
        	recent: false,
        	popular: false
        }
        getRecentIdeas();
        getPopularIdeas();

        $scope.showRecent = function(event){
        	$scope.display.recent = true;
        	$scope.display.popular = false;
        	$scope.idea_list= $scope.recentIdea_list;
        }

        $scope.showPopular = function($event){
        	$scope.display.recent = false;
        	$scope.display.popular = true;
        	$scope.idea_list= $scope.popularIdea_list;
        }

        //Get Recent
        function getRecentIdeas (){
            var recentIdea = Restangular.all('/idea/recent')
            recentIdea.getList().then(
                function(response) {
                    $scope.recentIdea_list = response;
                    $scope.showRecent();
                }, function(error) {
                    return null;
                }
            );  
        }

         //Get Popular
        function getPopularIdeas (){
            var recentIdea = Restangular.all('/idea/popular')
            recentIdea.getList().then(
                function(response) {
                    $scope.popularIdea_list = response;
                }, function(error) {
                    return null;
                }
            );  
        }

    }

})();
