(function() {

	'use strict';

	var ideaHubApp = angular
	  .module('ideaHubApp', [
	    // 'ngResource',
	       'ngRoute',
	       'restangular'
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

		// Headers: 'Authorization: Bearer TXwyoVskhA9/utoHWUhPeLgVLYpCeoIKfKNX+0NDHUT5rCZSWToH4rOMIgKcfKNcQckCuYcucizVoWHbInQQLg=='
		// RestangularProvider.setDefaultHeaders({Authorization:'Bearer '+ StorageService.get("access_token")});         
        
        //var tempToken = 'sA5H93yFaHJv/MZJtQKazZ9Vl3ELISi91sl/s3Os/gxhKkVqyOKYF2S7PKiby0uvhE6ObqPUyLOirtfnCBjjIA==';
        //var tempToken = '';
        //RestangularProvider.setDefaultHeaders({Authorization:'Bearer '+tempToken});

        RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
            var responseData = response.data;    
            return responseData;
        });

        RestangularProvider.setResponseExtractor(function (response, operation, what) {
            return response;
        });

	});  



})();