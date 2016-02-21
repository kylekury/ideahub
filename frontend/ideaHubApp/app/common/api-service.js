(function() {


	'use strict';

	angular
	    .module("ideaHubApp")
	    .service("ApiService", ApiService);

	ApiService.$inject = ['Restangular', '$rootScope', '$q'];

	function ApiService(Restangular, $rootScope, $q) {

        // http://169.44.56.200:8080/idea/1
		// URI: /idea/definition
		// Method: GET
		// Headers: 'Authorization: Bearer TXwyoVskhA9/utoHWUhPeLgVLYpCeoIKfKNX+0NDHUT5rCZSWToH4rOMIgKcfKNcQckCuYcucizVoWHbInQQLg=='
		// Accept: application/json;

		// function getRecentIedas (){
		//           ApiService.getRecentIdeas().then(
		//               function(response){
		//                   $scope.recentIdea_list = response;
		//               }
		//           );
		//       }

   //      this.getDefinition = function () {
   //          var definition = Restangular.all('/idea/definition')
			// return definition.getList().then(
	  //           function(response) {
	  //           	//console.log(response);
	  //               //return parseResponseDataToArray(response);
	  //               return response;
	  //           }, function(error) {
			//         return null;
	  //           }
   //      	);	
   //      }


   //      //http://169.44.56.200:8080/idea/recent
   //      this.getRecentIdeas = function () {
   //      	var recentIdea = Restangular.all('/idea/recent')
			// return recentIdea.getList().then(
	  //           function(response) {
	  //               return response;
	  //           }, function(error) {
			//         return null;
	  //           }
   //      	);	
   //      }





		

	}





})();