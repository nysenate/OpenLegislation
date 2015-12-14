var billModule = angular.module('open.bill', ['open.core', 'open.api']);

/** --- Parent Bill Controller --- */

billModule.controller('BillCtrl', ['$scope', '$rootScope', '$location', '$route', '$routeParams',
                      'BillUtils', 'MemberApi',
                       function($scope, $rootScope, $location, $route, $routeParams, BillUtils, MemberApi) {
    $scope.setHeaderVisible(true);
    $scope.setHeaderText('Search NYS Legislation');

    $scope.selectedView = (parseInt($routeParams.view, 10) || 0);

    /** Watch for changes to the current view. */
    $scope.$watch('selectedView', function(n, o) {
        if (n !== o && $location.search().view !== n) {
            $location.search('view', $scope.selectedView);
            $scope.$broadcast('viewChange', $scope.selectedView);
        }
    });

    $scope.$on('$locationChangeSuccess', function() {
        $scope.selectedView = +($location.search().view) || 0;
    });

    $scope.getStatusDesc = function(status) {
        return BillUtils.getStatusDesc(status);
    }
}]);

/** --- Bill Search Controller --- */

billModule.controller('BillSearchCtrl', ['$scope', '$filter', '$routeParams', '$location', '$sce',
                                         'BillListingApi', 'BillSearchApi', 'PaginationModel',
                      function($scope, $filter, $routeParams, $location, $sce, BillListing, BillSearch, PaginationModel) {
    $scope.curr = {
        state: 'initial',
        pagination: angular.extend({}, PaginationModel, { itemsPerPage: 6 }),
        billSearch: {
            term: $routeParams['search'] || '',
            refine: {},
            isRefined: false,
            sort: $routeParams['sort'] || '_score:desc,session:desc',
            response: {},
            results: [],
            error: false
        }
    };

    // The refine parameters in the url will be prefixed with the following string
    $scope.refineUrlParamPrefix = 'refine:';

    // The following maps refine params to search columns
    $scope.refinePaths = {
        actionText: "actions.\\*.text",
        agendaNo: "\\*.agendaId.number",
        billCalNo: "\\*.billCalNo",
        chamber: "billType.chamber",
        committeeName: "status.committeeName",
        fullText: "amendments.\\*.fullText",
        isRes: "billType.resolution",
        lawCode: "amendments.\\*.lawCode",
        lawSection: "amendments.\\*.lawSection",
        memo: "amendments.\\*.memo",
        printNo: "printNo",
        session: "session",
        sponsor: "sponsor.member.memberId",
        status: "status.statusType",
        title: "title"
    };
    // Any refined params that have custom query logic are defined here
    // These are params that don't have a simple column:value query.
    $scope.refineFixedPaths = {
        signed: '(signed:true OR adopted:true)',
        hasVotes: '(votes.size:>0)',
        hasApVetoMemo: '(vetoMessages.size:>0 OR !_empty_:approvalMessage)',
        isGov: '(programInfo.name:*Governor*)',
        isSubstituted: '(_exists_:substitutedBy)',
        isUni: '(amendments.\\*.uniBill:true)',
        isBudget: '(sponsor.budget:true)',
        isRulesSponsor: '(sponsor.rules:true)'
    };

    /** Initialize the bill search page. */
    $scope.init = function() {
        // Set the pagination to point to the page specified in the url
        $scope.curr.pagination.currPage = Math.max(parseInt($routeParams['searchPage']) || 1, 1);
        // Set the refine params from the url
        if (!$scope.getRefineUrlParams($scope.refineUrlParamPrefix)) {
            // Perform a search to kick things off if there are no refine params.
            // If there were refine params, a search would've kicked off as a result of a watch.
            $scope.simpleSearch(false);
        }
    };

    $scope.simpleSearch = function(resetPagination) {
        var term = $scope.curr.billSearch.term || '*';
        if (term) {
            $location.search("search", $scope.curr.billSearch.term);
            $scope.curr.state = 'searching';
            if (resetPagination) {
                $scope.curr.pagination.currPage = 1;
                $location.search('searchPage', 1);
            }
            var query = $scope.buildRefinedQuery(term);
            $scope.curr.billSearch.response = BillSearch.get({
                term: query, sort: $scope.curr.billSearch.sort, limit: $scope.curr.pagination.getLimit(),
                offset: $scope.curr.pagination.getOffset()},
                function() {
                    if ($scope.curr.billSearch.response && $scope.curr.billSearch.response.success) {
                        $scope.curr.billSearch.error = false;
                        $scope.curr.billSearch.results = $scope.curr.billSearch.response.result.items || [];
                        $scope.markHighlightsAsSafe($scope.curr.billSearch.results);
                        $scope.curr.pagination.setTotalItems($scope.curr.billSearch.response.total);
                        $scope.curr.state = 'searched';
                    }
                }, function(data) {
                    $scope.curr.billSearch.error = data.data;
                    $scope.curr.pagination.setTotalItems(0);
                    $scope.curr.state = 'searched';
                });
        }
        else {
            $scope.curr.billSearch.error = false;
            $scope.curr.billSearch.results = [];
            $scope.curr.pagination.setTotalItems(0);
        }
    };

    $scope.buildRefinedQuery = function(term) {
        var refine = $scope.curr.billSearch.refine;
        var refineTerms = [];
        for (var p in refine) {
            if (refine.hasOwnProperty(p) && refine[p]) {
                if ($scope.refineFixedPaths[p]) {
                    refineTerms.push($scope.refineFixedPaths[p]);
                }
                else if ($scope.refinePaths[p]) {
                    refineTerms.push($scope.refinePaths[p] + ":(" + refine[p] + ")");
                }
            }
        }
        if (refineTerms.length > 0) {
            term += " AND " + refineTerms.join(" AND ");
            $scope.curr.billSearch.isRefined = true;
        }
        else {
            $scope.curr.billSearch.isRefined = false;
        }
        return term;
    };

    $scope.onRefineUpdate = function() {
        $scope.setRefineUrlParams($scope.curr.billSearch.refine, $scope.refineUrlParamPrefix);
        $scope.simpleSearch(false);
    };

    /** Reset the refine filters. */
    $scope.resetRefine = function() {
        $scope.curr.billSearch.refine = {};
        $scope.curr.billSearch.isRefined = false;
        $scope.setRefineUrlParams($scope.curr.billSearch.refine, $scope.refineUrlParamPrefix);
        $scope.simpleSearch(true);
    };

    $scope.setRefineUrlParams = function(refine, paramPrefix) {
        // Delete any existing refine URL params
        for (var p in $routeParams) {
            if ($routeParams.hasOwnProperty(p) && p.slice(0, paramPrefix.length) === paramPrefix) {
                delete $routeParams[p];
                $scope.setSearchParam(p, null);
            }
        }
        // Set new refine URL params
        for (var p in refine) {
            if (refine.hasOwnProperty(p) && refine[p]) {
                $scope.setSearchParam(paramPrefix + p, refine[p]);
            }
        }
    };

    $scope.getRefineUrlParams = function(paramPrefix) {
        for (var p in $routeParams) {
            if ($routeParams.hasOwnProperty(p) && p.slice(0, paramPrefix.length) === paramPrefix) {
                $scope.curr.billSearch.refine[p.slice(paramPrefix.length)] = $routeParams[p];
                $scope.curr.billSearch.isRefined = true;
            }
        }
        return $scope.curr.billSearch.isRefined;
    };

    // Mark highlighted search results as safe html.
    $scope.markHighlightsAsSafe = function(results) {
        angular.forEach(results, function(r) {
            if (r.hasOwnProperty('highlights')) {
                for (var prop in r['highlights']) {
                    if (r['highlights'][prop][0]) {
                        r['highlights'][prop][0] = $sce.trustAsHtml(String(r['highlights'][prop][0]));
                    }
                }
            }
        });
    };

    // Trigger a search and update the url when the sort field is changed.
    $scope.sortChanged = function() {
        $scope.setSearchParam('sort', $scope.curr.billSearch.sort);
        $scope.simpleSearch(true);
    };

    // Method that is triggered when the search listing page is changed.
    $scope.pageChanged = function(newPage) {
        $scope.setSearchParam('searchPage', newPage);
        $scope.simpleSearch(false);
    };

    $scope.init();
}]);

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
            toDate: ($routeParams.toDate) ? new Date($routeParams.toDate) : moment().startOf('minute').toDate(),
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
            from: $scope.curr.options.fromDate.toISOString(), to: $scope.curr.options.toDate.toISOString(),
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
                paramVal = paramVal.toISOString();
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

/** --- Bill View Controller --- */

billModule.controller('BillViewCtrl', ['$scope', '$filter', '$location', '$routeParams', '$sce',
                                       'BillGetApi', 'BillDiffApi', 'BillUpdatesApi',
    function($scope, $filter, $location, $routeParams, $sce, BillGetApi, BillDiffApi, BillUpdatesApi) {

    $scope.apiPath = null;
    $scope.response = null;
    $scope.bill = null;
    $scope.loading = false;
    $scope.fullTextFetched = {}; // Contains a dict of versions to indicate the ones where text was fetched.
    $scope.curr = {
        amdVersion: '',
        compareVersion: 'None',
        selectedView: (parseInt($routeParams.view, 10) || 1),
        updateTypeFilter: 'status',
        updateOrder: 'desc'
    };
    $scope.diffHtml = null;
    $scope.updateHistory = null;
    $scope.billTheme = 'default';

    $scope.$watch('curr.selectedView', function(newView, oldView) {
        if (newView !== oldView) {
            $location.search('view', $scope.curr.selectedView).replace();
        }
        if (newView === 5) { // selected full text tab
            $scope.fetchFullText();
        }
    });

    $scope.$watch('curr.amdVersion', function(newVersion, oldVersion){
        if (newVersion !== oldVersion && $scope.curr.selectedView === 5) {
            $scope.fetchFullText();
        }
    });

    $scope.init = function() {
        $scope.session = $routeParams.session;
        $scope.printNo = $routeParams.printNo;
        $scope.loading = true;
        $scope.billApiPath = $sce.trustAsResourceUrl(apiPath + '/bills/' + $scope.session + '/' + $scope.printNo);
        $scope.response = BillGetApi.get({printNo: $scope.printNo, session: $scope.session, view: 'with_refs_no_fulltext'},
        function() {
            if ($scope.response.success) {
                $scope.bill = $scope.response.result;
                $scope.mergeActions($scope.bill);
                $scope.setHeaderText('NYS ' + $scope.bill.billType.desc + ' ' +
                    $filter('resolutionOrBill')($scope.bill.billType.resolution) + ' ' +
                    $scope.bill.basePrintNo + '-' + $scope.bill.session + (($scope.bill.session !== $scope.activeSession) ? " (Inactive) " : ""));
                $scope.curr.amdVersion = $scope.bill.activeVersion;
                if ($scope.bill.vetoed) {
                    $scope.billTheme = 'failure';
                }
                else if ($scope.bill.signed) {
                    $scope.billTheme = 'success';
                }
            }
            $scope.loading = false;
        }, function(response) {
            $scope.setHeaderText(response.data.message);
            $scope.response = response.data;
            $scope.loading = false;
        });
    }();

    $scope.fetchFullText = function() {
        if (!$scope.fullTextFetched[$scope.curr.amdVersion]) {
            $scope.loading = true;
            var fullTextResponse = BillGetApi.get(
                {printNo: $scope.printNo, session: $scope.session, view: 'only_fulltext', version: $scope.curr.amdVersion},
                function() {
                    if (fullTextResponse.success) {
                        var version = fullTextResponse.result.version;
                        var text = fullTextResponse.result.fullText;
                        $scope.bill.amendments.items[version].fullText = text;
                        $scope.fullTextFetched[version] = true;
                    }
                    $scope.loading = false;
                }, function() {
                    $scope.loading = false;
                });
        }
    };

    $scope.mergeActions = function(bill) {
        var currPrintNoStr = bill.basePrintNoStr;
        if (bill.substitutedBy) {
            var subPrintNoStr = bill.substitutedBy.basePrintNoStr;
            var mergedActions = bill.actions.items.concat(bill.billInfoRefs.items[subPrintNoStr].actions.items)
                .sort(function(a,b) {
                    return moment(a.date).diff(moment(b.date));
                });
            var actions1 = mergedActions.slice(0);
            var actions2 = mergedActions.slice(0);
            // Set sub bill actions to null in list 1
            actions1 = actions1.map(function(a) {
                return (a.billId.basePrintNoStr == currPrintNoStr) ? a : null;
            });
            // Set primary bill actions to null in list 2
            actions2 = actions2.map(function(a) {
                return (a.billId.basePrintNoStr == subPrintNoStr) ? a : null;
            });
            bill.mergedActions = [[currPrintNoStr, actions1], [subPrintNoStr, actions2]];
        }
        else {
            bill.mergedActions = [[currPrintNoStr, bill.actions.items]];
        }
    };

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
             filter: $scope.curr.updateTypeFilter, offset: 1, limit: 1000}, function() {
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
                      '<div class="bill-ms-step" ng-class="{\'filled\': milestone.actionDate !== null, ' +
                                                           '\'vetoed\': milestone.statusDesc == \'Vetoed\'}">' +
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

billModule.directive('billListing', ['BillUtils', function(BillUtils) {
    return {
        restrict: 'E',
        scope: {
            'billIds': '=',
            'billRefsMap': '=',
            'bills': '=',
            'billSearchTerm': '=',
            'showTitle': '=',
            'showImg': '='
        },
        templateUrl: ctxPath + '/partial/content/bill/bill-listing-view',
        controller: function($scope, $element) {
            $scope.billUtils = BillUtils;
            if ($scope.billIds && $scope.billRefsMap && !$scope.bills) {
                $scope.bills = $scope.billIds.map(function(id) {
                    var baseIdStr = id.basePrintNo + '-' + id.session;
                    if ($scope.billRefsMap[baseIdStr]) {
                        return $scope.billRefsMap[baseIdStr];
                    }
                    return angular.extend({}, id, {'idOnly': true});
                });
            }
        }
    };
}]);

billModule.directive('billSearchListing', ['BillUtils', function(BillUtils) {
    return {
        restrict: 'E',
        scope: {
            'billSearchResponse': '=',
            'billSearchTerm': '=',
            'pagination': '=',
            'onPageChange': '=',
            'showTitle': '=',
            'showImg': '='
        },
        templateUrl: ctxPath + '/partial/content/bill/bill-search-listing-view',
        controller: function($scope, $element){
            $scope.billUtils = BillUtils;
            var currPage = $scope.pagination.currPage;
            $scope.pageChange = function(newPageNumber) {
                if (currPage != newPageNumber && $scope.onPageChange) {
                    currPage = newPageNumber; // Prevents duplicate calls
                    $scope.onPageChange(newPageNumber);
                }
            }
        }
    };
}]);

billModule.directive('billUpdatesListing', ['BillUtils', function(BillUtils) {
    return {
        restrict: 'E',
        scope: {
            'billUpdateResponse': '=',
            'pagination': '=',
            'onPageChange': '=',
            'showTitle': '=',
            'showImg': '=',
            'showDetail': '='
        },
        templateUrl: ctxPath + '/partial/content/bill/bill-update-listing-view',
        controller: function($scope, $element){
            $scope.billUtils = BillUtils;
            var currPage = $scope.pagination.currPage;
            $scope.pageChange = function(newPageNumber) {
                if (currPage != newPageNumber && $scope.onPageChange) {
                    currPage = newPageNumber; // Prevents duplicate calls
                    $scope.onPageChange(newPageNumber);
                }
            }
        }
    };
}]);

billModule.directive('billRefineSearchPanel', ['BillUtils', function(BillUtils) {
    return {
        restrict: 'E',
        scope: {
            searchParams: '=',
            onChange: '='
        },
        templateUrl: ctxPath + '/partial/content/bill/bill-refine-search-panel',
        controller: function($scope, $element) {
            $scope.params = $scope.params || {};
            $scope.$watchCollection('searchParams', function(n,o) {
                $scope.onChange($scope.searchParams);
            });
        }
    }
}]);