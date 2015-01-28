/** Routing Configuration --- */

angular.module('open').config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider
        /** --- Home --- */

        .when(ctxPath, {
            templateUrl: ctxPath + '/partial/home/landing'
        })
        .when(ctxPath + '/data', {
            template: 'Data Home Page'
        })

        /** --- Bills --- */

        .when(ctxPath + '/bills', { templateUrl: ctxPath + '/partial/content/bill-search', reloadOnSearch: false })
        .when(ctxPath + '/bills/:session/:printNo', { templateUrl: ctxPath + '/partial/content/bill-view', reloadOnSearch: false })

        /** --- Agendas --- */

        .when(ctxPath + '/agendas/', { template: 'Agendas Page' })
        .when(ctxPath + '/agendas/:year/', { template: 'Agendas Page' })
        .when(ctxPath + '/agendas/:year/:agendaNo', { template: 'Agendas Page' })
        .when(ctxPath + '/agendas/:year/:agendaNo/:committee', { template: 'Agendas Page' })

        /** --- Calendars --- */

        .when(ctxPath + '/calendars', {
            templateUrl: ctxPath + '/partial/content/calendar/calendar-view'
        })
        .when(ctxPath + '/calendars/:year/:calNo', {
            templateUrl: ctxPath + '/partial/content/calendar/calendar-view'
        })

        /** --- Laws --- */
        .when(ctxPath + '/laws', {
            templateUrl: ctxPath + '/partial/content/law-search'
        })

        .when(ctxPath + '/laws/:lawId', {
            templateUrl: ctxPath + '/partial/content/law-view'
        })

        /** --- Transcripts --- */
        .when(ctxPath + '/transcripts', {
            templateUrl: ctxPath + '/partial/content/..'
        })

        /** --- Reports --- */

        .when(ctxPath + '/reports', {
            templateUrl: ctxPath + '/partial/report/daybreak-report-summary'
        })

        /** --- Manage --- */
        .when(ctxPath + '/manage', {
            template: 'Manage Page'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
}]);
