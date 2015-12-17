var agendaModule = angular.module('open.agenda');

agendaModule.controller('AgendaUpdatesCtrl', ['$scope', '$rootScope', '$location', '$routeParams', 'PaginationModel', 'AgendaAggUpdatesApi',
    function($scope, $rootScope, $location, $routeParams, PaginationModel, AgendaAggUpdatesApi) {

        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.itemsPerPage = 20;

        $scope.curr = {
            fromDate: moment().subtract(30, 'days').startOf('minute').toDate(),
            toDate: moment().startOf('minute').toDate(),
            type: $routeParams.type || 'published',
            sortOrder: $routeParams.sortOrder || 'DESC',
            detail: $routeParams.detail || true
        };

        $scope.agendaUpdates = {
            response: {},
            fetching: false,
            result: {},
            errMsg: ''
        };

        $scope.tabInit = function() {
            $scope.setHeaderText('Agenda Updates');
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
        };

        $scope.getUpdates = function() {
            $scope.agendaUpdates.fetching = true;
            $scope.agendaUpdates.response = AgendaAggUpdatesApi.get({
                from: $scope.curr.fromDate.toISOString(), to: $scope.curr.toDate.toISOString(),
                type: $scope.curr.type, order: $scope.curr.sortOrder, detail: $scope.curr.detail,
                filter: $scope.curr.filter, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()
            }, function() {
                $scope.agendaUpdates.result = $scope.agendaUpdates.response.result;
                $scope.pagination.setTotalItems($scope.agendaUpdates.response.total);
                $scope.agendaUpdates.fetching = false;
            }, function(resp) {
                $scope.agendaUpdates.response.success = false;
                $scope.pagination.setTotalItems(0);
                $scope.agendaUpdates.errMsg = resp.data.message;
                $scope.agendaUpdates.fetching = false;
            });
        };

        $scope.$on('viewChange', function(ev) {
            $scope.getUpdates();
        });

        $scope.$watchCollection('curr', function(n, o) {
            if ($scope.selectedView === 2) {
                $scope.getUpdates();
                $scope.pagination.reset();
            }
        });

        $scope.$watch('pagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage) {
                $scope.getUpdates();
            }
        });

        $scope.init();
    }
]);
