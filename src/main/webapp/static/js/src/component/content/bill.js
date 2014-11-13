var contentModule = angular.module('content');

contentModule.factory('BillListing', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:sessionYear', {
        sessionYear: '@sessionYear'
    });
}]);

contentModule.factory('BillSearch', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/search/?term=:term&sort=:sort&limit=:limit&offset=:offset', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

contentModule.factory('BillView', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo', {
        session: '@session',
        printNo: '@printNo'
    });
}]);

/** --- Parent Bill Controller --- */

contentModule.controller('BillCtrl', ['$scope', function($scope) {

}]);

/** --- Bill Search Controller --- */

contentModule.controller('BillSearchCtrl', ['$scope', '$filter', '$location', 'BillListing', 'BillSearch', 'BillView',
    function($scope, $filter, $location, BillListing, BillSearch, BillView) {
    $scope.searchTerm = '';
    $scope.sort = '';
    $scope.billResults = {};
    $scope.billViewResult = null;
    $scope.billView = null;
    $scope.totalResults = 0;
    $scope.limit = 10;
    $scope.offset = 1;
    $scope.page = 1;

    $scope.VIEWS = {SEARCH: 'search', BILL: 'bill'};
    $scope.view = $scope.VIEWS.BILL;

    /**
     * Watch for changes to the query in the search bar and perform the search when a new term is detected.
     */
    $scope.$watch('searchTerm', function(newTerm, oldTerm) {
        if (newTerm && newTerm != oldTerm && newTerm.trim() != '') {
            $scope.page = 1;
            $scope.search();
        }
    });

    $scope.$watch('page', function(newPage, oldPage) {
        if (newPage != oldPage) {
            $scope.offset = $scope.limit * newPage;
            $scope.search();
        }
    });

    $scope.search = function() {
        if ($scope.searchTerm != null && $scope.searchTerm.trim() != '') {
            $scope.billResults = BillSearch.get(
                {term: $scope.searchTerm, sort: $scope.sort, limit: $scope.limit, offset: $scope.offset},
                function() {
                    $location.search("search", $scope.searchTerm);
                    if ($scope.billResults.total != $scope.totalResults) {
                        $scope.totalResults = $scope.billResults.total;
                    }
                    $scope.view = $scope.VIEWS.SEARCH;
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
                    $scope.view = $scope.VIEWS.BILL;
                }
            });
        }
    };

    $scope.clearSearch = function() {
        $scope.billResults = null;
    }
}]);

/** --- Bill View Controller --- */

contentModule.controller('BillCtrl', ['$scope', function($scope) {

}]);


