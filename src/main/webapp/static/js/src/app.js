/** --- Module configuration --- */

/** Such dependencies. wow. */
var openApp = angular.module('open',
// External modules
    ['ngRoute', 'ngResource', 'ngMaterial', 'smart-table', 'ui.calendar', 'angularUtils.directives.dirPagination',
        'diff-match-patch',
// Internal modules
    'open.bill', 'open.agenda', 'open.law', 'open.calendar', 'open.spotcheck', 'open.transcript', 'open.account',
    'open.dashboard', 'open.environment', 'open.notification.subscription', 'open.member']);

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
openApp.controller('AppCtrl', ['$scope', '$location', '$mdSidenav', '$mdDialog', '$http', '$interval',
function($scope, $location, $mdSidenav, $mdDialog, $http, $interval) {
    $scope.header = {text: '', visible: false};
    $scope.activeSession = 2015;

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
     * Navigate to the given url. Useful for ngClick callbacks.
     * @param url string
     */
    $scope.go = function(url) {
        $location.url(url);
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
    }
}]);

/**
 * Main Menu Directive
 * -------------------
 * Constructs the left navigation menu with collapsible sections. Check out the fancy ink ripples!
 * Usage:
 * <material-menu>
 *     <menu-section title="Title of the section">
 *         <menu-item url="URL of the section item">Title of the menu item</menu-item>
*          ...
 *     </menu-section>
 * </material-menu>
 */
openApp.directive('materialMenu', ['$compile', '$rootScope', '$mdSidenav', '$log', '$location',
                  function($compile, $rootScope, $mdSidenav, $log, $location) {
    return {
        scope: {},    // Isolated scope
        template:
        '<nav>' +
        '  <div ng-repeat="section in menu.sections">' +
        '    <a ng-class="{active: isSectionSelected(section)}" class="menu-item menu-title md-menu-item"' +
        '       ng-click="selectSection(section)" md-ink-ripple="#bbb" tab-index="-1"> {{section.title}}' +
        '    </a>' +
        '    <md-divider></md-divider> '  +
        '    <div ng-if="section.items" ng-repeat="item in section.items">' +
        '      <a ng-class="{active: isItemSelected(item)}" target="{{item.target}}"' +
        '         class="menu-item menu-sub-item md-menu-item" md-ink-ripple="#bbb" ' +
        '         ng-show="isSectionSelected(section)" tab-index="-1"' +
        '         ng-href="{{item.url}}">' +
        '         <span><i ng-class="item.icon" class="prefix-icon2"></i><span ng-bind="item.title"></span></span>' +
        '      </a>' +
        '    </div>' +
        '  </div>' +
        '</nav>',
        restrict: 'E',
        replace: true,
        controller : function($scope) {
            $scope.isSectionSelected = function(section) {
                return section.active;
            };
            $scope.isItemSelected = function(item) {
                return item.active;
            };
            $scope.selectSection = function(section) {
                deselectMenu(false);
                section.active = true;
            };
            $scope.selectItem = function(item) {
                deselectMenu(true);
                item.active = true;
                $mdSidenav('left').close();
            };

            function deselectMenu(itemsOnly) {
                angular.forEach($scope.urlMap, function(s) {
                    if (itemsOnly) {
                        s.ref.active = false;
                    }
                    else {
                        s.secRef.active = false;
                    }
                });
            }

            $rootScope.$on('$routeChangeSuccess', function() {
                $scope.urlMap.some(function(secItem) {
                    if (secItem.re.test($location.url())) {
                        $scope.selectSection(secItem.secRef);
                        $scope.selectItem(secItem.ref);
                        return true;
                    }
                    return false;
                });
            });
        },
        compile: function compile($elem, attrs, transclude) {
            return {
                pre: function preLink(scope, $elem, attrs) {
                    // The menu object is used to render the nav.
                    scope.menu = {sections: []};
                    var $sections = $($elem.context).children('menu-section');
                    angular.forEach($sections, function(_s) {
                        var section = {title: _s.title, items: []};
                        angular.forEach($(_s).children('menu-item'), function(_i) {
                            var item = {
                                url: $(_i).attr('url'),
                                title: $(_i).text(),
                                icon: $(_i).attr('icon'),
                                target: $(_i).attr('target')
                            };
                            section.items.push(item);
                        });
                        scope.menu.sections.push(section);
                    });
                    // The url map is used to find route matches when the location changes.
                    scope.urlMap = [];
                    angular.forEach(scope.menu.sections, function(section){
                        angular.forEach(section.items, function(item) {
                            scope.urlMap.push({isSection: false, url: item.url, re: new RegExp('^' + item.url),
                                               ref: item, secRef: section});
                        });
                    });
                    scope.urlMap.sort(function(a,b) {return b.url.length - a.url.length});
                }
            }
        }
    }
}]);
