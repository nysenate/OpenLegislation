var contentModule = angular.module('content');

contentModule.factory('BillListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:sessionYear', {
        sessionYear: '@sessionYear'
    });
}]);

contentModule.factory('BillSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/search/?term=:term&sort=:sort&limit=:limit&offset=:offset', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

contentModule.factory('BillViewApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo', {
        session: '@session',
        printNo: '@printNo'
    });
}]);

/** --- Parent Bill Controller --- */

contentModule.controller('BillCtrl', ['$scope', '$route', function($scope, $route) {

}]);

/** --- Bill Search Controller --- */

contentModule.controller('BillSearchCtrl', ['$scope', '$filter', '$routeParams', '$location',
                                            'BillListingApi', 'BillSearchApi',
    function($scope, $filter, $routeParams, $location, BillListing, BillSearch) {
    $scope.searchTerm = '';
    $scope.sort = '';
    $scope.billResults = {};
    $scope.billViewResult = null;
    $scope.billView = null;
    $scope.totalResults = 0;
    $scope.limit = 10;
    $scope.offset = 1;
    $scope.currentPage = 1;
    $scope.performedSearch = false;

    $scope.init = function() {
        $scope.searchTerm = $routeParams.search;
        //$scope.offset = $scope.computeOffset($routeParams.page);
        $scope.doSearch();
    };

    $scope.$watch('currentPage', function(newPage, oldPage) {
        if (newPage != oldPage) {
            $scope.doSearch();
        }
    });

    $scope.isValidSearchTerm = function() {
        return $scope.searchTerm != null && $scope.searchTerm.trim() != '';
    };

    $scope.search = function() {
        $location.search("search", $scope.searchTerm);
    };

    $scope.doSearch = function() {
        if ($scope.isValidSearchTerm()) {
            $scope.billResults = BillSearch.get({
                term: $scope.searchTerm, sort: $scope.sort, limit: $scope.limit, offset: $scope.computeOffset($scope.currentPage)},
                function() {
                    $scope.totalResults = $scope.billResults.total;
                    $scope.performedSearch = true;
                    setTimeout(function() {$(".bill-result-anim").addClass("show")}, 0);
                });
        }
    };

    /**
     * Returns a formatted, all lower case string representing the latest milestone status.
     *
     * @param milestones
     * @returns {string}
     */
    $scope.getMilestoneDesc = function(milestones) {
        if (milestones && milestones.size > 0) {
            var milestone = milestones.items.slice(-1)[0];
            var desc = "";
            switch (milestone.statusType) {
                case "IN_SENATE_COMM":
                    desc = "In Senate " + milestone.committeeName + " Committee";
                    break;
                case "IN_ASSEMBLY_COMM":
                    desc = "In Assembly " + milestone.committeeName + " Committee";
                    break;
                case "SENATE_FLOOR":
                    desc = "On Senate Floor as Calendar No: " + milestone.billCalNo;
                    break;
                case "ASSEMBLY_FLOOR":
                    desc = "On Assembly Floor as Calendar No: " + milestone.billCalNo;
                    break;
                default:
                    desc = milestone.statusDesc;
            }
            return desc.toLocaleLowerCase();
        }
        return "Introduced";
    };

    $scope.getMilestoneDate = function(milestones) {
        if (milestones && milestones.size > 0) {
            var milestone = milestones.items.slice(-1)[0];
            return moment(milestone.actionDate).format("MMMM DD, YYYY");
        }
    };

    /**
     * Gets the full bill view for a specified printNo and session year.
     * @param printNo {string}
     * @param session {int}
     */
    $scope.getBill = function(printNo, session) {
        if (printNo && session) {
            $scope.billViewResult = BillView.get({printNo: printNo, session: session}, function() {
                if ($scope.billViewResult.success) {
                    $scope.billView = $scope.billViewResult.result;
                }
            });
        }
    };

    $scope.clearSearch = function() {
        $scope.billResults = null;
    };

    $scope.computeOffset = function(page) {
        return ((page - 1) * $scope.limit) + 1;
    };

    $scope.init();
}]);

/** --- Bill View Controller --- */

contentModule.controller('BillViewCtrl', ['$scope', '$location', 'BillViewApi',
    function($scope, BillViewApi) {
    $scope.basePrintNo;
    $scope.session;

    $scope.init = function() {

    }
}]);


