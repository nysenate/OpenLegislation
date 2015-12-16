var apiModule = angular.module('open.api', []);

/** --- Bill API --- */

apiModule.factory('BillListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:sessionYear?sort=:sort&limit=:limit&offset=:offset', {
        sessionYear: '@sessionYear',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

apiModule.factory('BillSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/search/?term=:term&sort=:sort&limit=:limit&offset=:offset', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

apiModule.factory('BillAggUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/updates/:from/:to?order=:order&type=:type&filter=:filter&limit=:limit&offset=:offset&summary=true', {
        from: '@from',
        to: '@to',
        type: '@type',
        order: '@order',
        filter: '@filter',
        limit: '@limit',
        offset: '@offset'
    });
}]);

apiModule.factory('BillGetApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo?view=:view&version=:version', {
        session: '@session',
        printNo: '@printNo',
        view: '@view',
        version: '@version'
    });
}]);

apiModule.factory('BillUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo/updates?order=:order&filter=:filter&limit=:limit&offset=:offset', {
        session: '@session',
        printNo: '@printNo',
        order: '@order',
        filter: '@filter',
        limit: '@limit',
        offset: '@offset'
    });
}]);

apiModule.factory('BillDiffApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo/diff/:version1/:version2/', {
        session: '@session',
        printNo: '@printNo',
        version1: '@version1',
        version2: '@version2'
    });
}]);

/** --- Calendar API --- */


apiModule.factory('CalendarViewApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year/:calNo', {
        year: '@year',
        calNo: '@calNo'
    });
}]);

apiModule.factory('CurrentCalendarIdApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year?order=DESC&limit=1', {
        year: '@year'
    });
}]);

apiModule.factory('CalendarIdsApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year', {
        year: '@year'
    });
}]);

apiModule.factory('CalendarSearchApi', ['$resource', function ($resource) {
    return $resource(apiPath + '/calendars/search', {});
}]);

apiModule.factory('CalendarUpdatesApi', ['$resource', function ($resource) {
    return $resource(apiPath + '/calendars/:year/:calNo/updates', {
        year: '@year',
        calNo: '@calNo'
    });
}]);

apiModule.factory('CalendarFullUpdatesApi', ['$resource', function ($resource) {
    return $resource(apiPath + '/calendars/updates/:fromDateTime/:toDateTime/', {
        fromDateTime: '@fromDateTime', toDateTime: '@toDateTime'
    });
}]);

/** --- Law API --- */

apiModule.factory('LawListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws');
}]);

apiModule.factory('LawTreeApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId?fromLocation=:fromLocation&depth=:depth', {
        lawId: '@lawId',
        fromLocation: '@fromLocation',
        depth: '@depth'
    });
}]);

apiModule.factory('LawDocApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/:docId/', {
        lawId: '@lawId',
        docId: '@docId'
    });
}]);

apiModule.factory('LawFullSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/search?term=:term', {
        term: '@term'
    });
}]);

apiModule.factory('LawVolumeSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/search?term=:term', {
        lawId: '@lawId',
        term: '@term'
    });
}]);

apiModule.factory('LawFullUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/updates/:from/:to/', {
        from: '@from',
        to: '@to',
        type: '@type',
        order: '@order'
    });
}]);

apiModule.factory('LawVolumeUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/updates', {
        lawId: '@lawId',
        order: '@order'
    });
}]);
