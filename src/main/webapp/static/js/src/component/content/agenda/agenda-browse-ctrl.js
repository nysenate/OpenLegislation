var agendaModule = angular.module('open.agenda');

agendaModule.controller('AgendaBrowseCtrl', ['$scope', '$rootScope', '$location', '$routeParams', '$timeout', 'AgendaMeetingApi',
    function($scope, $rootScope, $location, $routeParams, $timeout, AgendaMeetingApi) {

        $scope.calendarConfig = {};
        $scope.meetingEventSources = [];
        $scope.meetings = {};
        $scope.agendaSet = {};
        $scope.yearsFetched = {};
        $scope.loading = false;

        $scope.curr = {
            selectedDateTime: null,
            month: null,
            year: null
        };

        $scope.tabInit = function() {
            $scope.setHeaderText('Agenda Calendar');
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
            $scope.calendarConfig = $scope.getCalendarConfig();
            // Set the selected date time to either the route param or the current date.
            // This datetime will set the month the calendar renders first, which will trigger the api call
            // to get the meetings.
            $scope.curr.selectedDateTime =
                ($routeParams['bdate'] && moment($routeParams['bdate']).isValid())
                    ? moment($routeParams['bdate']).toDate()
                    : new Date();
            $scope.curr.month = $scope.curr.selectedDateTime.getMonth();
            $scope.curr.year = $scope.curr.selectedDateTime.getFullYear();
        };

        $scope.renderCalendar = function(selectedDate) {
            $timeout(function() {
                if (selectedDate) {
                    angular.element('#agenda-date-picker').fullCalendar('gotoDate', selectedDate);
                }
                angular.element('#agenda-date-picker').fullCalendar('render');
            });
        };

        // Set the search param to match the currently viewed month
        $scope.viewRenderHandler = function(view, element) {
            var month = moment(view.start).month();
            var year = moment(view.start).year();
            if (!$scope.yearsFetched.hasOwnProperty(year)) {
                $scope.fetchMeetingEvents(year);
            }
            $scope.curr.month = month;
            $scope.curr.year = year;
        };

        $scope.updateSelectedDate = function() {
            $scope.curr.selectedDateTime = new Date($scope.curr.year, $scope.curr.month);
        };

        $scope.$watch('curr.selectedDateTime', function(newDate) {
            $scope.renderCalendar(newDate);
        });

        $scope.agendaEventClickHandler = function(calEvent) {
            if (calEvent.agendaId.year) {
                $location.url($scope.getAgendaUrlFromEvent(calEvent));
            }
        };

        $scope.agendaEventMouseOverHandler = function(calEvent, jsEvent, view) {
            if (calEvent.notes && calEvent.notes.trim()) {
                $(jsEvent.target).append("<div class='cal-tooltip agenda-meeting'>" + calEvent.notes + "</div>");
            }
        };

        $scope.agendaEventMouseOutHandler = function(calEvent, jsEvent, view) {
            $(".cal-tooltip.agenda-meeting").remove();
        };

        $scope.getAgendaUrlFromEvent = function(event) {
            var url = window.ctxPath + '/agendas/' + event.agendaId.year + '/' + event.agendaId.number
                + '?sview=browse' + ((event.committeeId) ? '&comm=' + event.committeeId.name : '');
            return url;
        };

        // Hide events from different months
        function eventRenderHandler(event, element, view) {
            if (event.start.getMonth() !== view.start.getMonth()) {
                element[0].style.opacity = '0.3';
            }
        }

        $scope.fetchMeetingEvents = function(year) {
            $scope.yearsFetched[year] = true;
            $scope.loading = true;
            var meetingResponse = AgendaMeetingApi.get({from: new Date(year, 0, 1).toISOString(),
                    to: new Date(year + 1, 0, 1).toISOString()},
                function() {
                    var meetings = meetingResponse.result.items;
                    var meetingEvents = [];
                    var foundMonths = {};
                    angular.forEach(meetings, function(m) {
                        var weekOfMoment = moment(m.weekOf);
                        foundMonths[weekOfMoment.month()] = true;
                        if (!$scope.agendaSet[m.agendaId.year]) {
                            $scope.agendaSet[m.agendaId.year] = {};
                        }
                        if (!$scope.agendaSet[m.agendaId.year][m.agendaId.number]) {
                            meetingEvents.push({
                                title: 'Agenda ' + m.agendaId.number + ' - ' + m.agendaId.year +
                                ' (Week of ' + weekOfMoment.format('ll') + ')',
                                start: m.weekOf,
                                end: weekOfMoment.endOf('week').toISOString(),
                                allDay: true,
                                agendaId: m.agendaId,
                                color: '#43AC6A',
                                textColor: 'white'
                            });
                            $scope.agendaSet[m.agendaId.year][m.agendaId.number] = true;
                        }
                        meetingEvents.push({
                            title: '\n' + m.committeeId.name + ((m.addendum) ? '\nAddendum: ' + m.addendum : ''),
                            start: m.meeting.meetingDateTime,
                            allDay: false,
                            notes: m.meeting.notes,
                            agendaId: m.agendaId,
                            committeeId: m.committeeId
                        });
                    });
                    // Add an event on the first day of months that don't have any sessions
                    for (var month = 0; month < 12; month++) {
                        if (!foundMonths.hasOwnProperty(month)) {
                            var momentDate = moment([year, month, 1]);
                            meetingEvents.push({
                                title: "No committee meetings exist for the month of " + momentDate.format("MMMM") + ".",
                                start: momentDate.day("Monday").add(7, "days").format("YYYY-MM-DD"),
                                className: 'no-agenda-month'
                            });
                        }
                    }
                    $scope.meetingEventSources.push({
                        events: meetingEvents,
                        editable: false
                    });
                    $scope.loading = false;

                }, function() {
                    $scope.loading = false;
                });
        };

        $scope.getCalendarConfig = function() {
            return {
                editable: false,
                theme: false,
                header:{
                    left: window.innerWidth > 550 ? 'prevYear prev,next nextYear'
                        : 'prev,next',
                    center: 'title',
                    right: 'today'
                },
                fixedWeekCount: false,
                viewRender: $scope.viewRenderHandler,
                //defaultView: 'basicWeek',
                aspectRatio: 1.5,
                hiddenDays: [0, 6],
                timeFormat: 'h:mm TT',
                eventRender: eventRenderHandler,
                eventClick: $scope.agendaEventClickHandler,
                eventMouseover: $scope.agendaEventMouseOverHandler,
                eventMouseout: $scope.agendaEventMouseOutHandler,
                buttonText: {
                    today: 'View Current month'
                }
            };
        };

        $scope.init();
    }
]);
