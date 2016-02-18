var calendarModule = angular.module('open.calendar');

calendarModule.controller('CalendarUpdatesCtrl', ['$scope', '$rootScope', 'CalendarUpdatesApi',
    function($scope, $rootScope, UpdatesApi) {
        $scope.updateResponse = {result:{items: []}};
        $scope.updatesOrder = 'ASC';

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
                var newHeader = 'Search For Calendars';

                if (pageName == 'browse') {
                    newHeader = 'Browse Calendars';
                } else if (pageName == 'updates') {
                    newHeader = 'View Calendar Updates';
                }
                $scope.setHeaderText(newHeader);
            });
        };

        $scope.getCalendarUrl = function(year, calNum, hash) {
            var url = ctxPath + '/calendars/' + year + '/' + calNum;
            var firstParam = true;
            for (var param in $location.search()) {
                url += (firstParam ? '?' : '&') + (param == 'view' ? 'sview' : param) + '=' + $location.search()[param];
                firstParam = false;
            }
            if (hash) {
                url += '#' + hash;
            }
            return url;
        };

        $scope.renderCalendarEvent = function() {
            $rootScope.$emit('renderCalendarEvent');
        };

        init();
    }]);


calendarModule.controller('CalendarFullUpdatesCtrl', ['$scope', '$routeParams', '$location', '$mdToast',
    'CalendarFullUpdatesApi', 'PaginationModel',
    function ($scope, $routeParams, $location, $mdToast, UpdatesApi, PaginationModel) {
        $scope.updateResponse = {};
        $scope.updateOptions = {
            order: 'DESC',
            type: 'processed',
            detail: true
        };
        var initialTo = moment().startOf('minute');
        var initialFrom = moment(initialTo).subtract(7, 'days');
        $scope.updateOptions.toDateTime = initialTo.toDate();
        $scope.updateOptions.fromDateTime = initialFrom.toDate();

        $scope.pagination = angular.extend({}, PaginationModel);

        function init() {
            if ('uorder' in $routeParams && ['ASC', 'DESC'].indexOf($routeParams['uorder']) >= 0) {
                $scope.updateOptions.order = $routeParams['uorder'];
            }
            if (!$routeParams.hasOwnProperty('udetail') || $routeParams['udetail'] !== 'false') {
                $scope.updateOptions.detail = true;
            }
            if ('utype' in $routeParams) {
                if ($routeParams['utype'] == 'published') {
                    $scope.updateOptions.type = 'published';
                }
            }
            if ('ufrom' in $routeParams) {
                var from = moment($routeParams['ufrom']);
                if (from.isValid()) {
                    $scope.updateOptions.fromDateTime = from.toDate();
                }
            }
            if ('uto' in $routeParams) {
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
                console.log('Getting updates from', $scope.toZonelessISOString(from), 'to', $scope.toZonelessISOString(to));
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
            $scope.setSearchParam('uorder', opts.order, opts.order === 'ASC');
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
