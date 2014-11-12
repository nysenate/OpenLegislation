var commonModule = angular.module('common', ['ngTable', 'mm.foundation']);

commonModule.filter('default', ['$filter', function($filter) {
    return function(input, defaultVal) {
        return (!input) ? defaultVal : input;
    };
}]);

commonModule.filter('moment', ['$filter', function($filter) {
    return function(input, format) {
        return moment(input).format(format);
    };
}]);

/** --- Top Navigation --- */

/**
 * Controller to handle the top navigation component.
 */
commonModule.controller('TopNavCtrl', ['$scope', function($scope) {
    $scope.showMobileMenu = false;
}]);

/** --- CheckButton --- */

commonModule.directive('checkButton', function(){
    return {
        restrict: 'E',
        scope: {
            btnclass: '@btnClass',
            btnmodel: '=ngModel'
        },
        transclude: true,
        template:
            "<button type='button' class='check-button {{btnclass}}' ng-class='{success: btnmodel, disabled: !btnmodel }' " +
                "btn-checkbox ng-model='btnmodel' ng-transclude>" +
            "</button>"
    };
});

/** --- Spotcheck Diff --- */

commonModule.directive('mismatchDiff', function(){
    return {
        restrict: 'E',
        scope: {
            diff: '='
        },
        template:
            "<span ng-repeat='segment in diff' ng-class=\"{'mismatch-diff-equal': segment.operation=='EQUAL', " +
            "'mismatch-diff-insert': segment.operation=='INSERT', 'mismatch-diff-delete': segment.operation=='DELETE'}\" >" +
                "{{segment.text}}" +
            "</span>"
    };
});