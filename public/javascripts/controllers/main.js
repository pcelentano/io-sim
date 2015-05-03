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
        self.members = ['Pablo Celentano','Nicolas Schejktman', 'Juan Perez' , 'Some Name'];
        self.items = {};
        self.editing = true;
        self.loading = false;
        self.formData = {};


        self.submit = function() {

            self.editing = false;
            self.loading = true;

            console.log(self.formData);

            var req = {
                method: 'POST',
                url: '/api/simulation',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: self.formData
            };

            $http(req).then(function(response) {
                self.items = response.data;
                console.log(response.data);
                self.loading = false;
            }, function(errResponse) {
                console.error('Error while fetching data');
                self.loading = false;
            });

        };

        self.reset = function() {
            self.editing = true;
            self.loading = false;
            self.formData = {};
        };
    }]);
