var calendarModule = angular.module('open.calendar');

calendarModule.controller('CalendarViewCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$q', '$filter', '$timeout',
    'CalendarViewApi', 'CurrentCalendarIdApi',
    function($scope, $rootScope, $routeParams, $location, $q, $filter, $timeout, CalendarViewApi, CurrentCalendarIdApi) {

        $scope.calendarView = null;

        // Top header text
        $scope.calendarHeaderText = '';

        // Stores reference to context path in scope
        $scope.ctxPath = ctxPath;

        // Current state variables
        $scope.curr = {
            state: 'initial',
            activeIndex: 2,
            topListIndex: {},
            openSections: {},
            sectionFilter: {}
        };

        // Page types
        $scope.pageNames = ['sklerch', 'active-list', 'floor', 'updates'];

        $scope.highlightValue = 'none';

        $scope.init = function() {
            $scope.getCalendarViewById($routeParams.year, $routeParams.calNo);
            if ($routeParams.hasOwnProperty('view') && ['active-list', 'sklerch'].indexOf($routeParams['view']) < 0) {
                $scope.tabParam = $routeParams['view'];
            }
            if ('sview' in $routeParams) {
                $scope.previousPage = $routeParams['sview'];
            }

            $scope.$watch('curr.activeIndex', function(newIndex, oldIndex) {
                if (newIndex >=1) {
                    $location.search('view', $scope.pageNames[newIndex]).replace();
                } else if (newIndex < 0 || newIndex >= $scope.pageNames.length) {
                    $location.search('view', null).replace();
                }
            });
        };

        /** --- Tab / Header Management --- */

        $scope.changeTab = function(pageName) {
            console.log("switching to " + pageName);
            var newIndex = $scope.pageNames.indexOf(pageName);
            if (newIndex >= 0) $scope.curr.activeIndex = newIndex;
        };

        /** --- Get Calendar Data --- */

        // Performs tasks that follow the loading of a new calendar view such as setting the header text
        // and alerting child controllers
        function processNewCalendarView() {

            $scope.calendarNum = $scope.calendarView['calendarNumber'];
            $scope.year = $scope.calendarView['year'];

            // Set the header text
            $scope.setHeaderText('Senate Calendar #' + $scope.calendarView['calendarNumber'] + ' - ' +
                $filter('moment')($scope.calendarView.calDate, 'll'));

            // Alert child scopes of new calendar view
            $rootScope.$emit('newCalendarEvent');

            // Scroll to the bill if specified in the url hash
            $scope.scrollToBill($location.hash());

            // Switch to the tab specified for the incoming route or by default the active list or floor tab
            if (!$scope.tabParam || $scope.pageNames.indexOf($scope.tabParam) < 0) {
                if ($scope.calendarView.activeLists.size > 0) {
                    $scope.tabParam = 'active-list';
                } else {
                    $scope.tabParam = 'floor';
                }
            }

            $scope.changeTab($scope.tabParam)
        }

        // Loads a calendar according to the specified year and calendar number
        $scope.getCalendarViewById = function (calendarYear, calendarNo) {
            console.log('loading calendar', calendarYear, calendarNo);
            $scope.curr.state = 'fetching';
            $scope.calendarResponse = CalendarViewApi.get(
                {year: calendarYear, calNo: calendarNo }, function() {
                    if ($scope.calendarResponse.success === true) {
                        console.log('received successful calendar response');
                        $scope.calendarView = $scope.calendarResponse.result;
                        processNewCalendarView();
                    }
                    $scope.curr.state = 'fetching';
                }, function(response) {
                    $scope.setHeaderText(response.data.message);
                    $scope.calendarResponse = response.data;
                    $scope.curr.state = 'fetching';
                });
        };

        // Loads the most recent calendar
        function loadCurrentCalendar(year) {
            var response = CurrentCalendarIdApi.get(
                {year: year}, function() {
                    if (response['success'] && response['result']['size'] > 0) {
                        $scope.calendarView = response['result']['items'][0];
                        $location.path(ctxPath + '/calendars/' + $scope.calendarView['year'] + '/' + $scope.calendarView['calendarNumber']);
                    } else if (year === moment().year()) {
                        loadCurrentCalendar(year - 1);
                    }
                });
        }

        // Scrolls to the bill specified by identifier
        $scope.scrollToBill = function (identifier) {
            identifier = identifier.toUpperCase();
            var billCalNoPattern = /^\d+$/;
            var printNoPattern = /^([SA]\d+)[A-Z]?$/i;
            var calEntryPredicate;
            if (billCalNoPattern.test(identifier)) {
                calEntryPredicate = getCalEntryPredicate('billCalNo', parseInt(identifier));
            } else {
                var pnResult = printNoPattern.exec(identifier);
                if (pnResult) {
                    calEntryPredicate = getCalEntryPredicate('basePrintNo', pnResult[1]);
                } else {
                    return;
                }
            }

            var match;
            match = billInFloorCal(calEntryPredicate);
            if (!match) {
                match = billInActiveList(calEntryPredicate);
                $scope.tabParam = 'active-list';
            }
            if (match) {
                $scope.tabParam = 'floor';
                for (var section in match) {
                    $scope.curr.topListIndex[section] = match[section];
                    $scope.curr.openSections[section] = true;
                }
                $scope.highlightValue = identifier;
            }
        };

        // returns a predicate that takes in an object and returns true if the 'field' property of the object equals the matchingValue
        function getCalEntryPredicate(field, matchingValue) {
            return function(calEntry) {
                return calEntry[field] === matchingValue;
            }
        }

        // Returns true if the current active lists contain an entry that matches the given predicate
        function billInActiveList(calEntryPredicate) {
            var activeLists = $scope.calendarView.activeLists.items;
            for(var ali in activeLists) {
                var index = 0;
                for (var ei in activeLists[ali].entries.items) {
                    if (calEntryPredicate(activeLists[ali].entries.items[ei])) {
                        return {'active-list': index};
                    }
                    index++;
                }
            }
            return false;
        }

        // Searches through every floor calendar entry returning the containing section of the first entry that matches
        //  the given predicate.  Returns false if no entries match
        function billInFloorCal(calEntryPredicate) {
            var floorCals = [$scope.calendarView.floorCalendar];
            for (var k in $scope.calendarView.supplementalCalendars.items) {
                floorCals.push($scope.calendarView.supplementalCalendars.items[k]);
            }
            for (var fci in floorCals) {
                var floorCalSections = floorCals[fci].entriesBySection.items;
                for (var section in floorCalSections) {
                    var index = 0;
                    for (var entry in floorCalSections[section].items) {
                        if (calEntryPredicate(floorCalSections[section].items[entry])) {
                            var res = {};
                            res[section] = index;
                            return res;
                        }
                        index++;
                    }
                }
            }
            return false;
        }

        // Returns to the search page, restoring any saved request parameters
        $scope.backToSearch = function() {
            var currentParams = $location.search();
            var url = ctxPath + '/calendars';
            var firstParam = true;
            for (var param in currentParams) {
                if (param != 'view') {
                    url += (firstParam ? '?' : '&') + (param == 'sview' ? 'view' : param) + '=' + currentParams[param];
                    firstParam = false;
                }
            }
            $location.url(url);
        };

        // Calendar Bill Number Search

        $scope.getCalBillNumUrl = function(year, calBillNum) {
            return ctxPath + '/calendars?view=search&sfield=billCalNo&syear=' + year + '&svalue=' + calBillNum;
        };

        $scope.init();
    }]);


calendarModule.controller('CalendarActiveListCtrl', ['$scope', '$rootScope', function($scope, $rootScope) {

    $scope.activeLists = [];

    $scope.activeListFilter = {};

    $scope.displayedEntries = [];

    // Creates a list of active list supplementals from a full calendar object in the parent scope
    function populateActiveLists() {
        if ($scope.calendarView) {
            $scope.activeLists = [];
            for (var seqNo = 0; $scope.calendarView.activeLists.items.hasOwnProperty(seqNo); seqNo++) {
                $scope.activeLists.push($scope.calendarView.activeLists.items[seqNo]);
            }
            generateActiveListFilter();
        }
    }

    // Initializes the filter object based on the current active lists
    function generateActiveListFilter() {
        $scope.activeListFilter = {};
        angular.forEach($scope.activeLists, function(activeList) {
            $scope.activeListFilter[activeList['sequenceNumber']] = true;
        });
    }

    // Sets the contents of the displayedEntries based on the currently active filter
    function filterActiveListEntries() {
        $scope.displayedEntries = [];
        angular.forEach($scope.activeLists, function(activeList) {
            if ($scope.activeListFilter[activeList['sequenceNumber']]) {
                $scope.displayedEntries = $scope.displayedEntries.concat(activeList['entries']['items']);
            }
        });
    }

    $rootScope.$on('newCalendarEvent', populateActiveLists);

    $scope.$watch('activeListFilter', filterActiveListEntries, true);

    populateActiveLists();
}]);

calendarModule.controller('FloorCalendarCtrl', ['$scope', '$rootScope', function($scope, $rootScope) {

    $scope.floorCals = {};

    $scope.floorCalFilter = {};

    $scope.floorCalVersions = [];

    $scope.displayedSections = {};

    // Creates a dictionary of floor calendar supplementals from a full calendar object in the parent scope
    function populateFloorCals() {
        if ($scope.calendarView) {
            $scope.floorCals = {};
            if ($scope.calendarView['floorCalendar']['year']) {
                $scope.floorCals = {floor: $scope.calendarView['floorCalendar']};
            }
            if ($scope.calendarView['supplementalCalendars']['size'] > 0) {
                angular.forEach($scope.calendarView['supplementalCalendars']['items'], function (floorCal, version) {
                    $scope.floorCals[version] = floorCal;
                });
            }
            generateFloorCalFilter();
        }
    }

    // Constructs a filter object for the currently loaded floor and supplemental calendars
    function generateFloorCalFilter() {
        $scope.floorCalFilter = {};
        $scope.floorCalVersions = [];
        angular.forEach($scope.floorCals, function(floorCal, version) {
            $scope.floorCalFilter[version] = true;
            $scope.floorCalVersions.push(version);
        });
    }

    // Adds sections and entries to the displayed list for floor calendars that pass the filter
    function filterFloorCalendarEntries() {
        $scope.displayedSections = {};
        angular.forEach($scope.floorCals, function(floorCal, version) {
            if ($scope.floorCalFilter[version]) {
                angular.forEach(floorCal['entriesBySection']['items'], function(section, sectionName) {
                    if (!$scope.displayedSections.hasOwnProperty(sectionName)) {
                        $scope.displayedSections[sectionName] = [];
                    }
                    $scope.displayedSections[sectionName] = $scope.displayedSections[sectionName].concat(section['items'])
                });
            }
        });
    }

    $scope.versionSortValue = function(version) {
        if (version == 'floor') {
            return 0;
        } else {
            return version.charCodeAt(0);
        }
    };

    $scope.sectionSortValue = sectionArray.indexOf;

    $rootScope.$on('newCalendarEvent', populateFloorCals);

    $scope.$watch('floorCalFilter', filterFloorCalendarEntries, true);

    populateFloorCals();
}]);

