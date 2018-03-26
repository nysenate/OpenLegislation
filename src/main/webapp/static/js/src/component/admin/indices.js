var adminModule = angular.module('open.admin');

/* Feedback from midterm presentation:
 * - It would be good to implement a "button disabled" functionality on the back end to ensure that only one API call is being made at any given time.
 *   This is for in case the user can get around the front end button disabled, which they can. If the user starts one action and then reloads to another page
 *   and comes back, the user can then fire off a second API call even if the first one hasn't finished.
 * - Implement an API function to get the # of documents in each elastic search index to be displayed on the index cards.
 */

adminModule.factory('IndexAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/index/:indexType', {
        'indexType': '@indexType'
    }, {
        'update': { method: 'PUT' }
    });
}]);

adminModule.controller('IndicesCtrl', ['$scope', '$timeout', '$mdDialog', 'IndexAPI', function($scope, $timeout, $mdDialog, IndexAPI) {
    $scope.indices = [];
    $scope.clearing = {};
    $scope.rebuilding = {};

    $scope.showErrorMessage = function(resp) {
        console.error(resp);
        $mdDialog.show(
            $mdDialog.alert()
                .clickOutsideToClose(true)
                .title('Error ' + resp.status)
                .textContent(resp.data.message)
                .ariaLabel('Error Message')
                .ok('Close')
        );
    };

    $scope.isProcessing = function() {
        var ret = false;
        for (var k1 in $scope.clearing) {
            ret = ret || $scope.clearing[k1];
        }
        for (var k2 in $scope.rebuilding) {
            ret = ret || $scope.rebuilding[k2];
        }
        return ret
    };

    // dynamically set $scope.indices using API call to get index names
    $scope.fetchIndices = function() {
        $scope.indexResp = IndexAPI.get({}, function() {
            if ($scope.indexResp.success === true) {
                $scope.indices = $scope.indexResp.result.items;
                $scope.indices.unshift("ALL");
            }
        }, function (resp) {
            $scope.showErrorMessage(resp)
        });
    };

    // Confirmation prompt for CLEARING the elastic search index
    $scope.showClearConfirm = function(indexName) {
        var confirm = $mdDialog.confirm()
            .clickOutsideToClose(true)
            .title('Clear Confirmation')
            .textContent('Are you sure you want to clear ' + indexName + '?')
            .ariaLabel('Clear Confirmation')
            .ok('Confirm')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            $scope.clearing[indexName] = true;
            IndexAPI.delete({indexType: indexName}).$promise.catch(
                $scope.showErrorMessage
            ).finally(function(){
                $scope.clearing[indexName] = false;
            });
        });
    };

    // Confirmation prompt for REBUILDING the elastic search index
    $scope.showRebuildConfirm = function(indexName) {
        var confirm = $mdDialog.confirm()
            .clickOutsideToClose(true)
            .title('Rebuild Confirmation')
            .textContent('Are you sure you want to rebuild ' + indexName + '?')
            .ariaLabel('Rebuild Confirmation')
            .ok('Confirm')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function() {
            $scope.rebuilding[indexName] = true;
            IndexAPI.update({indexType: indexName}).$promise.catch(
                $scope.showErrorMessage
            ).finally(function(){
                $scope.rebuilding[indexName] = false;
            });
        });
    };

    $scope.init = function() {
        $scope.fetchIndices();
    };

}]);