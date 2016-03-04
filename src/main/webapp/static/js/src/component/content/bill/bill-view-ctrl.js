var billModule = angular.module('open.bill');

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
            var baseVersionRegex = /default|base/i;
            var requestedVersion = baseVersionRegex.exec($routeParams.version) ? ''
                : $routeParams.version && $routeParams.version.toUpperCase();
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
                        $scope.curr.amdVersion = $scope.bill.amendments.items.hasOwnProperty(requestedVersion)
                                ? requestedVersion : $scope.bill.activeVersion;
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
