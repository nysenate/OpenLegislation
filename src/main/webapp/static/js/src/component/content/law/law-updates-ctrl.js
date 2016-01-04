var lawModule = angular.module('open.law');

/**
 * Law Updates Ctrl
 */
lawModule.controller('LawUpdatesCtrl', ['$scope', '$location', '$routeParams', 'PaginationModel', 'LawFullUpdatesApi',
    function($scope, $location, $routeParams, PaginationModel, LawFullUpdatesApi) {
        function tabInit() {
            $scope.setHeaderVisible(true);
        }

        $scope.curr = {
            fromDate: moment().subtract(30, 'days').startOf('minute').toDate(),
            toDate: moment().startOf('minute').toDate(),
            type: $routeParams.type || 'published',
            sortOrder: $routeParams.sortOrder || 'desc',
            detail: $routeParams.detail || true
        };

        $scope.lawUpdates = {
            response: {},
            fetching: false,
            result: {},
            errMsg: ''
        };

        $scope.$on('viewChange', function() {
            tabInit();
        });

        $scope.pagination = angular.extend({}, PaginationModel);

        $scope.init = function() {
            tabInit();
            $scope.getUpdates();
        };

        $scope.getUpdates = function() {
            $scope.lawUpdates.fetching = true;
            $scope.lawUpdates.response = LawFullUpdatesApi.get({
                from: $scope.curr.fromDate.toISOString(), to: $scope.curr.toDate.toISOString(),
                type: $scope.curr.type, order: $scope.curr.sortOrder, detail: $scope.curr.detail,
                filter: $scope.curr.filter, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()
            }, function() {
                $scope.lawUpdates.result = $scope.lawUpdates.response.result;
                $scope.pagination.setTotalItems($scope.lawUpdates.response.total);
                $scope.lawUpdates.fetching = false;
            }, function(resp) {
                $scope.lawUpdates.response.success = false;
                $scope.pagination.setTotalItems(0);
                $scope.lawUpdates.errMsg = resp.data.message;
                $scope.lawUpdates.fetching = false;
            });
        };

        $scope.onParamChange = function() {
            $scope.pagination.reset();
            $scope.getUpdates();
        };

        $scope.$watch('pagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage) {
                $scope.getUpdates();
            }
        });

        $scope.init();
    }
]);





