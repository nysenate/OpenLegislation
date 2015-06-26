
angular.module('open.spotcheck')
    .controller('detailDialogCtrl', ['$scope', '$mdDialog', 'mismatchRow', 'getDetails', 'findFirstOpenedDates',
        'getMismatchId', 'getContentId', 'getContentUrl',
function($scope, $mdDialog, mismatchRow, getDetails, findFirstOpenedDates, getMismatchId, getContentId, getContentUrl) {

    $scope.selectedIndex = 0;

    $scope.getDetails = getDetails;
    $scope.findFirstOpenedDates = findFirstOpenedDates;
    $scope.getMismatchId = getMismatchId;
    $scope.getContentId = getContentId;
    $scope.getContentUrl = getContentUrl;

    $scope.reportType = mismatchRow.refType;


    $scope.newDetails = function (newmismatchRow) {
        $scope.details = newmismatchRow;

        console.log(newmismatchRow)
        $scope.contentId = $scope.getContentId($scope.reportType, newmismatchRow.observation.key);
        $scope.contentUrl = $scope.getContentUrl($scope.reportType, newmismatchRow.observation.key);
        $scope.observation = newmismatchRow.observation;
        $scope.currentMismatch = newmismatchRow.mismatch;
        $scope.multiLine = $scope.currentMismatch.referenceData.split(/\n/).length > 1 ||
                $scope.currentMismatch.observedData.split(/\n/).length > 1;
        $scope.allMismatches = newmismatchRow.observation.mismatches.items;

        $scope.firstOpened = $scope.findFirstOpenedDates($scope.currentMismatch, $scope.observation);
        $scope.formatDisplayData();
    };

    $scope.openNewDetail = function(mismatchId) {
        $scope.newDetails($scope.getDetails(mismatchId));
    };

    $scope.cancel = function () {
        $mdDialog.hide();
    };

    $scope.isBillTextMismatch = function() {
        return ['BILL_TEXT_LINE_OFFSET', 'BILL_TEXT_CONTENT'].indexOf($scope.currentMismatch.mismatchType) >= 0;
    };

    $scope.billTextCtrls = {
        normalizeSpaces: false,
        removeNonAlphaNum: false,
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

    function formatBillText() {
        var texts = [$scope.currentMismatch.referenceData, $scope.currentMismatch.observedData];
        if ($scope.billTextCtrls.removeLinePageNums) {
            texts = texts.map(removeLinePageNumbers);
        }
        if ($scope.billTextCtrls.removeNonAlphaNum) {
            texts = texts.map(function (text) {return text.replace(/(?:[^\w]|_)+/g, '')});
        } else if ($scope.billTextCtrls.normalizeSpaces) {
            texts = texts.map(function (text) {return text.replace(/[ ]+/g, ' ')});
            texts = texts.map(function (text) {return text.replace(/^[ ]+|[ ]+$/gm, '')});
        }

        $scope.lbdcData = texts[0];
        $scope.openlegData = texts[1];
    }

    $scope.formatDisplayData = function() {
        if ($scope.isBillTextMismatch()) {
            formatBillText();
        } else {
            $scope.lbdcData = $scope.currentMismatch.referenceData;
            $scope.openlegData = $scope.currentMismatch.observedData;
        }
    };

    function init() {
        $scope.newDetails(mismatchRow);
    }

    init();
}]);

