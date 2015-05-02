'use strict';

/**
 * @ngdoc function
 * @name ngTestApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the ngTestApp
 */
angular.module('ngSimulation')
    .controller('MainCtrl', ['$http' , function ($http) {
        var self = this;
        self.items = [];
        $http.get('/api/data').then(function(response) {
            self.items = response.data;
        }, function(errResponse) {
            console.error('Error while fetching notes');
        });
    }]);
