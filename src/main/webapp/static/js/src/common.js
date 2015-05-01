var commonModule = angular.module('open.common', ['ngTable']);

commonModule.filter('default', ['$filter', function($filter) {
    return function(input, defaultVal) {
        return (!input) ? defaultVal : input;
    };
}]);

commonModule.filter('moment', ['$filter', function($filter) {
    return function(input, format, defaultVal) {
        if (input) {
            return moment(input).format(format);
        }
        else {
            return (typeof defaultVal !== 'undefined') ? defaultVal : "--";
        }
    };
}]);

/**
* Appends an appropriate ordinal suffix to the input number
*/
commonModule.filter('ordinalSuffix', ['$filter', function ($filter) {
    var suffixes = ["th", "st", "nd", "rd"];
    return function(input) {
        if (typeof input==='number' && (input%1)===0) {
            var relevantDigits = (input < 20) ? input % 20 : input % 10;
            return input.toString().concat((relevantDigits <= 3) ? suffixes[relevantDigits] : suffixes[0]);
        } else {
            return "D:"
        }
    };
}]);

/**
 * The toggle-panel directive wraps your content in expandable/collapsible container.
 *
 * Ex Usage
 * -----
 * <toggle-panel label="My Title" open="true" extra-classes="my-css">
 *   Insert your content here...
 * </toggle-panel>
 *
 * Attributes
 * ----------
 * label (String) The text for your container header
 * open (boolean) Set to true to expand the content, false to collapse
 * extra-classes (String) Any css classes you want to apply to the outermost toggle panel container
 * show-tip (boolean) Set to true to see a 'Click to expand section' tip when panel is collapsed.
 */
commonModule.directive('togglePanel', [function(){
    return {
        restrict: 'E',
        scope: {
            label: "@",
            extraClasses: "@"
        },
        replace: true,
        transclude: true,
        template:
            '<div class="panel white no-padding margin-bottom-10 {{extraClasses}}">' +
               '<label class="panel-label" id="billSponsorInfo" ng-click="open=!open">' +
                   '<a class="blue1">{{label}}</a>' +
                   '<span class="text-xsmall margin-left-20" ng-show="showTip && !open">(Click to expand section)</span>' +
                   '<i class="right" ng-class="{\'icon-arrow-up4\': open, \'icon-arrow-down5\': !open}"></i>' +
               '</label>' +
               '<div class="panel-content" ng-transclude></div>' +
            '</div>',
        link : function($scope, $element, $attrs) {
            // Convert attribute value to boolean using watch
            $scope.$watch($attrs.open, function(open) {
                $scope.open = open;
            });
            $scope.$watch($attrs.showTip, function(showTip) {
                $scope.showTip = showTip;
            });
            $scope.$watch('open', function(newOpen, oldOpen){
                var panelElem = $element.children(".panel-content");
                (newOpen) ? panelElem.slideDown(200) : panelElem.slideUp(200);
            });
        }
    }
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

//commonModule.directive('mismatchDiff', function(){
//    return {
//        restrict: 'E',
//        scope: {
//            diff: '='
//        },
//        template:
//            "<span ng-repeat='segment in diff' ng-class=\"{'mismatch-diff-equal': segment.operation=='EQUAL', " +
//            "'mismatch-diff-insert': segment.operation=='INSERT', 'mismatch-diff-delete': segment.operation=='DELETE'}\" >" +
//                "{{segment.text}}" +
//            "</span>"
//    };
//});