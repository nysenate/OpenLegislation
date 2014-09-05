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