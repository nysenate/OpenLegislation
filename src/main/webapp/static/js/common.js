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
            tabledata: '='
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

            // apply the plugin
            var thisTable = $(element).dataTable(options);

            // watch for any changes to our data, rebuild the DataTable
            scope.$watch('tabledata', function(value) {
                var val = value || null;
                if (val) {
                    thisTable.api().clear();
                    angular.forEach(value, function(row) {
                        thisTable.api().row.add(row);
                    });
                    thisTable.api().draw();
                }
            });
        }
    }
});
