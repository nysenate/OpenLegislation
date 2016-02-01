/** --- Module configuration --- */

/** Such dependencies. wow. */
var openApp = angular.module('open',
// External modules
    ['ngRoute', 'ngResource', 'ngMaterial', 'smart-table', 'ui.calendar', 'angularUtils.directives.dirPagination',
        'diff-match-patch', 'ngAnimate',
// Internal modules
    'open.bill', 'open.agenda', 'open.law', 'open.calendar', 'open.spotcheck', 'open.transcript',
    'open.admin']);

// Configure the material themes
openApp.config(function($mdThemingProvider) {
    var primaryPalette = $mdThemingProvider.extendPalette('grey', {
        '500': '#f1f1f1',
        '300': '#eee',
        '800': '#444',
        'A100':'#fff',
        'contrastDefaultColor': 'dark'
    });
    var accentPalette = $mdThemingProvider.extendPalette('blue', {
        'A200': '#008cba',
        'A100': '#165b81',
        'A400': '#2b6a90',
        //'A700':'#fff',
        'contrastLightColors': ['A200', 'A100', 'A400', 'A700']
    });
    $mdThemingProvider.definePalette('primaryPalette', primaryPalette);
    $mdThemingProvider.definePalette('bluePalette', accentPalette);
    $mdThemingProvider.theme('default')
        .primaryPalette('primaryPalette')
        .accentPalette('bluePalette');
})
// Disable gestures for now.
.constant('$mdGesture', {})
// Keep trailing slashes since they are needed for some API calls
.config(function($resourceProvider) {
    $resourceProvider.defaults.stripTrailingSlashes = false;
})
// Custom template for pagination
.config(function(paginationTemplateProvider) {
    paginationTemplateProvider.setPath(ctxPath +'/static/tpl/dirPagination.tpl.html');
});

/**
 * App Controller
 * --------------
 *
 * Since AppCtrl is the top-most parent controller, some useful utility methods are included here to be used
 * by the children controller.
 */
openApp.controller('AppCtrl', ['$scope', '$location', '$mdSidenav', '$mdDialog', '$http', '$interval', '$window', 'BillUtils',
function($scope, $location, $mdSidenav, $mdDialog, $http, $interval, $window) {
    $scope.header = {text: '', visible: false};
    $scope.activeSession = 2015;
    $scope.ctxPath = ctxPath;

    /**
     * Toggle the left navigation menu (only works on mobile, left nav is locked on larger screens).
     */
    $scope.toggleLeftNav = function() {
        $mdSidenav('left').toggle();
    };

    /**
     * Set the text of the top header bar. This should be called by any controller that is responsible for
     * rendering a view.
     * @param text string
     */
    $scope.setHeaderText = function(text) {
        $scope.header.text = text;
    };

    /**
     * If the screen is larger than 'sm', indicate whether to display the top header bar.
     * On small screens, the header will always be visible since it contains the mobile menu.
     *
     * @param visible boolean
     */
    $scope.setHeaderVisible = function(visible) {
        $scope.header.visible = visible;
    };

    /**
     * Log a message in the console
     * @param stuff
     */
    $scope.log = function(stuff) {
        console.log(stuff);
    };

    /**
     * Return the keys of the given object in array form
     * @param obj
     * @returns {Array}
     */
    $scope.keys = function(obj) {
        return Object.keys(obj);
    };

    /**
     * Navigate to the given url. Useful for ngClick callbacks.
     * @param url string
     */
    $scope.go = function(url) {
        $location.url(url);
    };

    /**
     * Navigate to the given url in a new tab.
     * @param url string
     */
    $scope.goNewTab = function(url) {
        console.log('hello');
        console.log('opening', url, 'in new tab');
        $window.open(url, '_blank');
    };

    /** Given a moment date object, return an iso-8601 string representing the local time */
    $scope.toZonelessISOString = function (momentDate) {
        return momentDate.format('YYYY-MM-DDTHH:mm:ss.SSS');
    };

    /**
     * Sets the request/search param 'paramName' to paramValue if condition is not false.
     * If param value is null/empty/false or condition is false, the request param is set to null,
     * effectively removing it from the url.  Replaces last url in history
     */
    $scope.setSearchParam = function(paramName, paramValue, condition) {
        $location.search(paramName, (condition !== false) ? paramValue : null).replace();
    };

    $scope.clearSearchParams = function() {
        $location.search({});
    };

    /**
     * Handles cases where an invalid api parameter was given by constructing a dialog from the error response
     */
    $scope.invalidParamDialog = function(response) {
        if (response.status === 400 && response.data.errorCode === 1) {
            var errorData = response.data.errorData;
            var paramName = errorData.parameterConstraint.name;
            $mdDialog.show($mdDialog.alert()
                                .title("Invalid Parameter: " + paramName)
                                .content("Value '" + errorData.receivedValue +
                                            "' is not a valid for request parameter " + paramName)
                                .ok('OK'));
        }
    };

    $scope.notImplementedDialog = function() {
        $mdDialog.show(
            $mdDialog.confirm()
                .title("Not Yet Implemented")
                .content("Coming Soon!")
                .ariaLabel("Feature Not Yet Implemented")
                .theme("md-primary")
                .ok("Can't Wait!!")
                .cancel("I can wait")
        );
    };
}]);