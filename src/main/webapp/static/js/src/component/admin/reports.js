var adminModule = angular.module('open.admin');

adminModule.factory('RunIntervalAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/spotcheck/run/interval', {
        year: '@year'
    });
}]);

adminModule.factory('RunCalendarIntervalAPI', ['$resource', function($resource){
    return $resource(adminApiPath + '/spotcheck/run/interval/calendar', {
        year: '@year'
    });
}]);

adminModule.factory('RunAgendaIntervalAPI', ['$resource', function($resource){
    return $resource(adminApiPath + '/spotcheck/run/interval/agenda', {
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


adminModule.controller('ReportsCtrl', ['$scope', '$mdDialog', 'RunIntervalAPI', 'RunCalendarIntervalAPI',
    'RunAgendaIntervalAPI','RunScrapeQueueAPI', function($scope, $mdDialog, RunIntervalAPI, RunCalendarIntervalAPI,
                                                         RunAgendaIntervalAPI, RunScrapeQueueAPI){
    $scope.runReports = function(defaultReportType,year){
        if(defaultReportType == "Both"){
            $scope.runIntervalReport(year);
        }
        else if(defaultReportType == "Calendar"){
            $scope.runCalendarIntervalReport(year);
        }
        else{
            $scope.runAgendaIntervalReport(year);
        }
    };

    $scope.runIntervalReport = function (year) {
        RunIntervalAPI.get({year: year}, function(response){
            console.log(response);
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title(response.message)
                    .ariaLabel('Alert Dialog Demo')
                    .ok('Got it!')
            );
        });
    };

    $scope.runCalendarIntervalReport = function(year){
        RunCalendarIntervalAPI.get({year: year}, function(response){
            console.log(response);
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title(response.message)
                    .ariaLabel('Alert Dialog Demo')
                    .ok('Got it!')
            );
        });
    };

    $scope.runAgendaIntervalReport = function(year){
        RunAgendaIntervalAPI.get({year: year}, function(response){
            console.log(response);
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title(response.message)
                    .ariaLabel('Report Alert')
                    .ok('Ok')
            );
        });
    };

    $scope.addToScrapeQueue = function(sessionYear, printNo){
        RunScrapeQueueAPI.update({sessionYear: sessionYear}, {printNo: printNo}, billSuccess, billFailure)
    };

    $scope.deleteFromScrapeQueue = function(sessionYear, printNo){
        RunScrapeQueueAPI.delete({sessionYear: sessionYear}, {printNo: printNo}, function(response){
            console.log(response);
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title(response.message.toUpperCase())
                    .textContent(JSON.stringify(response.result, null, '\t'))
                    .ariaLabel('Delete Queue Alert')
                    .ok('Ok')
            );
        });
    };

    function billSuccess(response) {
        console.log(response);
        $mdDialog.show(
            $mdDialog.alert()
                .clickOutsideToClose(true)
                .title(response.message.toUpperCase())
                .textContent("Added Bill " + response.result.basePrintNoStr + " to Scrape Queue")
                .ariaLabel('Add Queue Alert')
                .ok('Ok')
        );
    }

    function billFailure(response){
        console.log(response);
        $mdDialog.show(
            $mdDialog.alert()
                .clickOutsideToClose(true)
                .title(response.data.message.toUpperCase())
                .textContent("The Response Type Given was " + $scope.printNo + " the correct formatting is " +
                    response.data.errorData.parameterConstraint.constraint)
                .ariaLabel('Add Queue Alert')
                .ok('Ok')
        );
    }


    $scope.years = [];
    $scope.sessionYears = [];
    $scope.minYear = 2009;
    $scope.maxYear =  new Date().getFullYear();
    for(i = $scope.minYear; i<=$scope.maxYear; i++){
        $scope.years.unshift(i.toString());
    }
    for(i = $scope.minYear; i<=$scope.maxYear; i+=2){
        $scope.sessionYears.unshift(i.toString());
    }
    $scope.reportType = ["Both", "Calendar", "Agenda"];
    $scope.defaultReportType = "Both";


    $scope.year = $scope.years[0];
    $scope.sessionYear = $scope.sessionYears[0];
    $scope.dSessionYear = $scope.sessionYears[0];
    $scope.printNo = "";

}]);

