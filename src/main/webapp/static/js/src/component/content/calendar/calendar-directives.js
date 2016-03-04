var calendarModule = angular.module('open.calendar');

calendarModule.directive('calendarEntryTable', ['BillUtils', function(BillUtils) {
    return {
        scope: {
            year: '=',
            calEntries: '=',
            getCalBillNumUrl: '&',
            highlightValue: '=',
            sectionType: '@',
            scrollTo: '=',
            calEntryFilter: '='
        },
        templateUrl: ctxPath + '/partial/content/calendar/calendar-entry-table',
        controller: function($scope) {
            $scope.billUtils = BillUtils;
            $scope.billPageBaseUrl = ctxPath + '/bills';
            $scope.getCalBillNumUrl = $scope.getCalBillNumUrl();
        }
    };
}]);