'use strict';

/**
 * @ngdoc overview
 * @name ngTestApp
 * @description
 * # ngTestApp
 *
 * Main module of the application.
 */
angular.module('ngSimulation', ['ngRoute'])
    .config(['$routeProvider',function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/assets/views/main.html',
                controller: 'MainCtrl as mainCtrl'
            })
            .when('/about', {
                templateUrl: '/assets/views/about.html'
                //controller: 'MainCtrl as mainCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);
