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



/** --- Am Charts --- */

coreModule.directive('amChart', function () {
    return {
        restrict: 'E',
        replace:true,
        scope: {
            chartId: '@',
            chartClass: '@',
            config: '='
        },
        template: '<div id="{{chartId}}" class="am-chart {{chartClass}}" style="min-width: 310px; height: 400px; margin: 0 auto"></div>',
        link: function (scope, element, attrs) {

            var chart = false;

            var initChart = function() {
                if (chart) chart.destroy();
                var chartId = scope.chartId || 'am-chart';
                var config = scope.config || {};
                chart = AmCharts.makeChart(chartId, config);
            };
            initChart();


//            {
//                "type": "serial",
//                "theme": "none",
//                "marginLeft": 20,
//                "pathToImages": "http://www.amcharts.com/lib/3/images/",
//                "dataProvider": [
//                {
//                    "year": "2000",
//                    "value": 0.267
//                }, {
//                    "year": "2001",
//                    "value": 0.411
//                }, {
//                    "year": "2002",
//                    "value": 0.462
//                }, {
//                    "year": "2003",
//                    "value": 0.47
//                }, {
//                    "year": "2004",
//                    "value": 0.445
//                }, {
//                    "year": "2005",
//                    "value": 0.47
//                }],
//                "valueAxes": [{
//                "axisAlpha": 0,
//                "inside": true,
//                "position": "left",
//                "ignoreAxisWidth": true
//            }],
//                "graphs": [{
//                "balloonText": "[[category]]<br><b><span style='font-size:14px;'>[[value]]</span></b>",
//                "bullet": "round",
//                "bulletSize": 6,
//                "lineColor": "#d1655d",
//                "lineThickness": 2,
//                "negativeLineColor": "#637bb6",
//                "type": "smoothedLine",
//                "valueField": "value"
//            }],
//                "chartScrollbar": {},
//                "chartCursor": {
//                "categoryBalloonDateFormat": "YYYY",
//                    "cursorAlpha": 0,
//                    "cursorPosition": "mouse"
//            },
//                "dataDateFormat": "YYYY",
//                "categoryField": "year",
//                "categoryAxis": {
//                "minPeriod": "YYYY",
//                    "parseDates": true,
//                    "minorGridAlpha": 0.1,
//                    "minorGridEnabled": true
//            }
//            }

        }//end watch
    }
});