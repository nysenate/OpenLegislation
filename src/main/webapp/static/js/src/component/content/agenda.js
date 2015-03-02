var agendaModule = angular.module('open.agenda', ['open.core']);

agendaModule.factory('AgendaListingApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/:year?sort=:sort&limit=:limit&offset=:offset', {
        sessionYear: '@year',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

agendaModule.controller('AgendaCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.setHeaderVisible(true);
    }
]);

agendaModule.controller('AgendaBrowseCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.setHeaderText('Explore NYS Senate Agendas');
    }
]);
