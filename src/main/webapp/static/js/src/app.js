/** --- Main configuration --- */

var commonModule = angular.module('common', []);
var contentModule = angular.module('content', [commonModule.name]);
var reportModule = angular.module('report', ['ngRoute', commonModule.name]);

var openApp = angular.module('open', ['ngRoute', 'ngResource', contentModule.name, reportModule.name]);
openApp.constant('appProps', {
    ctxPath: window.ctxPath
});

openApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {

    /** --- Bills --- */

    $routeProvider.when(ctxPath + '/bills', {
        templateUrl: ctxPath + '/static/partial/content/bills-home.html',
        controller: 'BillHomeCtrl'
    });

    $routeProvider.when(ctxPath + '/bills/:session/:printNo', {
        templateUrl: ctxPath + '/static/partial/content/bills-home.html',
        controller: 'BillHomeCtrl'
    });

    /** --- Reports --- */

    $routeProvider.when(ctxPath + '/report', {
        redirectTo: ctxPath + '/report/daybreak'
    });

    $routeProvider.when(ctxPath + '/report/daybreak', {
        templateUrl: ctxPath + '/static/partial/report/daybreak-report-summary.html',
        controller: 'DaybreakSummaryCtrl'
    });

    $routeProvider.when(ctxPath + '/report/daybreak/:reportDateTime', {
        templateUrl: ctxPath + '/static/partial/report/daybreak-report-error.html',
        controller: 'DaybreakReportErrorCtrl'
    });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
}]);