var coreModule = angular.module('open.core', []);

coreModule.factory('MemberApi', ['$resource', function($resource) {
    return $resource(apiPath + '/members/:sessionYear/:chamber?limit=1000', {
        sessionYear: '@sessionYear',
        chamber: '@chamber'
    });
}]);

coreModule.factory('CommitteeListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/committees/:sessionYear/senate', {
        sessionYear: '@sessionYear'
    });
}]);


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

coreModule.filter('label', function() {
    return function (item, labelMap) {
        if (item in labelMap) {
            return labelMap[item];
        }
        return item;
    };
});

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

coreModule.filter('unCamelCase', function () {
    return function(str) {
        return str.split(/(?=[A-Z])/)
            .map(function (word) {return word.toLowerCase()})
            .join(" ");
    }
});

coreModule.filter('titleCaps', function () {
    return function(str) {
        return str.replace(/\w\S*/g, function (word) {
            return word.charAt(0).toUpperCase() + word.substring(1).toLowerCase();
        });
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
                this.currPage = 1
            }
        },

        reset: function() {
            this.currPage = 1;
        },

        needsPagination: function() {
            return this.totalItems > this.itemsPerPage;
        },

        getOffset: function() {
            return (this.itemsPerPage * (this.currPage - 1)) + 1;
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
 * A page number input designed to work for an angular smart tables pagination template
 */
coreModule.directive('stPaginationInput', function() {
    return {
        restrict: 'E',
        template: '<input type="number" class="select-page" min="1" max="{{numPages}}"' +
        'ng-model="inputPage" ng-change="selectPage(inputPage)" ng-model-options="{debounce:100}">',
        link: function(scope, element, attrs) {
            scope.$watch('currentPage', function(c) {
                scope.inputPage = c;
            });
        }
    }
});

/**
 * Safe Highlights Directive
 * -------------------------
 */
coreModule.factory('safeHighlights', ['$sce', function($sce) {
    return function (results) {
        angular.forEach(results, function(r) {
            if (r.hasOwnProperty('highlights')) {
                for (var prop in r['highlights']) {
                    var highlightCount = r['highlights'][prop].length || 0;
                    for (var i = 0; i < highlightCount; i++) {
                        if (r['highlights'][prop][i]) {
                            r['highlights'][prop][i] =
                                $sce.trustAsHtml(String(r['highlights'][prop][i]).replace(/\\n/g, ' ... '));
                        }
                    }
                }
            }
        });
    }
}]);

/**
 * Updates List
 *
 * Displays a list of updates.
 *
 * Usage
 * -----
 * <update-list updates-response="updatesResponse" [pagination="pagination"]
 *              [show-id="showId"] [show-details="showDetails"]></update-list>
 *
 * Attributes
 * ----------
 * updates-response - The full json updates api response
 * pagination - pagination object - When included, the list will display a pagination bar based on this object
 * show-id - boolean variable - default true - Content Ids will be shown when this is true
 * show-details - boolean variable - default true - Update detail tables will be shown when this is true
 */
coreModule.directive('updateList', ['PaginationModel', function(PaginationModel) {
    return {
        restrict: 'E',
        scope: {
            updateResponse: '=',
            pagination: '=?',
            showId: '=?',
            showDetails: '=?',
            fromDate: '=?',
            toDate: '=?'
        },
        templateUrl: ctxPath + '/partial/core/update-list',
        link: function($scope, $elem, $attrs) {
            $scope.showId = $scope.showId || true;
            $scope.showDetails = $scope.showDetails !== false;
            if (!$scope.pagination) {
                $scope.paginationModel = angular.extend({}, PaginationModel);
                $scope.paginationModel.itemsPerPage = Number.MAX_SAFE_INTEGER;
            }
        }
    };
}]);


/**
 * Update Id
 *
 * Generates a content id string for an update based on its scope
 * Returns a less specific Id if the update is an update token
 */
coreModule.filter('updateId', function() {
    return function (update) {
        var contentType = update['contentType'];
        var id = update['id'];
        var idString = "";
        // Calendars
        if (contentType === 'CALENDAR') {
            idString = 'Calendar ' + id['calendarNumber'] + ' (' + id['year'] + ')';
            if ('fields' in update) {
                if ('supVersion' in update['fields']) {
                    var supVersion = update['fields']['supVersion'];
                    idString += "-" + (supVersion == "" ? "floor" : supVersion)
                } else if ('sequenceNo' in update['fields']) {
                    idString += "-" + update['fields']['sequenceNo'];
                }
            }
        }
        // Agendas
        else if (contentType === 'AGENDA') {
            idString = 'Agenda ' + id['number'] + ' (' + id['year'] + ')';
        }
        // Laws
        else if (contentType === 'LAW') {
            idString = id['lawId'] + ' (' + id['locationId'] + ')';
        }
        // Bills
        else if (contentType === 'BILL') {
            idString = id['basePrintNo'] + '-' + id['session'];
        }
        return idString;
    };
});

/**
 * Image Error Placeholder
 *
 * When set on an img tag, changes the src to a placeholder value if an error occurs while loading the image.
 *
 * Usage
 * -----
 * <img ng-src="imageURL" err-src="placeHolderURL"/>
 */
coreModule.directive('errSrc', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('error', function() {
                if (attrs.src != attrs.errSrc) {
                    attrs.$set('src', attrs.errSrc);
                }
            });
        }
    }
});

coreModule.directive('lineNumbers', function() {
    return {
        restrict: 'E',
        scope: {
            lineStart: '=?',
            lineEnd: '='
        },
        replace: true,
        transclude: true,
        template:
        "<span class='line-numbers'>" +
            "<span ng-repeat='i in range(lineEnd-lineStart + 1) track by $index'>{{$index + lineStart}}</span>" +
        "</span>"
        ,
        link: function($scope) {
            $scope.lineStart = $scope.lineStart || 1;
            $scope.range = function(num) {
                return new Array(num);
            };
        }
    }
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
 * render-closed (boolean) Allows closed content to render when set to true
 * extra-classes (String) Any css classes you want to apply to the outermost toggle panel container
 * show-tip (boolean) Set to true to see a 'Click to expand section' tip when panel is collapsed.
 */
coreModule.directive('togglePanel', [function(){
    return {
        restrict: 'E',
        scope: {
            label: "@",
            extraClasses: "@",
            callback: "&",
            renderClosed: "@"
        },
        replace: true,
        transclude: true,
        template:
        '<md-card class="toggle-panel {{extraClasses}}" ng-class="{\'open\': open}">' +
        '   <md-card-content class="toggle-panel-bar" ng-click="toggle()">' +
        '       <div>' +
        '           <a class="toggle-panel-label">{{label}}</a>' +
        '           <span flex></span>' +
        '           <i ng-class="{\'icon-chevron-up\': open, \'icon-chevron-down\': !open}" style="float: right"></i>' +
        '           <span class="text-xsmall margin-right-20" ng-show="showTip && !open" style="float: right">' +
        '               (Click to expand section)</span>' +
        '       </div>' +
        '   </md-card-content>' +
        '   <md-card-content ng-if="opened || renderClosed" ng-show="open" class="panel-content" ng-cloak ng-transclude></md-card-content>' +
        '</md-card>',
        link : function($scope, $element, $attrs) {
            $scope.opened = false;
            $scope.toggle = function() {
                $scope.open = !$scope.open;
                if ($scope.callback) {
                    $scope.callback($scope.open);
                }
            };
            $scope.renderClosed = $scope.renderClosed == 'true';
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
                $scope.opened = newOpen || $scope.opened;
                //console.log("opened", $scope.opened);
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