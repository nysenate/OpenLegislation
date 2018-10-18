/* ------------------ Added Section ------------------ */

angular.module('open.spotcheck')
    .controller('detailDialogCtrl', ['$scope', '$mdDialog', '$filter',
                                     'mismatch', 'source', 'contentType', 'CalendarGetApi', 'SpotcheckList', 'SpotcheckIssueApi', 'SpotcheckRel',  detailDialogCtrl])

    .controller('newIssueDialogCtrl', ['$scope', '$mdDialog', newIssueDialogCtrl]);

function newIssueDialogCtrl ($scope, $mdDialog) {

    $scope.cancel = $mdDialog.cancel;
    $scope.createIssue = function () {
        $mdDialog.hide({
            subject: $scope.subject,
            description: $scope.description
        })
    }

    $scope.createLink = function () {
        $mdDialog.hide({
            linkIssues: $scope.linkIssues
        })
    }
}



function detailDialogCtrl($scope, $mdDialog, $filter, mismatch, source, contentType, calendarGetApi, SpotcheckList, SpotcheckIssueApi, SpotcheckRel) {

    /*&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    ------------------------------------ SPOTCHECK API CODE -------------------------------------------
    &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&*/

    // show the dialogue box for generating an issue
    $scope.showGen = function () {
        mismatch.diffLoading = true;

        $mdDialog.show({
            controller: 'newIssueDialogCtrl',
            template: '<md-dialog>' +
            '  <md-dialog-content>' +
            '<h1 style="font-weight: bold; text-align: center; margin-left: 30px; margin-right: 30px"> Issue Generation </h1>' +
            '<span ng-click="cancel()" class="icon-cross mismatch-diff-view-exit" style="transform: translateY(-60px)" role="button" tabindex="0"></span>' +
            '<div style="margin: 0px 10px;">' +
            '<label for="subjVal">Subject:</label>' +
            '<input type="text" style="width:95%; border: 2px solid #ccc;" id="subjVal" ng-model="subject">' +
            '</div>' +
            '<br>' +
            '<label for="descVal" style="margin-left: 10px" >Description:</label>' +
            '<br>' +
            '<textarea id ="descVal" ng-model="description" name="descVal" rows="8" style="width:88%; margin-left: 10px; border: 2px solid #ccc;" id="fname" name="fname">' +
            '</textarea>' +
            '<br>' +
            '<br>' +
            '<md-button style="margin-left: 35%; font-weight: bold;" class="md-primary md-raised" ng-click="createIssue()">' +
            'Create' +
            '</md-button>' +
            '<br>' +
            '<br>' +
            '  </md-dialog-content>' +
            '</md-dialog>'
        }).then(createIss)
            .catch(function () {
                console.log('canceled issue create');
            })
            .finally(function() {
                mismatch.diffLoading = false;
            })

    };

    // show the dialogue box for linking an issue
    $scope.showLink = function () {
        mismatch.diffLoading = true;

            $mdDialog.show({
                controller: 'newIssueDialogCtrl',
                template: '<md-dialog>' +
                '  <md-dialog-content>' +
                '<h1 style="font-weight: bold; text-align: center"> Issue Linkage </h1>' +
                '<span ng-click="cancel()" class="icon-cross mismatch-diff-view-exit" style="transform: translateY(-60px)" role="button" tabindex="0"></span>' +
                '<div style="margin: 0px 10px;">' +
                '<label for="fname">Search Issues:</label>' +
                '<input ng-model="linkIssues" type="text" style="width:95%; background-color: white;\n' +
                '    background-image: url(\'/image.flaticon.com/icons/svg/49/49116.svg\');' +
                '    background-size: 10px 10px;' +
                '    background-position: 10px 5px;' +
                '    background-repeat: no-repeat;' +
                '    padding-left: 40px;">' +
                '<br>' +
                '</div>' +
                '<br>' +
                '<md-button style="margin: 0px 35%; font-weight: bold;" class="md-primary md-raised" ng-click="createLink()">' +
                'Link' +
                '</md-button>'+
                '<br>' +
                '<br>' +
                '  </md-dialog-content>' +
                '</md-dialog>'
            }).then(createLk)
                .catch(function () {
                    console.log('canceled issue link');
                })
                .finally(function() {
                    mismatch.diffLoading = false;
                })



            var myObj = SpotcheckList.get({project_id : 151});
            setTimeout(function () {
                // console.log(myObj.issues.length);
                var uniqueSubjects = [];
                for (var i = 0; i < myObj.issues.length; i++) {
                    if (!(uniqueSubjects.includes(myObj.issues[i].subject))) {
                        uniqueSubjects.push(myObj.issues[i].subject);
                    }
                }
                console.log(uniqueSubjects);
            }, 1000);
            mismatch.diffLoading = false;

    };

    $scope.closeDialog = function() {
        $mdDialog.hide();
    }

    function createLk(input) {
        var linkIssues = input.linkIssues;
        console.log(linkIssues);

        var myObj = SpotcheckList.get({project_id : 151});
        setTimeout(function () {
            // console.log(myObj.issues.length);
            for (var i = 0; i < myObj.issues.length; i++) {
                console.log("Check: " + myObj.issues[i].subject);
                if (linkIssues == myObj.issues[i].subject) {
                    console.log("MATCH WITH " + myObj.issues[i].subject + " Issue Id: " + myObj.issues[i].id);


                    setTimeout(function()
                    {
                        var issueToLink = myObj.issues[i].id;
                        myObj.$promise.then(function(data) {

                            $scope.ngtextBox = issueToLink;

                            mismatch.issueInput = '/issues/' + issueToLink;
                            mismatch.issue = '#' + issueToLink;

                            function onSuccess() {
                                mismatch.issueSaved = true;
                                $timeout(function () {  // toggle the saved flag to trigger css transition
                                    mismatch.issueSaved = false;
                                });
                            }

                            function onFail(resp) {
                                console.error('Error updating issue id:', resp)
                            }

                            //promise.then(onSuccess, onFail); //TODO
                        });

                    }, 1000);






                    // var issueToLink = myObj.issues[i].id;
                    //
                    //
                    // mismatch.issueInput = 'https://dev.nysenate.gov/issues/' + issueToLink;
                    // mismatch.issue = '#' + issueToLink;
                    //
                    // mismatch.issueSaved = true;
                    //
                    //     mismatch.issueSaved = false;


                    break;
                    break;
                }
            }
        }, 1000);
    }

    function createIss(input) {
        var subject = input.subject;
        var description = input.description;
        console.log(subject);
        console.log(description);

        var xhr = new XMLHttpRequest();
        var url = 'https:///issues.json';
        var params = '{"issue":{"project_id":"151","subject":"'+ subject +'","description":"'+ description +'"}}';


        // return $http(...)
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('X-Redmine-API-Key', '');
        xhr.setRequestHeader('Content-Type', 'application/json');

        //Call a function when the state changes.
        xhr.onreadystatechange = function() {
            if (xhr.readyState == XMLHttpRequest.DONE && xhr.status >= 200 && xhr.status <= 210) {
                alert('Status='+xhr.status+'; Response='+xhr.responseText);
            }
        };

        xhr.send(params);

        // Stalls function so the proper issue_id is generated before it is sent off
        setTimeout(function()
        {
            var myObj = SpotcheckList.get({project_id : 151, limit: 1});
            var issueID = "";
            var mismatchID = 0;
            myObj.$promise.then(function(data) {
                //console.log(data.offset); // Gets the offset value
                //console.log(data.issues['0'].id); // Gets the issue id no. -> NEED ' ' AROUND THE INDEX NUMBER
                issueID = data.issues['0'].id;
                mismatchID = mismatch.id;
                //console.log(mismatch.id);
                $scope.ngtextBox = issueID;

                mismatch.issueInput = '/issues/' + issueID;
                mismatch.issue = '#' + issueID;

                //var promise = spotcheckMismatchTrackingAPI.save(params).$promise;

                function onSuccess() {
                    mismatch.issueSaved = true;
                    $timeout(function () {  // toggle the saved flag to trigger css transition
                        mismatch.issueSaved = false;
                    });
                    mismatch.issue = mismatch.issueInput;
                }

                function onFail(resp) {
                    console.error('Error updating issue id:', resp)
                }

                //promise.then(onSuccess, onFail);
            });

        }, 1000);

    }

    /*--------------------------------- End of Added Section ----------------------------------------------*/

    $scope.reportType = mismatch.refType;

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

    $scope.formatDisplayData = function () {
        var texts = [$scope.currentMismatch.referenceData, $scope.currentMismatch.observedData];
        if ($scope.textControls.removeLinePageNums) {
            texts = texts.map(removeLinePageNumbers);
        }
        switch ($scope.textControls.whitespace) {
            case 'stripNonAlpha':
                texts = texts.map(function (text) {
                    return text.replace(/(?:[^\w\n]|_)+/g, '')
                });
                break;
            case 'normalize':
                texts = texts.map(function (text) {
                    return text.replace(/[ ]+/g, ' ')
                });
                texts = texts.map(function (text) {
                    return text.replace(/^[ ]+|[ ]+$/gm, '')
                });
                break;
        }
        if ($scope.textControls.capitalize) {
            texts = texts.map(function (text) {
                return text.toUpperCase();
            });
        }

        // Swap source and ref if openleg is viewed as the ref
        if (isOpenlegRef()) {
            $scope.referenceData = texts[1];
            $scope.observedData = texts[0];
        } else {
            $scope.referenceData = texts[0];
            $scope.observedData = texts[1];
        }
    };

    /**
     * Return true if openleg is the reference in this comparison
     * @return {boolean}
     */
    function isOpenlegRef() {
        return $filter('isOLRef')(source);
    }

    function setCalDate(year, calNo) {
        $scope.currentMismatch.calDate = 'N/A';
        calendarGetApi.get({year: year, calNo: calNo, full: false}, function(response) {
            $scope.currentMismatch.calDate = response.result.calDate;
        });
    }

    function init() {
        $scope.contentType = contentType;
        $scope.date = moment().format('l');
        console.log('loading detail dialog for', mismatch);
        $scope.observation = mismatch.observedData;
        $scope.currentMismatch = mismatch;
        setDefaultTextOptions(mismatch.mismatchType);
        $scope.formatDisplayData();
        if ($scope.contentType === 'CALENDAR') {
            setCalDate($scope.currentMismatch.key.year, $scope.currentMismatch.key.calNo);
        }
    }

    init();
}