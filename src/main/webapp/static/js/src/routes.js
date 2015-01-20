/** Routing Configuration --- */

angular.module('open').config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {

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
    /** --- Agendas --- */
    $routeProvider.when(ctxPath + '/agendas', {
        templateUrl: ctxPath + '/partial/content/..'
    });
    /** --- Calendars --- */
    $routeProvider.when(ctxPath + '/calendars', {
        templateUrl: ctxPath + '/partial/content/calendar/calendar-view'
    });
    $routeProvider.when(ctxPath + '/calendars/:year/:calNo', {
        templateUrl: ctxPath + '/partial/content/calendar/calendar-view'
    });
    /** --- Laws --- */
    $routeProvider.when(ctxPath + '/laws', {
        templateUrl: ctxPath + '/partial/content/law-search'
    });
    $routeProvider.when(ctxPath + '/laws/:lawId', {
        templateUrl: ctxPath + '/partial/content/law-view'
    });
    /** --- Transcripts --- */
    $routeProvider.when(ctxPath + '/transcripts', {
        templateUrl: ctxPath + '/partial/content/..'
    });
    /** --- Admin Reports --- */
    $routeProvider.when(ctxPath + '/admin/report', {
        redirectTo: ctxPath + '/admin/report/daybreak'
    });
    $routeProvider.when(ctxPath + '/admin/report/daybreak', {
        templateUrl: ctxPath + '/partial/report/daybreak-report-summary',
        controller: 'DaybreakSummaryCtrl'
    });
    $routeProvider.when(ctxPath + '/admin/report/daybreak/:reportDateTime', {
        templateUrl: ctxPath + '/partial/report/daybreak-report-error',
        controller: 'DaybreakReportErrorCtrl'
    });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
}]);
