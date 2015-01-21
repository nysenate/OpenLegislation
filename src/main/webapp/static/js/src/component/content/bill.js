var billModule = angular.module('open.bill', ['open.core']);

billModule.factory('BillListingApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:sessionYear', {
        sessionYear: '@sessionYear'
    });
}]);

billModule.factory('BillSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/search/?term=:term&sort=:sort&limit=:limit&offset=:offset', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

billModule.factory('BillGetApi', ['$resource', function($resource) {
    return $resource(apiPath + '/bills/:session/:printNo?detail=true', {
        session: '@session',
        printNo: '@printNo'
    });
}]);

/** --- Parent Bill Controller --- */

billModule.controller('BillCtrl', ['$scope', '$location', '$route', function($scope, $location, $route) {

    $scope.selectedView = parseInt($route.current.params.view, 10) || 0;

    /**
     * Returns a formatted, all lower case string representing the latest milestone status.
     *
     * @param milestones
     * @returns {string}
     */
    $scope.getMilestoneDesc = function(milestones) {
        if (milestones && milestones.size > 0) {
            var milestone = milestones.items.slice(-1)[0];
            var desc = $scope.getStatusDesc(milestone);
            return desc.toLocaleLowerCase();
        }
        return "Introduced";
    };

    $scope.getMilestoneDate = function(milestones) {
        if (milestones && milestones.size > 0) {
            var milestone = milestones.items.slice(-1)[0];
            return moment(milestone.actionDate).format("MMMM DD, YYYY");
        }
    };

    $scope.getStatusDesc = function(status) {
        var desc = "";
        if (status) {
            switch (status.statusType) {
                case "IN_SENATE_COMM":
                    desc = "In Senate " + status.committeeName + " Committee"; break;
                case "IN_ASSEMBLY_COMM":
                    desc = "In Assembly " + status.committeeName + " Committee"; break;
                case "SENATE_FLOOR":
                    desc = "On Senate Floor as Calendar No: " + status.billCalNo; break;
                case "ASSEMBLY_FLOOR":
                    desc = "On Assembly Floor as Calendar No: " + status.billCalNo; break;
                default:
                    desc = status.statusDesc;
            }
        }
        return desc;
    }
}]);

/** --- Bill Search Controller --- */

billModule.controller('BillSearchCtrl', ['$scope', '$filter', '$routeParams', '$location','BillListingApi', 'BillSearchApi',
                      function($scope, $filter, $routeParams, $location, BillListing, BillSearch) {
    $scope.setHeaderText('Browse NYS Bills and Resolutions');

    $scope.billSearch = {
        searched: false,
        term: $routeParams.search || '',
        response: {},
        results: [],
        totalResultCount: 0,
        limit: 6,
        offset: 1,
        page: 1
    };

    $scope.init = function() {
        if ($scope.billSearch.term != '') {
            $scope.simpleSearch();
        }
    };

    $scope.simpleSearch = function() {
        var term = $scope.billSearch.term;
        if (term) {
            $location.search("search", $scope.billSearch.term);
            $scope.billSearch.response = BillSearch.get({
                term: term, sort: $scope.billSearch.sort, limit: $scope.billSearch.limit, offset: $scope.billSearch.offset},
                function() {
                    $scope.billSearch.results = $scope.billSearch.response.result.items || [];
                    $scope.billSearch.totalResultCount = $scope.billSearch.response.total;
                    $scope.billSearch.searched = true;
                });
        }
        else {
            $scope.billSearch.results = [];
            $scope.billSearch.totalResultCount = 0;
        }
    };

    /**
     * Gets the full bill view for a specified printNo and session year.
     * @param printNo {string}
     * @param session {int}
     */
    $scope.getBill = function(printNo, session) {
        if (printNo && session) {
            $scope.billViewResult = BillView.get({printNo: printNo, session: session}, function() {
                if ($scope.billViewResult.success) {
                    $scope.billView = $scope.billViewResult.result;
                }
            });
        }
    };

    $scope.nextPage = function() {
        if ($scope.billSearch.totalResultCount > ($scope.billSearch.offset + $scope.billSearch.limit - 1)) {
            $scope.billSearch.page += 1;
            $scope.billSearch.offset += $scope.billSearch.limit;
            $location.search('page', $scope.billSearch.page);
            $scope.simpleSearch();
        }
    };

    $scope.computeOffset = function(page) {
        return ((page - 1) * $scope.limit) + 1;
    };

    $scope.init();
}]);

/** --- Bill View Controller --- */

billModule.filter('resolutionOrBill', function() {
    return function(input) {
        return (input) ? "Resolution" : "Bill";
    }
});

billModule.filter('prettyAmendVersion', function() {
    return function(input) {
        return (input) ? input : "Original";
    }
});

billModule.filter('prettySponsorMemo', function($sce){
    var headingPattern = /(([A-Z][A-Za-z ]+)+:)/g;
    return function(memo) {
        if (memo) {
            var htmlMemo = memo.replace(headingPattern, "<div class='bill-memo-heading'>$1</div>");
            return $sce.trustAsHtml(htmlMemo);
        }
        return memo;
    }
});

billModule.filter('prettyFullText', function($sce) {
    var lineNumberPattern = /^\s{1,5}[0-9]+/gm;
    return function(text) {
        if (text) {
            if (text.length > 100000) {
                text = text.substring(0, 100000) + "\n --- INTENTIONALLY TRUNCATED ---";
            }
            console.log("---- START BILL TEXT PARSE ----");
            text = text.replace(lineNumberPattern, "   ");
            var section = 1;
            var sectionRegex = new RegExp("^\\s*(S(?:ection)?\\s+(\\d+)\\.)", "gm");
            var match = null;
            while ((match = sectionRegex.exec(text)) !== null) {
                console.log("MATCH -> " + match);
                if (match[2] == section) {
                    console.log("Found section: " + match[2] + " at index " + match.index);
                    text = text.slice(0, match.index) + "<span class='label'>"
                          + "Section " + match[2] + "</span>" + text.slice(match.index + match[0].length, text.length);
                    section++;
                }
            }
        }
        else {
            text = "No full text available.";
        }
        return $sce.trustAsHtml(text);
    }
});

billModule.filter('prettyResolutionText', function($sce) {
    var whereasPattern = /^ *WHEREAS[,; ]/gm;
    var resolvedPattern = /^ *RESOLVED[,; ]/gm;
    return function(text) {
        if (text)  {
            text = text.replace(/-\s+/gm, "")
                .replace(whereasPattern, "<div class='resolution-heading whereas'>WHEREAS</div>")
                .replace(resolvedPattern, "<div class='resolution-heading resolved'>RESOLVED</div>");
        }
        else {
            text = "";
        }
        return $sce.trustAsHtml(text);
    }
});

billModule.filter('voteTypeFilter', function() {
    return function(voteType) {
        switch (voteType) {
            case 'AYE': return 'Aye';
            case 'NAY': return 'Nay';
            case 'AYEWR': return 'Aye with reservations';
            case 'ABS': return 'Absent';
            case 'ABD': return 'Abstained';
            case 'EXC': return 'Excused';
            default: return 'Unknown';
        }
    }
});

billModule.controller('BillViewCtrl', ['$scope', '$filter', '$location', '$routeParams', 'BillGetApi',
    function($scope, $filter, $location, $routeParams, BillGetApi) {

    $scope.response = null;
    $scope.bill = null;
    $scope.amdVersion = "";

    $scope.init = function() {
        $scope.session = $routeParams.session;
        $scope.printNo = $routeParams.printNo;
        $scope.response = BillGetApi.get({printNo: $scope.printNo, session: $scope.session}, function() {
            if ($scope.response.success) {
                $scope.bill = $scope.response.result;
                $scope.setHeaderText('NYS ' + $filter('resolutionOrBill')($scope.bill.billType.resolution) + ' ' +
                                     $scope.bill.basePrintNo + '-' + $scope.bill.session);
                $scope.amdVersion = $scope.bill.activeVersion;
            }
        });
    }();

    $scope.getCurrentVersion = function() {
        if ($scope.bill) {
            return $scope.bill.amendments.items[$scope.selectedVersion];
        }
        return null;
    };

    $scope.setSelectedVersion = function(version) {
        $scope.selectedVersion = version;
    };

    /**
     * The milestones array from the bill api response only includes data for milestones that have been met. This
     * method will return an array such that any missing milestones are also included (with null actionDates).
     * @returns {Array}
     */
    $scope.getPaddedMilestones = function() {
        var milestoneArr = [];
        if ($scope.bill) {
            milestoneArr = $scope.defaultBillMilestones($scope.bill.billType.chamber);
            // Replacing part of the arrays that overlap.. maybe there is a cleaner way?
            [].splice.apply(milestoneArr, [0, $scope.bill.milestones.size].concat($scope.bill.milestones.items));
        }
        return milestoneArr;
    };

    /**
     * Returns an array of default milestones for a bill.
     * @param chamber String - SENATE or ASSEMBLY
     * @returns {Array}
     */
    $scope.defaultBillMilestones = function(chamber) {
        var milestoneArr = [];
        var createMilestone = function(desc) {
            return {statusDesc: desc, actionDate: null};
        };
        var senateMilestones = [
            createMilestone("In Senate Committee"),
            createMilestone("On Senate Floor"),
            createMilestone("Passed Senate")
        ];
        var assemblyMilestones = [
            createMilestone("In Assembly Committee"),
            createMilestone("On Assembly Floor"),
            createMilestone("Passed Assembly")
        ];
        if (chamber == 'SENATE') {
            milestoneArr = milestoneArr.concat(senateMilestones).concat(assemblyMilestones);
        }
        else {
            milestoneArr = milestoneArr.concat(assemblyMilestones).concat(senateMilestones);
        }
        milestoneArr = milestoneArr.concat([
            createMilestone("Sent to Governor"),
            createMilestone("Signed Into Law")]);
        return milestoneArr;
    }
}]);

billModule.directive('votePie', [function() {
    var convertVotesToSeries = function(votes) {
        var colors = {
            'AYE': '#43ac6a', 'AYEWR': '#348853', 'NAY': '#f04124', 'ABD': '#666', 'EXC': '#ccc', 'ABS': '#f1f1f1'
        };
        var series = [];
        for (var code in votes) {
            if (votes.hasOwnProperty(code)) {
                series.push({name: code, y: votes[code].size, color: colors[code]});
            }
        }
        return series;
    };
    return {
        restrict: 'AE',
        scope: {
            votes: '=',
            plotBg: '@',
            height: '@',
            width: '@'
        },
        replace: true,
        link: function($scope, $element, $attrs) {
            $element.highcharts({
                credits: {
                    enabled: false
                },
                chart: {
                    plotBackgroundColor: $scope.plotBg,
                    plotBorderWidth: 0,
                    plotShadow: true,
                    height: eval($scope.height),
                    width: eval($scope.width),
                    spacing: [0, 0, 0, 0]
                },
                title: {
                    text: null
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: false,
                        cursor: 'pointer',
                        size: 90
                    }
                },
                series: [{
                    type: 'pie',
                    name: 'Votes',
                    data: convertVotesToSeries($scope.votes)
                }]
            });
        }
    }
}]);