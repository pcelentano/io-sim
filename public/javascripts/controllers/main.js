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
            simParam: self.simulation,
            simData : {
                time : 3000
            }
        };

        self.chartData = [{ data: [], yaxis: 1, label: "Queue Length" }];
        self.myChartOptions = {

            lines: {
                show: true,
                steps: true
            }
        };

        self.eventsToggle = function(){
            if(self.formData.simData.withEvents){
                self.formData.simData.time = 10;
            } else {
                self.formData.simData.time = 3000;
            }
        };

        self.simulations = [
            {
                name : 'Pablo Celentano',
                priority : "Relative",
                tolerance : "Intolerant",
                intolerance : "Total",
                resumption : undefined,
                url : "chelenSimulation"

            },
            {
                name : 'Martin Gutierrez',
                priority : "Total",
                tolerance : "Tolerant",
                resumption : "Resumption",
                intolerance : undefined,
                url : "guteSimulation"
            },
            {
                name: 'Juan D. Llorente',
                priority : "None",
                tolerance : undefined,
                resumption : undefined,
                intolerance : undefined,
                url : "mingoSimulation"
            }
        ];

        self.simulation = {};
        angular.copy(self.simulations[1], self.simulation);
        self.disableSubmit = false;

        self.populateChats = function(){
            self.chartData[0].data = [];
            angular.forEach(self.items.events , function(e){
                self.chartData[0].data.push([e.initTime , e.queueLength]);
            });
        };


        self.submit = function() {

            self.editing = false;
            self.loading = true;
            self.error = false;

            console.log(self.formData);

            var req = {
                method: 'POST',
                url: '/api/simulation/' + self.simulation.url,
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
                self.populateChats();
            }, function(errResponse) {
                console.error('Error while fetching data');
                self.reset();
                self.error = true;
            });

        };

        self.reset = function() {
            self.editing = true;
            self.loading = false;
            self.formData = {
                simData : {
                    time : 3000
                }
            };
            self.error = false;

        };

        self.selectSim = function(){
            if(self.simulation.priority=='None'){
                self.simulation.intolerance = undefined;
                self.simulation.resumption = undefined;
                self.simulation.tolerance = undefined;

            }
            if(self.simulation.tolerance == 'Tolerant') self.simulation.intolerance = undefined;
            else if(self.simulation.tolerance == 'Intolerant') self.simulation.resumption = undefined;


            for (var i = 0; i < self.simulations.length; i++){
                var sim = self.simulations[i];
                if (checkEquals(sim)){
                    angular.copy(sim, self.simulation);
                    self.disableSubmit = false;
                    return;
                }
            }
            self.disableSubmit = true;
        };

        function checkEquals(simulation) {
            return simulation.priority == self.simulation.priority && simulation.tolerance == self.simulation.tolerance &&
                simulation.intolerance == self.simulation.intolerance && simulation.resumption == self.simulation.resumption;
        }
    }]);


