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

billModule.factory('BillAggUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/updates/:from/:to?order=:order&type=:type&filter=:filter&limit=:limit&offset=:offset&summary=true', {
        from: '@from',
        to: '@to',
        type: '@type',
        order: '@order',
        filter: '@filter',
        limit: '@limit',
        offset: '@offset'
    });
}]);

billModule.factory('BillGetApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo?view=:view&version=:version', {
        session: '@session',
        printNo: '@printNo',
        view: '@view',
        version: '@version'
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

billModule.controller('BillCtrl', ['$scope', '$rootScope', '$location', '$route', '$routeParams', 'MemberApi',
                       function($scope, $rootScope, $location, $route, $routeParams, MemberApi) {
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
    $scope.curr = {
        searching: false,
        pagination: angular.extend({}, PaginationModel)
    };
    $scope.curr.pagination.itemsPerPage = 6;

    var defaultRefine = {
        sort: '_score:desc,session:desc',
        session: '',
        chamber: '',
        type: '',
        sponsor: '',
        status: '',
        hasVotes: false,
        isSigned: false,
        isGovProg: false,
        isRefined: false // Set to true if this model is updated
    };

    $scope.billSearch = {
        searched: false,
        term: $routeParams.search || '',
        refine: {
            sort: $routeParams['sort'] || '_score:desc,session:desc',
            session: $routeParams['session'],
            chamber: $routeParams['chamber'],
            type: $routeParams['type'],
            sponsor: $routeParams['sponsor'],
            status: $routeParams['status'],
            hasVotes: $routeParams['hasVotes'],
            isSigned: $routeParams['isSigned'],
            isGovProg: $routeParams['isGovProg'],
            isRefined: false
        },
        refineQuery: '',
        sort: '',
        response: {},
        results: [],
        error: false
    };

    /** Initialize the bill search page. */
    $scope.init = function() {
        if ($scope.selectedView == 0) {
            $scope.curr.pagination.currPage = Math.max(parseInt($routeParams['searchPage']) || 1, 1);
            $scope.simpleSearch(false);
        }
    };

    $scope.simpleSearch = function(resetPagination) {
        var term = $scope.billSearch.term || '*';
        if (term) {
            $location.search("search", $scope.billSearch.term);
            $scope.curr.searching = true;
            $scope.billSearch.searched = false;
            if (resetPagination) {
                $scope.curr.pagination.currPage = 1;
                $location.search('searchPage', 1);
            }
            var query = $scope.applyRefineToQuery(term);
            $scope.billSearch.response = BillSearch.get({
                term: query, sort: $scope.billSearch.sort, limit: $scope.curr.pagination.getLimit(),
                offset: $scope.curr.pagination.getOffset()},
                function() {
                    if ($scope.billSearch.response && $scope.billSearch.response.success) {
                        $scope.billSearch.error = false;
                        $scope.billSearch.results = $scope.billSearch.response.result.items || [];
                        $scope.billSearch.searched = true;
                        $scope.markHighlightsAsSafe($scope.billSearch.results);
                        $scope.curr.pagination.setTotalItems($scope.billSearch.response.total);
                        $scope.curr.searching = false;
                    }
                }, function(data) {
                    $scope.billSearch.searched = true;
                    $scope.curr.searching = false;
                    $scope.billSearch.error = data.data;
                    $scope.curr.pagination.setTotalItems(0);
                });
        }
        else {
            $scope.billSearch.error = false;
            $scope.billSearch.results = [];
            $scope.curr.pagination.setTotalItems(0);
        }
    };

    $scope.applyRefineToQuery = function(term) {
        var refineQuery = '(' + term + ')';
        var refine = $scope.billSearch.refine;
        $scope.billSearch.sort = refine.sort;

        $scope.setRefineUrlParams(refine);

        if (refine.session) {
            refineQuery += ' AND session:' + refine.session;
        }
        if (refine.chamber) {
            refineQuery += ' AND billType.chamber:' + refine.chamber;
        }
        // Bill type
        if (refine.type === 'bills') {
            refineQuery += ' AND billType.resolution:false';
        }
        else if (refine.type === 'resolutions') {
            refineQuery += ' AND billType.resolution:true';
        }
        // Bill Sponsor
        if (refine.sponsor) {
            refineQuery += ' AND sponsor.member.memberId:' + refine.sponsor;
        }
        // Bill status
        if (refine.status) {
            refineQuery += ' AND status.statusType:"' + refine.status + '"';
        }
        if (refine.hasVotes) {
            refineQuery += ' AND votes.size:>0';
        }
        if (refine.isSigned) {
            refineQuery += ' AND (signed:true OR adopted:true)'
        }
        if (refine.isGovProg) {
            refineQuery += ' AND programInfo.name:Governor'
        }
        return refineQuery;
    };

    /** Reset the refine filters. */
    $scope.resetRefine = function() {
        $scope.billSearch.refine = angular.extend({}, defaultRefine);
        $scope.setRefineUrlParams($scope.billSearch.refine);
    };

    $scope.setRefineUrlParams = function(refine) {
        // Set URL params
        for (var p in refine) {
            if (refine.hasOwnProperty(p)) {
                $scope.setSearchParam(p, refine[p]);
            }
        }
    };

    // Mark highlighted search results as safe html.
    $scope.markHighlightsAsSafe = function(results) {
        angular.forEach(results, function(r) {
            if (r.hasOwnProperty('highlights')) {
                for (prop in r['highlights']) {
                    if (r['highlights'][prop][0]) {
                        r['highlights'][prop][0] = $sce.trustAsHtml(String(r['highlights'][prop][0]));
                    }
                }
            }
        });
    };

    /** Watch for changes to the current search page. */
    $scope.$watch('curr.pagination.currPage', function(newPage, oldPage) {
        if (newPage !== oldPage) {
            $location.search('searchPage', newPage);
            $scope.simpleSearch();
         }
    });

    $scope.$watchCollection('billSearch.refine', function(n, o) {
        if (!angular.equals(n, o)) {
            $scope.billSearch.refine.isRefined = !angular.equals(n, defaultRefine);
            $scope.simpleSearch();
        }
    });

    $scope.init();
}]);

/** --- Bill Updates Controller --- */

billModule.controller('BillUpdatesCtrl', ['$scope', '$location', 'BillAggUpdatesApi', 'PaginationModel',
    function($scope, $location, BillAggUpdatesApi, PaginationModel){

    $scope.pagination = angular.extend({}, PaginationModel);
    $scope.pagination.itemsPerPage = 20;

    $scope.curr = {
        fromDate: moment().subtract(5, 'days').startOf('minute').toDate(),
        toDate: moment().startOf('minute').toDate(),
        type: $location.$$search.type || 'published',
        sortOrder: $location.$$search.sortOrder || 'desc',
        detail: $location.$$search.detail === true,
        filter: $location.$$search.filter ||  ''
    };

    $scope.billUpdates = {
        response: {},
        fetching: false,
        total: 0,
        result: {},
        errMsg: ''
    };

    $scope.$on('viewChange', function(ev) {
        $scope.getUpdates();
    });

    $scope.getUpdates = function() {
        $scope.billUpdates.fetching = true;
        $scope.billUpdates.response = BillAggUpdatesApi.get({
            from: $scope.curr.fromDate.toISOString(), to: $scope.curr.toDate.toISOString(),
            type: $scope.curr.type, order: $scope.curr.sortOrder, detail: $scope.curr.detail,
            filter: $scope.curr.filter, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()
        }, function() {
            $scope.billUpdates.total = $scope.billUpdates.response.total;
            $scope.billUpdates.result = $scope.billUpdates.response.result;
            $scope.billUpdates.fetching = false;
        }, function(resp) {
            $scope.billUpdates.response.success = false;
            $scope.billUpdates.total = 0;
            $scope.billUpdates.errMsg = resp.data.message;
            $scope.billUpdates.fetching = false;
        });
    };

    $scope.refreshParams = function() {
        angular.forEach($scope.curr, function(val, key) {
            var paramVal = val;
            if (paramVal instanceof Date) {
                paramVal = paramVal.toISOString();
            }
            $location.search(key, paramVal);
        });
    };

    $scope.$watchCollection('curr', function(n, o) {
        if ($scope.selectedView === 1) {
            $scope.getUpdates();
        }
    });

    $scope.$watch('pagination.currPage', function(newPage, oldPage) {
        if (newPage !== oldPage) {
            $scope.getUpdates();
        }
    });
}]);

/** --- Bill View Controller --- */

billModule.controller('BillViewCtrl', ['$scope', '$filter', '$location', '$routeParams', '$sce',
                                       'BillGetApi', 'BillDiffApi', 'BillUpdatesApi',
    function($scope, $filter, $location, $routeParams, $sce, BillGetApi, BillDiffApi, BillUpdatesApi) {

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
        if (newView === 4) { // selected full text tab
            $scope.fetchFullText();
        }
    });

    $scope.$watch('curr.amdVersion', function(newVersion, oldVersion){
        if (newVersion !== oldVersion && $scope.curr.selectedView === 4) {
            $scope.fetchFullText();
        }
    });

    $scope.init = function() {
        $scope.session = $routeParams.session;
        $scope.printNo = $routeParams.printNo;
        $scope.loading = true;
        $scope.response = BillGetApi.get({printNo: $scope.printNo, session: $scope.session, view: 'with_refs_no_fulltext'},
        function() {
            if ($scope.response.success) {
                $scope.bill = $scope.response.result;
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
