(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$q', '$scope','ApiService','Restangular','DefinitionFactory'];

    function HomeController($q, $scope, ApiService, Restangular, Definition) {

        // {
        // "name": "elevator_pitch",
        // "allow_multiple": false,
        // "metadata": {
        //     "name_text": "Elevator Pitch",
        //     "justification_text": "How will this entice someone to want to know more about your idea?"
        // }
        $scope.definition_list= [];

        ApiService.getDefinition().then(
            function(response){
                $scope.definition_list = parseResponseDataToDefinitionArray(response);
                //console.log($scope.definition_list);
            }
        );

        function parseResponseDataToDefinitionArray(response) {
            var data = [];
            if (response) {
                angular.forEach(response, function(value, key) {
                    data.push(new Definition(value));
                });
            }
            return data;
        }





        

    }

})();
