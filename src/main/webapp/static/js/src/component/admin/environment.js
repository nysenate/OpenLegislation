var envModule = angular.module('open.environment', ['open.core']);

/** --- REST resources for getting and setting environment variables --- */

envModule.factory('EnvironmentAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/environment');
}]);

envModule.factory('SetEnvironmentAPI', ['$resource', function ($resource) {
    return $resource(adminApiPath + '/environment/set');
}]);

envModule.controller('EnvironmentCtrl', ['$scope', 'EnvironmentAPI', 'SetEnvironmentAPI',
function ($scope, EnvApi, SetEnvApi) {
    $scope.vars = [];
    $scope.requestedVars = [];

    $scope.init = function() {
        $scope.getVariables();
    };

    $scope.log = function(stuff) {
        console.log(stuff);
    };

    $scope.getVariables = function() {
        $scope.loading = true;
        EnvApi.get({varName: $scope.requestedVars},
            function(response) {
                $scope.error = $scope.loading = false;
                $scope.vars = response.result.items;
            }, function() {
                $scope.loading = false;
                $scope.error = true;
            });
    };

    $scope.setVariable = function(varName) {
        var variable = $scope.getVar(varName);
        if (variable != null) {
            console.log("setting", varName, "to", variable.newValue);
            variable.setting = true;
            SetEnvApi.get({varName: variable.name, value: variable.newValue},
                function() {
                    variable.setting = variable.error = false;
                    variable.value = variable.newValue;
                }, function() {
                    variable.newValue = variable.value;
                    variable.setting = false;
                    variable.error = true;
                });
        }
    };

    $scope.getVar = function(varName) {
        for (var i in $scope.vars) {
            if ($scope.vars[i].name === varName) {
                return $scope.vars[i];
            }
        }
        return null;
    }
}]);
