var apiModule = angular.module('open.api', []);

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