(function() {

	'use strict';

	var ideaHubApp = angular
	  .module('ideaHubApp', [
	    // 'ngResource',
	       'ngRoute',
	    // 'ngSanitize',
	    // 'ngTouch',
	    // 'ngAnimate',
	    // 'ngCookies'
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
		otherwise({
			redirectTo: '/home'
		});
	}]);



})();