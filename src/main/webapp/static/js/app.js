/** --- Main configuration --- */

var commonModule = angular.module('common', []);
var reportModule = angular.module('report', ['ngRoute', commonModule.name]);

var openApp = angular.module('open', ['ngRoute', 'ngResource', reportModule.name]);
openApp.constant('appProps', {
    ctxPath: window.ctxPath
});

openApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
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


