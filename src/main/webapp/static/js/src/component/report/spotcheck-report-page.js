angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, spotcheckMismatchApi, mismatchSummaryApi) {

    $scope.billCategories = ['Status', 'Bill', 'Type', 'Date', 'Issue', 'Source'];
    $scope.calendarCategories = ['Status', 'Date', 'Error', 'Type', 'Nbr', 'Date/Time', 'Issue', 'Source'];
    $scope.agendaCategories = ['Status', 'Date', 'Error', 'Nbr', 'Committee', 'Date/Time', 'Issue', 'Source'];
    $scope.exampleData = ['New', 'S23', 'Action', '8/11/2016', '#1234', 'Daybreak'];


    $scope.datasource = 'OPENLEG';
    $scope.status = 'OPEN'; // TODO: OPEN status = NEW + EXISTING?

    $scope.mismatchSummary = {};
    $scope.billMismatches = [];

    $scope.init = function (rtmap, rtDispMap, mtmap) {
        // don't think we need rtmap
        // console.log(rtmap);
        // console.log(rtDispMap);
        // console.log(mtmap);

        // TODO: Date will prob be a url search param.
        $scope.date = moment().format('l');

        /** Mismatch Summary API and Testing */
        mismatchSummaryApi.get('OPENLEG')
            .then(function (mismatchSummary) {
                $scope.mismatchSummary = mismatchSummary;
                console.log(mismatchSummary);
            });

        /** Mismatch Detail API and Testing */

        spotcheckMismatchApi.getBills('OPENLEG')
            .then(function (billMismatches) {
                $scope.billMismatches = billMismatches;
                console.log(billMismatches);
            });

    };

    $scope.onDatasourceChange = function () {
        console.log($scope.datasource.selected);
    };

    $scope.onStatusChange = function () {
        console.log($scope.status);
    }
}
