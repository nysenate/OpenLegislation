
var coreModule = angular.module('open.core', []);

coreModule.filter('default', ['$filter', function($filter) {
    return function(input, defaultVal) {
        return (!input) ? defaultVal : input;
    };
}]);

coreModule.filter('moment', ['$filter', function($filter) {
    return function(input, format, defaultVal) {
        if (input) {
            return moment(input).format(format);
        }
        else {
            return (typeof defaultVal !== 'undefined') ? defaultVal : "--";
        }
    };
}]);

coreModule.filter('sessionYear', ['$filter', function ($filter) {
    return function (year) {
        return (year % 2 === 0) ? year - 1 : year;
    };
}]);

/**
 * Appends an appropriate ordinal suffix to the input number
 */
coreModule.filter('ordinalSuffix', ['$filter', function ($filter) {
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

/** --- CheckButton --- */

coreModule.directive('checkButton', function(){
    return {
        restrict: 'E',
        scope: {
            btnClass: '@',
            btnModel: '=ngModel',
            ariaLabel: '@'
        },
        transclude: true,
        template:
        "<md-button class='check-butt md-default-theme {{btnClass}}' aria-label='{{ariaLabel}}'" +
        "   ng-mouseenter='hover = true' ng-mouseleave='hover = false'" +
        "   ng-class='{\"md-primary\": btnModel, \"md-raised\": btnModel || hover, \"md-background\": !btnModel }' " +
        "   ng-click='toggle()'> <ng-transclude></ng-transclude>" +
        "</md-button>",
        controller: function($scope) {
            $scope.toggle = function() {
                $scope.btnModel = !$scope.btnModel;
            };
        }
    };
});


/** --- Am Charts --- */

coreModule.directive('amChart', function () {
    return {
        restrict: 'E',
        replace:true,
        scope: {
            chartId: '@',
            chartClass: '@',
            chartConfig: '=',
            chartData: '='
        },
        template: '<div id="{{chartId}}" class="am-chart {{chartClass}}" style="min-width: 310px; height: 400px; margin: 0 auto"></div>',
        link: function (scope, element, attrs) {
            console.log("hi");
            if (!scope.chartId) {scope.chartId = 'am-chart';}
            scope.chart = false;

            var initChart = function () {
                if (scope.chart) {
                    scope.chart.destroy();
                }
                scope.chartConfig.dataProvider = scope.chartData;
                console.log(scope.chartConfig);
                scope.chart = AmCharts.makeChart(scope.chartId, scope.chartConfig);
            };
            scope.$watch(scope.chartData, initChart, true);
        }
    }
});