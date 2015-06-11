var agendaModule = angular.module('open.agenda', ['open.core']);

agendaModule.factory('AgendaListingApi', ['$resource', function($resource){
    return $resource(apiPath + '/agendas/:year?sort=:sort&limit=:limit&offset=:offset', {
        year: '@year',
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
    return $resource(apiPath + '/agendas/search?term=:term&sort=:sort&limit=:limit&offset=:offset', {
        term: '@term',
        sort: '@sort',
        limit: '@limit',
        offset: '@offset'
    });
}]);

agendaModule.factory('AgendaGetApi', ['$resource', function($resource) {
    return $resource(apiPath + '/agendas/:year/:agendaNo/', {
        year: '@year',
        agendaNo: '@agendaNo'
    });
}]);

agendaModule.factory('AgendaUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/agendas/:year/:agendaNo/updates', {
        year: '@year',
        agendaNo: '@agendaNo'
    });
}]);

agendaModule.factory('AgendaAggUpdatesApi', ['$resource', function($resource) {
    return $resource(apiPath + '/agendas/updates/:from/:to', {
        from: '@from',
        to: '@to'
    })
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
                $location.search('view', $scope.selectedView).replace();
                $scope.$broadcast('viewChange', $scope.selectedView);
            }
        });

        $scope.$on('$locationChangeSuccess', function() {
            $scope.selectedView = +($location.search().view) || 0;
        });
    }
]);

agendaModule.controller('AgendaSearchCtrl', ['$scope', '$location', '$route', '$routeParams', 
                                             'PaginationModel', 'AgendaListingApi', 'AgendaSearchApi', 'CommitteeListingApi',
    function($scope, $location, $route, $routeParams, PaginationModel, AgendaListingApi, AgendaSearchApi, CommitteeListingApi) {
        $scope.tabInit = function() {
            $scope.setHeaderText('Search Agendas');
        };
        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.itemsPerPage = 6;

        $scope.searchParams = {
            year: $routeParams['year'] || 2015,
            agendaNo: $routeParams['agendaNo'] || '',
            commName: $routeParams['commName'] || '',
            printNo: $routeParams['printNo'] || '',
            weekOf: $routeParams['weekOf'] || '',
            notes: $routeParams['notes'] || ''
        };

        // Mapping of search param names to search query field names.
        $scope.searchParamFields = {
            year: 'agenda.id.year',
            agendaNo: 'agenda.id.number',
            commName: 'committee.committeeId.name',
            printNo: 'committee.addenda.items.bills.items.billId.basePrintNo',
            weekOf: 'agenda.weekOf',
            notes: 'committee.addenda.items.meeting.notes'
        };

        $scope.searchSort = 'agenda.id.number:desc';

        $scope.getWeekOfListing = function() {
            var weekOfListResponse = AgendaListingApi.get({year: $scope.searchParams.year, limit: 100}, function(){
                $scope.weekOfListing = weekOfListResponse.result.items.map(function(a) {return a.weekOf});
            });
        };

        $scope.selectedYearChanged = function() {
            $scope.searchParams['weekOf'] = '';
            $scope.getWeekOfListing();
        };

        $scope.resetSearchParams = function() {
            for (p in $scope.searchParams) {
                if ($scope.searchParams.hasOwnProperty(p) && p !== 'year') {
                    $scope.searchParams[p] = '';
                }
            }
        };

        $scope.years = [2015, 2014, 2013, 2012, 2011, 2010, 2009];

        // Create list of numbers between 1 and 20.
        $scope.agendaNoList = Array.apply(0, Array(25)).map(function(x,i) { return i + 1; });

        var committees = CommitteeListingApi.get({sessionYear: $scope.searchParams.year}, function() {
            $scope.committeeListing = committees.result.items;
        });

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

        var buildSearch = function() {
            var queryList = [];
            for (var p in $scope.searchParams) {
                if ($scope.searchParams.hasOwnProperty(p)) {
                    if ($scope.searchParams[p]) {
                        queryList.push('(' + $scope.searchParamFields[p] + ':"' + $scope.searchParams[p] + '")');
                        $scope.setSearchParam(p, $scope.searchParams[p]);
                    }
                    else {
                        $scope.setSearchParam(p, $scope.searchParams[p], false);
                    }
                }
            }
            return queryList.join(' AND ');
        };
        
        $scope.init = function() {
            $scope.tabInit();
            $scope.getWeekOfListing();
        };
        
        $scope.simpleSearch = function(resetPagination) {
            var term = buildSearch();
            if (term) {
                $scope.agendaSearch.searching = true;
                $scope.agendaSearch.searched = false;
                if (resetPagination) {
                    $scope.pagination.reset();
                }
                $scope.agendaSearch.response = AgendaSearchApi.get({
                    term: term, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset(),
                    sort: $scope.searchSort},
                function() {
                    if ($scope.agendaSearch.response && $scope.agendaSearch.response.success) {
                        $scope.agendaSearch.error = false;
                        $scope.agendaSearch.results = $scope.agendaSearch.response.result.items || [];
                        $scope.agendaSearch.searched = true;
                        $scope.agendaSearch.searching = false;
                        $scope.pagination.setTotalItems($scope.agendaSearch.response.total);
                    }
                }, function(data) {
                    $scope.agendaSearch.searched = true;
                    $scope.agendaSearch.searching = false;
                    $scope.agendaSearch.error = data.data;
                    $scope.agendaSearch.results = [];
                    $scope.pagination.setTotalItems(0);
                    $scope.pagination.reset();
                });
            }
        };

        $scope.$watch('pagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage) {
                $scope.simpleSearch(false);
            }
        });

        $scope.$watchCollection('searchParams', function(n, o) {
            $scope.simpleSearch();
        });

        $scope.init();
    }
]);

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
                                     + '?sview=browse' + ((event.committeeId) ? '&comm=' + event.committeeId.name : '');
            return url;
        };

        $scope.fetchMeetingEvents = function(year) {
            $scope.yearsFetched[year] = true;
            $scope.loading = true;
            var meetingResponse = AgendaMeetingApi.get({from: new Date(year, 0, 1).toISOString(),
                                                        to: new Date(year + 1, 0, 1).toISOString()},
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
                    $scope.meetingEventSources.push({
                        events: meetingEvents,
                        editable: false,
                        className: 'agenda-event'
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

agendaModule.controller('AgendaUpdatesCtrl', ['$scope', '$rootScope', '$location', 'PaginationModel', 'AgendaAggUpdatesApi',
    function($scope, $rootScope, $location, PaginationModel, AgendaAggUpdatesApi) {

        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.itemsPerPage = 20;

        $scope.curr = {
            fromDate: moment().subtract(30, 'days').startOf('minute').toDate(),
            toDate: moment().startOf('minute').toDate(),
            type: $location.$$search.type || 'published',
            sortOrder: $location.$$search.sortOrder || 'desc',
            detail: $location.$$search.detail || true
        };

        $scope.agendaUpdates = {
            response: {},
            fetching: false,
            result: {},
            errMsg: ''
        };

        $scope.tabInit = function() {
            $scope.setHeaderText('Agenda Updates');
        };

        $scope.$on('viewChange', function() {
            $scope.tabInit();
        });

        $scope.init = function() {
            $scope.tabInit();
        };

        $scope.getUpdates = function() {
            $scope.agendaUpdates.fetching = true;
            $scope.agendaUpdates.response = AgendaAggUpdatesApi.get({
                from: $scope.curr.fromDate.toISOString(), to: $scope.curr.toDate.toISOString(),
                type: $scope.curr.type, order: $scope.curr.sortOrder, detail: $scope.curr.detail,
                filter: $scope.curr.filter, limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()
            }, function() {
                $scope.agendaUpdates.result = $scope.agendaUpdates.response.result;
                $scope.pagination.setTotalItems($scope.agendaUpdates.response.total);
                $scope.agendaUpdates.fetching = false;
            }, function(resp) {
                $scope.agendaUpdates.response.success = false;
                $scope.pagination.setTotalItems(0);
                $scope.agendaUpdates.errMsg = resp.data.message;
                $scope.agendaUpdates.fetching = false;
            });
        };

        $scope.$on('viewChange', function(ev) {
            $scope.getUpdates();
        });

        $scope.$watchCollection('curr', function(n, o) {
            if ($scope.selectedView === 2) {
                $scope.getUpdates();
                $scope.pagination.reset();
            }
        });

        $scope.$watch('pagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage) {
                $scope.getUpdates();
            }
        });

        $scope.init();
    }
]);

/**
 * AgendaViewCtrl
 * --------------
 *
 * Handles the fetch/display for single week committee agendas.
 * Includes change log for the given agenda.
 */
agendaModule.controller('AgendaViewCtrl', ['$scope', '$location', '$routeParams', 'PaginationModel', 'AgendaGetApi',
                        'AgendaUpdatesApi', '$timeout',
    function($scope, $location, $routeParams, PaginationModel, AgendaGetApi, AgendaUpdatesApi, $timeout) {
        $scope.searchTabName = ($scope.viewMap.hasOwnProperty($routeParams['sview'])
                                 ? $routeParams['sview'] : 'browse');
        $scope.searchTabIdx = $scope.viewMap[$scope.searchTabName];

        // Stores the agenda object from the response
        $scope.agenda = null;

        // Identify the agenda
        $scope.year = $routeParams.year;
        $scope.no = $routeParams.agendaNo;
        $scope.commName = $routeParams.comm;

        // Indicates if a committee toggle panel should be open when the page loads.
        $scope.selectedComm = {};

        // Lookup map for bill-id to votes
        $scope.votes = {};

        // Updates pagination
        $scope.updatesPagination = angular.extend({}, PaginationModel);

        $scope.updates = [];

        // Current state
        $scope.curr = {
            updateOrder: 'desc',
            fetchedInitialUpdates: false,
            loading: false
        };

        /**
         * Initialize the agenda view by fetching the agenda and scrolling down to a committee if necessary.
         */
        $scope.init = function() {
            if ($scope.commName) {
                $scope.selectedComm[$scope.commName.toLowerCase()] = true;
            }
            $scope.curr.loading = true;
            $scope.response = AgendaGetApi.get({year: $scope.year, agendaNo: $scope.no}, function() {
                $scope.agenda = $scope.response.result;
                $scope.setHeaderText('Agenda ' + $scope.agenda.id.number + ' - ' + $scope.agenda.id.year);
                // A lookup map needs to created to render a single bill listing for both info and vote addenda.
                $scope.generateVoteLookup();
                // Scroll down to a committee if specified in the params
                if ($scope.commName) {
                    $timeout(function() {
                        $('html, body').animate({
                            scrollTop : $("md-card[data-committee='" + $scope.commName.toLowerCase() + "']").offset().top
                        })
                    }, 0);
                }
                $scope.curr.loading = false;
            }, function(resp) {
                $scope.setHeaderText(resp.status);
                $scope.response.success = false;
                $scope.curr.loading = false;
            });
        };

        $scope.getUpdates = function(intial) {
            if (!intial || !$scope.fetchedInitialUpdates) {
                $scope.updatesResponse = AgendaUpdatesApi.get({agendaNo: $scope.no, year: $scope.year,
                                                               offset: $scope.updatesPagination.getOffset(),
                                                               limit: $scope.updatesPagination.getLimit(),
                                                               order: $scope.curr.updateOrder},
                function() {
                    $scope.curr.fetchedInitialUpdates = true;
                    $scope.updatesPagination.setTotalItems($scope.updatesResponse.total);
                },
                function() {
                    $scope.updatesPagination.setTotalItems(0);
                });
            }
        };

        $scope.$watch('updatesPagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage) {
                $scope.getUpdates();
            }
        });

        /**
         * A vote-lookup map is generated to make it easier to display vote information in the template.
         * This is because bills are listed in both 'info' and 'vote' addendum, but we want to render them
         * in a single list to make things less complicated.
         */
        $scope.generateVoteLookup = function() {
            angular.forEach($scope.agenda.committeeAgendas.items, function(commAgenda) {
                angular.forEach(commAgenda.addenda.items, function(commAddendum) {
                    if (commAddendum.hasVotes === true) {
                        angular.forEach(commAddendum.voteInfo.votesList.items, function(billVote) {
                            if (!$scope.votes[billVote.bill.basePrintNo]) {
                                $scope.votes[billVote.bill.basePrintNo] = {};
                            }
                            $scope.votes[billVote.bill.basePrintNo][commAgenda.committeeId.name] = billVote;
                        });
                    }
                })
            });
        };

        /**
         * Go back to where ye came.
         */
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