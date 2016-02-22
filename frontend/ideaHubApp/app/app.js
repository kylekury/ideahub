(function() {

	'use strict';

	var ideaHubApp = angular
	  .module('ideaHubApp', [
	    // 'ngResource',
	       'ngRoute',
	       'restangular',
	    // 'ngSanitize',
	    // 'ngTouch',
	    // 'ngAnimate',
	       'ngCookies'
	  ]);


	ideaHubApp.config(['$routeProvider', function($routeProvider) {

		$routeProvider.
			when('/login', {
			templateUrl: 'app/login/login.html',
			controller: 'LoginController'
		}).
			when('/home', {
			templateUrl: 'app/home/home.html',
			controller: 'HomeController'
		}).
			when('/explore', {
			templateUrl: 'app/explore/explore.html',
			controller: 'HomeController'
		}).
			when('/profile', {
			templateUrl: 'app/profile/profile.html',
			controller: 'ProfileController'
		}).
			when('/project', {
			templateUrl: 'app/project/project.html',
			controller: 'ProjectController'
		}).
			when('/dashboard', {
			templateUrl: 'app/dashboard/dashboard.html',
			controller: 'DashboardController'
		}).
		otherwise({
			redirectTo: '/home'
		});
	}]);

	ideaHubApp.config(function(RestangularProvider) {    

	    var apiBaseUrl = "http://169.44.56.200:8080";
		RestangularProvider.setBaseUrl(apiBaseUrl);    

        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var responseData = response.data;    
            return responseData;
        });

        RestangularProvider.setResponseExtractor(function (response, operation, what) {
            return response;
        });

	});  

	ideaHubApp.run(['$rootScope', '$cookies', 'Restangular',
	    function ($rootScope, $cookies, Restangular) {

	    	var tookenCookie = $cookies.get('ideahub_token');
	    	console.log("======tookenCookie=========");
	    	console.log(tookenCookie);
	    	console.log("======ENDtookenCookie=========");
    		Restangular.setDefaultHeaders({Authorization:'Bearer '+tookenCookie});

	}]);

})();