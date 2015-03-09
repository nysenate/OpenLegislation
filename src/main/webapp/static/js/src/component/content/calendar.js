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
    return $resource(apiPath + '/calendars/:year', {
        year: '@year'
    });
}]);

calendarModule.factory('CalendarSearchApi', ['$resource', function ($resource) {
    return $resource(apiPath + '/calendars/search', {});
}]);

calendarModule.factory('CalendarUpdatesApi', ['$resource', function ($resource) {
    return $resource(apiPath + '/calendars/:year/:calNo/updates', {
        year: '@year',
        calNo: '@calNo'
    });
}]);

calendarModule.factory('CalendarFullUpdatesApi', ['$resource', function ($resource) {
    return $resource(apiPath + '/calendars/updates/:fromDateTime/:toDateTime/', {
        fromDateTime: '@fromDateTime', toDateTime: '@toDateTime'
    });
}]);

/** --- Calendar View --- */

calendarModule.controller('CalendarViewCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$q', '$filter', '$timeout',
                                                'CalendarViewApi', 'CurrentCalendarIdApi',
function($scope, $rootScope, $routeParams, $location, $q, $filter, $timeout, CalendarViewApi, CurrentCalendarIdApi) {

    $scope.calendarView = null;

    $scope.calendarHeaderText = "";

    $scope.ctxPath = ctxPath;

    $scope.activeIndex = 2;

    var pageNames = ['search', 'active-list', 'floor', 'updates'];

    $scope.init = function() {
        $scope.getCalendarViewById($routeParams.year, $routeParams.calNo);
        if ($routeParams.hasOwnProperty('view') && ['active-list', 'search'].indexOf($routeParams['view']) < 0) {
            $scope.tabParam = $routeParams['view'];
        }
    };

    /** --- Tab / Header Management --- */

    $scope.changeTab = function(pageName) {
        console.log('changing view to', pageName);
        $scope.activeIndex = pageNames.indexOf(pageName);
    };

    /** --- Get Calendar Data --- */

    // Performs tasks that follow the loading of a new calendar view such as setting the header text and alerting child controllers
    function processNewCalendarView() {
        console.log('new calendar view!');

        $scope.calendarNum = $scope.calendarView['calendarNumber'];
        $scope.year = $scope.calendarView['year'];

        // Set the header text
        $scope.setHeaderText("Senate Calendar #" + $scope.calendarView['calendarNumber'] + " " +
                $filter('moment')($scope.calendarView.calDate, 'll'));

        // Alert child scopes of new calendar view
        $rootScope.$emit('newCalendarEvent');

        // Switch to the tab specified for the incoming route or by default the active list or floor tab
        if ($scope.tabParam) {
            $scope.changeTab($scope.tabParam)
        } else if ($scope.calendarView.activeLists.size > 0) {
            $scope.changeTab('active-list');
        } else {
            $scope.changeTab('floor');
        }

        $scope.$watch('activeIndex', function() {
            if ($scope.activeIndex >=0) {
                $location.search('view', pageNames[$scope.activeIndex]);
            } else {
                $location.search('view', null);
            }
        });
    }

    // Loads a calendar according to the specified year and calendar number
    $scope.getCalendarViewById = function (calendarYear, calendarNo) {
        console.log('loading calendar', calendarYear, calendarNo);
        var response = CalendarViewApi.get(
            {year: calendarYear, calNo: calendarNo }, function() {
                if (response.success) {
                    $scope.calendarView = response.result;
                    processNewCalendarView();
                }
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

    // Back to search
    $scope.backToSearch = function() {
        var currentParams = $location.search();
        var url = ctxPath + "/calendars?view=";
        if (currentParams.hasOwnProperty('sview')) {
            url += currentParams['sview'];
        } else {
            url += 'search';
        }
        if (currentParams.hasOwnProperty('search')) {
            url += '&search=' + currentParams['search'];
        }
        $location.url(url);
    };

    // Calendar Bill Number Search

    $scope.getCalBillNumUrl = function(calBillNum) {
        var searchTerm = "\\*.billCalNo:" + calBillNum + " AND year:" + $scope.calendarView.year;
        return ctxPath + "/calendars?view=search&search=" + searchTerm;
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

    $rootScope.$on('newCalendarEvent', populateFloorCals);

    $scope.$watch('floorCalFilter', filterFloorCalendarEntries, true);
}]);

calendarModule.controller('CalendarUpdatesCtrl', ['$scope', '$rootScope', 'CalendarUpdatesApi',
    function($scope, $rootScope, UpdatesApi) {
        $scope.updateResponse = {result:{items: []}};
        $scope.updatesOrder = "ASC";
        var calendarGot = false;

        $scope.getUpdates = function() {
            var response = UpdatesApi.get({year: $scope.year, calNo: $scope.calendarNum, detail: true, order: $scope.updatesOrder},
                function () {
                    if (response.success) {
                        $scope.updateResponse = response;
                    }
                });
        };

        $rootScope.$on('newCalendarEvent', function() {
            $scope.getUpdates();
            calendarGot = true;
        });

        $scope.$watch('updatesOrder', function () {
            if (calendarGot) {
                $scope.getUpdates();
            }
        });
    }]);

/** --- Calendar Search Page --- */

calendarModule.controller('CalendarSearchPageCtrl', ['$scope', '$routeParams', '$location', '$timeout',
function ($scope, $routeParams, $location, $timeout) {

    var pageNames = ['search', 'browse', 'updates'];

    function init() {
        if ($routeParams.hasOwnProperty('view')) {
            $scope.changeTab($routeParams['view']);
        }

        $scope.$watch('activeIndex', function(newIndex, oldIndex) {
            if (pageNames[newIndex]) {
                $location.search('view', pageNames[newIndex]);
            } else {
                $location.search('view', null);
            }
        })
    }

    $scope.changeTab = function (pageName) {
        console.log('changing view to', pageName);
        $scope.activeIndex = pageNames.indexOf(pageName);
    };

    $scope.setCalendarHeaderText = function() {
        $timeout(function() {   // Set text on next digest to account for delay in active index change
            var pageName = pageNames[$scope.activeIndex];
            var newHeader = "8)";

            if (pageName == "search") {
                newHeader = "Search for Calendars";
            } else if (pageName == "browse") {
                newHeader = "Browse Calendars";
            } else if (pageName == "updates") {
                newHeader = "View Calendar Updates";
            }
            $scope.setHeaderText(newHeader);
        }, 0 );
    };

    init();
}]);

calendarModule.controller('CalendarSearchCtrl', ['$scope', '$routeParams', '$location', 'CalendarSearchApi', 'PaginationModel',
function($scope, $routeParams, $location, SearchApi, paginationModel) {

    $scope.searchResults = [];
    $scope.searchResponse = {};

    $scope.pagination = angular.extend({}, paginationModel);

    $scope.searched = false;

    $scope.init = function() {
        if ($routeParams.hasOwnProperty('search')) {
            $scope.searchTerm = $routeParams['search'];
            $scope.termSearch(true);
        }
    };

    // Perform a simple serch based on the current search term
    $scope.termSearch = function(resetPagination) {
        var term = $scope.searchTerm;
        console.log('searching for', term);
        if (term) {
            $location.search('search', term);
            $scope.searched = false;
            $scope.searchResponse = SearchApi.get({
                    term: term, sort: $scope.sort, limit: $scope.pagination.getLimit(),
                    offset: $scope.pagination.getOffset()},
                function() {
                    $scope.searchResults = $scope.searchResponse.result.items || [];
                    $scope.searched = true;
                    if (resetPagination) {
                        $scope.pagination.currPage = 1;
                    }
                    $scope.pagination.setTotalItems($scope.searchResponse.total);
                });
        }
        else {
            $scope.searchResults = [];
            $scope.pagination.setTotalItems(0);
        }
    };

    // Manipulates the pagination object and displayed results based on the input action
    $scope.paginate = function(action) {
        var oldPage = $scope.pagination.currPage;
        switch (action) {
            case 'first': $scope.pagination.toFirstPage(); break;
            case 'prev': $scope.pagination.prevPage(); break;
            case 'next': $scope.pagination.nextPage(); break;
            case 'last': $scope.pagination.toLastPage(); break;
        }
        if (oldPage !== $scope.pagination.currPage) {
            $location.search('searchPage', $scope.pagination.currPage);
            $scope.termSearch(false);
        }
    };

    $scope.getTotalActiveListBills = function (cal) {
        var count = 0;
        angular.forEach(cal.activeLists.items, function (activeList) {
            count += activeList.totalEntries;
        });
        return count;
    };

    $scope.getTotalFloorBills = function (cal) {
        var count = 0;
        if (cal.floorCalendar.year) {
            count += cal.floorCalendar.totalEntries;
        }
        angular.forEach(cal.supplementalCalendars.items, function (supCal) {
            count += supCal.totalEntries;
        });
        return count;
    };

    $scope.getCalendarUrl = function(year, calNum) {
        return ctxPath + "/calendars/" + year + "/" + calNum + "?search=" + $scope.searchTerm +
            ($scope.pagination.currPage > 1 ? "&searchPage=" + $scope.pagination.currPage : "");
    };

    $scope.init();
}]);

calendarModule.controller('CalendarPickCtrl', ['$scope', '$q', 'CalendarIdsApi',
function($scope, $q, CalendarIdsApi) {

    $scope.eventSources = [];
    $scope.calendarConfig = null;
    $scope.calendarIds = {};

    $scope.init = function () {
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
        var promise = CalendarIdsApi.get({year: year, limit: "all"},
            function() {
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

calendarModule.controller('CalendarFullUpdatesCtrl', ['$scope', '$routeParams', '$location', '$mdToast',
                                                    'CalendarFullUpdatesApi', 'debounce', 'PaginationModel',
function ($scope, $routeParams, $location, $mdToast, UpdatesApi, debounce, PaginationModel) {
    $scope.updateResponse = {};
    $scope.updateOptions = {
        order: "ASC",
        detail: true
    };
    var initialTo = moment().startOf('minute');
    var initialFrom = moment(initialTo).subtract(7, 'days');
    $scope.updateOptions.toDateTime = initialTo.toDate();
    $scope.updateOptions.fromDateTime = initialFrom.toDate();

    $scope.pagination = angular.extend({}, PaginationModel);

    function init() {
        if ("uorder" in $routeParams && ["ASC", "DESC"].indexOf($routeParams['uorder']) >= 0) {
            $scope.updateOptions.order = $routeParams['uorder'];
        }
        if ($routeParams.hasOwnProperty('udetail')) {
            console.log('yo');
            $scope.updateOptions.detail = false;
        }
        if ("ufrom" in $routeParams) {
            var from = moment($routeParams['ufrom']);
            if (from.isValid()) {
                $scope.updateOptions.fromDateTime = from.toDate();
            }
        }
        if ("uto" in $routeParams) {
            var to = moment($routeParams['uto']);
            if (to.isValid()) {
                $scope.updateOptions.toDateTime = to.toDate();
            }
        }

    }

    $scope.getUpdates = function (resetPagination) {
        if (resetPagination) {
            $scope.pagination.currPage = 1;
        }
        var from = moment($scope.updateOptions.fromDateTime);
        var to = moment($scope.updateOptions.toDateTime);
        if (from.isAfter(to)) {
            $scope.invalidRangeToast();
            $scope.updateResponse = {};
            $scope.pagination.setTotalItems(0);
        } else if (from.isValid() && to.isValid()) {
            $scope.updateResponse = UpdatesApi.get({
                detail: $scope.updateOptions.detail,
                fromDateTime: moment($scope.updateOptions.fromDateTime).toISOString(),
                toDateTime: moment($scope.updateOptions.toDateTime).toISOString(),
                limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset()
            }, function () {
                if ($scope.updateResponse.success) {
                    $scope.pagination.setTotalItems($scope.updateResponse.total);
                } else {
                    $scope.pagination.setTotalItems(0);
                }
            });
        }
    };

    $scope.invalidRangeToast = function () {
        $mdToast.show({
            template: '<md-toast>from date cannot exceed to date!</md-toast>',
            parent: angular.element('.error-toast-parent')
        });
    };

    init();

    $scope.$watch('updateOptions', function() {
            debounce(function() {$scope.getUpdates(true);}, 500)();
            var opts = $scope.updateOptions;
            if (opts.order == "DESC") {
                $location.search("uorder", "DESC");
            } else { $location.search("uorder", null); }
            if (!opts.detail) {
                $location.search("udetail", false);
            } else { $location.search("udetail", null); }
            var to = moment(opts.toDateTime);
            var from = moment(opts.fromDateTime);
            if (to.isValid() && !to.isSame(initialTo)) {
                $location.search("uto", to.toISOString());
            } else { $location.search("uto", null); }
            if (from.isValid() && !from.isSame(initialFrom)) {
                $location.search("ufrom", from.toISOString());
            } else { $location.search("ufrom", null); }
        }, true);

    $scope.$watch('pagination.currPage', function (newPage, oldPage) {
        if (newPage !== oldPage && newPage > 0) {
            $scope.getUpdates(false);
        }
    });
}]);

calendarModule.directive('calendarEntryTable', function() {
    return {
        scope: {
            calEntries: '=calEntries',
            getCalBillNumUrl: '&'
        },
        templateUrl: ctxPath + '/partial/content/calendar/calendar-entry-table',
        controller: function($scope) {
            $scope.billPageBaseUrl = ctxPath + '/bills';
            $scope.getCalBillNumUrl = $scope.getCalBillNumUrl();
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
