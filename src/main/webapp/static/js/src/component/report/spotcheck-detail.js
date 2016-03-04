
angular.module('open.spotcheck')
    .controller('detailDialogCtrl', ['$scope', '$mdDialog', 'mismatchRow',
function($scope, $mdDialog, mismatchRow) {

    $scope.iDiffTab = 0;

    $scope.reportType = mismatchRow.refType;

    $scope.newDetails = function (newMismatchRow) {
        $scope.mismatchRow = newMismatchRow;

        console.log('loading detail dialog for', newMismatchRow);
        $scope.observation = newMismatchRow.observation;
        $scope.currentMismatch = newMismatchRow.mismatch;
        $scope.allMismatches = newMismatchRow.observation.mismatches.items;

        setDefaultTextOptions(newMismatchRow.type);
        $scope.formatDisplayData();
    };

    $scope.$watchGroup(['referenceData', 'displayData'], function () {
        $scope.obsMultiLine = $scope.observedData && $scope.observedData.indexOf('\n') > -1;
        $scope.refMultiLine = $scope.referenceData && $scope.referenceData.indexOf('\n') > -1;
        $scope.multiLine = $scope.obsMultiLine || $scope.refMultiLine;
    });

    $scope.cancel = function () {
        $mdDialog.hide();
    };

    function setDefaultTextOptions(mismatchType) {
        var nonAlphaMismatches = ['BILL_TEXT_LINE_OFFSET', 'BILL_TEXT_CONTENT'];
        var noLinePageNumMismatches = ['BILL_TEXT_CONTENT'];
        if (nonAlphaMismatches.indexOf(mismatchType) > -1) {
            $scope.textControls.whitespace = 'stripNonAlpha';
        }
        if (noLinePageNumMismatches.indexOf(mismatchType) > -1) {
            $scope.textControls.removeLinePageNums = true;
        }
    }

    $scope.whitespaceOptions = {
        initial: 'No Formatting',
        normalize: 'Normalize Whitespace',
        stripNonAlpha: 'Strip Non-Alphanumeric'
    };

    $scope.textControls = {
        whitespace: 'initial',
        capitalize: false,
        removeLinePageNums: false
    };

    var lineNumberRegex = /^( {4}\d| {3}\d\d)/;
    var pageNumberRegex = /^ {7}[A|S]\. \d+(--[A-Z])?[ ]+\d+([ ]+[A|S]\. \d+(--[A-Z])?)?$/;
    var budgetPageNumberRegex = /^[ ]{42,43}\d+[ ]+\d+-\d+-\d+$/;
    var explanationRegex = /^[ ]+EXPLANATION--Matter in ITALICS \(underscored\) is new; matter in brackets\n/;
    var explanationRegex2 = /^[ ]+\[ ] is old law to be omitted.\n[ ]+LBD\d+-\d+-\d+$/;
    var pageLineNumRegex = new RegExp("(?:" + [lineNumberRegex.source, pageNumberRegex.source,
            budgetPageNumberRegex.source, explanationRegex.source, explanationRegex2.source
        ].join(")|(?:") + ")", 'gm');

    function removeLinePageNumbers(text) {
        return text.replace(pageLineNumRegex, '')
            .replace(/\n+/, '\n');
    }

    $scope.formatDisplayData = function() {
        var texts = [$scope.currentMismatch.referenceData, $scope.currentMismatch.observedData];
        if ($scope.textControls.removeLinePageNums) {
            texts = texts.map(removeLinePageNumbers);
        }
        switch ($scope.textControls.whitespace) {
            case 'stripNonAlpha':
                texts = texts.map(function (text) {return text.replace(/(?:[^\w]|_)+/g, '')});
                break;
            case 'normalize':
                texts = texts.map(function (text) {return text.replace(/[ ]+/g, ' ')});
                texts = texts.map(function (text) {return text.replace(/^[ ]+|[ ]+$/gm, '')});
                break;
        }
        if ($scope.textControls.capitalize) {
            texts = texts.map(function(text) { return text.toUpperCase();});
        }

        $scope.referenceData = texts[0];
        $scope.observedData = texts[1];
    };

    function init() {
        $scope.newDetails(mismatchRow);
    }

    init();
}]);

