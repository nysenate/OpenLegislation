var adminModule = angular.module('open.admin');

adminModule.factory('RunIntervalAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/spotcheck/run/interval', {
        year: '@year'

    });
}]);

adminModule.factory('RunScrapeQueueAPI',['$resource', function($resource){
    return $resource(adminApiPath + '/scraping/billqueue/:sessionYear/:printNo', {
            sessionYear: '@sessionYear',
            printNo: '@printNo'},
        {'update': {method:'PUT'}
        });
}]);

adminModule.controller('ReportsCtrl', ['$scope', 'RunIntervalAPI', 'RunScrapeQueueAPI', function($scope, RunIntervalAPI, RunScrapeQueueAPI){
    $scope.runInvervalReport = function(year){
        RunIntervalAPI.get({year: year}, function (response) {
            console.log(response);
            window.alert(response.message);
        });
    };

    $scope.addToScrapeQueue = function(sessionYear, printNo){
        RunScrapeQueueAPI.update({sessionYear: sessionYear}, {printNo: printNo}, function(response){
            console.log(response);
            window.alert(response.message + '\n' + JSON.stringify(response.result));
        });
    };

    $scope.years = [
        "2018",
        "2017",
        "2016",
        "2015",
        "2014",
        "2013",
        "2012",
        "2011",
        "2010",
        "2009"
    ];

    $scope.year = "2018";
    $scope.year2 = "2018";
    $scope.sessionYear = "";
    $scope.printNo = "";

}]);

