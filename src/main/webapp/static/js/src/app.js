/** --- Module configuration --- */

var openApp = angular.module('open',
// External modules
    ['ngRoute', 'ngResource', 'ngMaterial', 'smart-table', 'ui.calendar', 'angularUtils.directives.dirPagination',
// Internal modules
    'open.bill', 'open.agenda', 'open.law', 'open.calendar', 'open.daybreak', 'open.transcript', 'open.account',
    'open.notification.subscription', 'open.member']);

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
    $mdThemingProvider.theme('success').primaryPalette('green');
    $mdThemingProvider.theme('failure').primaryPalette('red');

})
.config(function($resourceProvider) {
    $resourceProvider.defaults.stripTrailingSlashes = false;
})
.config(function(paginationTemplateProvider) {
    paginationTemplateProvider.setPath(ctxPath +'/static/tpl/dirPagination.tpl.html');
})
.config(function($httpProvider){
    // We set a ui key in the http header to allow front end users to bypass api key restrictions
    $httpProvider.defaults.headers.common['UIKey'] = $("#uikey").val();
});

/**
 * App Controller
 */
openApp.controller('AppCtrl', ['$scope', '$location', '$mdSidenav', function($scope, $location, $mdSidenav) {
    $scope.header = {text: '', visible: false};
    $scope.activeSession = 2015;

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
    $scope.setHeaderText('');
    $scope.dataWeProvide = [
        { type: 'New York State Bills and Resolutions', blurb: 'Discover current and prior legislation that impacts New York State.',
          icon: 'icon-newspaper', url: ctxPath + '/bills'},

        { type: 'New York State Laws', blurb: 'Search through the current laws of NYS.',
          icon: 'icon-bookmarks', url: ctxPath + '/laws'},

        { type: 'Senate Session/Hearing Transcripts', blurb: 'Records of Senate session floor discussion since 1993.',
          icon: 'icon-text', url: ctxPath + '/transcripts'},

        { type: 'Senate Committee Agendas', blurb: 'Committee meetings to discuss bills and the votes to move them to the floor.',
          icon: 'icon-clipboard', url: ctxPath + '/agendas'},

        { type: 'Senate Floor Calendars', blurb: 'Listings of bills that are scheduled for discussion and voting on the senate floor.',
          icon: 'icon-calendar', url: ctxPath + '/calendars'},

        { type: 'Senate/Assembly Membership', blurb: 'Senators and assemblymembers for the current session.',
          icon: 'icon-users', url: ctxPath + '/members'}
    ];

    /** Api Key Registration */
    $scope.signedup = false;
    $scope.email = '';
    $scope.signup = function() {
        $scope.errmsg = '';
        if ($scope.email && $scope.email.indexOf('@') > -1) {
            $scope.processing = true;
            $http.post(ctxPath + "/register/signup", {name:$scope.name, email:$scope.email})
            .success(function(data, status, headers, config) {
                if (data.success == false) {
                    $scope.errmsg = data.message;
                }
                else {
                    $scope.signedup = true;
                }
                $scope.processing = false;
            })
            .error(function(data, status, headers, config) {
                $scope.processing = false;
            });
        }
        else {
            $scope.errmsg = 'Please enter a valid email!';
        }
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
                            var item = {url: $(_i).attr('url'), title: $(_i).text(), icon: $(_i).attr('icon')};
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
