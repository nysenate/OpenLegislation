/** --- Module configuration --- */

var commonModule = angular.module('common', []);
var contentModule = angular.module('content', ['ngRoute', commonModule.name]);
var reportModule = angular.module('report', ['ngRoute', commonModule.name]);

var openApp = angular.module('open', ['ngRoute', 'ngResource', contentModule.name, reportModule.name]);
openApp.constant('appProps', {
    ctxPath: window.ctxPath
});

/** Routing Configuration --- */

openApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {

    /** --- Home --- */
    $routeProvider.when(ctxPath, {
        templateUrl: ctxPath + '/partial/home/landing'
    });
    /** --- Bills --- */
    $routeProvider.when(ctxPath + '/bills', {
        templateUrl: ctxPath + '/partial/content/bill-search'
    });
    $routeProvider.when(ctxPath + '/bills/:session/:printNo', {
        templateUrl: ctxPath + '/partial/content/bill-view'
    });
    /** --- Admin Reports --- */
    $routeProvider.when(ctxPath + '/admin/report', {
        redirectTo: ctxPath + '/admin/report/daybreak'
    });
    $routeProvider.when(ctxPath + '/admin/report/daybreak', {
        templateUrl: ctxPath + '/partial/report/daybreak-report-summary.html',
        controller: 'DaybreakSummaryCtrl'
    });
    $routeProvider.when(ctxPath + '/admin/report/daybreak/:reportDateTime', {
        templateUrl: ctxPath + '/partial/report/daybreak-report-error.html',
        controller: 'DaybreakReportErrorCtrl'
    });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
}]);