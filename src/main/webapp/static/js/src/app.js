/** --- Module configuration --- */

var coreModule = angular.module('open.core', ['ngRoute']);
//var reportModule = angular.module('report', ['ngRoute', commonModule.name]);

var openApp = angular.module('open',
    // External modules
    ['ngRoute', 'ngResource', 'ngMaterial',
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
 */
openApp.directive('materialMenu', ['$compile', '$location', function($compile, $location) {
    return {
        restrict: 'E',
        replace: true,
        scope: {},
        template:
            '<nav>' +
            '  <div ng-repeat="section in menu.sections">' +
            '    <a ng-class="{active: isSectionSelected(section)}" ng-href="{{section.url}}"' +
            '       class="menu-item menu-title md-menu-item" md-ink-ripple="#bbb" ' +
            '       ng-click="selectSection(section)">{{section.title}}' +
            '    </a>' +
            '    <a class="menu-item menu-sub-item md-menu-item" md-ink-ripple="#bbb" ' +
            '       ng-show="isSectionSelected(section)" ' +
            '       ng-repeat="item in section.items"' +
            '       ng-href="{{item.url}}">' +
            '      <span ng-bind="item.title"></span>' +
            '    </a>' +
            '  </div>' +
            '</nav>',
        controller : function($scope) {
            $scope.isSectionSelected = function(section) {
                return section.active;
            };
            $scope.selectSection = function(section) {
                $scope.menu.sections.forEach(function(s) {s.active = false;});
                section.active = true;
            }
        },
        compile: function compile($elem, attrs, transclude) {
            return {
                pre: function preLink(scope, $elem, attrs) {
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
                }
            }
        }
    }
}]);

openApp.controller('TopNavCtrl', ['$scope', '$route', function($scope, $route) {

    $scope.currActiveLink;

    $scope.$on('$routeChangeSuccess', function(event, r) {
        if ($scope.currActiveLink != r.$$route.originalPath) {
            $("nav a").parent().removeClass("active");
            $("nav a[href='" + r.$$route.originalPath + "']").parent().addClass("active");
            $scope.currActiveLink = r.$$route.originalPath;
        }
    });
}]);