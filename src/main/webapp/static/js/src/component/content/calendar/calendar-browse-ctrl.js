var calendarModule = angular.module('open.calendar');

calendarModule.controller('CalendarBrowseCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$timeout', '$q',
    '$mdToast', '$mdMedia', 'CalendarIdsApi',
    function($scope, $rootScope, $routeParams, $location, $timeout, $q, $mdToast, $mdMedia, CalendarIdsApi) {

        $scope.curr = {
            prevStart: null,       // Keep track of the last month the user viewed
            monthHasEvents: false, // Indicates if the current month has any events,
            startDate: null
        };

        $scope.eventSources = [];
        $scope.calendarConfig = null;
        $scope.calendarIds = {};
        $scope.requestsInProgress = 0;
        $scope.events = [];
        $scope.loadedYears = [];    // years for which ids have been loaded into the event list

        // Initialize the calendar
        $scope.init = function () {
            $scope.eventSources.push(getEventSourcesObject());
            $scope.calendarConfig = getCalendarConfig();
            if ('bdate' in $routeParams) {
                $scope.setCalendarDate($routeParams['bdate']);
            } else {
                $scope.renderCalendar();
            }
        };

        // Render the calendar using the default settings
        $scope.renderCalendar = function () {
            $timeout(function () {
                angular.element('#calendar-date-picker').fullCalendar('render');
            });
        };

        $rootScope.$on('renderCalendarEvent', $scope.renderCalendar);

        // Render the calendar using a specific date
        $scope.setCalendarDate = function(date) {
            var momentDate = moment(date);
            if (momentDate.isValid()) {
                $timeout(function() {
                    angular.element('#calendar-date-picker').fullCalendar('gotoDate', momentDate.toDate());
                    $scope.renderCalendar();
                });
            }
        };

        // Fetches the calendar info for a given year from the server
        function getCalendarIds(year) {
            var deferred = $q.defer();
            var idResponse = CalendarIdsApi.get({year: year, limit: 'all'},
                function() {
                    if (idResponse.success) {
                        $scope.calendarIds[year] = idResponse.result.items;
                        deferred.resolve($scope.calendarIds);
                    } else {
                        deferred.reject('unsuccessful calendar id request');
                    }
                });
            return deferred.promise;
        }

        // Gets the object model for a single session event
        function getEvent(calendarId) {
            var fitsFull = window.innerWidth > 820;
            var title = (fitsFull)
                ? ('Floor Cal #' + calendarId.calendarNumber + ' - ' + calendarId.floorCalendar.totalEntries + ' Bills\n' +
            calendarId.supplementalCalendars.size + ' Supplementals\n' +
            calendarId.activeLists.size + ' Active Lists')
                : ('Calendar ' + calendarId.calendarNumber);
            return {
                title: title,
                start: calendarId.calDate,
                calNo: calendarId.calendarNumber,
                className: 'session-day'
            };
        }

        // Fetches all the events during a given date range
        function getCalendarEvents(start, end, callback) {
            var calendarIdPromises = [];
            var years = [];
            for (var year = start.getFullYear(); year <= end.getFullYear(); year++) {
                if ($scope.activeYears.indexOf(year) >= 0 && $scope.loadedYears.indexOf(year) < 0) {
                    calendarIdPromises.push(getCalendarIds(year));
                    years.push(year);
                }
            }
            if (calendarIdPromises.length > 0) {
                $scope.requestsInProgress += 1;
                $q.all(calendarIdPromises).then(function () {
                    for (var i in years) {
                        if (years.hasOwnProperty(i) && $scope.loadedYears.indexOf(years[i]) < 0) {
                            console.log("Fetching year " + years[i]);
                            var foundMonths = [];
                            $scope.calendarIds[years[i]]
                                .map(getEvent)
                                .forEach(function(event) {
                                    // Keep track of which months we have events for
                                    foundMonths[new Date(event.start).getMonth()] = true;
                                    $scope.events.push(event);
                                });
                            // Add an event on the first day of months that don't have any sessions
                            for (var month = 0; month < 12; month++) {
                                if (!foundMonths.hasOwnProperty(month)) {
                                    var momentDate = moment([years[i], month, 1]);
                                    $scope.events.push({
                                        title: "No sessions exist for the month of " + momentDate.format("MMMM") + ".",
                                        start: momentDate.day("Monday").add(7, "days").format("YYYY-MM-DD"),
                                        //end: momentDate.add(14, "days").format("YYYY-MM-DD"),
                                        className: 'no-session-month'
                                    });
                                }
                            }
                            $scope.loadedYears.push(years[i]);
                        }
                    }
                    $scope.requestsInProgress -= 1;
                    callback($scope.events);
                });
            } else {
                callback($scope.events);
            }
        }

        // Stores all the events and some default options
        function getEventSourcesObject() {
            return {
                events: getCalendarEvents,
                allDay: true,
                className: 'calendar-event',
                editable: false
            }
        }

        // Handle a click event when a session is clicked on the calendar
        function onEventClick(event, jsEvent, view) {
            if (event.calNo) {
                $location.url($scope.getCalendarUrl(event.start.getFullYear(), event.calNo));
            }
        }

        // Set the search param to match the currently viewed month
        function viewRenderHandler(view, element) {
            var monthStart = moment(view.start);
            $scope.setSearchParam('bdate', monthStart.format('YYYY-MM-DD'), !monthStart.isSame(moment(), 'month'));
        }

        // Hide events from different months
        function eventRenderHandler(event, element, view) {
            if (event.start.getMonth() !== view.start.getMonth()) {
                element[0].hidden = true;
            }
        }

        // Configures the full calendar directive.
        // See http://fullcalendar.io/docs/ for complete config options.
        function getCalendarConfig() {
            return {
                editable: false,
                theme: false,
                header:{
                    left: window.innerWidth > 550 ? 'prevYear prev,next nextYear'
                        : 'prev,next',
                    center: 'title',
                    right: 'today'
                },
                viewRender: viewRenderHandler,
                eventRender: eventRenderHandler,
                fixedWeekCount: false,
                aspectRatio: 1.5,
                eventClick: onEventClick,
                weekends: false,
                buttonText: {
                    today: 'View Current month'
                }
            };
        }

        $scope.init();
    }]);
