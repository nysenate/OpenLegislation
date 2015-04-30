var lawModule = angular.module('open.law', ['open.core', 'infinite-scroll']);

lawModule.factory('LawListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws');
}]);

lawModule.factory('LawTreeApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId?fromLocation=:fromLocation&depth=:depth', {
        lawId: '@lawId',
        fromLocation: '@fromLocation',
        depth: '@depth'
    });
}]);

lawModule.factory('LawDocApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/:docId/', {
        lawId: '@lawId',
        docId: '@docId'
    });
}]);

lawModule.factory('LawFullSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/search?term=:term', {
        term: '@term'
    });
}]);

lawModule.factory('LawVolumeSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/search?term=:term', {
        lawId: '@lawId',
        term: '@term'
    });
}]);

lawModule.factory('LawFullUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/updates/:from/:to/', {
        from: '@from',
        to: '@to',
        type: '@type',
        order: '@order'
    });
}]);

lawModule.factory('LawVolumeUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/laws/:lawId/updates', {
        lawId: '@lawId',
        order: '@order'
    });
}]);

/**
 * LawCtrl
 * -------
 */
lawModule.controller('LawCtrl', ['$scope', '$location', '$routeParams', function($scope, $location, $routeParams) {
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
}]);

/**
 * LawListingCtrl
 * --------------
 */
lawModule.controller('LawListingCtrl', ['$scope', '$location', '$route', 'LawListingApi',
                        function($scope, $location, $route, LawListingApi) {
    function tabInit() {
        $scope.setHeaderText('NYS Laws Listing');
        $scope.setHeaderVisible(true);
    }

    $scope.$on('viewChange', function() {
        tabInit();
    });

    $scope.curr = {
        listingLimit: 20
    };
    $scope.init = function() {
        tabInit();
        if (!$scope.lawListingResponse || !$scope.lawListingResponse.success) {
            $scope.lawListingResponse = LawListingApi.get({}, function() {
                $scope.lawListing = $scope.lawListingResponse.result.items;
            });
        }
    };

    $scope.keepScrolling = function() {
        $scope.curr.listingLimit += 10;
    };

    $scope.init();
}]);

/**
 * Law Search Ctrl
 * ---------------
 */
lawModule.controller('LawSearchCtrl', ['$scope', '$location', '$routeParams', 'LawFullSearchApi', 'PaginationModel', 'safeHighlights',
                     function($scope, $location, $routeParams, LawFullSearchApi, PaginationModel, safeHighlights) {
    function tabInit() {
        $scope.setHeaderText('Search NYS Laws');
        $scope.setHeaderVisible(true);
    }

    $scope.$on('viewChange', function() {
       tabInit();
    });

    $scope.pagination = angular.extend({}, PaginationModel);

    $scope.lawSearch = {
        term: $routeParams.search || '',
        searching: false,
        searched: false,
        response: null,
        error: null,
        results: []
    };

    $scope.init = function() {
        tabInit();
        $scope.pagination.currPage = Math.max(parseInt($routeParams['searchPage']) || 1, 1);
        $scope.simpleSearch();
    };

    $scope.simpleSearch = function(resetPagination) {
        var term = $scope.lawSearch.term;
        $scope.setSearchParam('search', term);
        if (term) {
            if (resetPagination) {
                $scope.pagination.reset();
            }
            $scope.lawSearch.searched = false;
            $scope.lawSearch.searching = true;
            $scope.lawSearch.response = LawFullSearchApi.get(
                {term: term, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()},
                function() {
                    if ($scope.lawSearch.response.result) {
                        $scope.lawSearch.results = $scope.lawSearch.response.result.items;
                        safeHighlights($scope.lawSearch.results);
                    }
                    $scope.pagination.setTotalItems($scope.lawSearch.response.total);
                    $scope.lawSearch.searched = true;
                    $scope.lawSearch.searching = false;
                    $scope.lawSearch.error = false;
                },
                function(error) {
                    $scope.pagination.setTotalItems(0);
                    $scope.lawSearch.error = error.data;
                    $scope.lawSearch.searched = true;
                    $scope.lawSearch.searching = false;
                });
        }
    };

    /** Watch for changes to the current search page. */
    $scope.$watch('pagination.currPage', function(newPage, oldPage) {
        if (newPage !== oldPage) {
            $scope.setSearchParam('searchPage', newPage);
            $scope.simpleSearch();
        }
    });

    $scope.init();
}]);

/**
 * Law Updates Ctrl
 * ----------------
 */
lawModule.controller('LawUpdatesCtrl', ['$scope', '$location', '$routeParams', 'PaginationModel', 'LawFullUpdatesApi',
    function($scope, $location, $routeParams, PaginationModel, LawFullUpdatesApi) {
        function tabInit() {
            $scope.setHeaderText('NYS Laws Updates');
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



/**
 * LawViewCtrl
 * -----------
 */
lawModule.controller('LawViewCtrl', ['$scope', '$q', '$routeParams', '$location', '$route', '$timeout', '$anchorScroll',
                                     'PaginationModel', '$sce', '$mdToast', 'LawTreeApi', 'LawDocApi', 'LawVolumeSearchApi',
                                     'LawVolumeUpdatesApi',
                        function($scope, $q, $routeParams, $location, $route, $timeout, $anchorScroll, PaginationModel,
                                 $sce, $mdToast, LawTreeApi, LawDocApi, LawVolumeSearchApi, LawVolumeUpdatesApi) {
    $scope.setHeaderVisible(true);

    $scope.curr = {
        lawId : $routeParams.lawId,
        lawRoot: null,
        lawTree: null,
        lawDocs: {},
        expanded: {},
        showDoc: {},
        showNested: {},
        lawText: {},
        updateOrder: 'desc',
        fetchedInitialUpdates: false
    };

    // Updates
    $scope.updatesPagination = angular.extend({}, PaginationModel);
    $scope.updates = [];

    $scope.loading = false;
    $scope.listingLimit = 10;

    /**
     *
     */
    $scope.init = function() {
        $scope.selectedView = 1;
        $scope.loading = true;
        $scope.lawTreeResponse = LawTreeApi.get({lawId: $scope.curr.lawId}, function(){
            $scope.curr.lawRoot = $scope.lawTreeResponse.result;
            $scope.curr.lawTree = $scope.curr.lawRoot.documents.documents.items;
            if (!$scope.curr.lawTree.length) {
                $scope.curr.showDoc[$scope.curr.lawRoot.documents.locationId] = true;
            }
            $scope.fetchLawDoc($scope.curr.lawRoot.documents);
            $scope.setHeaderText($scope.curr.lawRoot.info.name + " Law");
            if (!$routeParams.location) {
                $scope.loading = false;
            }
            $timeout(function() {
                if ($routeParams.location) {
                    $scope.navigateToLawDoc({locationId: $routeParams.location});
                }
            });
        });
    };

    /**
     *
     * @param node
     */
    $scope.fetchLawDoc = function(node) {
        var deferred = $q.defer();
        if (!$scope.curr.lawDocs[node.locationId]) {
            var lawDocResponse = LawDocApi.get({lawId: $scope.curr.lawId, docId: node.locationId}, function(){
                var lawText = lawDocResponse.result.text
                .replace(/\\n\s{2}/g, "<br/><br/>&nbsp;&nbsp;")
                .replace(node.title + ".", "<strong>$&</strong><br/>&nbsp;&nbsp;")
                .replace(/\\n/g, " ");
                $scope.curr.lawDocs[node.locationId] = lawDocResponse.result;
                $scope.curr.lawText[node.locationId] = $sce.trustAsHtml(lawText);
                deferred.resolve(lawDocResponse.result);
            });
        }
        else {
            deferred.resolve($scope.curr.lawDocs[node.locationId]);
        }
        return deferred.promise;
    };

    $scope.getUpdates = function(intial) {
        if (!intial || !$scope.fetchedInitialUpdates) {
            $scope.updatesResponse = LawVolumeUpdatesApi.get({lawId: $scope.curr.lawId,
                offset: $scope.updatesPagination.getOffset(),
                limit: $scope.updatesPagination.getLimit(),
                order: $scope.curr.updateOrder},
            function() {
                $scope.curr.fetchedInitialUpdates = true;
                $scope.updatesPagination.setTotalItems($scope.updatesResponse.total);
            },
            function() {
                $scope.updatesPagination.setTotalItems(0);
            });
        }
    };

    $scope.$watch('updatesPagination.currPage', function(newPage, oldPage) {
        if (newPage !== oldPage) {
            $scope.getUpdates();
        }
    });

    /**
     *
     * @param node
     */
    $scope.navigateToLawDoc = function(node, callback) {
        $scope.loading = true;
        var lawDocResult = $scope.fetchLawDoc(node);
        lawDocResult.then(function(doc) {
            var locationsToPath = doc.parentLocationIds.slice(0);
            locationsToPath.shift();
            locationsToPath.push(node.locationId);
            for (var i = 0; i < $scope.curr.lawTree.length; i++) {
                if ($scope.curr.lawTree[i].locationId === locationsToPath[0]) {
                    locationsToPath.shift();
                    $scope.expandNodesBelow($scope.curr.lawTree[i], locationsToPath);
                    break;
                }
            }
            $scope.listingLimit = 1000;
            $timeout(function() {
                $scope.setSearchParam('location', node.locationId);
                $location.hash(node.locationId);
                $scope.loading = false;
                $anchorScroll();
                if (callback) {
                    callback();
                }
            });
        });
    };

    /**
     *
     * @param node
     */
    $scope.toggleLawNode = function(node) {
        var show = !$scope.curr.showNested[node.locationId];
        $scope.curr.showNested[node.locationId] = show;
        if (node.docType === 'SECTION') {
            if (show && !$scope.curr.lawText[node.locationId]) {
                $scope.fetchLawDoc(node);
            }
        }
    };

    $scope.setLink = function(locationId) {
        $location.search('location', locationId).replace();
    };

    /**
     *
     * @param node
     */
    $scope.toggleNodeText = function(node) {
        var show = !$scope.curr.showDoc[node.locationId];
        $scope.curr.showDoc[node.locationId] = show;
        if (show && !$scope.curr.lawText[node.locationId]) {
            $scope.fetchLawDoc(node);
        }
    };

    $scope.getFilterResults = function(filterText) {
        var deferred = $q.defer();
        var term = 'docLevelId:' + filterText + '* OR docLevelId:' + filterText;
        var response = LawVolumeSearchApi.get(
            {lawId: $scope.curr.lawId, term: term, limit: 20, sort: '_score:desc,docLevelId:asc'},
            function() {
                deferred.resolve(response.result.items);
            }, function() {
                deferred.resolve([]);
            });
        return deferred.promise;
    };

    /**
     *
     * @param node
     * @returns {boolean}
     */
    $scope.expandNodesBelow = function(node, filterList) {
        $scope.curr.expanded[node.locationId] = true;
        $scope.curr.showNested[node.locationId] = true;
        if (node.docType === 'SECTION' && (!filterList || filterList[0] === node.locationId)) {
            if (!$scope.curr.lawText[node.locationId]) {
                $scope.fetchLawDoc(node);
            }
        }
        angular.forEach(node.documents.items, function(childNode) {
            if (!filterList || childNode.locationId === filterList[0]) {
                if (filterList) {
                    filterList.shift();
                }
                $scope.expandNodesBelow(childNode, filterList);
            }
        });
    };

    /**
     *
     * @param node
     * @returns {boolean}
     */
    $scope.collapseNodesBelow = function(node) {
        $scope.curr.expanded[node.locationId] = false;
        $scope.curr.showNested[node.locationId] = false;
        if (node.docType === 'SECTION') return false;
        angular.forEach(node.documents.items, function(childNode) {
            $scope.collapseNodesBelow(childNode);
        });
    };

    /**
     *
     * @param node
     * @param depth
     */
    $scope.setNode = function(node, depth) {
        $scope.curr.nodes[depth] = node;
        $timeout(function() {
            $anchorScroll();
        }, 200);
    };

    $scope.keepScrolling = function() {
        $scope.listingLimit += 15;
    };

    $scope.backToListings = function() {
        $location.path(ctxPath + '/laws');
    };

    /* Initialize */
    $scope.init();
}]);


