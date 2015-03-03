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


/**
 * Converts the properties of an object to an array of key, value pairs.
 * Useful when you want to use the orderBy filter on the properties of an object
 */
coreModule.filter('toDictionaryArray', function () {
    return function (obj) {
        if (!(obj instanceof Object)) return obj;

        var arr = [];
        for (var key in obj) {
            arr.push({ key: key, value: obj[key] });
        }
        return arr;
    }
});

coreModule.factory('PaginationModel', function() {
    return {
        firstPage: 1,
        currPage: 1,
        lastPage: 1,
        itemsPerPage: 6,
        totalItems: 0,

        setTotalItems: function(totalResults) {
            this.totalItems = totalResults;
            this.lastPage = Math.ceil(this.totalItems / this.itemsPerPage);
            if (this.currPage > this.lastPage) {
                this.currPage = this.lastPage;
            }
        },

        needsPagination: function() {
            return this.totalItems > this.itemsPerPage;
        },

        getOffset: function() {
            return this.itemsPerPage * (this.currPage - 1);
        },

        getLimit: function() {
            return this.itemsPerPage;
        },

        nextPage: function() {
            this.currPage += 1;
        },

        hasNextPage: function() {
            return this.currPage < this.lastPage;
        },

        prevPage: function() {
            this.currPage = Math.max(this.currPage - 1, 0);
        },

        hasPrevPage: function() {
            return this.currPage > this.firstPage;
        },

        toLastPage: function() {
            this.currPage = this.lastPage;
        },

        toFirstPage: function() {
            this.currPage = this.firstPage;
        }
    };
});

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
coreModule.directive('togglePanel', [function(){
    return {
        restrict: 'E',
        scope: {
            label: "@",
            extraClasses: "@"
        },
        replace: true,
        transclude: true,
        template:
        '<md-card class="{{extraClasses}}">' +
        '   <md-card-content class="" ng-click="open=!open">' +
        '       <div>' +
        '           <a class="">{{label}}</a>' +
        '           <span flex></span>' +
        '           <i ng-class="{\'icon-arrow-up4\': open, \'icon-arrow-down5\': !open}" style="float: right"></i>' +
        '           <span class="text-xsmall margin-left-20" ng-show="showTip && !open" style="float: right">' +
        '               (Click to expand section)</span>' +
        '       </div>' +
        '   </md-card-content>' +
        '   <md-card-content class="panel-content" ng-cloak ng-transclude></md-card-content>' +
        '</md-card>',
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