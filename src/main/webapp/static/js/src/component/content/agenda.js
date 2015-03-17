var agendaModule = angular.module('open.agenda', ['open.core']);

agendaModule.factory('AgendaListingApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/:year?sort=:sort&limit=:limit&offset=:offset', {
        sessionYear: '@year',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

agendaModule.factory('AgendaGetApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/:year/:agendaNo/', {
        year: '@year',
        agendaNo: '@agendaNo'
    });
}]);

agendaModule.controller('AgendaCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$route',
    function($scope, $rootScope, $routeParams, $location, $route) {
        $scope.setHeaderVisible(true);
        $scope.selectedView = (parseInt($routeParams.view, 10) || 0) % 3;

        /** Watch for changes to the current view. */
        $scope.$watch('selectedView', function(n, o) {
            if (n !== o && $location.search().view !== n) {
                $location.search('view', $scope.selectedView);
                $scope.$broadcast('viewChange', $scope.selectedView);
            }
        });

        $scope.$on('$locationChangeSuccess', function() {
            $scope.selectedView = $location.search().view || 0;
        });


    }
]);

agendaModule.controller('AgendaSearchCtrl', ['$scope', '$location', '$route', '$routeParams',
    function($scope, $location, $route, $routeParams) {
        $scope.tabInit = function() {
            $scope.setHeaderText('Search Senate Agendas');
        };

        $scope.agendaSearch = {
            searched: false,
            term: $routeParams.search || '',
            response: {},
            result: [],
            error: false
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
        };

        $scope.init();
    }
]);

agendaModule.controller('AgendaBrowseCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.tabInit = function() {
            $scope.setHeaderText('Browse Senate Agendas');
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
        };

        $scope.init();
    }
]);

agendaModule.controller('AgendaUpdatesCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.tabInit = function() {
            $scope.setHeaderText('View Senate Agenda Updates');
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
        };

        $scope.init();
    }
]);

agendaModule.controller('AgendaViewCtrl', ['$scope', '$location', '$routeParams', 'AgendaGetApi',
    function($scope, $location, $routeParams, AgendaGetApi) {

        // Stores the agenda object from the response
        $scope.agenda = null;

        $scope.init = function() {
            $scope.year = $routeParams.year;
            $scope.no = $routeParams.agendaNo;
            $scope.votes = {};
            $scope.response = AgendaGetApi.get({year: $scope.year, agendaNo: $scope.no}, function(){
                $scope.agenda = $scope.response.result;
                $scope.setHeaderText('Agenda ' + $scope.agenda.id.number + ' - ' + $scope.agenda.id.year);
                $scope.generateVoteLookup();
            });
        };

        // A vote-lookap map is generated to make it easier to display vote information in the template.
        $scope.generateVoteLookup = function() {
            angular.forEach($scope.agenda.committeeAgendas.items, function(commAgenda) {
                angular.forEach(commAgenda.addenda.items, function(commAddendum) {
                    if (commAddendum.hasVotes === true) {
                        angular.forEach(commAddendum.voteInfo.votesList.items, function(billVote) {
                            $scope.votes[billVote.bill.basePrintNo] = billVote;
                        });
                    }
                })
            });
        };

        /** Initialize */
        $scope.init();
    }
]);

agendaModule.filter('agendaActionFilter', ['$filter', function($filter) {
    return function(input) {
        switch (input) {
            case 'FIRST_READING': return 'Sent to First Reading';
            case 'THIRD_READING': return 'Sent to Third Reading';
            case 'REFERRED_TO_COMMITTEE': return 'Referred to Committee';
            case 'DEFEATED': return 'Defeated';
            case 'RESTORED_TO_THIRD': return 'Restored to Third Reading';
            case 'SPECIAL': return 'Special Action';
        }
        return 'Unknown';
    }
}]);