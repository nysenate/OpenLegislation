var calendarModule = angular.module('open.calendar', ['open.core']);

calendarModule.factory('CalendarViewApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year/:calNo', {
        year: '@year',
        calNo: '@calNo'
    });
}]);

calendarModule.factory('CurrentCalendarIdApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year?order=DESC&limit=1', {
        year: '@year'
    });
}]);

calendarModule.factory('CalendarIdsApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year?limit=all', {
        year: '@year'
    });
}]);

/** --- Calendar Page Controller --- */

calendarModule.controller('CalendarPageCtrl', ['$scope', '$routeParams', '$location', '$q', '$filter', '$timeout',
                                                'CalendarViewApi', 'CurrentCalendarIdApi', 'CalendarIdsApi',
function($scope, $routeParams, $location, $q, $filter, $timeout, CalendarViewApi, CurrentCalendarIdApi) {

    $scope.calendarView = null;

    $scope.activeLists = [];
    $scope.floorCals = {};

    $scope.calendarHeaderText = "";

    $scope.ctxPath = ctxPath;

    var pageNames = ['search', 'calendar', 'active-list', 'floor', 'updates'];

    $scope.changeTab = function(pageName) {
        $scope.activeIndex = pageNames.indexOf(pageName);
    };

    $scope.setCalendarHeaderText = function() {
        $timeout(function() {   // Set text on next digest to account for delay in active index change
            var pageName = pageNames[$scope.activeIndex];
            var newHeader = "8)";

            console.log('switching to', pageName, 'page');
            if (pageName == "search") {
                newHeader = "Search for Calendars";
            } else if (pageName == "calendar") {
                newHeader = "Browse Calendars";
            } else if (["active-list", "floor", "updates"].indexOf(pageName) >= 0) {
                newHeader = $scope.calendarHeaderText;
            }
            $scope.setHeaderText(newHeader);
        }, 0 );
    };

    $scope.init = function() {
        if ($routeParams.hasOwnProperty('year') && $routeParams.hasOwnProperty('calNo')) {
            $scope.getCalendarViewById($routeParams.year, $routeParams.calNo);
        } else {
            $scope.redirectToCurrentCalendar(moment().year());
        }
        if ($routeParams.hasOwnProperty('view')) {
            $scope.changeTab($routeParams['view']);
        }

        $scope.$watch('activeIndex', function() { $location.search('view', pageNames[$scope.activeIndex]); });
        console.log('cal init!');
    };

    $scope.getCalendarViewById = function (calendarYear, calendarNo) {
        var response = CalendarViewApi.get(
            {year: calendarYear, calNo: calendarNo }, function() {
                if (response.success) {
                    $scope.calendarView = response.result;
                    $scope.calendarHeaderText = "Senate Calendar #" + $scope.calendarView.calendarNumber + " " +
                    $filter('moment')($scope.calendarView.calDate, 'll');
                    $scope.setCalendarHeaderText();
                    $scope.populateActiveLists();
                    $scope.populateFloorCals();
                    $scope.$broadcast('newCalendarEvent');
                }
            });
    };

    $scope.redirectToCurrentCalendar = function(year) {
        $scope.calendarViewResult = CurrentCalendarIdApi.get(
            {year: year}, function() {
                if ($scope.calendarViewResult.success && $scope.calendarViewResult.result.size > 0) {
                    $scope.calendarView = $scope.calendarViewResult.result.items[0];
                    $location.url(ctxPath + '/calendars/' + $scope.calendarView.year + '/' + $scope.calendarView.calendarNumber);
                } else if (year == moment().year()) {
                    $scope.redirectToCurrentCalendar(year - 1);
                }
            });
    };

    $scope.populateActiveLists = function() {
        $scope.activeLists = [];
        for (var seqNo=0; $scope.calendarView.activeLists.items.hasOwnProperty(seqNo); seqNo++) {
            $scope.activeLists.push($scope.calendarView.activeLists.items[seqNo]);
        }
    };

    $scope.populateFloorCals = function() {
        $scope.floorCals = {};
        if ($scope.calendarView.floorCalendar.year) {
            $scope.floorCals = {floor: $scope.calendarView.floorCalendar};
        }
        if ($scope.calendarView.supplementalCalendars.size > 0) {
            angular.forEach($scope.calendarView.supplementalCalendars.items, function (floorCal, version) {
                $scope.floorCals[version] = floorCal;
            });
        }
        console.log($scope.floorCals);
    };

    $scope.init();
}]);

calendarModule.controller('CalendarActiveListCtrl', ['$scope', function($scope) {
    $scope.activeListFilter = {};

    $scope.displayedEntries = [];

    function generateActiveListFilter() {
        console.log('generateActiveListFilter');
        $scope.activeListFilter = {};
        angular.forEach($scope.activeLists, function(activeList) {
            $scope.activeListFilter[activeList['sequenceNumber']] = true;
        });
    }

    function filterActiveListEntries() {
        $scope.displayedEntries = [];
        angular.forEach($scope.activeLists, function(activeList) {
            if ($scope.activeListFilter[activeList['sequenceNumber']]) {
                $scope.displayedEntries = $scope.displayedEntries.concat(activeList['entries']['items']);
            }
        });
        console.log($scope.displayedEntries);
    }

    $scope.$watch('activeLists', generateActiveListFilter, true);

    $scope.$watch('activeListFilter', filterActiveListEntries, true);
}]);

calendarModule.controller('FloorCalendarCtrl', ['$scope', function($scope) {
    $scope.floorCalFilter = {};

    $scope.floorCalVersions = [];

    $scope.displayedSections = {};

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
        if (version == "floor") {
            return 0;
        } else {
            return version.charCodeAt(0);
        }
    };

    var sectionOrder = [
        'ORDER_OF_THE_FIRST_REPORT',
        'ORDER_OF_THE_SECOND_REPORT',
        'ORDER_OF_THE_SPECIAL_REPORT',
        'THIRD_READING_FROM_SPECIAL_REPORT',
        'THIRD_READING',
        'STARRED_ON_THIRD_READING'
    ];

    $scope.sectionSortValue = sectionOrder.indexOf;

    $scope.$watch('floorCals', generateFloorCalFilter, true);

    $scope.$watch('floorCalFilter', filterFloorCalendarEntries, true);
}]);

calendarModule.controller('CalendarPickCtrl', ['$scope', '$q', 'CalendarIdsApi',
    function($scope, $q, CalendarIdsApi) {
        $scope.eventSources = [];
        $scope.calendarConfig = null;
        $scope.calendarIds = {};

        $scope.init = function () {
            console.log('calendar picker init');
            $scope.eventSources.push($scope.getEventSourcesObject());
            $scope.calendarConfig = $scope.getCalendarConfig();
            //angular.element('#calendar-date-picker').fullCalendar('render');
        };

        $scope.setCalendarDate = function(date) {
            // TODO make the calendar picker start on the date of the selected calendar
            //console.log('setting calendar date');
            //$scope.calendarYearDisplay = date.year();
            //$scope.calendarMonthDisplay = date.month();
            //console.log(date, $scope.calendarYearDisplay, $scope.calendarMonthDisplay);
            ////angular.element('#calendar-date-picker').fullCalendar('gotoDate', date);
        };

        $scope.getCalendarIds = function(year) {
            var deferred = $q.defer();
            var promise = CalendarIdsApi.get(
                {year: year}, function() {
                    if (promise.success) {
                        $scope.calendarIds[year] = promise.result.items;
                        deferred.resolve($scope.calendarIds);
                    } else {
                        deferred.reject("unsuccessful calendar id request");
                    }
                });
            return deferred.promise;
        };

        $scope.getEvent = function(calendarId) {
            return {
                title: "#" + calendarId.calendarNumber.toString(),
                start: calendarId.calDate,
                calNo: calendarId.calendarNumber
            };
        };

        $scope.getCalendarEvents = function(start, end, callback) {
            var events = [];
            var calendarIdPromises = [];
            for (var year = start.getFullYear(); year <= end.getFullYear(); year++) {
                if (!$scope.calendarIds.hasOwnProperty(year)) {
                    calendarIdPromises.push($scope.getCalendarIds(year));
                }
            }
            $q.all(calendarIdPromises).then(function() {
                for (var year = start.getFullYear(); year <= end.getFullYear(); year++) {
                    $scope.calendarIds[year]
                        .map($scope.getEvent)
                        .forEach(function (event) {
                            events.push(event)
                        });
                }
                callback(events);
            });
        };

        $scope.getEventSourcesObject = function() {
            return {
                events: $scope.getCalendarEvents,
                allDay: true,
                className: 'calendar-event',
                editable: false
            }
        };

        $scope.onEventClick = function(event, jsEvent, view) {
            $scope.getCalendarViewById(event.start.getFullYear(), event.calNo);
        };

        $scope.viewDisplayHandler = function(view){
            var viewStart = view.start;
            $scope.calendarYearDisplay = viewStart.getFullYear();
            $scope.calendarMonthDisplay = viewStart.getMonth();
        };

        $scope.$watch('isCalendarOpen', function(newValue, oldValue) {
            if (newValue) {
                angular.element('#calendar-date-picker').fullCalendar('render');
            }
        });

        $scope.getCalendarConfig = function() {
            return {
                editable: false,
                theme: false,
                header:{
                    left: 'prev',
                    center: 'title',
                    right: 'today next'
                },
                buttonText: {
                    prev: 'LEFT',//'&laquo',
                    next: 'RIGHT'//'&raquo'
                },
                aspectRatio: 1.5,
                //viewDisplay: $scope.viewDisplayHandler,
                //month: $scope.calendarMonthDisplay,
                //year: $scope.calendarYearDisplay,
                eventClick: $scope.onEventClick
            };
        };

        $scope.init();
    }]);

calendarModule.directive('calendarEntryTable', function() {
    return {
        scope: {
            calEntries: '=calEntries'
        },
        templateUrl: ctxPath + '/partial/content/calendar/calendar-entry-table',
        controller: function($scope) {
            $scope.billPageBaseUrl = ctxPath + '/bills';
        }
    };
});

calendarModule.filter('sectionDisplayName', function() {
    var sectionNameMap = {
       'ORDER_OF_THE_FIRST_REPORT' : "First Report",
       'ORDER_OF_THE_SECOND_REPORT' : "Second Report",
       'ORDER_OF_THE_SPECIAL_REPORT' : "Special Report",
       'THIRD_READING_FROM_SPECIAL_REPORT' : "Third Reading from Special Report",
       'THIRD_READING' : "Third Reading",
       'STARRED_ON_THIRD_READING' : "Starred on Third Reading"
    };
    return function(input) {
        console.log(input);
        if (sectionNameMap.hasOwnProperty(input)) {
            return sectionNameMap[input];
        }
        else return "* " + input;
    };
});

calendarModule.filter('orderBySection', function() {
    var sectionOrder = [
        'ORDER_OF_THE_FIRST_REPORT',
        'ORDER_OF_THE_SECOND_REPORT',
        'ORDER_OF_THE_SPECIAL_REPORT',
        'THIRD_READING_FROM_SPECIAL_REPORT',
        'THIRD_READING',
        'STARRED_ON_THIRD_READING'
    ];
    return function(obj) {
        var array = [];
        Object.keys(obj).forEach(function(key) { array.push(obj[key]); });
        array.sort(function(a, b) {
            return sectionOrder.indexOf(a.items[0].sectionType) - sectionOrder.indexOf(b.items[0].sectionType);
        });
        return array;
    };
});
