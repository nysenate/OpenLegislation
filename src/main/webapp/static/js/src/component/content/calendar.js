var contentModule = angular.module('content');

contentModule.factory('CalendarViewApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year/:calNo', {
        year: '@year',
        calNo: '@calNo'
    });
}]);

contentModule.factory('CurrentCalendarIdApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year?order=DESC&limit=1', {
        year: '@year'
    });
}]);

contentModule.factory('CalendarIdsApi', ['$resource', function($resource) {
    return $resource(apiPath + '/calendars/:year?limit=all', {
        year: '@year'
    });
}]);

/** --- Calendar View Controller --- */

var calendarController = contentModule.controller('CalendarViewCtrl', ['$scope', '$routeParams', '$location', '$q',
    'CalendarViewApi', 'CurrentCalendarIdApi', 'CalendarIdsApi',
function($scope, $routeParams, $location, $q, CalendarViewApi, CurrentCalendarIdApi, CalendarIdsApi) {
    $scope.eventSources = [];
    $scope.calendarConfig = null;

    $scope.calendarView = null;
    $scope.calendarViewResult = null;
    $scope.calendarIds = {};

    $scope.activeLists = [];
    $scope.supplementalCals = [];

    $scope.ctxPath = ctxPath;

    $scope.init = function() {
        $scope.calendarConfig = $scope.getCalendarConfig();
        $scope.eventSources.push($scope.getEventSourcesObject());
        if ($routeParams.hasOwnProperty('year') && $routeParams.hasOwnProperty('calNo')) {
            $scope.getCalendarViewById($routeParams.year, $routeParams.calNo);
        } else {
            $scope.redirectToCurrentCalendar(moment().year());
        }
    };

    $scope.getCalendarView = function() {
        if ($scope.calendarYear != 0 && $scope.calendarNo != 0) {
            $scope.getCalendarViewById($scope.calendarYear, $scope.calendarNo);
        }
    };

    $scope.getCalendarViewById = function (calendarYear, calendarNo) {
        $scope.calendarViewResult = CalendarViewApi.get(
            {year: calendarYear, calNo: calendarNo }, function() {
                if ($scope.calendarViewResult.success) {
                    $scope.calendarView = $scope.calendarViewResult.result;
                    $scope.populateActiveLists();
                    $scope.populateSupplementals();
                    $scope.setCalendarDate(moment($scope.calendarView.calDate));
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

    $scope.populateSupplementals = function() {
        $scope.supplementalCals = [];
        if ($scope.calendarView.supplementalCalendars.size > 0) {
            $scope.supplementalCals.push($scope.calendarView.supplementalCalendars.items['']);
            for (var version = 'A'; $scope.calendarView.supplementalCalendars.items.hasOwnProperty(version)
                                    ; version = String.fromCharCode(version.charCodeAt(0) + 1)) {
                $scope.supplementalCals.push($scope.calendarView.supplementalCalendars.items[version]);
            }
        }
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
                console.log('getting events for ', year);
                calendarIdPromises.push($scope.getCalendarIds(year));
            }
        }
        console.log(calendarIdPromises);
        $q.all(calendarIdPromises).then(function() {
            console.log($scope.calendarIds);
            for (var year = start.getFullYear(); year <= end.getFullYear(); year++) {
                $scope.calendarIds[year]
                    .map($scope.getEvent)
                    .forEach(function (event) {
                        events.push(event)
                    });
            }
            console.log(events.z());
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
        $scope.calendarNo = event.calNo;
        $scope.calendarYear = event.start.getFullYear();
        $scope.getCalendarView();
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

calendarController.directive('calendarEntryTable', function() {
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

calendarController.filter('sectionDisplayName', function() {
    var sectionNameMap = {
       'ORDER_OF_THE_FIRST_REPORT' : "First Report",
       'ORDER_OF_THE_SECOND_REPORT' : "Second Report",
       'ORDER_OF_THE_SPECIAL_REPORT' : "Special Report",
       'THIRD_READING_FROM_SPECIAL_REPORT' : "Third Reading from Special Report",
       'THIRD_READING' : "Third Reading",
       'STARRED_ON_THIRD_READING' : "Starred on Third Reading"
    };
    return function(input) {
        if (sectionNameMap.hasOwnProperty(input)) {
            return sectionNameMap[input];
        }
        else return "*";
    };
});

calendarController.filter('orderBySection', function() {
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
