var agendaModule = angular.module('open.agenda');

agendaModule.directive('agendaBillListing', ['BillUtils', function(BillUtils) {
    return {
        restrict: 'E',
        scope: {
            'agendaBills': '=',
            'votes': '=',
            'committee': '=',
            'showTitle': '=',
            'showImg': '='
        },
        templateUrl: ctxPath + '/partial/content/agenda/agenda-bill-listing-view',
        controller: function($scope, $element) {
            $scope.billUtils = BillUtils;
            console.log($scope.agendaBills);
        }
    };
}]);