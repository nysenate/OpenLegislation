var transcriptModule = angular.module('open.transcript');

transcriptModule.controller('TranscriptSearchCtrl', ['$scope', '$sce', '$routeParams', '$location', 'PaginationModel',
    'TranscriptSearchApi', 'PublicHearingSearchApi', 'YearGenerator',
    function($scope, $sce, $routeParams, $location, PaginationModel, TranscriptSearchApi, PublicHearingSearchApi, YearGenerator) {

        $scope.years = YearGenerator.getSingleYearsStr(1993, moment().year()).reverse();

        $scope.transcriptSearch = {
            term: $routeParams.term || '',
            state: 'initial',
            year: $routeParams.year || '',
            type: $routeParams.txtype || 'session',
            response: {},
            matches: [],
            error: false,
            paginate: angular.extend({}, PaginationModel)
        };

        var sessionIsSelected = function() {
            return $scope.transcriptSearch.type == 'session';
        };

        var hearingIsSelected = function() {
            return $scope.transcriptSearch.type == 'hearing';
        };

        $scope.init = function() {
            $scope.transcriptSearch.paginate.itemsPerPage = 25;
            $scope.transcriptSearch.paginate.currPage = $routeParams.page || 1;
            var resetPagination = !$scope.transcriptSearch.paginate.currPage > 1;
            $scope.search(resetPagination);
        };

        $scope.search = function(resetPagination) {
            if (resetPagination) {
                $scope.transcriptSearch.paginate.reset();
            }
            $scope.setParams();
            var term = $scope.transcriptSearch.term || '*';
            $scope.transcriptSearch.state = 'searching';
            var searchApi = (sessionIsSelected()) ? TranscriptSearchApi : PublicHearingSearchApi;
            $scope.transcriptSearch.response = searchApi.get({
                    term: term,
                    year: $scope.transcriptSearch.year,
                    limit: $scope.transcriptSearch.paginate.getLimit(),
                    offset: $scope.transcriptSearch.paginate.getOffset()},
                function() {
                    if ($scope.transcriptSearch.response && $scope.transcriptSearch.response.success) {
                        $scope.transcriptSearch.error = false;
                        $scope.transcriptSearch.matches = $scope.transcriptSearch.response.result.items || [];
                        $scope.transcriptSearch.paginate.setTotalItems($scope.transcriptSearch.response.total);
                        angular.forEach($scope.transcriptSearch.matches, function(match) {
                            for (var field in match.highlights) {
                                for (var fragment in match.highlights[field]) {
                                    if (match.highlights[field][fragment]) {
                                        match.highlights.hasFields = true;
                                        match.highlights[field][fragment] = $sce.trustAsHtml(match.highlights[field][fragment]);
                                    }
                                }
                            }
                        });
                        $scope.transcriptSearch.state = 'searched';
                    }
                    else {
                        $scope.transcriptSearch.error = true;
                        $scope.transcriptSearch.matches = [];
                        $scope.transcriptSearch.paginate.setTotalItems($scope.transcriptSearch.response.total);
                        $scope.transcriptSearch.state = 'searched';
                    }
                })
        };

        $scope.changePage = function(newPageNumber) {
            $scope.search(false);
        };

        $scope.setParams = function() {
            $scope.setSearchParam('term', $scope.transcriptSearch.term);
            $scope.setSearchParam('txtype', $scope.transcriptSearch.type);
            $scope.setSearchParam('year', $scope.transcriptSearch.year);
            $scope.setSearchParam('page', $scope.transcriptSearch.paginate.currPage);
        };

        $scope.init();
    }]);
