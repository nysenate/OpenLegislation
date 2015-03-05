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


agendaModule.controller('AgendaCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.setHeaderVisible(true);
    }
]);

agendaModule.controller('AgendaSearchCtrl', ['$scope', '$location', '$route',
    function($scope, $location, $route) {
        $scope.setHeaderText('Search Senate Agendas');
    }
]);

agendaModule.controller('AgendaBrowseCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.setHeaderText('Browse Senate Agendas');
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

        $scope.generateVoteLookup = function(){
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

        $scope.test = function(open) {
            console.log(open);
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