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

    console.log($location);
    console.log($route);

    $scope.selectedView = parseInt($route.current.params.view, 10) || 0;

    $scope.getStatusDesc = function(status) {
        var desc = "";
        if (status) {
            switch (status.statusType) {
                case "IN_SENATE_COMM":
                    desc = "In Senate " + status.committeeName + " Committee";
                    break;
                case "IN_ASSEMBLY_COMM":
                    desc = "In Assembly " + status.committeeName + " Committee";
                    break;
                case "SENATE_FLOOR":
                    desc = "On Senate Floor as Calendar No: " + status.billCalNo;
                    break;
                case "ASSEMBLY_FLOOR":
                    desc = "On Assembly Floor as Calendar No: " + status.billCalNo;
                    break;
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

    $scope.searchTerm = 'Meow';
    $scope.billResults = {};
    $scope.billViewResult = null;
    $scope.billView = null;
    $scope.totalResults = 0;
    $scope.limit = 10;
    $scope.offset = 1;
    $scope.currentPage = 1;

    $scope.init = function() {
        $scope.searchTerm = $routeParams.search;
        $scope.doSearch();
    };

    $scope.$watch('currentPage', function(newPage, oldPage) {
        if (newPage != oldPage) {
            $scope.doSearch();
        }
    });

    $scope.isValidSearchTerm = function() {
        return $scope.searchTerm != null && $scope.searchTerm.trim() != '';
    };

    $scope.search = function() {
        $location.search("search", $scope.searchTerm);
    };

    $scope.doSearch = function() {
        if ($scope.isValidSearchTerm()) {
            $scope.billResults = BillSearch.get({
                term: $scope.searchTerm, sort: $scope.sort, limit: $scope.limit, offset: $scope.computeOffset($scope.currentPage)},
                function() {
                    $scope.totalResults = $scope.billResults.total;
                    $scope.performedSearch = true;
                    setTimeout(function() {$(".bill-result-anim").addClass("show")}, 0);
                });
        }
    };

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

    $scope.clearSearch = function() {
        $scope.billResults = null;
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

billModule.filter('defaultVersion', function() {
    return function(input) {
        return (input) ? input : "Initial";
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

billModule.controller('BillViewCtrl', ['$scope', '$location', '$routeParams', 'BillGetApi',
    function($scope, $location, $routeParams, BillGetApi) {
    $scope.billResult = null;
    $scope.bill = null;
    $scope.paddedMilestones = [];
    $scope.selectedVersion = "";

    $scope.init = function() {
        $scope.session = $routeParams.session;
        $scope.printNo = $routeParams.printNo;
        $scope.billResult = BillGetApi.get({printNo: $scope.printNo, session: $scope.session}, function() {
            if ($scope.billResult.success) {
                $scope.bill = $scope.billResult.result;
                $scope.selectedVersion = $scope.bill.activeVersion;
                $scope.paddedMilestones = $scope.getPaddedMilestones();
            }
        });
    }();

    $scope.search = function() {
        $location.search("search", $scope.searchTerm);
    };

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