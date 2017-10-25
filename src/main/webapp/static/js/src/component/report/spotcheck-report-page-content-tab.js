angular.module('open.spotcheck')
    .directive('spotcheckReportContentTab', spotcheckReportContentTab)
;

function spotcheckReportContentTab () {
    return {
        restrict: 'E',
        scope: true,
        templateUrl: ctxPath + '/partial/report/spotcheck-report-page-content-tab',
        compile: function compile($elem, attrs, transclude) {
            return {
                pre: function preLink($scope, $elem, $attrs) {
                    $scope.title = $attrs.title;
                    $scope.type = $attrs.type;
                    $scope.paginationId = $scope.type + '-mismatches';
                }
            }
        }
    }
}
