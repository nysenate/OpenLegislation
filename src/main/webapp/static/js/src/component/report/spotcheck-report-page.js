angular.module('open.spotcheck')
    .controller('SpotcheckReportCtrl',
        ['$scope', 'SpotcheckMismatchApi', 'SpotcheckMismatchSummaryApi', ReportCtrl]);

function ReportCtrl($scope, spotcheckMismatchApi, mismatchSummaryApi) {

    $scope.billCategories = ['Status', 'Bill', 'Type', 'Date', 'Issue', 'Source'];
    $scope.calendarCategories = ['Status', 'Date', 'Error', 'Type', 'Nbr', 'Date/Time', 'Issue', 'Source'];
    $scope.agendaCategories = ['Status', 'Date', 'Error', 'Nbr', 'Committee', 'Date/Time', 'Issue', 'Source'];
    $scope.exampleData = ['New', 'S23', 'Action', '8/11/2016', '#1234', 'Daybreak'];


    $scope.datasource = {
        values: [
            {
                value: 'OPENLEG',
                label: 'LBDC - OpenLegislation'
            },
            {
                value: 'NYSENATE_DOT_GOV',
                label: 'OpenLegislation - NYSenate.gov'
            }
        ],
        selected: {}
    };

    $scope.mismatchSummary = {};
    $scope.billMismatches = [];

    $scope.mismatches = [];

    $scope.init = function (rtmap, rtDispMap, mtmap) {
        $scope.datasource.selected = $scope.datasource.values[0];
        // don't think we need rtmap
        // console.log(rtmap);
        // console.log(rtDispMap);
        // console.log(mtmap);

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
        // spotcheckApi.mismatches('OPENLEG', 'BILL')
        //     .then(function (r) {
        //         console.log(r);
        //         var mo = new MismatchObservations(r);
        //         $scope.mismatches = mo.getObservations();
        //         console.log($scope.mismatches);
        //     });


        // spotcheckApi.reportSummaries().then(function (r) {
        //     console.log(r);
        // });


        $scope.date = moment().format('l');
    };

    $scope.onDatasourceChange = function () {
        console.log($scope.datasource.selected);
    };

    $scope.checkBoxOptions = {
        initial: 'LBDC - OpenLegislation',
        secondary: 'OpenLegislation - NYSenate.gov'
    };

    $scope.getSummaries = function () {
        $scope.mismatches = $scope.mismatchRows;
        $scope.mismatches.forEach(function (mismatch) {
            console.log(mismatch);
        });
    }
}
