var billModule = angular.module('open.bill', ['open.core']);

billModule.factory('BillListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:sessionYear?sort=:sort&limit=:limit&offset=:offset', {
        sessionYear: '@sessionYear',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

billModule.factory('BillSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/search/?term=:term&sort=:sort&limit=:limit&offset=:offset', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

billModule.factory('BillGetApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo?detail=true', {
        session: '@session',
        printNo: '@printNo'
    });
}]);

billModule.factory('BillUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo/updates?order=:order&filter=:filter&limit=:limit&offset=:offset', {
        session: '@session',
        printNo: '@printNo',
        order: '@order',
        filter: '@filter',
        limit: '@limit',
        offset: '@offset'
    });
}]);

billModule.factory('BillDiffApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo/diff/:version1/:version2/', {
        session: '@session',
        printNo: '@printNo',
        version1: '@version1',
        version2: '@version2'
    });
}]);

/** --- Parent Bill Controller --- */

billModule.controller('BillCtrl', ['$scope', '$rootScope', '$location', '$route',
                       function($scope, $rootScope, $location, $route) {
    $scope.setHeaderVisible(true);

    $scope.getStatusDesc = function(status) {
        var desc = "";
        if (status) {
            switch (status.statusType) {
                case "IN_SENATE_COMM":
                    desc = "In Senate " + status.committeeName + " Committee"; break;
                case "IN_ASSEMBLY_COMM":
                    desc = "In Assembly " + status.committeeName + " Committee"; break;
                case "SENATE_FLOOR":
                    desc = "On Senate Floor as Calendar No: " + status.billCalNo; break;
                case "ASSEMBLY_FLOOR":
                    desc = "On Assembly Floor as Calendar No: " + status.billCalNo; break;
                default:
                    desc = status.statusDesc;
            }
        }
        return desc;
    }
}]);

/** --- Bill Search Controller --- */

billModule.controller('BillSearchCtrl', ['$scope', '$filter', '$routeParams', '$location', '$sce',
                                        'BillListingApi', 'BillSearchApi', 'PaginationModel',
                      function($scope, $filter, $routeParams, $location, $sce, BillListing, BillSearch, PaginationModel) {
    $scope.setHeaderText('Search NYS Legislation');

    $scope.curr = {
        selectedView: (parseInt($routeParams.view, 10) || 0),
        pagination: angular.extend({}, PaginationModel)
    };
    $scope.curr.pagination.itemsPerPage = 20;

    /**
     * Watch for changes to the current view.
     */
    $scope.$watch('curr.selectedView', function() {
        $location.search('view', $scope.curr.selectedView);
    });

    $scope.billSearch = {
        searched: false,
        term: $routeParams.search || '',
        response: {},
        results: [],
        error: false
    };

    /**
    * Initialize the bill search page.
    */
    $scope.init = function() {
        $scope.curr.pagination.currPage = Math.max(parseInt($routeParams['searchPage']) || 1, 1);
        $scope.simpleSearch(false);
    };

    $scope.simpleSearch = function(resetPagination) {
        var term = $scope.billSearch.term;
        if (term) {
            $location.search("search", $scope.billSearch.term);
            $scope.billSearch.searched = false;
            if (resetPagination) {
                $scope.curr.pagination.currPage = 1;
                $location.search('searchPage', 1);
            }
            $scope.billSearch.response = BillSearch.get({
                term: term, sort: $scope.billSearch.sort, limit: $scope.curr.pagination.getLimit(),
                offset: $scope.curr.pagination.getOffset()},
                function() {
                    if ($scope.billSearch.response && $scope.billSearch.response.success) {
                        $scope.billSearch.error = false;
                        $scope.billSearch.results = $scope.billSearch.response.result.items || [];
                        $scope.billSearch.searched = true;
                        // Mark highlighted search results as safe html.
                        angular.forEach($scope.billSearch.results, function(r) {
                            for (prop in r.highlights) {
                                if (r.highlights[prop][0]) {
                                    r.highlights[prop][0] = $sce.trustAsHtml(r.highlights[prop][0]);
                                }
                            }
                        });
                        $scope.curr.pagination.setTotalItems($scope.billSearch.response.total);
                    }
                }, function(data) {
                    $scope.billSearch.searched = true;
                    $scope.billSearch.error = data.data;
                });
        }
        else {
            $scope.billSearch.error = false;
            $scope.billSearch.results = [];
            $scope.curr.pagination.setTotalItems(0);
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

    $scope.paginate = function(newPage) {
        $location.search('searchPage', newPage);
        $scope.simpleSearch();
    };

    /** Initialize */
    $scope.init();
}]);

/** --- Bill Info Controller --- */

billModule.controller('BillExploreCtrl', ['$scope', 'BillListingApi', function($scope, BillListingApi) {
    $scope.setHeaderText("Explore NYS Legislation");

    var i = 0;
    $scope.senatorList = Array.apply(0, Array(63)).map(function() { i +=1; return i; });

    $scope.recentBillsResponse = BillListingApi.get({sessionYear: 2011, sort: 'publishedDateTime:DESC', limit: 5, offset: 1},
        function() {
            $scope.recentBills = $scope.recentBillsResponse.result.items;
    });

    $scope.recentStatusBillsResponse = BillListingApi.get({sessionYear: 2011, sort: 'status.actionDate:DESC', limit: 5, offset: 1},
    function() {
        $scope.recentStatusBills = $scope.recentStatusBillsResponse.result.items;
    });
}]);

/** --- Bill View Controller --- */

billModule.controller('BillViewCtrl', ['$scope', '$filter', '$location', '$routeParams', '$sce',
                                       'BillGetApi', 'BillDiffApi', 'BillUpdatesApi',
    function($scope, $filter, $location, $routeParams, $sce, BillGetApi, BillDiffApi, BillUpdatesApi) {

    $scope.response = null;
    $scope.bill = null;
    $scope.curr = {
        amdVersion: '',
        compareVersion: 'None',
        selectedView: (parseInt($routeParams.view, 10) || 1),
        updateTypeFilter: 'status',
        updateOrder: 'desc'
    };
    $scope.diffHtml = null;
    $scope.updateHistory = null;

    $scope.$watch('curr.selectedView', function() {
        $location.search('view', $scope.curr.selectedView);
    });

    $scope.init = function() {
        $scope.session = $routeParams.session;
        $scope.printNo = $routeParams.printNo;
        $scope.response = BillGetApi.get({printNo: $scope.printNo, session: $scope.session}, function() {
            if ($scope.response.success) {
                $scope.bill = $scope.response.result;
                $scope.setHeaderText('NYS ' + $scope.bill.billType.desc + ' ' +
                    $filter('resolutionOrBill')($scope.bill.billType.resolution) + ' ' +
                    $scope.bill.basePrintNo + '-' + $scope.bill.session);
                $scope.curr.amdVersion = $scope.bill.activeVersion;
            }
        }, function(response) {
            $scope.setHeaderText(response.data.message);
            $scope.response = response.data;
        });
    }();

    $scope.diffBills = function() {
        if ($scope.curr.compareVersion !== 'None') {
            $scope.diffResponse = BillDiffApi.get({
                printNo: $scope.bill.printNo, session: $scope.bill.session,
                version1: $scope.curr.compareVersion.trim(), version2: $scope.curr.amdVersion},
            function() {
                $scope.diffHtml = $sce.trustAsHtml($scope.diffResponse.result.diffHtml);
            });
        }
        else {
            $scope.diffHtml = null;
        }
    };

    $scope.initialGetUpdates = function() {
        if ($scope.updateHistory === null) {
          $scope.getUpdates();
        }
    };

    $scope.getUpdates = function() {
        $scope.updateHistoryResponse = BillUpdatesApi.get(
            {printNo: $scope.printNo, session: $scope.session, order: $scope.curr.updateOrder,
             filter: $scope.curr.updateTypeFilter, offset: 1, limit: 200}, function() {
            if ($scope.updateHistoryResponse.success === true) {
                $scope.updateHistory = $scope.updateHistoryResponse.result;
            }
        });
    };

    $scope.backToSearch = function() {
        $location.search('view', 0);
        $location.path(ctxPath + '/bills');
    };
}]);

/** --- Filters --- */

billModule.filter('resolutionOrBill', function() {
    return function(input) {
        return (input) ? "Resolution" : "Bill";
    }
});

billModule.filter('prettyAmendVersion', function() {
    return function(input) {
        return (input) ? input : "Original";
    }
});

billModule.filter('prettySponsorMemo', function($sce){
    var headingPattern = /(([A-Z][A-Za-z ]+)+:)/g;
    return function(memo) {
        if (memo) {
            var htmlMemo = memo.replace(headingPattern, "<div class='bill-memo-heading'>$1</div>");
            return $sce.trustAsHtml(htmlMemo);
        }
        return memo;
    }
});

billModule.filter('voteTypeFilter', function() {
    return function(voteType) {
        switch (voteType) {
            case 'AYE': return 'Aye';
            case 'NAY': return 'Nay';
            case 'AYEWR': return 'Aye with reservations';
            case 'ABS': return 'Absent';
            case 'ABD': return 'Abstained';
            case 'EXC': return 'Excused';
            default: return 'Unknown';
        }
    }
});

/** --- Directives --- */

billModule.directive('milestones', [function(){
    /** Returns an array of milestone descriptions. */
    var defaultMilestones = function(chamber) {
        var milestoneArr = [];
        var create = function(desc) {
            return {statusDesc: desc, actionDate: null};
        };
        var senateMilestones = [create("In Senate Committee"), create("On Senate Floor"), create("Passed Senate")];
        var assemblyMilestones = [create("In Assembly Committee"), create("On Assembly Floor"), create("Passed Assembly")];
        if (chamber == 'SENATE') {
            milestoneArr = milestoneArr.concat(senateMilestones).concat(assemblyMilestones);
        }
        else {
            milestoneArr = milestoneArr.concat(assemblyMilestones).concat(senateMilestones);
        }
        milestoneArr = milestoneArr.concat([create("Sent to Governor"), create("Signed Into Law")]);
        return milestoneArr;
    };
    /**
     * The milestones array from the bill api response only includes data for milestones that have been met. This
     * method will return an array such that any missing milestones are also included (with null actionDates).
     * @returns {Array}
     */
    var getPaddedMilestones = function(milestoneArr, chamber) {
        var paddedMsArr = [];
        if (milestoneArr) {
            paddedMsArr = defaultMilestones(chamber);
            // Replacing part of the arrays that overlap.. maybe there is a cleaner way, idk...
            [].splice.apply(paddedMsArr, [0, milestoneArr.size].concat(milestoneArr.items));
        }
        return paddedMsArr;
    };

    return {
        restrict: 'E',
        scope: {
            'milestoneArr': '=',
            'chamber': '='
        },
        replace: true,
        template: '<div class="bill-ms-container">' +
                    '<div ng-repeat="milestone in paddedMs">' +
                      '<div class="bill-ms-step" ng-class="{\'filled\': milestone.actionDate !== null}">' +
                        '<md-tooltip>{{milestone.statusDesc}}' +
                          '<span ng-if="milestone.actionDate !== null"><br/>On {{milestone.actionDate | moment:\'MMM DD, YYYY\'}}</span>' +
                          '<span ng-if="milestone.committeeName"><br/>{{milestone.committeeName}}</span>' +
                        '</md-tooltip></div>' +
                      '<div ng-class="{\'bill-ms-line\': $index !== 7}"></div>' +
                    '</div>' +
                  '</div>',
        link: function($scope, $element, $attrs) {
            $scope.paddedMs = getPaddedMilestones($scope.milestoneArr, $scope.chamber);
        }
    }
}]);
