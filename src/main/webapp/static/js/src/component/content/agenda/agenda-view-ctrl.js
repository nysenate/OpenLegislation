var agendaModule = angular.module('open.agenda');

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

