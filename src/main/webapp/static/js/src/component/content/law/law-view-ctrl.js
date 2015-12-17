var lawModule = angular.module('open.law');

/**
 * LawViewCtrl
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

        // Initialize
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

        $scope.collapseNodesBelow = function(node) {
            $scope.curr.expanded[node.locationId] = false;
            $scope.curr.showNested[node.locationId] = false;
            if (node.docType === 'SECTION') return false;
            angular.forEach(node.documents.items, function(childNode) {
                $scope.collapseNodesBelow(childNode);
            });
        };

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
