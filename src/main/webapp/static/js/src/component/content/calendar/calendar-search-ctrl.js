var calendarModule = angular.module('open.calendar');

calendarModule.controller('CalendarSearchCtrl', ['$scope', '$routeParams', '$location', 'CalendarSearchApi', 'PaginationModel',
    function($scope, $routeParams, $location, SearchApi, paginationModel) {

        $scope.searchResults = [];
        $scope.searchResponse = {};

        $scope.searchActiveIndex = 0;

        $scope.searchQuery = {
            term: '',
            sort: ''
        };

        var defaultFields = {
            year: 2015,
            fieldName: 'calendarNumber',
            fieldValue: '',
            activeList: false,
            order: 'DESC'
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
                    console.log('searching.', 'term:', term, 'sort:', sort);
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
                    console.log('not searching.');
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
                    $scope.searchQuery.term = queryString ? queryString : '';
                }
                if ($scope.searchQuery.sort !== getFieldSortString()) {
                    $scope.searchQuery.sort = sortString;
                }
            }
            setFieldParameters();
        };

        function addSearchTerm(query, term) {
            return query ? query + ' AND ' + term : term;
        }

        // Constructs a search query string from the current search fields
        function buildFieldQuery() {
            var query = false;
            if ($scope.searchFields.year) {
                query = addSearchTerm(query, 'year:' + $scope.searchFields.year);
            }
            if ($scope.searchFields.fieldName && $scope.searchFields.fieldValue) {
                if ($scope.searchFields.fieldName === 'calendarNumber') {
                    query = addSearchTerm(query, 'calendarNumber:' + $scope.searchFields.fieldValue);
                } else {
                    query = addSearchTerm(query, ($scope.searchFields.activeList ? 'activeLists' : '')
                        + '\\*.' + $scope.searchFields.fieldName + ':' + $scope.searchFields.fieldValue);
                }
            }
            return query;
        }

        function getFieldSortString() {
            if ($scope.searchFields.order in $scope.orderOptions) {
                return 'calDate:' + $scope.searchFields.order;
            }
            return '';
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