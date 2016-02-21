(function() {

	'use strict';

    angular
        .module('ideaHubApp')
        .controller('ExploreController', HomeController);

    HomeController.$inject = ['$q', '$scope'];

    function HomeController($q, $scope) {

    	console.log("HomeController");


        



    }

})();
