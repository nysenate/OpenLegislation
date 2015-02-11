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

lawModule.controller('LawCtrl', ['$scope', '$location', '$route', function($scope, $location, $route) {

}]);

lawModule.controller('LawListingCtrl', ['$scope', '$location', '$route', 'LawListingApi',
                        function($scope, $location, $route, LawListingApi) {
    $scope.setHeaderText('NYS Laws');
    $scope.curr = {
        selectedView: 0,
        listingLimit: 20
    };
    $scope.init = function() {
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

lawModule.controller('LawViewCtrl', ['$scope', '$routeParams', '$location', '$route', '$timeout', '$anchorScroll', '$sce',
                                     'LawTreeApi', 'LawDocApi',
                        function($scope, $routeParams, $location, $route, $timeout, $anchorScroll, $sce, LawTreeApi, LawDocApi) {
    $scope.curr = {
        lawId : $routeParams.lawId,
        lawRoot: null,
        lawTree: null,
        selectedView : 1,
        expanded: {},
        showDoc: {},
        showNested: {},
        lawText: {}
    };

    $scope.init = function() {
        $scope.lawTreeResponse = LawTreeApi.get({lawId: $scope.curr.lawId, depth: 1}, function(){
            $scope.curr.lawRoot = $scope.lawTreeResponse.result;
            $scope.curr.lawTree = $scope.curr.lawRoot.documents.documents.items;
            $scope.setHeaderText($scope.curr.lawRoot.info.name + " Law");
        });
    };

    $scope.fetchLawDoc = function(node) {
        var lawDocResponse = LawDocApi.get({lawId: node.lawId, docId: node.locationId}, function(){
            var lawText = lawDocResponse.result.text
            .replace(/\\n\s{2}/g, "<br/><br/>&nbsp;&nbsp;")
            .replace(node.title + ".", "<strong>$&</strong><br/>&nbsp;&nbsp;")
            .replace(/\\n/g, " ");
            $scope.curr.lawText[node.locationId] = $sce.trustAsHtml(lawText);
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
        else if (show) {
            var lawTreeResponse = LawTreeApi.get({lawId: $scope.curr.lawId, fromLocation: node.locationId, depth: 1},
            function() {
                node.documents = lawTreeResponse.result.documents.documents;
            });
        }
        $location.search('location', node.locationId);
    };

    $scope.toggleNodeText = function(node) {
        var show = !$scope.curr.showDoc[node.locationId];
        $scope.curr.showDoc[node.locationId] = show;
        if (show && !$scope.curr.lawText[node.locationId]) {
            $scope.fetchLawDoc(node);
        }
    };

    $scope.expandNodesBelow = function(node) {
        $scope.curr.expanded[node.locationId] = true;
        $scope.curr.showNested[node.locationId] = true;
        if (node.docType === 'SECTION') {
            if (!$scope.curr.lawText[node.locationId]) {
                $scope.fetchLawDoc(node);
            }
            return false;
        }
        angular.forEach(node.documents.items, function(childNode) {
            $scope.expandNodesBelow(childNode);
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

    $scope.setTab = function(index, tabTitle) {
        $scope.curr.tabs[index] = {title: tabTitle};
    };

    $scope.setNode = function(node, depth) {
        $scope.curr.nodes[depth] = node;
        $timeout(function() {
            $scope.curr.selectedView = depth;
            $anchorScroll();
        }, 200);
    };

    $scope.backToListings = function() {
        $location.path(ctxPath + '/laws');
    };

    $scope.init();
}]);


