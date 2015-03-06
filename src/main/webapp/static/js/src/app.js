/** --- Module configuration --- */

var openApp = angular.module('open',
// External modules
    ['ngRoute', 'ngResource', 'ngMaterial', 'smart-table', 'ui.calendar', 'angularUtils.directives.dirPagination',
// Internal modules
    'open.bill', 'open.agenda', 'open.law', 'open.calendar', 'open.daybreak', 'open.transcript', 'open.account',
    'open.notification.subscription']);

// Configure the material themes
openApp.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
        .primaryPalette('blue-grey', {
            'hue-1': '50',
            'hue-2': '400',
            'hue-3': '800'
        })
        .accentPalette('light-green', {
            'hue-1': '300',
            'hue-2': '400',
            'hue-3': '600'
        });
    $mdThemingProvider.theme('dark').primaryPalette('grey');
})
.config(function($resourceProvider) {
    $resourceProvider.defaults.stripTrailingSlashes = false;
})
.config(function(paginationTemplateProvider) {
    paginationTemplateProvider.setPath(ctxPath +'/static/bower_components/angular-utils-pagination/dirPagination.tpl.html');
});

/**
 * App Controller
 */
openApp.controller('AppCtrl', ['$scope', '$location', '$mdSidenav', function($scope, $location, $mdSidenav) {
    $scope.header = {text: '', visible: false};

    $scope.toggleLeftNav = function() {
        $mdSidenav('left').toggle();
    };

    $scope.toggleRightNav = function() {
        $mdSidenav('right').toggle();
    };

    $scope.setHeaderText = function(text) {
        $scope.header.text = text;
    };

    $scope.setHeaderVisible = function(visible) {
        $scope.header.visible = visible;
    };

    $scope.go = function(url) {
        $location.url(url);
    }
}]);

openApp.controller('LandingCtrl', ['$scope', '$http', function($scope, $http) {
    $scope.setHeaderVisible(true);
    $scope.email = '';
    $scope.dataWeProvide = [
        { type: 'New York State Bills and Resolutions', blurb: 'From 2009 To Present. Updated in real-time.',
          icon: 'icon-newspaper', url: ctxPath + '/bills'},

        { type: 'New York State Laws', blurb: 'From 2014. Updated weekly.',
          icon: 'icon-bookmarks', url: ctxPath + '/laws'},

        { type: 'Senate Session/Hearing Transcripts', blurb: 'From 1993 to Present.',
          icon: 'icon-text', url: ctxPath + '/transcripts'},

        { type: 'Senate Committee Agendas', blurb: 'From 2009 To Present. Updated in real-time.',
          icon: 'icon-clipboard', url: ctxPath + '/agendas'},

        { type: 'Senate Floor Calendars', blurb: 'From 2009 To Present. Updated in real-time.',
          icon: 'icon-calendar', url: ctxPath + '/calendars'},

        { type: 'Senate/Assembly Membership', blurb: 'Member data',
          icon: 'icon-users', url: ctxPath + '/members'}
    ];

    $scope.signedup = false;

    $scope.signup = function() {
        $scope.errmsg = "";
        $scope.processing = true;

        $http.post(ctxPath + "/register/signup", {name:$scope.name, email:$scope.email}).success(function(data, status, headers, config) {            if (data.success == false) {
                $scope.errmsg = data.message;

            } else {
                $scope.signedup = true;
            }
            $scope.processing = false;
        })
            .error(function(data, status, headers, config) {
                $scope.processing = false;
            });
    };
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
                console.log($location.url());
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
                            var item = {url: $(_i).attr('url'), title: $(_i).text()};
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
