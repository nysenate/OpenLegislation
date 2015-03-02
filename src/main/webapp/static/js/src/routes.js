/** Routing Configuration --- */

angular.module('open').config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider

        /** --- Bills --- */

        .when(ctxPath + '/bills', { templateUrl: ctxPath + '/partial/content/bill/bill-search', reloadOnSearch: false })
        .when(ctxPath + '/bills/:session/:printNo', { templateUrl: ctxPath + '/partial/content/bill/bill-view', reloadOnSearch: false })

        /** --- Agendas --- */

        .when(ctxPath + '/agendas/', { templateUrl: ctxPath + '/partial/content/agenda/agenda-search' })
        .when(ctxPath + '/agendas/:year/', { templateUrl: ctxPath + '/partial/content/agenda/agenda-search' })
        .when(ctxPath + '/agendas/:year/:agendaNo', { templateUrl: ctxPath + '/partial/content/agenda/agenda-view' })
        .when(ctxPath + '/agendas/:year/:agendaNo/:committee', { templateUrl: ctxPath + '/partial/content/agenda/agenda-view' })

        /** --- Calendars --- */

        .when(ctxPath + '/calendars', { templateUrl: ctxPath + '/partial/content/calendar/calendar' })
        .when(ctxPath + '/calendars/:year/:calNo', { templateUrl: ctxPath + '/partial/content/calendar/calendar', reloadOnSearch: false })

        /** --- Laws --- */

        .when(ctxPath + '/laws', { templateUrl: ctxPath + '/partial/content/law/law-search' })
        .when(ctxPath + '/laws/:lawId', { templateUrl: ctxPath + '/partial/content/law/law-view', reloadOnSearch: false })

        /** --- Transcripts --- */

        .when(ctxPath + '/transcripts', { templateUrl: ctxPath + '/partial/content/transcript-list'})
        .when(ctxPath + '/transcripts/session/:filename', { templateUrl: ctxPath + '/partial/content/session-transcript-view'})
        .when(ctxPath + '/transcripts/hearing/:filename', { templateUrl: ctxPath + '/partial/content/hearing-transcript-view'})

        /** --- Reports --- */

        .when(ctxPath + '/admin/report/daybreak', { templateUrl: ctxPath + '/partial/report/daybreak', reloadOnSearch: false })

        /** --- Admin --- */

        .when(ctxPath + '/admin', {
            template: 'Manage Page'
        })

        .when(ctxPath + '/admin/account', {
            templateUrl: ctxPath + '/partial/admin/account',
            reloadOnSearch: false
        })

        /** --- Home Page --- */

        .otherwise({
            templateUrl: ctxPath + '/partial/home/landing'
        });

    $locationProvider.html5Mode(true);
    $locationProvider.hashPrefix('!');
}]);
