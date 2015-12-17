var agendaModule = angular.module('open.agenda', ['open.core', 'open.api']);

agendaModule.controller('AgendaCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$route',
    function($scope, $rootScope, $routeParams, $location, $route) {
        $scope.setHeaderVisible(true);
        $scope.selectedView = (parseInt($routeParams.view, 10) || 0) % 3;
        $scope.viewMap = {
            'browse': 0,
            'search': 1,
            'updates': 2
        };

        /** Watch for changes to the current view. */
        $scope.$watch('selectedView', function(n, o) {
            if (n !== o && $location.search().view !== n) {
                $location.search('view', $scope.selectedView).replace();
                $scope.$broadcast('viewChange', $scope.selectedView);
            }
        });

        $scope.$on('$locationChangeSuccess', function() {
            $scope.selectedView = +($location.search().view) || 0;
        });
    }
]);
