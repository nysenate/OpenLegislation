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

    $scope.curr = {activeIndex: 2};

    $scope.pageNames = ['sklerch', 'active-list', 'floor', 'updates'];

    $scope.openSections = {};

    $scope.highlightValue = "none";

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
        console.log('changing view to', pageName);
        var newIndex = $scope.pageNames.indexOf(pageName);
        if (newIndex >= 0) $scope.curr.activeIndex = newIndex;
    };

    /** --- Get Calendar Data --- */

    // Performs tasks that follow the loading of a new calendar view such as setting the header text and alerting child controllers
    function processNewCalendarView() {

        $scope.calendarNum = $scope.calendarView['calendarNumber'];
        $scope.year = $scope.calendarView['year'];

        // Set the header text
        $scope.setHeaderText("Senate Calendar #" + $scope.calendarView['calendarNumber'] + " - " +
                $filter('moment')($scope.calendarView.calDate, 'll'));

        // Alert child scopes of new calendar view
        $rootScope.$emit('newCalendarEvent');

        // Switch to the tab specified for the incoming route or by default the active list or floor tab
        if (!$scope.tabParam || $scope.pageNames.indexOf($scope.tabParam) < 0) {
            if ($scope.calendarView.activeLists.size > 0) {
                $scope.tabParam = 'active-list';
            } else {
                $scope.tabParam = 'floor';
            }
        }

        $scope.scrollToBill($location.hash());

        $scope.changeTab($scope.tabParam)
    }

    // Loads a calendar according to the specified year and calendar number
    $scope.getCalendarViewById = function (calendarYear, calendarNo) {
        console.log('loading calendar', calendarYear, calendarNo);
        $scope.calendarResponse = CalendarViewApi.get(
            {year: calendarYear, calNo: calendarNo }, function() {
                if ($scope.calendarResponse.success === true) {
                    console.log('received successful calendar response');
                    $scope.calendarView = $scope.calendarResponse.result;
                    processNewCalendarView();
                }
            }, function(response) {
            $scope.setHeaderText(response.data.message);
            $scope.calendarResponse = response.data;
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
        var attrName;
        if (billCalNoPattern.test(identifier)) {
            calEntryPredicate = getCalEntryPredicate('billCalNo', parseInt(identifier));
            attrName = "data-cal-no";
        } else {
            var pnResult = printNoPattern.exec(identifier);
            if (pnResult) {
                calEntryPredicate = getCalEntryPredicate('basePrintNo', pnResult[1]);
                attrName = "data-print-no";
            } else {
                return;
            }
        }

        $scope.highlightValue = identifier;

        var openSection;

        if ($scope.tabParam === "floor" || $scope.tabParam === "active-list" && !billInActiveList(calEntryPredicate)) {
            openSection = billInFloorCal(calEntryPredicate);
            if (openSection) {
                $scope.tabParam = "floor";
                $scope.openSections[openSection] = true;
            }
        } else {
            openSection = "active-list";
        }
        $timeout(function () {
            var containerSelector = "section." + openSection;
            var entrySelector = "md-list-item[" + attrName + "='" + identifier + "']";
            console.log(containerSelector, entrySelector);
            $(containerSelector).animate({
                scrollTop : $(entrySelector).parent().scrollTop() + $(entrySelector).offset().top - $(entrySelector).parent().offset().top
            })
        }, 500);
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
            for (var ei in activeLists[ali].entries.items) {
                if (calEntryPredicate(activeLists[ali].entries.items[ei])) {
                    return true;
                }
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
                for (var entry in floorCalSections[section].items) {
                    if (calEntryPredicate(floorCalSections[section].items[entry])) {
                        return section;
                    }
                }
            }
        }
        return false;
    }

    // Returns to the search page, restoring any saved request parameters
    $scope.backToSearch = function() {
        var currentParams = $location.search();
        var url = ctxPath + "/calendars";
        var firstParam = true;
        for (var param in currentParams) {
            if (param != 'view') {
                url += (firstParam ? "?" : "&") + (param == 'sview' ? 'view' : param) + "=" + currentParams[param];
                firstParam = false;
            }
        }
        $location.url(url);
    };

    // Calendar Bill Number Search

    $scope.getCalBillNumUrl = function(year, calBillNum) {
        return ctxPath + "/calendars?view=search&sfield=billCalNo&syear=" + year + "&svalue=" + calBillNum;
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
        if (version == "floor") {
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

calendarModule.controller('CalendarUpdatesCtrl', ['$scope', '$rootScope', 'CalendarUpdatesApi',
    function($scope, $rootScope, UpdatesApi) {
        $scope.updateResponse = {result:{items: []}};
        $scope.updatesOrder = "ASC";

        $scope.getUpdates = function() {
            if ($scope.year && $scope.calendarNum) {
                $scope.loadingUpdates = true;
                $scope.updateResponse = {result:{items: []}};
                var response = UpdatesApi.get({
                        year: $scope.year,
                        calNo: $scope.calendarNum,
                        detail: true,
                        order: $scope.updatesOrder
                    },
                    function () {
                        $scope.loadingUpdates = false;
                        if (response.success) {
                            $scope.updateResponse = response;
                        }
                    },
                    function () {
                        $scope.loadingUpdates = false;
                    });
            }
        };

        $rootScope.$on('newCalendarEvent', function() {
            $scope.getUpdates();
        });

        $scope.$watch('updatesOrder', function () {
            $scope.getUpdates();
        });
    }]);

/** --- Calendar Search Page --- */

calendarModule.controller('CalendarSearchPageCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$timeout',
function ($scope, $rootScope, $routeParams, $location, $timeout) {

    $scope.pageNames = ['browse', 'search', 'updates'];

    $scope.activeYears = [];
    for (var year = moment().year(); year >= 2009; year--) {
        $scope.activeYears.push(year);
    }

    function init() {
        if ('view' in $routeParams) {
            $scope.changeTab($routeParams['view']);
        }

        $scope.$watch('activeIndex', function(newIndex, oldIndex) {
            $scope.setSearchParam('view', $scope.pageNames[newIndex]);
        })
    }

    $scope.changeTab = function (pageName) {
        console.log('changing view to', pageName);
        $scope.activeIndex = $scope.pageNames.indexOf(pageName);
    };

    $scope.pageIsActive = function (pageName) {
        return $scope.activeIndex == $scope.pageNames.indexOf(pageName);
    };

    $scope.setCalendarHeaderText = function() {
        $timeout(function() {   // Set text on next digest to account for delay in active index change
            var pageName = $scope.pageNames[$scope.activeIndex];
            var newHeader = "8)";

            if (pageName == "search") {
                newHeader = "Search for Calendars";
            } else if (pageName == "browse") {
                newHeader = "Browse Calendars";
            } else if (pageName == "updates") {
                newHeader = "View Calendar Updates";
            }
            $scope.setHeaderText(newHeader);
        });
    };

    $scope.getCalendarUrl = function(year, calNum, hash) {
        var url = ctxPath + "/calendars/" + year + "/" + calNum;
        var firstParam = true;
        for (var param in $location.search()) {
            url += (firstParam ? "?" : "&") + (param == "view" ? "sview" : param) + "=" + $location.search()[param];
            firstParam = false;
        }
        if (hash) {
            url += "#" + hash;
        }
        return url;
    };

    $scope.renderCalendarEvent = function() {
        $rootScope.$emit('renderCalendarEvent');
    };

    init();
}]);

calendarModule.controller('CalendarSearchCtrl', ['$scope', '$routeParams', '$location', 'CalendarSearchApi', 'PaginationModel',
function($scope, $routeParams, $location, SearchApi, paginationModel) {

    $scope.searchResults = [];
    $scope.searchResponse = {};

    $scope.searchActiveIndex = 0;

    $scope.searchQuery = {
        term: "",
        sort: ""
    };

     var defaultFields = {
        year: 2015,
        fieldName: "calendarNumber",
        fieldValue: "",
        activeList: false,
        order: "DESC"
    };
    $scope.searchFields = angular.extend({}, defaultFields);
    var fieldStorage = angular.extend({}, defaultFields);

    $scope.fieldOptions = {calendarNumber:'Calendar No.', printNo: 'Print No.', billCalNo: 'Bill Calendar No.'};
    $scope.orderOptions = {DESC: 'Newest First', ASC: 'Oldest First'};

    $scope.pagination = angular.extend({}, paginationModel);

    $scope.searching = false;

    $scope.init = function() {
        if ('stype' in $routeParams && $routeParams['stype'] === 'string') {
            $scope.searchActiveIndex = 1;
        }
        if ('search' in $routeParams && $scope.searchActiveIndex === 1) {
            $scope.searchQuery.term = $routeParams['search'];
        }
        if ('sort' in $routeParams && $scope.searchActiveIndex ===1) {
            $scope.searchQuery.sort = $routeParams['sort'];
        }
        if ('syear' in $routeParams) {
            var year = parseInt($routeParams['syear']);
            if ($scope.activeYears.indexOf(year) >= 0) {
                $scope.searchFields.year = year;
            }
        }
        if ('sfield' in $routeParams && $routeParams['sfield'] in $scope.fieldOptions) {
            $scope.searchFields.fieldName = $routeParams['sfield'];
        }
        if ('svalue' in $routeParams) {
            $scope.searchFields.fieldValue = $routeParams['svalue'];
        }
        if ('sactlist' in $routeParams) {
            $scope.searchFields.activeList = $routeParams['sactlist'] === 'true';
        }
        if ('sorder' in $routeParams && $routeParams['sorder'] in $scope.orderOptions) {
            $scope.searchFields.order = $routeParams['sorder'];
        }
        $scope.$watchCollection('searchFields', $scope.fieldSearch);
        $scope.$watch('pagination.currPage', function(newPage, oldPage) {
            if (newPage !== oldPage && newPage > 0) {
                $scope.setSearchParam('searchPage', newPage, newPage > 1);
                $scope.termSearch(false);
            }
        });
        $scope.$watch('searchActiveIndex', function(newIndex, oldIndex) {
            if (newIndex === 0) {
                $scope.fieldSearch();
            }
            $scope.setSearchParam('stype', 'string', newIndex === 1);
            setFieldParameters();
            setQueryParameters();
        });
        $scope.$watchCollection('searchQuery', function (newQ, oldQ) {
            $scope.termSearch(true);
            setQueryParameters();
        });
    };

    // Perform a simple search based on the current search term
    $scope.termSearch = function(resetPagination) {
        // If pagination is to be reset and it is not on page 1 just change pagination to trigger the watch
        if (resetPagination && $scope.pagination.currPage != 1) {
            $scope.pagination.currPage = 1;
        } else {
            var term = $scope.searchQuery.term;
            var sort = $scope.searchQuery.sort;
            if (term) {
                console.log("searching.", "term:", term, "sort:", sort);
                $scope.searching = true;
                $scope.searchResponse = SearchApi.get({
                        term: term, sort: sort, limit: $scope.pagination.getLimit(),
                        offset: $scope.pagination.getOffset()
                    },
                    function () {
                        $scope.searchResults = $scope.searchResponse.result.items || [];
                        $scope.searching = false;
                        $scope.pagination.setTotalItems($scope.searchResponse.total);
                    }, function() {$scope.searching = false;});
            } else {
                console.log("not searching.");
                $scope.searchResults = [];
                $scope.pagination.setTotalItems(0);
            }
        }
    };

    $scope.fieldSearch = function () {
        if ($scope.searchActiveIndex === 0) {
            var queryString = buildFieldQuery();
            var sortString = getFieldSortString();
            if ($scope.searchQuery.term !== queryString) {
                $scope.searchQuery.term = queryString ? queryString : "";
            }
            if ($scope.searchQuery.sort !== getFieldSortString()) {
                $scope.searchQuery.sort = sortString;
            }
        }
        setFieldParameters();
    };

    function addSearchTerm(query, term) {
        return query ? query + " AND " + term : term;
    }

    // Constructs a search query string from the current search fields
    function buildFieldQuery() {
        var query = false;
        if ($scope.searchFields.year) {
            query = addSearchTerm(query, "year:" + $scope.searchFields.year);
        }
        if ($scope.searchFields.fieldName && $scope.searchFields.fieldValue) {
            if ($scope.searchFields.fieldName === "calendarNumber") {
                query = addSearchTerm(query, "calendarNumber:" + $scope.searchFields.fieldValue);
            } else {
                query = addSearchTerm(query, ($scope.searchFields.activeList ? "activeLists" : "")
                                                + "\\*." + $scope.searchFields.fieldName + ":" + $scope.searchFields.fieldValue);
            }
        }
        return query;
    }

    function getFieldSortString() {
        if ($scope.searchFields.order in $scope.orderOptions) {
            return "calDate:" + $scope.searchFields.order;
        }
        return "";
    }

    function setFieldParameters() {
        angular.forEach({
            syear: $scope.searchFields.year,
            sfield: $scope.searchFields.fieldName,
            svalue: $scope.searchFields.fieldValue,
            sactlist: $scope.searchFields.activeList,
            sorder: $scope.searchFields.order
        }, function(value, paramName) {
            $scope.setSearchParam(paramName, value, $scope.searchActiveIndex === 0);
        });
    }

    function setQueryParameters() {
        // Only display search parameter if query search tab is active
        $scope.setSearchParam('search', $scope.searchQuery.term, $scope.searchActiveIndex === 1);
        $scope.setSearchParam('sort', $scope.searchQuery.sort, $scope.searchActiveIndex === 1);
    }

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

    $scope.init();
}]);

calendarModule.controller('CalendarBrowseCtrl', ['$scope', '$rootScope', '$routeParams', '$location', '$timeout', '$q',
                                                 '$mdToast', '$mdMedia', 'CalendarIdsApi',
function($scope, $rootScope, $routeParams, $location, $timeout, $q, $mdToast, $mdMedia, CalendarIdsApi) {

    $scope.eventSources = [];
    $scope.calendarConfig = null;
    $scope.calendarIds = {};
    $scope.requestsInProgress = 0;
    $scope.events = [];
    $scope.loadedYears = [];    // years for which ids have been loaded into the event list

    $scope.init = function () {
        $scope.eventSources.push(getEventSourcesObject());
        $scope.calendarConfig = getCalendarConfig();
        if ('bdate' in $routeParams) {
            $scope.setCalendarDate($routeParams['bdate']);
        } else {
            $scope.renderCalendar();
        }
    };

    $scope.renderCalendar = function () {
        $timeout(function () {
            angular.element('#calendar-date-picker').fullCalendar('render');
        });
    };

    $rootScope.$on('renderCalendarEvent', $scope.renderCalendar);

    $scope.setCalendarDate = function(date) {
        var momentDate = moment(date);
        if (momentDate.isValid()) {
            $timeout(function() {
                angular.element('#calendar-date-picker').fullCalendar('gotoDate', momentDate.toDate());
                $scope.renderCalendar();
            });
        }
    };

    function getCalendarIds(year) {
        var deferred = $q.defer();
        var idResponse = CalendarIdsApi.get({year: year, limit: "all"},
            function() {
                if (idResponse.success) {
                    $scope.calendarIds[year] = idResponse.result.items;
                    deferred.resolve($scope.calendarIds);
                } else {
                    deferred.reject("unsuccessful calendar id request");
                }
            });
        return deferred.promise;
    }

    function getEvent(calendarId) {
        return {
            title: ($mdMedia('gt-sm') ?
                    ($mdMedia('gt-lg') ? "Senate Calendar\n" : "")
                    + calendarId.year + " " : "")
                + "#" + calendarId.calendarNumber,
            start: calendarId.calDate,
            calNo: calendarId.calendarNumber
            //rendering: 'background'
        };
    }

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
            console.log("loading calendar ids for", years.join(", "));
            showLoadingToast();
            $scope.requestsInProgress += 1;
            $q.all(calendarIdPromises).then(function () {
                for (var i in years){
                    if ($scope.loadedYears.indexOf(years[i]) < 0) {
                        $scope.calendarIds[years[i]]
                            .map(getEvent)
                            .forEach(function (event) {
                                $scope.events.push(event);
                            });
                        $scope.loadedYears.push(years[i]);
                    }
                }
                $scope.requestsInProgress -= 1;
                hideLoadingToast();
                callback($scope.events);
            });
        } else {
            callback($scope.events);
        }
    }

    function showLoadingToast() {
        if ($scope.requestsInProgress < 1) {
            $mdToast.show({
                template: "<md-toast>" +
                          "  loading calendars... " +
                          "  <md-progress-circular md-mode='indeterminate' md-diameter='20'></md-progress-circular>" +
                          "</md-toast>",
                hideDelay: false,
                parent: angular.element("#calendar-date-picker"),
                position: "fit"
            });
        }
    }

    function hideLoadingToast () {
        if ($scope.requestsInProgress < 1) {
            $mdToast.hide();
        }
    }

    function getEventSourcesObject() {
        return {
            events: getCalendarEvents,
            allDay: true,
            className: 'calendar-event',
            editable: false
        }
    }

    function onEventClick(event, jsEvent, view) {
        $location.url($scope.getCalendarUrl(event.start.getFullYear(), event.calNo));
    }

    // Set the search param to match the currently viewed month
    function viewRenderHandler(view, element) {
        var monthStart = moment(view.start);
        $scope.setSearchParam('bdate', monthStart.format('YYYY-MM-DD'), !monthStart.isSame(moment(), 'month'));
    }

    function getCalendarConfig() {
        return {
            editable: false,
            theme: false,
            header:{
                left: window.innerWidth > 550 ? 'prevYear prev,next nextYear today'
                    : window.innerWidth > 380 ? 'prev,next,today' : 'prev,next',
                center: 'title',
                right: ''
            },
            viewRender: viewRenderHandler,
            fixedWeekCount: false,
            aspectRatio: 1.5,
            eventClick: onEventClick
        };
    }

    $scope.init();
}]);

calendarModule.controller('CalendarFullUpdatesCtrl', ['$scope', '$routeParams', '$location', '$mdToast',
                                                    'CalendarFullUpdatesApi', 'PaginationModel',
function ($scope, $routeParams, $location, $mdToast, UpdatesApi, PaginationModel) {
    $scope.updateResponse = {};
    $scope.updateOptions = {
        order: "DESC",
        type: "processed",
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
        if (!$routeParams.hasOwnProperty('udetail') || $routeParams['udetail'] !== "false") {
            $scope.updateOptions.detail = true;
        }
        if ('utype' in $routeParams) {
            if ($routeParams['utype'] == 'published') {
                $scope.updateOptions.type = 'published';
            }
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
        var from = moment.parseZone($scope.updateOptions.fromDateTime);
        var to = moment.parseZone($scope.updateOptions.toDateTime);
        if (from.isAfter(to)) {
            $scope.invalidRangeToast();
            $scope.updateResponse = {};
            $scope.pagination.setTotalItems(0);
        } else if (from.isValid() && to.isValid()) {
            console.log("Getting updates from", $scope.toZonelessISOString(from), "to", $scope.toZonelessISOString(to));
            $scope.loadingUpdates = true;
            $scope.updateResponse = UpdatesApi.get({
                detail: $scope.updateOptions.detail, type: $scope.updateOptions.type,
                fromDateTime: $scope.toZonelessISOString(from), toDateTime: $scope.toZonelessISOString(to),
                limit: $scope.pagination.getLimit(), offset: $scope.pagination.getOffset(),
                order: $scope.updateOptions.order
            }, function () {
                $scope.loadingUpdates = false;
                if ($scope.updateResponse.success) {
                    $scope.pagination.setTotalItems($scope.updateResponse.total);
                } else {
                    $scope.pagination.setTotalItems(0);
                }
            }, function () {
                $scope.loadingUpdates = false;
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

    $scope.$watchCollection('updateOptions', function() {
            $scope.getUpdates(true);
            var opts = $scope.updateOptions;
            $scope.setSearchParam('uorder', opts.order, opts.order === "ASC");
            //$scope.setSearchParam('udetail', opts.detail, opts.detail === false);
            var to = moment(opts.toDateTime).local();
            var from = moment(opts.fromDateTime).local();
            $scope.setSearchParam('uto', $scope.toZonelessISOString(to), to.isValid() && !to.isSame(initialTo));
            $scope.setSearchParam('ufrom', $scope.toZonelessISOString(from), from.isValid() && !from.isSame(initialFrom));
        });

    $scope.$watch('pagination.currPage', function (newPage, oldPage) {
        if (newPage !== oldPage && newPage > 0) {
            $scope.getUpdates(false);
        }
    });
}]);

calendarModule.directive('calendarEntryTable', function() {
    return {
        scope: {
            year: '=',
            calEntries: '=calEntries',
            getCalBillNumUrl: '&',
            highlightValue: "=",
            sectionType: "@"
        },
        templateUrl: ctxPath + '/partial/content/calendar/calendar-entry-table',
        controller: function($scope) {
            $scope.billPageBaseUrl = ctxPath + '/bills';
            $scope.getCalBillNumUrl = $scope.getCalBillNumUrl();
            $scope.listingLimit = 20;

            $scope.keepScrolling = function() {
                $scope.listingLimit += 10;
            };

            $scope.$on('billScrollEvent', function (sectionType) {
                if ($scope.sectionType == sectionType) {
                    $scope.listingLimit = 99999;
                    console.log("buffed listing limit of", $scope.sectionType);
                }
            });
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

var sectionArray = [
    'ORDER_OF_THE_FIRST_REPORT',
    'ORDER_OF_THE_SECOND_REPORT',
    'ORDER_OF_THE_SPECIAL_REPORT',
    'THIRD_READING_FROM_SPECIAL_REPORT',
    'THIRD_READING',
    'STARRED_ON_THIRD_READING'
];
calendarModule.filter('orderBySection', function() {
    return function(obj) {
        var array = [];
        Object.keys(obj).forEach(function(key) { array.push(obj[key]); });
        array.sort(function(a, b) {
            return sectionArray.indexOf(a.items[0].sectionType) - sectionArray.indexOf(b.items[0].sectionType);
        });
        return array;
    };
});
