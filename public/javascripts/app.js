'use strict';

/**
 * @ngdoc overview
 * @name ngTestApp
 * @description
 * # ngTestApp
 *
 * Main module of the application.
 */
angular.module('ngSimulation', [
    'ngRoute',
    'ngAnimate',
    'angular-flot'
    ])
    .filter('percentage', ['$filter', function ($filter) {
        return function (input, decimals) {
            return $filter('number')(input * 100, decimals) + '%';
        };
    }])
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
