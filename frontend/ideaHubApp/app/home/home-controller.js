(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$q', '$scope','Restangular'];

    function HomeController($q, $scope, Restangular) {


        //Initialize
        $scope.definition_list= [];  
        $scope.recentIdea_list= [];    
        $scope.popularIdea_list= [];             
        
        getRecentIedas();
        getPopularIedas();
      //getIdeas ();
        
        //Get Recent
        function getRecentIedas (){
            var recentIdea = Restangular.all('/idea/recent')
            recentIdea.getList().then(
                function(response) {
                    $scope.recentIdea_list = response;
                }, function(error) {
                    return null;
                }
            );  
        }
        
         //Get Recent
        function getPopularIedas (){
            var recentIdea = Restangular.all('/idea/popular')
            recentIdea.getList().then(
                function(response) {
                    $scope.popularIdea_list = response;
                }, function(error) {
                    return null;
                }
            );  
        }



        // {
        // "name": "elevator_pitch",
        // "allow_multiple": false,
        // "metadata": {
        //     "name_text": "Elevator Pitch",
        //     "justification_text": "How will this entice someone to want to know more about your idea?"
        // }
        function getIdeas (){
            var definition = Restangular.all('/idea/definition')
            definition.getList().then(
                function(response) {
                    //console.log(response);
                    //return parseResponseDataToArray(response);
                    $scope.definition_list = response;

                }, function(error) {
                    return null;
                }
            );  
        }

    }

})();
