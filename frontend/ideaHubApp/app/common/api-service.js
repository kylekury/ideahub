(function() {


	'use strict';

	angular
	    .module("ideaHubApp")
	    .service("ApiService", ApiService);

	ApiService.$inject = ['Restangular', '$rootScope', '$q'];

	function ApiService(Restangular, $rootScope, $q) {

        //http://169.44.56.200:8080/idea/1
		// URI: /idea/definition
		// Method: GET
		// Headers: 'Authorization: Bearer TXwyoVskhA9/utoHWUhPeLgVLYpCeoIKfKNX+0NDHUT5rCZSWToH4rOMIgKcfKNcQckCuYcucizVoWHbInQQLg=='
		// Accept: application/json;
        this.getDefinition = function  () {
            var definition = Restangular.all('/idea/definition')
			return definition.getList().then(
	            function(response) {
	            	//console.log(response);
	                //return parseResponseDataToArray(response);
	                return response;
	            }, function(error) {
			        return null;
	            }
        	);	
        }


        //Create an idea
		// URI: /idea
		// Method: POST
		// Headers:
		//     Accept: application/json;

		// Output:
		//     {
		//         "id": 1,
		//         "user_id": 1,
		//         "is_private": 0
		//     }

        this.createIdea = function (data) {


        }



		

	}





})();