var commonModule = angular.module('common');

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

/** --- Datatables directive --- */

commonModule.directive('datatable', function() {
    return {
        scope: {
            options: '=',
            columns: '=',
            tabledata: '=',
            filterfn: '=',
            labelrow: '='
        },
        link: function(scope, element, attrs) {
            // Apply DataTable options, use defaults if none specified by user
            var options;
            if (scope.options) {
                options = scope.options;
            }
            else {
                options = {
                    lengthChange: false,
                    pageLength: 100,
                    searching: false
                };
            }

            if (scope.columns) {
                options.columns = scope.columns;
            }

            if (scope.tabledata) {
                options.data = scope.tabledata;
            }

            function rebuildTable(){
                if(thisTable && scope.tabledata && scope.filterfn) {
                    console.log("building table...");
                    var tabledata = scope.tabledata || null;
                    if (tabledata) {
                        thisTable.api().clear();
                        angular.forEach(tabledata, function (row) {
                            if (scope.filterfn(row)) {
                                scope.labelrow(row);
                                thisTable.api().row.add(row);
                            }
                        });
                        thisTable.api().draw();
                    }
                    console.log("table built");
                }
            }

            // watch for any changes to our data, rebuild the DataTable
            scope.$watch('tabledata', rebuildTable);
            scope.$watch('filterfn', rebuildTable);

            // apply the plugin
            var thisTable = $(element).dataTable(options);
        }
    }
});

/** --- Jquery UI Buttonset --- */

commonModule.directive('buttonset', ['$timeout', function($timeout){
    return {
        scope: {
            labels: '=',
            fn: '='
        },
        link: function($scope, element, attrs) {
            $timeout(function() {
                element.buttonset();
            }, 0);

            $scope.$watch('labels', $scope.fn, true);
        },
        template:
            "<input ng-repeat-start='(label, enabled) in labels' id='{{label}}' type='checkbox' ng-model='labels[label]'>" +
            "<label ng-repeat-end for='{{label}}'> {{label}} </label>"
    }
}]);


