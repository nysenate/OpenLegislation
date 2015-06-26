
/** --- Open Mismatches Controller --- */

angular.module('open.spotcheck')
    .controller('SpotcheckMismatchCtrl', ['$scope', '$filter', '$routeParams', 'SpotcheckOpenMismatchAPI',
function ($scope, $filter, $routeParams, OpenMMAPI) {

    $scope.defaultLimit = 20;
    $scope.defaultOrderBy = 'OBSERVED_DATE';
    $scope.defaultOrder = 'DESC';
    $scope.reverseOrder = true;

    $scope.params = {
        reportType: "unselected",
        useObservedAfter: false,
        observedAfter: moment().subtract(1, 'months').toDate(),
        resolvedShown: false,
        ignoredShown: false,
        limit: $scope.defaultLimit,
        offset: 1,
        orderBy: $scope.defaultOrderBy,
        order: $scope.defaultOrder,
        mismatchType: []
    };

    $scope.requestCount = 0;
    $scope.lastReceived = 0;

    $scope.typeFilter = {all: false, types: {}};
    $scope.refTypeMismatchMap = {};
    $scope.tableData = [];
    $scope.total = 0;

    $scope.init = function(refTypeMismatchMap) {
        $scope.refTypeMismatchMap = refTypeMismatchMap;
        for (var param in $routeParams) {
            if ($scope.params.hasOwnProperty(param)) {
                var value = $routeParams[param];
                if (param === 'observedAfter') {
                    $scope.params.useObservedAfter = true;
                    $scope.params.observedAfter = moment(value).toDate();
                } else if ((typeof $scope.params[param]) === "boolean") {
                    $scope.params[param] = Boolean(value);
                }else {
                    $scope.params[param] = value;
                }
            }
        }
        $scope.initializeTypeFilter();
    };

    $scope.setSearchParams = function() {
        var p = $scope.params;
        $scope.setSearchParam('reportType', p.reportType, p.reportType !== 'unselected');
        $scope.setSearchParam('observedAfter', moment(p.observedAfter).format('YYYY-MM-DD'), p.useObservedAfter);
        $scope.setSearchParam('resolvedShown', p.resolvedShown, p.resolvedShown);
        $scope.setSearchParam('ignoredShown', p.ignoredShown, p.ignoredShown);
        $scope.setSearchParam('limit', p.limit, p.limit !== $scope.defaultLimit);
        $scope.setSearchParam('offset', p.offset, p.offset > 1);
        $scope.setSearchParam('orderBy', p.orderBy, p.orderBy !== $scope.defaultOrderBy);
        $scope.setSearchParam('order', p.order, p.order !== $scope.defaultOrder);
    };

    var mismatchOrderByMap = {
        observed: 'OBSERVED_DATE',
        key: 'CONTENT_KEY',
        type: 'MISMATCH_TYPE',
        status: 'STATUS'
    };

    $scope.mismatchTablePipe = function(tableState) {
        if (!$scope.tableState) {
            $scope.tableState = tableState;
            tableState.pagination.number = $scope.params.limit;
        }
        $scope.params.limit  = $scope.resultsPerPage = tableState.pagination.number || $scope.params.limit;
        $scope.params.offset = (tableState.pagination.start || 0) + 1;
        $scope.params.orderBy = mismatchOrderByMap[tableState.sort.predicate || $scope.params.limit];
        $scope.params.order = tableState.sort.reverse === true ? "ASC" : "DESC";
    };

    function getParams() {
        var params = angular.copy($scope.params);
        if (params.useObservedAfter) {
            var observedAfter = moment(params.observedAfter);
            if (observedAfter.isValid()) {
                params.observedAfter = $scope.toZonelessISOString(observedAfter);
            } else {
                params.observedAfter = observedAfter._i;
            }
        } else {
            delete params.observedAfter
        }
        delete params.useObservedAfter;
        return params;
    }

    $scope.openDetailWindow = function(mismatchRow) {
        $scope.showMismatchDetails(mismatchRow, null);
    };

    $scope.mismatchCompareVal = [function(row) {
        var sortVal;
        switch ($scope.query.orderBy) {
            case 'OBSERVED_DATE': sortVal = row.observed; break;
            case 'CONTENT_KEY': sortVal = $scope.getContentId($scope.query.refType, row.key); break;
            case 'MISMATCH_TYPE': sortVal = row.type; break;
            case 'STATUS': sortVal = row.status; break;
        }
        return sortVal;
    }, function(row) {
        return $scope.getContentId($scope.query.refType, row.key);
    }];

    $scope.getOpenMismatches = function() {
        $scope.parameterError = $scope.requestError = false;
        if ($scope.params.reportType !== "unselected") {
            var params = getParams();
            var reqId = ++$scope.requestCount;
            OpenMMAPI.get(params, function (response) {
                    // Do not set data if the received request was made before the latest received
                    if (reqId > $scope.lastReceived) {
                        $scope.query = response.query;
                        $scope.tableState.pagination.numberOfPages =
                            response.limit > 0 ? Math.ceil(response.total / response.limit) : 1;
                        $scope.tableData = $scope.extractMismatchRows(response.observations, $scope.query.refType);
                        $scope.total = response.total;
                        $scope.reverseOrder = $scope.query.order === 'DESC';
                        $scope.lastReceived = reqId;
                        console.log('got it');
                    }
                }, function (response) {
                    if (reqId > $scope.lastReceived) {
                        console.log('uh oh:', response);
                        $scope.tableData = [];
                        $scope.lastReceived = reqId;
                        if (response.status === 400 && response.data.errorCode === 1) {
                            $scope.parameterError = true;
                            $scope.parameterErrorVal = response.data.errorData.parameterConstraint.name;
                            $scope.invalidParamDialog(response);
                        } else {
                            $scope.requestError = true;
                        }
                    }
                });
        }
    };

    $scope.initializeTypeFilter = function () {
        $scope.typeFilter.types = {};
        var mismatchTypes = $scope.refTypeMismatchMap[$scope.params.reportType] || [];
        for (var i in mismatchTypes) {
            $scope.typeFilter.types[mismatchTypes[i]] = $scope.typeFilter.all;
        }
    };

    $scope.applyTypeFilterAll = function() {
        for(var type in $scope.typeFilter.types) {
            $scope.typeFilter.types[type] = $scope.typeFilter.all;
        }
    };

    $scope.$watchCollection('typeFilter.types', function () {
        var allowedTypes = [];
        for(var type in $scope.typeFilter.types) {
            if ($scope.typeFilter.types[type]) {
                allowedTypes.push(type);
            } else {
                $scope.typeFilter.all = false;
            }
        }
        $scope.params.mismatchType = allowedTypes;
    });

    $scope.$watchCollection('params', function (newParams, oldParams) {
        var paramsChanged = false;

        // Reset the offset if anything but the offset changed
        if (newParams.offset === oldParams.offset && oldParams.offset !== 1) {
            paramsChanged = true;
            $scope.params.offset = 1;
            $scope.tableState.pagination.start = 0;
        }
        if (newParams.reportType !== oldParams.reportType) {
            paramsChanged = true;
            $scope.tableData = [];
            $scope.initializeTypeFilter();
        }
        // If the params were modified/ will be modified on watch, wait for the next watch to call API
        if (!paramsChanged){
            $scope.setSearchParams();
            $scope.getOpenMismatches();
        }
    });
}]);
