angular.module('open.spotcheck')
    .directive('mismatchView',
        ['$rootScope', '$mdDialog', 'SpotcheckDefaultFilter', 'SpotcheckMismatchIgnoreAPI', 'SpotcheckMismatchTrackingAPI',
            'IgnoreStatuses', mismatchViewDirective])
    .filter('orderByLabel', orderByLabelFilter)
    .filter('statusSelectLabel', ['$filter', statusSelectLabelFilter])
    .filter('typeSelectLabel', ['$filter', typeSelectLabelFilter])
    ;

function mismatchViewDirective($rootScope, $mdDialog, defaultFilter, IgnoreApi, TrackingApi, ignoreStatuses) {
    return {
        scope: {
            mismatches: '=',
            summary: '=',
            filter: '=?',
            loading: '@'
        },
        restrict: 'E',
        templateUrl: ctxPath + "/partial/report/spotcheck-mismatch-view",
        link: function mismatchFacetLink($scope, $element, $attrs) {

            //$scope.$watch('mismatches', function () {
            //    console.log('first mismatch', $scope.mismatches[0]);
            //});

            var defaultOptions = ['all'];

            $scope.showStatusFilter = !$attrs.hasOwnProperty('noStatusFilter');
            $scope.ctxPath = ctxPath;

            $scope.state = {
                allStatuses: true,
                allTypes: true,
                total: 0,
                ignoreFilter: 0,
                trackingFilter: 3,
                filterLoaded: false,
                iSelectedStatus: 0,
                iSelectedType: 0,
                selectedStatus: 'all',
                selectedType: 'all',
                statusOptions: angular.extend(defaultOptions),
                typeOptions: angular.extend(defaultOptions),
                currentPage: 1,
                settingIssueId: false,
                settingIgnoreStatus: false,
                requestError: false
            };

            $scope.filter = $scope.filter || angular.merge({}, defaultFilter);

            $scope.orderByLabels = orderByLabels;
            $scope.sortOrderLabels = {
                ASC: 'Ascending',
                DESC: 'Descending'
            };
            $scope.ignoreStatuses = ignoreStatuses;

            $scope.setIgnoreStatus = function (mismatchRow, ignoreStatus) {
                var mismatch = mismatchRow.mismatch;
                $scope.state.settingIgnoreStatus = true;
                console.log("setting ignore status", mismatchRow, ignoreStatus);
                IgnoreApi.save({mismatchId: mismatch.mismatchId, ignoreLevel: ignoreStatus}, function(response) {
                    if (mismatch.ignoreStatus !== "NOT_IGNORED") {
                        var ignoreStatusIndex = mismatchRow.chips.indexOf(mismatch.ignoreStatus);
                        if (ignoreStatusIndex >= 0) {
                            mismatchRow.chips.splice(ignoreStatusIndex, 1);
                        }
                    }
                    if (ignoreStatus !== "NOT_IGNORED") {
                        mismatchRow.chips.unshift(ignoreStatus);
                    }
                    mismatch.ignoreStatus = ignoreStatus;
                    $scope.state.settingIgnoreStatus = false;
                }, function (errorResponse) {
                    $scope.state.settingIgnoreStatus = false;
                    $scope.state.requestError = true;
                    console.error('error setting ignore status', mismatchRow, ignoreStatus, errorResponse);
                })
            };

            $scope.addIssueId = function (mismatchRow) {
                var mismatch = mismatchRow.mismatch;
                var newIssueId = mismatchRow.newIssueId;
                console.log("adding issue id", mismatchRow, newIssueId);
                for (var issueId in mismatch.issueIds.items) {
                    if (issueId === newIssueId) {
                        console.log('mismatch already has issue id', mismatchRow, newIssueId);
                        return;
                    }
                }
                $scope.state.settingIssueId = true;
                TrackingApi.save({mismatchId: mismatch.mismatchId, issueId: newIssueId}, function (response) {
                    $scope.state.settingIssueId = false;
                    mismatchRow.chips.push(newIssueId);
                    mismatch.issueIds.items.push(newIssueId);
                    mismatchRow.newIssueId = "";
                }, function (errorResponse) {
                    $scope.state.settingIssueId = false;
                    $scope.state.requestError = true;
                    console.error('error adding issue id', mismatchRow, newIssueId, errorResponse);
                });
            };

            $scope.removeIssueIdPrompt = function (mismatchRow, remIssueId) {
                // Don't do anything if an issue id is being added/removed or the removed issue id is no longer present
                if ($scope.state.settingIssueId || mismatchRow.mismatch.issueIds.items.indexOf(remIssueId) < 0) {
                    return;
                }
                $mdDialog.show(
                    $mdDialog.confirm()
                        .title('Remove Issue')
                        .textContent('Do you wish to remove issue id #' + remIssueId + '?')
                        .ok('Yes')
                        .cancel('No')
                ).then(function () {
                    $scope.removeIssueId(mismatchRow, remIssueId);
                });
            };

            $scope.removeIssueId = function (mismatchRow, remIssueId) {
                var mismatch = mismatchRow.mismatch;
                console.log('removing issue id', mismatchRow, remIssueId);
                $scope.state.settingIssueId = true;
                TrackingApi.delete({mismatchId: mismatch.mismatchId, issueId: remIssueId}, function (response) {
                    $scope.state.settingIssueId = false;
                    var chipIndex = mismatchRow.chips.indexOf(remIssueId);
                    if (chipIndex >= 0) {
                        mismatchRow.chips.splice(chipIndex, 1);
                    }
                    var issueIdIndex = mismatch.issueIds.items.indexOf(remIssueId);
                    if (issueIdIndex >= 0) {
                        mismatch.issueIds.items.splice(issueIdIndex, 1);
                    }
                }, function (errorResponse) {
                    $scope.state.settingIssueId = false;
                    $scope.state.requestError = true;
                    console.error('error removing issue id', mismatchRow, newIssueId, errorResponse);
                })
            };

            $scope.getIssueUrl = function (issueId) {
                var issueIdPlaceholder = "${issueId}";
                var issueUrlTemplate = "http://dev.nysenate.gov/issues/" + issueIdPlaceholder;
                return issueUrlTemplate.replace(issueIdPlaceholder, issueId);
            };

            // Get a count of mismatches of a particular type based on the filtering of mismatch statuses
            $scope.getTypeCount = function(type, ignored) {
                var typeCounts = $scope.summary.mismatchCounts[type];
                if (!typeCounts) {
                    return "?!";
                }
                var typeCount = 0;
                angular.forEach(typeCounts, function (ignoreStatuses, status) {
                    angular.forEach(ignoreStatuses, function (trackedStatuses, ignoreStatus) {
                        angular.forEach(trackedStatuses, function(count, trackedStatus) {
                            if ($scope.filter.passes(status, true, ignoreStatus, trackedStatus)) {
                                typeCount += count;
                            }
                        })
                    })
                });
                return typeCount;
            };

            $scope.passes = function(mismatch) {
                return $scope.filter.passes(mismatch);
            };

            $scope.isLoading = function () {
                return $attrs.loading === 'true';
            };

            $scope.$watch('state.currentPage', function () {
                var offset = ($scope.state.currentPage - 1) * $scope.filter.limit + 1;
                console.log('new page', $scope.state.currentPage, 'limit', $scope.filter.limit, 'offset', offset);
                if (offset !== $scope.filter.offset) {
                    $scope.filter.offset = offset;
                    $scope.onFilterChange(true);
                }
            });

            // Watch the summary for changes and alter the filter accordingly
            $scope.$watch('summary', onSummaryChange, true);
            function onSummaryChange() {
                if ($scope.summary) {
                    updateFilter();
                    setTotal();
                }
            }

            $scope.onStatusChange = function () {
                console.log('selected new status:', $scope.state.selectedStatus);
                var status = $scope.state.selectedStatus;
                setAllProperties($scope.filter.statuses, status === 'all');
                if ($scope.filter.statuses.hasOwnProperty(status)) {
                    $scope.filter.statuses[status] = true;
                }
                $scope.onFilterChange();
            };

            $scope.onTypeChange = function () {
                console.log('selected new type:', $scope.state.selectedType);
                var type = $scope.state.selectedType;
                setAllProperties($scope.filter.types, type === 'all');
                if ($scope.filter.types.hasOwnProperty(type)) {
                    $scope.filter.types[type] = true;
                }
                $scope.onFilterChange();
            };

            $scope.ignoreFilterOptions = ['Hide Ignored', 'Show Ignored', 'unused', 'Show Only Ignored'];
            $scope.onIgnoreChange = function onIgnoreChange() {
                var ignoredShownMask = 1;
                var ignoredOnlyMask = 2;
                $scope.filter.ignoredShown = (ignoredShownMask & $scope.state.ignoreFilter) > 0;
                $scope.filter.ignoredOnly = (ignoredOnlyMask & $scope.state.ignoreFilter) > 0;
                $scope.onFilterChange();
            };

            $scope.trackingFilterOptions = ['unused', 'Show Tracked', 'Show Untracked', 'Show All'];
            $scope.onTrackingChange = function onTrackingChange() {
                var trackedShownMask = 1;
                var untrackedShownMask = 2;
                $scope.filter.trackedShown = (trackedShownMask & $scope.state.trackingFilter) > 0;
                $scope.filter.untrackedShown = (untrackedShownMask & $scope.state.trackingFilter) > 0;
                $scope.onFilterChange();
            };

            // Update the filter to match the types/statuses in the summary
            function updateFilter() {
                ensureProperties($scope.summary.mismatchStatuses, $scope.filter.statuses, true);
                ensureProperties($scope.summary.mismatchCounts, $scope.filter.types, true);
                updateOptions();
                $scope.state.filterLoaded = true;
            }

            // Update status/type options
            function updateOptions() {
                $scope.state.statusOptions = defaultOptions.concat(Object.keys($scope.summary.mismatchStatuses));
                $scope.state.typeOptions = defaultOptions.concat(Object.keys($scope.summary.mismatchCounts));
            }

            function setTotal() {
                $scope.total = 0;
                angular.forEach($scope.summary.mismatchStatuses, function(count) {
                    $scope.total += count;
                });
            }

            $scope.onFilterChange = function (offsetChanged) {
                if (!offsetChanged) {
                    $scope.filter.offset = 1;
                    $scope.state.currentPage = 1;
                }
                console.log('filter changed', $scope.filter);
                $rootScope.$emit('mismatchFilterChange');
            };

            // Triggers a detail sheet popup for the mismatch designated by mismatchId
            $scope.showDetailedDiff = function(mismatchRow) {
                console.log('wtf');
                $mdDialog.show({
                    templateUrl: 'mismatchDetailWindow',
                    controller: 'detailDialogCtrl',
                    locals: {
                        mismatchRow: mismatchRow
                    }
                });
            };
        }
    };
}

var orderByLabels = {
    OBSERVED_DATE: "Observed Date",
    CONTENT_KEY: "Content Key",
    REFERENCE_DATE: "Reference Date",
    MISMATCH_TYPE: "Mismatch Type",
    STATUS: "Mismatch Status"
};

function orderByLabelFilter () {
    return function orderByLabelFilter(orderBy) {
        return orderByLabels.hasOwnProperty(orderBy)
            ? orderByLabels[orderBy]
            : "Order By!?";
    };
}

function extractIgnoreTrackFilter(filter) {
    var simpleFilter = {};
    if (filter.ignoredOnly) {
        simpleFilter.ignored = true;
    } else if (!filter.ignoredShown) {
        simpleFilter.ignored = false;
    }
    if (!filter.trackedShown) {
        simpleFilter.tracked = false;
    } else if (!filter.untrackedShown) {
        simpleFilter.tracked = true;
    }
    return simpleFilter;
}

function statusSelectLabelFilter($filter) {
    return function (status, summary, filter) {
        var simpleFilter = extractIgnoreTrackFilter(filter);
        simpleFilter.status = status;
        var count = $filter('mismatchCount')(summary, simpleFilter);
        return $filter('mismatchStatusLabel')(status) + ' (' + count + ')';
    }
}

function typeSelectLabelFilter($filter) {
    return function (type, summary, filter, status) {
        var simpleFilter = extractIgnoreTrackFilter(filter);
        simpleFilter.type = type;
        simpleFilter.status = status;
        var count = $filter('mismatchCount')(summary, simpleFilter);
        return $filter('mismatchTypeLabel')(type) + ' (' + count + ')';
    }
}

// Ensures that the copycat object contains properties
// corresponding to the property names of the model object
// properties with the default val will be created if they do not exist already
function ensureProperties(model, copycat, defaultVal) {
    var desiredType = typeof(defaultVal);
    // Delete properties of copycat that do not exist in the model or are not boolean props
    for (var copycatProp in copycat) {
        if (copycat.hasOwnProperty(copycatProp) &&
            (!model.hasOwnProperty(copycatProp) ||
                (desiredType !== "undefined" && typeof(copycat[copycatProp]) !== desiredType))) {
            delete copycat[copycatProp];
        }
    }
    // Add any model properties that are missing from the copycat
    for (var modelProp in model) {
        if (model.hasOwnProperty(modelProp) && !copycat.hasOwnProperty(modelProp)) {
            copycat[modelProp] = desiredType !== "undefined" ? defaultVal : model[modelProp];
        }
    }
}

// Sets all properties in the given object to the given value
function setAllProperties(obj, value) {
    for (var prop in obj) {
        if (obj.hasOwnProperty(prop)) {
            obj[prop] = value;
        }
    }
}
