var agendaModule = angular.module('open.agenda', ['open.core']);

agendaModule.factory('AgendaListingApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/:year?sort=:sort&limit=:limit&offset=:offset', {
        sessionYear: '@year',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

agendaModule.factory('AgendaMeetingApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/meetings/:from/:to', {
        from: '@from',
        to: '@to'
    });
}]);

agendaModule.factory('AgendaSearchApi', ['$resource', function($resource) {
    return $resource(apiPath + '/agendas/search?term=:term', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

agendaModule.factory('AgendaGetApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/:year/:agendaNo/', {
        year: '@year',
        agendaNo: '@agendaNo'
    });
}]);

agendaModule.controller('AgendaCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$route',
    function($scope, $rootScope, $routeParams, $location, $route) {
        $scope.setHeaderVisible(true);
        $scope.selectedView = (parseInt($routeParams.view, 10) || 0) % 3;
        $scope.viewMap = {
            'browse': 0,
            'search': 1,
            'updates': 2
        };

        /** Watch for changes to the current view. */
        $scope.$watch('selectedView', function(n, o) {
            if (n !== o && $location.search().view !== n) {
                $location.search('view', $scope.selectedView);
                $scope.$broadcast('viewChange', $scope.selectedView);
            }
        });

        $scope.$on('$locationChangeSuccess', function() {
            $scope.selectedView = $location.search().view || 0;
        });
    }
]);

agendaModule.controller('AgendaSearchCtrl', ['$scope', '$location', '$route', '$routeParams', 'PaginationModel', 'AgendaSearchApi',
    function($scope, $location, $route, $routeParams, PaginationModel, AgendaSearchApi) {
        $scope.tabInit = function() {
            $scope.setHeaderText('Search Agendas');
        };
        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.itemsPerPage = 10;

        $scope.agendaSearch = {
            searched: false,
            searching: false,
            term: $routeParams.search || '',
            response: {},
            results: [],
            error: false
        };
        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });
        $scope.init = function() {
            $scope.tabInit();
        };
        $scope.simpleSearch = function(resetPagination) {
            var term = $scope.agendaSearch.term;
            if (term) {
                $location.search("search", $scope.agendaSearch.term);
                $scope.agendaSearch.searching = true;
                $scope.agendaSearch.searched = false;
                if (resetPagination) {
                    $scope.pagination.currPage = 1;
                    $location.search('searchPage', 1);
                }
                $scope.agendaSearch.response = AgendaSearchApi.get({
                    term: term, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()},
                function() {
                    if ($scope.agendaSearch.response && $scope.agendaSearch.response.success) {
                        $scope.agendaSearch.error = false;
                        $scope.agendaSearch.results = $scope.agendaSearch.response.result.items || [];
                        $scope.agendaSearch.searched = true;
                        $scope.pagination.setTotalItems($scope.agendaSearch.response.total);
                        $scope.agendaSearch.searching = false;
                    }
                }, function(data) {
                    $scope.agendaSearch.searched = true;
                    $scope.agendaSearch.searching = false;
                    $scope.agendaSearch.error = data.data;
                    $scope.agendaSearch.results = [];
                    $scope.pagination.setTotalItems(0);
                });
            }
        };

        $scope.init();
    }
]);

agendaModule.controller('AgendaBrowseCtrl', ['$scope', '$rootScope', '$location', '$routeParams', '$timeout', 'AgendaMeetingApi',
    function($scope, $rootScope, $location, $routeParams, $timeout, AgendaMeetingApi) {

        $scope.calendarConfig = {};
        $scope.meetingEventSources = [];
        $scope.meetings = {};
        $scope.agendaSet = {};

        $scope.curr = {
            fromDateTime: moment().startOf('year').toDate(),
            toDateTime: moment().endOf('year').toDate()
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
            $scope.renderCalendar($routeParams['bdate']);
            $scope.fetchMeetingEvents();
        };

        $scope.renderCalendar = function(selectedDate) {
            $timeout(function() {
                if (selectedDate && moment(selectedDate).isValid()) {
                    angular.element('#agenda-date-picker').fullCalendar('gotoDate', moment(selectedDate).toDate());
                }
                angular.element('#agenda-date-picker').fullCalendar('render');
            });
        };

        // Set the search param to match the currently viewed month
        $scope.viewRenderHandler = function(view, element) {
            var year = moment(view.start).year();
            if (!$scope.agendaSet.hasOwnProperty(year)) {
                console.log(year);
            }
        };

        $scope.agendaEventClickHandler = function(calEvent) {
            $location.url($scope.getAgendaUrlFromEvent(calEvent));
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
                                     + '?comm=' + event.committeeId.name + '&sview=browse';
            return url;
        };

        $scope.fetchMeetingEvents = function() {
            var meetingResponse = AgendaMeetingApi.get({from: $scope.curr.fromDateTime.toISOString(),
                                                        to: $scope.curr.toDateTime.toISOString()},
                function() {
                    var meetings = meetingResponse.result.items;
                    var meetingEvents = [];
                    angular.forEach(meetings, function(m) {
                        if (!$scope.agendaSet[m.agendaId.year]) {
                            $scope.agendaSet[m.agendaId.year] = {};
                        }
                        if (!$scope.agendaSet[m.agendaId.year][m.agendaId.number]) {
                            meetingEvents.push({
                                title: 'Agenda ' + m.agendaId.number + ' - ' + m.agendaId.year +
                                       ' (Week of ' + moment(m.weekOf).format('ll') + ')',
                                start: m.weekOf,
                                end: moment(m.weekOf).endOf('week').toISOString(),
                                allDay: true,
                                color: '#3a87ad',
                                textColor: 'white',
                                agendaId: m.agendaId
                            });
                            $scope.agendaSet[m.agendaId.year][m.agendaId.number] = true;
                        }
                        meetingEvents.push({
                            title: '\n' + m.committeeId.name,
                            start: m.meeting.meetingDateTime,
                            allDay: false,
                            notes: m.meeting.notes,
                            agendaId: m.agendaId,
                            committeeId: m.committeeId
                        });
                    });
                    $scope.meetingEventSources.push({
                        events: meetingEvents,
                        editable: false,
                        className: 'agenda-event'
                    });
                    $scope.renderCalendar();
                });
        };

        $scope.getCalendarConfig = function() {
            return {
                editable: false,
                theme: false,
                header:{
                    left: 'prev,next',
                    center: 'title',
                    right: ''
                },
                fixedWeekCount: false,
                viewRender: $scope.viewRenderHandler,
                //defaultView: 'basicWeek',
                aspectRatio: 1.5,
                hiddenDays: [0, 6],
                timeFormat: 'h:mm TT',
                eventClick: $scope.agendaEventClickHandler,
                eventMouseover: $scope.agendaEventMouseOverHandler,
                eventMouseout: $scope.agendaEventMouseOutHandler
            };
        };

        $scope.init();
    }
]);

agendaModule.controller('AgendaUpdatesCtrl', ['$scope', '$rootScope', '$location', '$route',
    function($scope, $rootScope, $location, $route) {
        $scope.tabInit = function() {
            $scope.setHeaderText('View Senate Agenda Updates');
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
        };

        $scope.init();
    }
]);

agendaModule.controller('AgendaViewCtrl', ['$scope', '$location', '$routeParams', 'AgendaGetApi', '$timeout',
    function($scope, $location, $routeParams, AgendaGetApi, $timeout) {

        $scope.searchTabName = ($scope.viewMap.hasOwnProperty($routeParams['sview'])
                                 ? $routeParams['sview'] : 'browse');
        $scope.searchTabIdx = $scope.viewMap[$scope.searchTabName];

        // Stores the agenda object from the response
        $scope.agenda = null;
        $scope.year = $routeParams.year;
        $scope.no = $routeParams.agendaNo;
        $scope.commName = $routeParams.comm;
        $scope.selectedComm = {};
        $scope.votes = {};

        $scope.init = function() {
            if ($scope.commName) {
                $scope.selectedComm[$scope.commName.toLowerCase()] = true;
            }
            $scope.response = AgendaGetApi.get({year: $scope.year, agendaNo: $scope.no}, function(){
                $scope.agenda = $scope.response.result;
                $scope.setHeaderText('Agenda ' + $scope.agenda.id.number + ' - ' + $scope.agenda.id.year);
                $scope.generateVoteLookup();
                if ($scope.commName) {
                    $timeout(function() {
                        $('html, body').animate({
                            scrollTop : $("md-card[data-committee='" + $scope.commName.toLowerCase() + "']").offset().top
                        })
                    }, 0);
                }
            });
        };

        // A vote-lookap map is generated to make it easier to display vote information in the template.
        $scope.generateVoteLookup = function() {
            angular.forEach($scope.agenda.committeeAgendas.items, function(commAgenda) {
                angular.forEach(commAgenda.addenda.items, function(commAddendum) {
                    if (commAddendum.hasVotes === true) {
                        angular.forEach(commAddendum.voteInfo.votesList.items, function(billVote) {
                            $scope.votes[billVote.bill.basePrintNo] = billVote;
                        });
                    }
                })
            });
        };

        $scope.backToSearch = function() {
            var browseDate = moment($scope.agenda.weekOf).format('YYYY-MM-DD');
            $location.url(window.ctxPath + '/agendas/?view=' + $scope.searchTabIdx + '&bdate=' + browseDate);
        };

        /** Initialize */
        $scope.init();
    }
]);

agendaModule.filter('agendaActionFilter', ['$filter', function($filter) {
    return function(input) {
        switch (input) {
            case 'FIRST_READING': return 'Sent to First Reading';
            case 'THIRD_READING': return 'Sent to Third Reading';
            case 'REFERRED_TO_COMMITTEE': return 'Referred to Committee';
            case 'DEFEATED': return 'Defeated';
            case 'RESTORED_TO_THIRD': return 'Restored to Third Reading';
            case 'SPECIAL': return 'Special Action';
        }
        return 'Unknown';
    }
}]);