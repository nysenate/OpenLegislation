var billModule = angular.module('open.bill');

/** --- Bill Search Controller --- */

billModule.controller('BillSearchCtrl', ['$scope', '$filter', '$routeParams', '$location', '$sce',
                                         'BillSearchApi', 'PaginationModel',
    function($scope, $filter, $routeParams, $location, $sce, BillSearch, PaginationModel) {
        $scope.curr = {
            state: 'initial',
            pagination: angular.extend({}, PaginationModel, { itemsPerPage: 6 }),
            billSearch: {
                term: $routeParams['search'] || '',
                session: $routeParams['session'] || '',
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
            //milestones: "milestones.size",
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

        // Initialize the bill search page.
        $scope.init = function() {
            // Set the pagination to point to the page specified in the url
            $scope.curr.pagination.currPage = Math.max(parseInt($routeParams['searchPage']) || 1, 1);
            // Set the refine params from the url
            $scope.getRefineUrlParams($scope.refineUrlParamPrefix);
            // Perform a search to kick things off if there are no refine params.
            $scope.simpleSearch(false);
        };


        // Performs a search against the API
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
                        session: $scope.curr.billSearch.session,
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

        // Combines the search term with any selected refine filters and constructs a search query
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

        // Reset the refine filters.
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

        // Trigger a search and update the url when the session field is changed.
        $scope.sessionChanged = function() {
            $scope.setSearchParam('session', $scope.curr.billSearch.session);
            $scope.simpleSearch(true);
        };

        // Method that is triggered when the search listing page is changed.
        $scope.pageChanged = function(newPage) {
            $scope.setSearchParam('searchPage', newPage);
            $scope.simpleSearch(false);
        };

        $scope.init();
    }]);
