var billModule = angular.module('open.bill');

/** --- Bill Updates Controller --- */

billModule.controller('BillUpdatesCtrl', ['$scope', '$location', '$routeParams', 'BillAggUpdatesApi', 'PaginationModel',
    function($scope, $location, $routeParams, BillAggUpdatesApi, PaginationModel){

        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.currPage = $routeParams.page || 1;
        $scope.pagination.itemsPerPage = 20;

        $scope.curr = {
            state: 'initial',
            options: {
                fromDate: ($routeParams.fromDate) ? new Date($routeParams.fromDate)
                    : moment().subtract(5, 'days').startOf('minute').toDate(),
                toDate: ($routeParams.toDate) ? new Date($routeParams.toDate) : moment().add(1, 'days').startOf('minute').toDate(),
                type: $routeParams.type || 'published',
                sortOrder: $routeParams.sortOrder || 'desc',
                detail: $routeParams.detail === true,
                filter: $routeParams.filter ||  ''
            },
            billUpdates: {
                response: {},
                total: 0,
                result: {},
                errMsg: ''
            }
        };

        $scope.getUpdates = function() {
            $scope.curr.state = 'searching';
            $scope.curr.billUpdates.response = BillAggUpdatesApi.get({
                from: $scope.toZonelessISOString(moment($scope.curr.options.fromDate)),
                to: $scope.toZonelessISOString(moment($scope.curr.options.toDate)),
                type: $scope.curr.options.type, order: $scope.curr.options.sortOrder, detail: $scope.curr.options.detail,
                filter: $scope.curr.options.filter, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()
            }, function() {
                $scope.curr.billUpdates.total = $scope.curr.billUpdates.response.total;
                $scope.curr.billUpdates.result = $scope.curr.billUpdates.response.result;
                $scope.curr.state = 'searched';
            }, function(resp) {
                $scope.curr.billUpdates.response.success = false;
                $scope.curr.billUpdates.total = 0;
                $scope.curr.billUpdates.errMsg = resp.data.message;
                $scope.curr.state = 'searched';
            });
        };

        $scope.setUrlParams = function() {
            angular.forEach($scope.curr.options, function(paramVal, key) {
                if (paramVal && paramVal instanceof Date) {
                    paramVal = $scope.toZonelessISOString(moment(paramVal));
                }
                $scope.setSearchParam(key, paramVal);
            });
        };

        $scope.onParamChange = function() {
            $scope.setUrlParams();
            $scope.getUpdates();
            $scope.pagination.reset();
        };

        $scope.onPageChange = function(newPage) {
            $scope.setSearchParam('page', newPage);
            $scope.getUpdates();
        };

        $scope.init = function() {
            $scope.getUpdates();
        };

        // Initialize
        $scope.init();

    }]);



