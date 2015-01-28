/** --- Module configuration --- */

var openApp = angular.module('open',
    // External modules
    ['ngRoute', 'ngResource', 'ngMaterial', 'smart-table',
    // Internal modules
     'open.bill']);

// Configure the material themes
openApp.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryColor('blue-grey', {
            'hue-1': '50',
            'hue-2': '400',
            'hue-3': '800'
        })
        .accentColor('light-green', {
            'hue-1': '300',
            'hue-2': '400',
            'hue-3': '600'
        });
    $mdThemingProvider.theme('dark').primaryColor('grey');
});

/**
 * App Controller
 */
openApp.controller('AppCtrl', ['$scope', '$mdSidenav', function($scope, $mdSidenav) {
    $scope.header = {text: ''};

    $scope.toggleLeftNav = function() {
        $mdSidenav('left').toggle();
    };

    $scope.setHeaderText = function(text) {
        $scope.header.text = text;
    };
}]);

openApp.controller('LandingCtrl', ['$scope', function($scope) {
    $scope.setHeaderText('Explore legislative information from the NYS Senate');
}]);

/**
 * Main Menu Directive
 * -------------------
 * Constructs the left navigation menu with collapsible sections. Check out the fancy ink ripples!
 * Usage:
 * <material-menu>
 *     <menu-section title="Title of the section" url="Optional Section URL">
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
        '    <a ng-class="{active: isSectionSelected(section)}" ng-href="{{section.url}}"' +
        '       class="menu-item menu-title md-menu-item" md-ink-ripple="#bbb" tab-index="-1"' +
        '       >{{section.title}}' +
        '    </a>' +
        '    <div ng-if="section.items" ng-repeat="item in section.items">' +
        '      <a ng-class="{active: isItemSelected(item)}"' +
        '         class="menu-item menu-sub-item md-menu-item" md-ink-ripple="#bbb" ' +
        '         ng-show="isSectionSelected(section)" tab-index="-1"' +
        '         ng-href="{{item.url}}">' +
        '         <span ng-bind="item.title"></span>' +
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
                deselectMenu();
                section.active = true;
            };
            $scope.selectItem = function(item) {
                deselectMenu(true);
                item.active = true;
            };

            function deselectMenu(itemsOnly) {
                angular.forEach($scope.urlMap, function(s) {
                    if (!itemsOnly || !s.isSection) {
                        s.ref.active = false;
                    }
                });
            }

            $rootScope.$on('$routeChangeSuccess', function() {
                console.log("Route update!" + $location.path());
                $scope.urlMap.some(function(secItem) {
                    if (secItem.re.test($location.path())) {
                        if (secItem.isSection) {
                            $scope.selectSection(secItem.ref);
                        }
                        else {
                            $scope.selectSection(secItem.secRef);
                            $scope.selectItem(secItem.ref);
                        }
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
                        var section = {title: _s.title, url: $(_s).attr('url'), items: []};
                        angular.forEach($(_s).children('menu-item'), function(_i) {
                            var item = {url: $(_i).attr('url'), title: $(_i).text()};
                            section.items.push(item);
                        });
                        scope.menu.sections.push(section);
                    });
                    // The url map is used to find route matches when the location changes.
                    scope.urlMap = [];
                    angular.forEach(scope.menu.sections, function(section){
                        scope.urlMap.push({isSection: true, url: section.url, re: new RegExp('^' + section.url), ref: section});
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