var agendaModule = angular.module('open.agenda');

agendaModule.controller('AgendaSearchCtrl', ['$scope', '$location', '$route', '$routeParams',
    'PaginationModel', 'AgendaListingApi', 'AgendaSearchApi', 'CommitteeListingApi', 'YearGenerator',
    function($scope, $location, $route, $routeParams, PaginationModel, AgendaListingApi, AgendaSearchApi,
             CommitteeListingApi, YearGenerator) {
        $scope.tabInit = function() {
            $scope.setHeaderText('Search Agendas');
        };
        $scope.pagination = angular.extend({}, PaginationModel);
        $scope.pagination.itemsPerPage = 6;

        $scope.searchParams = {
            year: parseInt($routeParams['year'], 10) || moment().year(),
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

        $scope.years = YearGenerator.getSingleYearsInt(2009);

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

