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
        self.items = {};
        self.editing = true;
        self.loading = false;
        self.error = false;
        self.formData = {
            simParam: self.simulation
        };

        self.simulations = [
            {
                name : 'Pablo Celentano',
                priority : "Relative",
                tolerance : "Intolerant",
                intolerance : "Total"
            },
            {
                name : 'Juan Perez',
                priority : "Relative",
                tolerance : "Tolerant",
                resumption : "Resumption"
            }
        ];

        self.simulation = self.simulations[0];


        self.submit = function() {

            self.editing = false;
            self.loading = true;
            self.error = false;

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
                self.error = false;
            }, function(errResponse) {
                console.error('Error while fetching data');
                self.reset();
                self.error = true;
            });

        };

        self.reset = function() {
            self.editing = true;
            self.loading = false;
            self.formData = {};
            self.error = false;

        };
    }]);
