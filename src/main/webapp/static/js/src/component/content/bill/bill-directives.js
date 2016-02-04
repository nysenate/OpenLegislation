var billModule = angular.module('open.bill');

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
        '<span ng-if="milestone.actionDate">&nbsp;| {{milestone.actionDate | moment:\'MMM DD, YYYY\'}}</span>' +
        '<span ng-if="milestone.committeeName">&nbsp;| {{milestone.committeeName}}</span>' +
        '<span ng-if="milestone.billCalNo">&nbsp;| Cal #{{milestone.billCalNo}}</span>' +
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
                $scope.billViews = $scope.billIds.map(function(id) {
                    var baseIdStr = id.basePrintNo + '-' + id.session;
                    if ($scope.billRefsMap[baseIdStr]) {
                        return $scope.billRefsMap[baseIdStr];
                    }
                    return angular.extend({}, id, {'idOnly': true});
                });
            }
            else if ($scope.bills) {
                $scope.billViews = $scope.bills;
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
                if (n != o) {
                    $scope.onChange($scope.searchParams);
                }
            });
        }
    }
}]);