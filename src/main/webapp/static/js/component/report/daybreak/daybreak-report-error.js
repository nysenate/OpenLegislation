var reportModule = angular.module('report');

reportModule.controller('DaybreakReportErrorCtrl', ['$scope', '$filter', '$http', function($scope, $filter, $http) {
    $scope.reportData = null;
    $scope.observations = [];
    $scope.totals = null;
    $scope.labels = getLabels();
    $scope.errorFilter = null;
    $scope.filteredTypeTotals = null;

    $scope.getReportData = function() { $scope.reportData = testReport; };
    $scope.onFilterUpdate = function(){
        $scope.filteredTypeTotals = getFilteredTypeTotals($scope.errorFilter, $scope.totals);
        console.log($scope.filteredTypeTotals);
    };
    $scope.getLabel = function(labelType, field){
        if($scope.labels[labelType] && $scope.labels[labelType][field]){
            return $scope.labels[labelType][field];
        }
        return field;
    };

    $scope.getReportData();
    $scope.totals = getTotals($scope.reportData);
    $scope.errorFilter = getDefaultFilter($scope.totals);
    $scope.$watch('errorFilter', $scope.onFilterUpdate);
}]);

function getTotals(reportData){
    var totals = {
        total: 0,
        statuses: reportData.details.mismatchStatuses,
        types: reportData.details.mismatchTypes
    };
    for(status in totals.statuses){
        totals.total += totals.statuses[status];
    }
    return totals;
}

function getDefaultFilter(totals){
    errorFilter = { statuses: {}, types: {} };
    for(status in totals.statuses){
        errorFilter.statuses[status] = true;
    }
    for(type in totals.types){
        errorFilter.types[type] = true;
    }
    return errorFilter;
}

function getFilteredTypeTotals(errorFilter, totals){
    var filteredTypeTotals = {};
    console.log(totals.types);
    for(type in totals.types){
        var runningTotal = 0;
        for(status in totals.types[type]){
            if(errorFilter.statuses[status]){
                runningTotal += totals.types[type][status];
            }
        }
        filteredTypeTotals[type] = runningTotal;
    }
    return filteredTypeTotals;
}

function getLabels(){
    return {
        statuses: {
            RESOLVED: "Closed",
            NEW: "Opened",
            EXISTING: "Existing",
            REGRESSION: "Reopened",
            IGNORE: "Ignored"
        },
        types: {
            BILL_ACTIVE_AMENDMENT: "Amendment",
            BILL_SPONSOR: "Sponsor",
            BILL_MULTISPONSOR: "Mul.Sponsor",
            BILL_ACTION: "Action",
            BILL_COSPONSOR: "Co Sponsor",
            BILL_AMENDMENT_PUBLISH: "Publish",
            BILL_FULLTEXT_PAGE_COUNT: "Page Count",
            BILL_LAW_CODE: "Law Code",
            BILL_LAW_CODE_SUMMARY: "Law/Summary",
            BILL_SPONSOR_MEMO: "Sponsor Memo",
            BILL_SAMEAS: "Same As",
            BILL_SUMMARY: "Summary",
            BILL_TITLE: "Title",
            BILL_LAW_SECTION: "Law Section",
            BILL_MEMO: "Memo",
            REFERENCE_DATA_MISSING: "Missing Ref.",
            OBSERVE_DATA_MISSING: "Missing Bill"
        }
    }
}

var testReport =
{
    "success" : true,
    "message" : "",
    "details" : {
        "referenceType" : "LBDC_DAYBREAK",
        "reportDateTime" : "2014-09-03T14:13:15",
        "mismatchStatuses" : {
            "RESOLVED" : 19,
            "NEW" : 0,
            "EXISTING" : 119,
            "REGRESSION" : 0,
            "IGNORE" : 0
        },
        "mismatchTypes" : {
            "BILL_ACTIVE_AMENDMENT" : {
                "RESOLVED" : 9
            },
            "BILL_SPONSOR" : {
                "EXISTING" : 2
            },
            "BILL_MULTISPONSOR" : {
                "EXISTING" : 101
            },
            "BILL_ACTION" : {
                "EXISTING" : 2
            },
            "BILL_COSPONSOR" : {
                "RESOLVED" : 1,
                "EXISTING" : 5
            },
            "BILL_AMENDMENT_PUBLISH" : {
                "RESOLVED" : 9
            },
            "BILL_FULLTEXT_PAGE_COUNT" : {
                "EXISTING" : 6
            },
            "BILL_LAW_CODE_SUMMARY" : {
                "EXISTING" : 3
            }
        },
        "observations" : {
            "S89-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S89",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S89"
                },
                "observedDateTime" : "2014-09-03T14:14:45.474",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTION",
                        "status" : "EXISTING",
                        "referenceData" : "SENATE - 01/09/13 REFERRED TO FINANCE\nSENATE - 06/11/13 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/11/13 ORDERED TO THIRD READING CAL.1214\nSENATE - 06/12/13 PASSED SENATE\nSENATE - 06/12/13 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/12/13 REFERRED TO CODES\nSENATE - 06/17/13 RECALLED FROM ASSEMBLY\nASSEMBLY - 06/17/13 RETURNED TO SENATE\nSENATE - 06/18/13 VOTE RECONSIDERED - RESTORED TO THIRD READING\nSENATE - 06/18/13 AMENDED ON THIRD READING 89A\nSENATE - 06/21/13 REPASSED SENATE\nSENATE - 06/21/13 RETURNED TO ASSEMBLY\nASSEMBLY - 06/24/13 REFERRED TO WAYS AND MEANS\nASSEMBLY - 01/08/14 DIED IN ASSEMBLY\nASSEMBLY - 01/08/14 RETURNED TO SENATE\nSENATE - 01/08/14 REFERRED TO FINANCE\nSENATE - 06/09/14 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/09/14 ORDERED TO THIRD READING CAL.1194\nSENATE - 06/10/14 PASSED SENATE\nSENATE - 06/10/14 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/10/14 REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/19/14 SUBSTITUTED FOR A7221A\nASSEMBLY - 06/19/14 ORDERED TO THIRD READING RULES CAL.599\nASSEMBLY - 06/19/14 PASSED ASSEMBLY\nASSEMBLY - 06/19/14 RETURNED TO SENATE",
                        "observedData" : "SENATE - 01/09/13 REFERRED TO FINANCE\nSENATE - 06/11/13 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/11/13 ORDERED TO THIRD READING CAL.1214\nSENATE - 06/12/13 PASSED SENATE\nSENATE - 06/12/13 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/12/13 REFERRED TO CODES\nSENATE - 06/17/13 RECALLED FROM ASSEMBLY\nASSEMBLY - 06/17/13 RETURNED TO SENATE\nSENATE - 06/18/13 VOTE RECONSIDERED - RESTORED TO THIRD READING\nSENATE - 06/18/13 AMENDED ON THIRD READING 89A\nSENATE - 06/21/13 REPASSED SENATE\nSENATE - 06/21/13 RETURNED TO ASSEMBLY\nASSEMBLY - 06/24/13 REFERRED TO WAYS AND MEANS\nASSEMBLY - 01/08/14 DIED IN ASSEMBLY\nASSEMBLY - 01/08/14 RETURNED TO SENATE\nSENATE - 01/08/14 REFERRED TO FINANCE\nSENATE - 06/09/14 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/09/14 ORDERED TO THIRD READING CAL.1194\nSENATE - 06/10/14 PASSED SENATE\nSENATE - 06/10/14 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/10/14 REFERRED TO WAYS AND MEANS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SENATE - 01/09/13 REFERRED TO FINANCE\nSENATE - 06/11/13 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/11/13 ORDERED TO THIRD READING CAL.1214\nSENATE - 06/12/13 PASSED SENATE\nSENATE - 06/12/13 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/12/13 REFERRED TO CODES\nSENATE - 06/17/13 RECALLED FROM ASSEMBLY\nASSEMBLY - 06/17/13 RETURNED TO SENATE\nSENATE - 06/18/13 VOTE RECONSIDERED - RESTORED TO THIRD READING\nSENATE - 06/18/13 AMENDED ON THIRD READING 89A\nSENATE - 06/21/13 REPASSED SENATE\nSENATE - 06/21/13 RETURNED TO ASSEMBLY\nASSEMBLY - 06/24/13 REFERRED TO WAYS AND MEANS\nASSEMBLY - 01/08/14 DIED IN ASSEMBLY\nASSEMBLY - 01/08/14 RETURNED TO SENATE\nSENATE - 01/08/14 REFERRED TO FINANCE\nSENATE - 06/09/14 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/09/14 ORDERED TO THIRD READING CAL.1194\nSENATE - 06/10/14 PASSED SENATE\nSENATE - 06/10/14 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/10/14 REFERRED TO WAYS AND MEANS"
                        }, {
                            "operation" : "DELETE",
                            "text" : "\nASSEMBLY - 06/19/14 SUBSTITUTED FOR A7221A\nASSEMBLY - 06/19/14 ORDERED TO THIRD READING RULES CAL.599\nASSEMBLY - 06/19/14 PASSED ASSEMBLY\nASSEMBLY - 06/19/14 RETURNED TO SENATE"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SENATE - 01/09/13 REFERRED TO FINANCE\nSENATE - 06/11/13 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/11/13 ORDERED TO THIRD READING CAL.1214\nSENATE - 06/12/13 PASSED SENATE\nSENATE - 06/12/13 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/12/13 REFERRED TO CODES\nSENATE - 06/17/13 RECALLED FROM ASSEMBLY\nASSEMBLY - 06/17/13 RETURNED TO SENATE\nSENATE - 06/18/13 VOTE RECONSIDERED - RESTORED TO THIRD READING\nSENATE - 06/18/13 AMENDED ON THIRD READING 89A\nSENATE - 06/21/13 REPASSED SENATE\nSENATE - 06/21/13 RETURNED TO ASSEMBLY\nASSEMBLY - 06/24/13 REFERRED TO WAYS AND MEANS\nASSEMBLY - 01/08/14 DIED IN ASSEMBLY\nASSEMBLY - 01/08/14 RETURNED TO SENATE\nSENATE - 01/08/14 REFERRED TO FINANCE\nSENATE - 06/09/14 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/09/14 ORDERED TO THIRD READING CAL.1194\nSENATE - 06/10/14 PASSED SENATE\nSENATE - 06/10/14 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/10/14 REFERRED TO WAYS AND MEANS"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "\nASSEMBLY - 06/19/14 SUBSTITUTED FOR A7221A\nASSEMBLY - 06/19/14 ORDERED TO THIRD READING RULES CAL.599\nASSEMBLY - 06/19/14 PASSED ASSEMBLY\nASSEMBLY - 06/19/14 RETURNED TO SENATE"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SENATE - 01/09/13 REFERRED TO FINANCE\nSENATE - 06/11/13 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/11/13 ORDERED TO THIRD READING CAL.1214\nSENATE - 06/12/13 PASSED SENATE\nSENATE - 06/12/13 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/12/13 REFERRED TO CODES\nSENATE - 06/17/13 RECALLED FROM ASSEMBLY\nASSEMBLY - 06/17/13 RETURNED TO SENATE\nSENATE - 06/18/13 VOTE RECONSIDERED - RESTORED TO THIRD READING\nSENATE - 06/18/13 AMENDED ON THIRD READING 89A\nSENATE - 06/21/13 REPASSED SENATE\nSENATE - 06/21/13 RETURNED TO ASSEMBLY\nASSEMBLY - 06/24/13 REFERRED TO WAYS AND MEANS\nASSEMBLY - 01/08/14 DIED IN ASSEMBLY\nASSEMBLY - 01/08/14 RETURNED TO SENATE\nSENATE - 01/08/14 REFERRED TO FINANCE\nSENATE - 06/09/14 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/09/14 ORDERED TO THIRD READING CAL.1194\nSENATE - 06/10/14 PASSED SENATE\nSENATE - 06/10/14 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/10/14 REFERRED TO WAYS AND MEANS"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "\nASSEMBLY - 06/19/14 SUBSTITUTED FOR A7221A\nASSEMBLY - 06/19/14 ORDERED TO THIRD READING RULES CAL.599\nASSEMBLY - 06/19/14 PASSED ASSEMBLY\nASSEMBLY - 06/19/14 RETURNED TO SENATE"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SENATE - 01/09/13 REFERRED TO FINANCE\nSENATE - 06/11/13 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/11/13 ORDERED TO THIRD READING CAL.1214\nSENATE - 06/12/13 PASSED SENATE\nSENATE - 06/12/13 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/12/13 REFERRED TO CODES\nSENATE - 06/17/13 RECALLED FROM ASSEMBLY\nASSEMBLY - 06/17/13 RETURNED TO SENATE\nSENATE - 06/18/13 VOTE RECONSIDERED - RESTORED TO THIRD READING\nSENATE - 06/18/13 AMENDED ON THIRD READING 89A\nSENATE - 06/21/13 REPASSED SENATE\nSENATE - 06/21/13 RETURNED TO ASSEMBLY\nASSEMBLY - 06/24/13 REFERRED TO WAYS AND MEANS\nASSEMBLY - 01/08/14 DIED IN ASSEMBLY\nASSEMBLY - 01/08/14 RETURNED TO SENATE\nSENATE - 01/08/14 REFERRED TO FINANCE\nSENATE - 06/09/14 COMMITTEE DISCHARGED AND COMMITTED TO RULES\nSENATE - 06/09/14 ORDERED TO THIRD READING CAL.1194\nSENATE - 06/10/14 PASSED SENATE\nSENATE - 06/10/14 DELIVERED TO ASSEMBLY\nASSEMBLY - 06/10/14 REFERRED TO WAYS AND MEANS"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "\nASSEMBLY - 06/19/14 SUBSTITUTED FOR A7221A\nASSEMBLY - 06/19/14 ORDERED TO THIRD READING RULES CAL.599\nASSEMBLY - 06/19/14 PASSED ASSEMBLY\nASSEMBLY - 06/19/14 RETURNED TO SENATE"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "S1273-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S1273",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S1273"
                },
                "observedDateTime" : "2014-09-03T14:13:38.961",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_COSPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "KRUEGER HASSELL-THOMPSON STAVISKY HOYLMAN PARKER SERRANO SQUADRON DIAZ MONTGOMERY",
                        "observedData" : "KRUEGER HASSELL-THOMPSON STAVISKY HOYLMAN PARKER SERRANO DIAZ MONTGOMERY",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "KRUEGER HASSELL-THOMPSON STAVISKY HOYLMAN PARKER SERRANO "
                        }, {
                            "operation" : "DELETE",
                            "text" : "SQUADRON "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "DIAZ MONTGOMERY"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "KRUEGER HASSELL-THOMPSON STAVISKY HOYLMAN PARKER SERRANO "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "SQUADRON "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "DIAZ MONTGOMERY"
                                } ]
                            } ],
                            "size" : 1
                        }
                    } ],
                    "size" : 1
                }
            },
            "S1980-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S1980",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S1980"
                },
                "observedDateTime" : "2014-09-03T14:14:02.103",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "B",
                        "observedData" : "A",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "B"
                        }, {
                            "operation" : "INSERT",
                            "text" : "A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "B"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "B"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "B"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A B",
                        "observedData" : "DEFAULT A",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT A"
                        }, {
                            "operation" : "DELETE",
                            "text" : " B"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT A"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " B"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT A"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " B"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT A"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " B"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "S2397-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S2397",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S2397"
                },
                "observedDateTime" : "2014-09-03T14:14:14.503",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_FULLTEXT_PAGE_COUNT",
                        "status" : "EXISTING",
                        "referenceData" : "{=6}",
                        "observedData" : "{=7}",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "{="
                        }, {
                            "operation" : "DELETE",
                            "text" : "6"
                        }, {
                            "operation" : "INSERT",
                            "text" : "7"
                        }, {
                            "operation" : "EQUAL",
                            "text" : "}"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "6"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "7"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "6"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "7"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "6"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "7"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "S2499-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S2499",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S2499"
                },
                "observedDateTime" : "2014-09-03T14:14:18.247",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_COSPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "KENNEDY BOYLE ESPAILLAT HASSELL-THOMPSON PERALTA PARKER O'BRIEN RITCHIE ADDABBO DILAN AVELLA LATIMER SAVINO",
                        "observedData" : "KENNEDY ADDABBO DILAN ESPAILLAT HASSELL-THOMPSON AVELLA LATIMER PERALTA PARKER O'BRIEN SAVINO RITCHIE",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "KENNEDY "
                        }, {
                            "operation" : "DELETE",
                            "text" : "BOYLE"
                        }, {
                            "operation" : "INSERT",
                            "text" : "ADDABBO DILAN"
                        }, {
                            "operation" : "EQUAL",
                            "text" : " ESPAILLAT HASSELL-THOMPSON "
                        }, {
                            "operation" : "INSERT",
                            "text" : "AVELLA LATIMER "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "PERALTA PARKER O'BRIEN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "SAVINO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RITCHIE"
                        }, {
                            "operation" : "DELETE",
                            "text" : " ADDABBO DILAN AVELLA LATIMER SAVINO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "KENNEDY "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "BOYLE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "ADDABBO DILAN"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " ESPAILLAT HASSELL-THOMPSON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "AVELLA LATIMER "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "PERALTA PARKER O'BRIEN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "SAVINO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RITCHIE"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " ADDABBO DILAN AVELLA LATIMER SAVINO"
                                } ]
                            } ],
                            "size" : 1
                        }
                    } ],
                    "size" : 1
                }
            },
            "S2503-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S2503",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S2503"
                },
                "observedDateTime" : "2014-09-03T14:14:16.988",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_COSPONSOR",
                        "status" : "RESOLVED",
                        "referenceData" : "MARCELLINO NOZZOLIO BOYLE ESPAILLAT AVELLA FLANAGAN LARKIN LAVALLE ZELDIN MARTINS GALLIVAN",
                        "observedData" : "MARCELLINO NOZZOLIO BOYLE ADDABBO ESPAILLAT AVELLA FLANAGAN LARKIN LAVALLE ZELDIN MARTINS GALLIVAN",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "MARCELLINO NOZZOLIO BOYLE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "ADDABBO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ESPAILLAT AVELLA FLANAGAN LARKIN LAVALLE ZELDIN MARTINS GALLIVAN"
                        } ],
                        "prior" : {
                            "items" : [ ],
                            "size" : 0
                        }
                    } ],
                    "size" : 1
                }
            },
            "S3392-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S3392",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S3392"
                },
                "observedDateTime" : "2014-09-03T14:14:05.746",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "S4709-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S4709",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S4709"
                },
                "observedDateTime" : "2014-09-03T14:13:36.851",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "S5052-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S5052",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S5052"
                },
                "observedDateTime" : "2014-09-03T14:13:45.224",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 2
                        }
                    } ],
                    "size" : 2
                }
            },
            "S6768-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S6768",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S6768"
                },
                "observedDateTime" : "2014-09-03T14:13:58.497",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "S7007-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S7007",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S7007"
                },
                "observedDateTime" : "2014-09-03T14:14:05.450",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "S7059-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S7059",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S7059"
                },
                "observedDateTime" : "2014-09-03T14:14:06.478",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_COSPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "AVELLA RANZENHOFER MAZIARZ LARKIN LITTLE DEFRANCISCO LIBOUS BONACIC GALLIVAN MARCHIONE RITCHIE",
                        "observedData" : "AVELLA RANZENHOFER MAZIARZ LARKIN LITTLE DEFRANCISCO LIBOUS BONACIC MARCHIONE RITCHIE",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "AVELLA RANZENHOFER MAZIARZ LARKIN LITTLE DEFRANCISCO LIBOUS BONACIC "
                        }, {
                            "operation" : "DELETE",
                            "text" : "GALLIVAN "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MARCHIONE RITCHIE"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "AVELLA RANZENHOFER MAZIARZ LARKIN LITTLE DEFRANCISCO LIBOUS BONACIC "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "GALLIVAN "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARCHIONE RITCHIE"
                                } ]
                            } ],
                            "size" : 1
                        }
                    } ],
                    "size" : 1
                }
            },
            "S7293-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S7293",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S7293"
                },
                "observedDateTime" : "2014-09-03T14:14:16.336",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "S7324-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S7324",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S7324"
                },
                "observedDateTime" : "2014-09-03T14:14:18.100",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_FULLTEXT_PAGE_COUNT",
                        "status" : "EXISTING",
                        "referenceData" : "{=3}",
                        "observedData" : "{=4}",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "{="
                        }, {
                            "operation" : "DELETE",
                            "text" : "3"
                        }, {
                            "operation" : "INSERT",
                            "text" : "4"
                        }, {
                            "operation" : "EQUAL",
                            "text" : "}"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "3"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "4"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "3"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "4"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "3"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "4"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "S7464-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S7464",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S7464"
                },
                "observedDateTime" : "2014-09-03T14:14:22.128",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 2
                        }
                    } ],
                    "size" : 2
                }
            },
            "S7489-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "S7489",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "S7489"
                },
                "observedDateTime" : "2014-09-03T14:14:24.503",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTIVE_AMENDMENT",
                        "status" : "RESOLVED",
                        "referenceData" : "A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "A"
                        }, {
                            "operation" : "INSERT",
                            "text" : "DEFAULT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "A"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "DEFAULT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_AMENDMENT_PUBLISH",
                        "status" : "RESOLVED",
                        "referenceData" : "DEFAULT A",
                        "observedData" : "DEFAULT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DEFAULT"
                        }, {
                            "operation" : "DELETE",
                            "text" : " A"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DEFAULT"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " A"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 2
                }
            },
            "A92-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A92",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A92"
                },
                "observedDateTime" : "2014-09-03T14:13:53.362",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "WEISENBERG",
                        "observedData" : "WEISENBERG GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "WEISENBERG"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A116-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A116",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A116"
                },
                "observedDateTime" : "2014-09-03T14:13:53.553",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SWEENEY",
                        "observedData" : "SWEENEY GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SWEENEY"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A121-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A121",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A121"
                },
                "observedDateTime" : "2014-09-03T14:13:53.767",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK WEISENBERG TITONE COOK HEASTIE GOTTFRIED",
                        "observedData" : "GLICK WEISENBERG TITONE COOK HEASTIE GABRYSZAK GOTTFRIED",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK WEISENBERG TITONE COOK HEASTIE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GOTTFRIED"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK WEISENBERG TITONE COOK HEASTIE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK WEISENBERG TITONE COOK HEASTIE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK WEISENBERG TITONE COOK HEASTIE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A256-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A256",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A256"
                },
                "observedDateTime" : "2014-09-03T14:13:55.895",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON WEISENBERG CROUCH SCARBOROUGH BARCLAY HOOPER",
                        "observedData" : "COLTON CASTRO WEISENBERG CROUCH SCARBOROUGH BARCLAY HOOPER",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON "
                        }, {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "WEISENBERG CROUCH SCARBOROUGH BARCLAY HOOPER"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG CROUCH SCARBOROUGH BARCLAY HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG CROUCH SCARBOROUGH BARCLAY HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG CROUCH SCARBOROUGH BARCLAY HOOPER"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A408-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A408",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A408"
                },
                "observedDateTime" : "2014-09-03T14:14:00.423",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAMOS DENDEKKER THIELE MAGEE RIVERA ARROYO SCHIMEL HEVESI WEISENBERG COOK SEPULVEDA SWEENEY SCARBOROUGH HEASTIE ABBATE SIMANOWITZ",
                        "observedData" : "RAMOS DENDEKKER THIELE MAGEE RIVERA ARROYO SCHIMEL HEVESI WEISENBERG COOK SEPULVEDA SWEENEY SCARBOROUGH HEASTIE ABBATE GABRYSZAK SIMANOWITZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAMOS DENDEKKER THIELE MAGEE RIVERA ARROYO SCHIMEL HEVESI WEISENBERG COOK SEPULVEDA SWEENEY SCARBOROUGH HEASTIE ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "SIMANOWITZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS DENDEKKER THIELE MAGEE RIVERA ARROYO SCHIMEL HEVESI WEISENBERG COOK SEPULVEDA SWEENEY SCARBOROUGH HEASTIE ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "SIMANOWITZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS DENDEKKER THIELE MAGEE RIVERA ARROYO SCHIMEL HEVESI WEISENBERG COOK SEPULVEDA SWEENEY SCARBOROUGH HEASTIE ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "SIMANOWITZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS DENDEKKER THIELE MAGEE RIVERA ARROYO SCHIMEL HEVESI WEISENBERG COOK SEPULVEDA SWEENEY SCARBOROUGH HEASTIE ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "SIMANOWITZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A474-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A474",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A474"
                },
                "observedDateTime" : "2014-09-03T14:14:02.912",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAIA SCHIMEL STECK DENDEKKER COOK LUPARDO MAGEE SWEENEY",
                        "observedData" : "RAIA SCHIMEL STECK DENDEKKER COOK LUPARDO MAGEE SWEENEY GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAIA SCHIMEL STECK DENDEKKER COOK LUPARDO MAGEE SWEENEY"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA SCHIMEL STECK DENDEKKER COOK LUPARDO MAGEE SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA SCHIMEL STECK DENDEKKER COOK LUPARDO MAGEE SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA SCHIMEL STECK DENDEKKER COOK LUPARDO MAGEE SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A622-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A622",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A622"
                },
                "observedDateTime" : "2014-09-03T14:14:43.575",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "PERRY MILLER DENDEKKER MOSLEY DINOWITZ HOOPER JACOBS MAGNARELLI MCKEVITT RIVERA ARROYO PAULIN HEVESI BRAUNSTEIN COOK CUSICK ENGLEBRIGHT ROBINSON MARKEY TITUS CLARK FARRELL CRESPO KELLNER ABINANTI CAMARA AUBRY GOTTFRIED PRETLOW BROOK-KRASNY KAVANAGH LAVINE WEISENBERG SWEENEY MILLMAN SCARBOROUGH ORTIZ MCDONOUGH",
                        "observedData" : "PERRY MILLER DENDEKKER MOSLEY DINOWITZ HOOPER JACOBS MAGNARELLI MCKEVITT RIVERA ARROYO PAULIN HEVESI BRAUNSTEIN COOK CUSICK ENGLEBRIGHT ROBINSON MARKEY TITUS CLARK FARRELL CRESPO KELLNER ABINANTI CAMARA AUBRY GOTTFRIED PRETLOW BROOK-KRASNY KAVANAGH LAVINE WEISENBERG SWEENEY MILLMAN SCARBOROUGH GABRYSZAK ORTIZ MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "PERRY MILLER DENDEKKER MOSLEY DINOWITZ HOOPER JACOBS MAGNARELLI MCKEVITT RIVERA ARROYO PAULIN HEVESI BRAUNSTEIN COOK CUSICK ENGLEBRIGHT ROBINSON MARKEY TITUS CLARK FARRELL CRESPO KELLNER ABINANTI CAMARA AUBRY GOTTFRIED PRETLOW BROOK-KRASNY KAVANAGH LAVINE WEISENBERG SWEENEY MILLMAN SCARBOROUGH "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ORTIZ MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY MILLER DENDEKKER MOSLEY DINOWITZ HOOPER JACOBS MAGNARELLI MCKEVITT RIVERA ARROYO PAULIN HEVESI BRAUNSTEIN COOK CUSICK ENGLEBRIGHT ROBINSON MARKEY TITUS CLARK FARRELL CRESPO KELLNER ABINANTI CAMARA AUBRY GOTTFRIED PRETLOW BROOK-KRASNY KAVANAGH LAVINE WEISENBERG SWEENEY MILLMAN SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY MILLER DENDEKKER MOSLEY DINOWITZ HOOPER JACOBS MAGNARELLI MCKEVITT RIVERA ARROYO PAULIN HEVESI BRAUNSTEIN COOK CUSICK ENGLEBRIGHT ROBINSON MARKEY TITUS CLARK FARRELL CRESPO KELLNER ABINANTI CAMARA AUBRY GOTTFRIED PRETLOW BROOK-KRASNY KAVANAGH LAVINE WEISENBERG SWEENEY MILLMAN SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY MILLER DENDEKKER MOSLEY DINOWITZ HOOPER JACOBS MAGNARELLI MCKEVITT RIVERA ARROYO PAULIN HEVESI BRAUNSTEIN COOK CUSICK ENGLEBRIGHT ROBINSON MARKEY TITUS CLARK FARRELL CRESPO KELLNER ABINANTI CAMARA AUBRY GOTTFRIED PRETLOW BROOK-KRASNY KAVANAGH LAVINE WEISENBERG SWEENEY MILLMAN SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A818-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A818",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A818"
                },
                "observedDateTime" : "2014-09-03T14:14:48.910",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "LUPINACCI NOLAN BRENNAN GOTTFRIED",
                        "observedData" : "LUPINACCI NOLAN BRENNAN GABRYSZAK GOTTFRIED",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "LUPINACCI NOLAN BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GOTTFRIED"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LUPINACCI NOLAN BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LUPINACCI NOLAN BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LUPINACCI NOLAN BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A822-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A822",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A822"
                },
                "observedDateTime" : "2014-09-03T14:14:49.200",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "KATZ",
                        "observedData" : "GABRYSZAK KATZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "KATZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A852-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A852",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A852"
                },
                "observedDateTime" : "2014-09-03T14:14:49.395",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK PERRY WEISENBERG TITONE ROBINSON",
                        "observedData" : "GLICK PERRY WEISENBERG TITONE GABRYSZAK ROBINSON",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK PERRY WEISENBERG TITONE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY WEISENBERG TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY WEISENBERG TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY WEISENBERG TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A871-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A871",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A871"
                },
                "observedDateTime" : "2014-09-03T14:14:49.939",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO PERRY WEISENBERG SOLAGES COOK MOSLEY",
                        "observedData" : "ARROYO PERRY WEISENBERG SOLAGES COOK MOSLEY GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO PERRY WEISENBERG SOLAGES COOK MOSLEY"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO PERRY WEISENBERG SOLAGES COOK MOSLEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO PERRY WEISENBERG SOLAGES COOK MOSLEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO PERRY WEISENBERG SOLAGES COOK MOSLEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A942-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A942",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A942"
                },
                "observedDateTime" : "2014-09-03T14:14:52.493",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CROUCH FINCH ORTIZ",
                        "observedData" : "CROUCH FINCH GABRYSZAK ORTIZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "CROUCH FINCH "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ORTIZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A984-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A984",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A984"
                },
                "observedDateTime" : "2014-09-03T14:14:53.716",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SCHIMEL THIELE GOODELL SCARBOROUGH BRENNAN CAMARA MARKEY MCDONOUGH",
                        "observedData" : "SCHIMEL THIELE GOODELL SCARBOROUGH BRENNAN CAMARA GABRYSZAK MARKEY MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SCHIMEL THIELE GOODELL SCARBOROUGH BRENNAN CAMARA "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MARKEY MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL THIELE GOODELL SCARBOROUGH BRENNAN CAMARA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARKEY MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL THIELE GOODELL SCARBOROUGH BRENNAN CAMARA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARKEY MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL THIELE GOODELL SCARBOROUGH BRENNAN CAMARA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARKEY MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1122-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1122",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1122"
                },
                "observedDateTime" : "2014-09-03T14:14:57.994",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK PERRY SALADINO CRESPO GALEF MILLER TITONE CROUCH CYMBROWITZ KOLB FINCH MCDONOUGH",
                        "observedData" : "GLICK PERRY CRESPO MILLER TITONE CROUCH CYMBROWITZ FINCH SALADINO GALEF KOLB GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK PERRY "
                        }, {
                            "operation" : "DELETE",
                            "text" : "SALADINO CRESPO GALEF"
                        }, {
                            "operation" : "INSERT",
                            "text" : "CRESPO"
                        }, {
                            "operation" : "EQUAL",
                            "text" : " MILLER TITONE CROUCH CYMBROWITZ "
                        }, {
                            "operation" : "DELETE",
                            "text" : "KOLB "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "FINCH"
                        }, {
                            "operation" : "INSERT",
                            "text" : " SALADINO GALEF KOLB GABRYSZAK"
                        }, {
                            "operation" : "EQUAL",
                            "text" : " MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "SALADINO CRESPO GALEF"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CRESPO"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " MILLER TITONE CROUCH CYMBROWITZ "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "KOLB "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "FINCH"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " SALADINO GALEF KOLB GABRYSZAK"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "SALADINO CRESPO GALEF"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CRESPO"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " MILLER TITONE CROUCH CYMBROWITZ "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "KOLB "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "FINCH"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " SALADINO GALEF KOLB GABRYSZAK"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "SALADINO CRESPO GALEF"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CRESPO"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " MILLER TITONE CROUCH CYMBROWITZ "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "KOLB "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "FINCH"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " SALADINO GALEF KOLB GABRYSZAK"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1153-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1153",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1153"
                },
                "observedDateTime" : "2014-09-03T14:14:59.240",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "BRENNAN",
                        "observedData" : "BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1201-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1201",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1201"
                },
                "observedDateTime" : "2014-09-03T14:14:59.341",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SCHIMEL COLTON THIELE MILLMAN ABBATE HOOPER",
                        "observedData" : "SCHIMEL COLTON THIELE MILLMAN ABBATE GABRYSZAK HOOPER",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SCHIMEL COLTON THIELE MILLMAN ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "HOOPER"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL COLTON THIELE MILLMAN ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL COLTON THIELE MILLMAN ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL COLTON THIELE MILLMAN ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "HOOPER"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1240-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1240",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1240"
                },
                "observedDateTime" : "2014-09-03T14:15:04.069",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK WEISENBERG GALEF LUPARDO KOLB SCARBOROUGH BRENNAN JACOBS",
                        "observedData" : "GLICK WEISENBERG GALEF LUPARDO KOLB SCARBOROUGH BRENNAN GABRYSZAK JACOBS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK WEISENBERG GALEF LUPARDO KOLB SCARBOROUGH BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "JACOBS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK WEISENBERG GALEF LUPARDO KOLB SCARBOROUGH BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK WEISENBERG GALEF LUPARDO KOLB SCARBOROUGH BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK WEISENBERG GALEF LUPARDO KOLB SCARBOROUGH BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1305-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1305",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1305"
                },
                "observedDateTime" : "2014-09-03T14:15:05.782",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "THIELE",
                        "observedData" : "THIELE GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "THIELE"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1519-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1519",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1519"
                },
                "observedDateTime" : "2014-09-03T14:15:11.973",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SALADINO LUPARDO ABINANTI JACOBS",
                        "observedData" : "SALADINO LUPARDO ABINANTI GABRYSZAK JACOBS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SALADINO LUPARDO ABINANTI "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "JACOBS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SALADINO LUPARDO ABINANTI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SALADINO LUPARDO ABINANTI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SALADINO LUPARDO ABINANTI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1677-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1677",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1677"
                },
                "observedDateTime" : "2014-09-03T14:14:43.170",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAMOS ROSENTHAL COLTON PERRY JACOBS ARROYO HEVESI GALEF GUNTHER ROBINSON MARKEY GLICK CLARK MORELLE ZEBROWSKI KELLNER CYMBROWITZ JAFFEE BENEDETTO AUBRY GOTTFRIED O'DONNELL LAVINE WEISENBERG MILLMAN LIFTON LENTOL ORTIZ",
                        "observedData" : "RAMOS ROSENTHAL COLTON PERRY JACOBS ARROYO HEVESI GALEF GUNTHER ROBINSON MARKEY GLICK CLARK MORELLE ZEBROWSKI KELLNER CYMBROWITZ JAFFEE BENEDETTO AUBRY GOTTFRIED O'DONNELL LAVINE WEISENBERG MILLMAN LIFTON GABRYSZAK LENTOL ORTIZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAMOS ROSENTHAL COLTON PERRY JACOBS ARROYO HEVESI GALEF GUNTHER ROBINSON MARKEY GLICK CLARK MORELLE ZEBROWSKI KELLNER CYMBROWITZ JAFFEE BENEDETTO AUBRY GOTTFRIED O'DONNELL LAVINE WEISENBERG MILLMAN LIFTON "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "LENTOL ORTIZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS ROSENTHAL COLTON PERRY JACOBS ARROYO HEVESI GALEF GUNTHER ROBINSON MARKEY GLICK CLARK MORELLE ZEBROWSKI KELLNER CYMBROWITZ JAFFEE BENEDETTO AUBRY GOTTFRIED O'DONNELL LAVINE WEISENBERG MILLMAN LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS ROSENTHAL COLTON PERRY JACOBS ARROYO HEVESI GALEF GUNTHER ROBINSON MARKEY GLICK CLARK MORELLE ZEBROWSKI KELLNER CYMBROWITZ JAFFEE BENEDETTO AUBRY GOTTFRIED O'DONNELL LAVINE WEISENBERG MILLMAN LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS ROSENTHAL COLTON PERRY JACOBS ARROYO HEVESI GALEF GUNTHER ROBINSON MARKEY GLICK CLARK MORELLE ZEBROWSKI KELLNER CYMBROWITZ JAFFEE BENEDETTO AUBRY GOTTFRIED O'DONNELL LAVINE WEISENBERG MILLMAN LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL ORTIZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1693-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1693",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1693"
                },
                "observedDateTime" : "2014-09-03T14:14:43",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "THIELE MONTESANO MCDONOUGH",
                        "observedData" : "THIELE GABRYSZAK MONTESANO MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "THIELE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MONTESANO MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1755-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1755",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1755"
                },
                "observedDateTime" : "2014-09-03T14:14:44.313",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "PERRY WEISENBERG CAHILL CURRAN LUPARDO SWEENEY MILLMAN WEINSTEIN ROBINSON GOTTFRIED PEOPLES-STOKES",
                        "observedData" : "PERRY WEISENBERG CAHILL CURRAN LUPARDO SWEENEY MILLMAN WEINSTEIN GABRYSZAK ROBINSON GOTTFRIED PEOPLES-STOKES",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "PERRY WEISENBERG CAHILL CURRAN LUPARDO SWEENEY MILLMAN WEINSTEIN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON GOTTFRIED PEOPLES-STOKES"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY WEISENBERG CAHILL CURRAN LUPARDO SWEENEY MILLMAN WEINSTEIN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED PEOPLES-STOKES"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY WEISENBERG CAHILL CURRAN LUPARDO SWEENEY MILLMAN WEINSTEIN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED PEOPLES-STOKES"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY WEISENBERG CAHILL CURRAN LUPARDO SWEENEY MILLMAN WEINSTEIN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED PEOPLES-STOKES"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1863-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1863",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1863"
                },
                "observedDateTime" : "2014-09-03T14:14:47.896",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "LOPEZ P KEARNS CROUCH GUNTHER TENNEY CERETTO GARBARINO KATZ",
                        "observedData" : "LOPEZ P KEARNS CROUCH GUNTHER GABRYSZAK TENNEY CERETTO GARBARINO KATZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "LOPEZ P KEARNS CROUCH GUNTHER "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "TENNEY CERETTO GARBARINO KATZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P KEARNS CROUCH GUNTHER "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY CERETTO GARBARINO KATZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P KEARNS CROUCH GUNTHER "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY CERETTO GARBARINO KATZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P KEARNS CROUCH GUNTHER "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY CERETTO GARBARINO KATZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A1941-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A1941",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A1941"
                },
                "observedDateTime" : "2014-09-03T14:14:49.457",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAIA SALADINO THIELE SEPULVEDA SWEENEY SCARBOROUGH DUPREY HAWLEY BARCLAY GARBARINO RA",
                        "observedData" : "RAIA SALADINO THIELE SEPULVEDA SWEENEY SCARBOROUGH DUPREY HAWLEY BARCLAY GABRYSZAK GARBARINO RA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAIA SALADINO THIELE SEPULVEDA SWEENEY SCARBOROUGH DUPREY HAWLEY BARCLAY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GARBARINO RA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA SALADINO THIELE SEPULVEDA SWEENEY SCARBOROUGH DUPREY HAWLEY BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GARBARINO RA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA SALADINO THIELE SEPULVEDA SWEENEY SCARBOROUGH DUPREY HAWLEY BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GARBARINO RA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA SALADINO THIELE SEPULVEDA SWEENEY SCARBOROUGH DUPREY HAWLEY BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GARBARINO RA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2054-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2054",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2054"
                },
                "observedDateTime" : "2014-09-03T14:14:53.440",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "HEVESI MALLIOTAKIS WEISENBERG CURRAN LUPARDO TENNEY GOTTFRIED KIM MARKEY MONTESANO KATZ MCDONOUGH",
                        "observedData" : "MALLIOTAKIS LUPARDO TENNEY GOTTFRIED KIM MONTESANO HEVESI WEISENBERG CURRAN GABRYSZAK MARKEY KATZ MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "HEVESI "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MALLIOTAKIS "
                        }, {
                            "operation" : "DELETE",
                            "text" : "WEISENBERG CURRAN "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "LUPARDO TENNEY GOTTFRIED KIM "
                        }, {
                            "operation" : "DELETE",
                            "text" : "MARKEY "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MONTESANO "
                        }, {
                            "operation" : "INSERT",
                            "text" : "HEVESI WEISENBERG CURRAN GABRYSZAK MARKEY "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "KATZ MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "HEVESI "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MALLIOTAKIS "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "WEISENBERG CURRAN "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LUPARDO TENNEY GOTTFRIED KIM "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "MARKEY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "HEVESI WEISENBERG CURRAN GABRYSZAK MARKEY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "HEVESI "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MALLIOTAKIS "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "WEISENBERG CURRAN "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LUPARDO TENNEY GOTTFRIED KIM "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "MARKEY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "HEVESI WEISENBERG CURRAN GABRYSZAK MARKEY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "HEVESI "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MALLIOTAKIS "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "WEISENBERG CURRAN "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LUPARDO TENNEY GOTTFRIED KIM "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "MARKEY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "HEVESI WEISENBERG CURRAN GABRYSZAK MARKEY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2148-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2148",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2148"
                },
                "observedDateTime" : "2014-09-03T14:14:56.542",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "BRENNAN CAMARA",
                        "observedData" : "BRENNAN CAMARA GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "BRENNAN CAMARA"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN CAMARA"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN CAMARA"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN CAMARA"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2232-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2232",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2232"
                },
                "observedDateTime" : "2014-09-03T14:14:57.741",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "WEISENBERG CAHILL GUNTHER",
                        "observedData" : "WEISENBERG CAHILL GUNTHER GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "WEISENBERG CAHILL GUNTHER"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG CAHILL GUNTHER"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG CAHILL GUNTHER"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG CAHILL GUNTHER"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2399-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2399",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2399"
                },
                "observedDateTime" : "2014-09-03T14:15:03.361",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CLARK COLTON CYMBROWITZ MAGEE GOTTFRIED JACOBS RIVERA WEISENBERG GALEF CAHILL COOK BRENNAN WRIGHT",
                        "observedData" : "CLARK COLTON CYMBROWITZ MAGEE GOTTFRIED JACOBS RIVERA WEISENBERG GALEF CAHILL COOK BRENNAN GABRYSZAK WRIGHT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "CLARK COLTON CYMBROWITZ MAGEE GOTTFRIED JACOBS RIVERA WEISENBERG GALEF CAHILL COOK BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "WRIGHT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK COLTON CYMBROWITZ MAGEE GOTTFRIED JACOBS RIVERA WEISENBERG GALEF CAHILL COOK BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WRIGHT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK COLTON CYMBROWITZ MAGEE GOTTFRIED JACOBS RIVERA WEISENBERG GALEF CAHILL COOK BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WRIGHT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK COLTON CYMBROWITZ MAGEE GOTTFRIED JACOBS RIVERA WEISENBERG GALEF CAHILL COOK BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WRIGHT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2491-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2491",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2491"
                },
                "observedDateTime" : "2014-09-03T14:15:06.783",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON GIGLIO SALADINO DENDEKKER JAFFEE",
                        "observedData" : "COLTON CASTRO GIGLIO SALADINO DENDEKKER JAFFEE",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON "
                        }, {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GIGLIO SALADINO DENDEKKER JAFFEE"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GIGLIO SALADINO DENDEKKER JAFFEE"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GIGLIO SALADINO DENDEKKER JAFFEE"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GIGLIO SALADINO DENDEKKER JAFFEE"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2494-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2494",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2494"
                },
                "observedDateTime" : "2014-09-03T14:15:06.759",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "MAGEE NOLAN MONTESANO",
                        "observedData" : "CASTRO MAGEE NOLAN MONTESANO",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MAGEE NOLAN MONTESANO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MAGEE NOLAN MONTESANO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MAGEE NOLAN MONTESANO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MAGEE NOLAN MONTESANO"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2509-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2509",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2509"
                },
                "observedDateTime" : "2014-09-03T14:15:08.200",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CROUCH FINCH MCDONOUGH",
                        "observedData" : "CROUCH FINCH GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "CROUCH FINCH "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2669-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2669",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2669"
                },
                "observedDateTime" : "2014-09-03T14:15:13.625",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_COSPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "DAVILA RUSSELL RODRIGUEZ WEPRIN PERRY MOSLEY AUBRY",
                        "observedData" : "DAVILA RUSSELL RODRIGUEZ WEPRIN MOSLEY AUBRY",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "DAVILA RUSSELL RODRIGUEZ WEPRIN "
                        }, {
                            "operation" : "DELETE",
                            "text" : "PERRY "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MOSLEY AUBRY"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DAVILA RUSSELL RODRIGUEZ WEPRIN "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "PERRY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MOSLEY AUBRY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DAVILA RUSSELL RODRIGUEZ WEPRIN "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "PERRY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MOSLEY AUBRY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "DAVILA RUSSELL RODRIGUEZ WEPRIN "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "PERRY "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MOSLEY AUBRY"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2709-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2709",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2709"
                },
                "observedDateTime" : "2014-09-03T14:15:15.092",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "BROOK-KRASNY MCLAUGHLIN SALADINO CROUCH HEASTIE FINCH MCDONOUGH",
                        "observedData" : "BROOK-KRASNY MCLAUGHLIN SALADINO CROUCH HEASTIE FINCH GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "BROOK-KRASNY MCLAUGHLIN SALADINO CROUCH HEASTIE FINCH "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BROOK-KRASNY MCLAUGHLIN SALADINO CROUCH HEASTIE FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BROOK-KRASNY MCLAUGHLIN SALADINO CROUCH HEASTIE FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BROOK-KRASNY MCLAUGHLIN SALADINO CROUCH HEASTIE FINCH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2773-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2773",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2773"
                },
                "observedDateTime" : "2014-09-03T14:15:17.420",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON PERRY GALEF CAHILL COOK BRENNAN LIFTON ROBINSON GOTTFRIED",
                        "observedData" : "COLTON PERRY GALEF CAHILL COOK BRENNAN LIFTON GABRYSZAK ROBINSON GOTTFRIED",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON PERRY GALEF CAHILL COOK BRENNAN LIFTON "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON GOTTFRIED"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON PERRY GALEF CAHILL COOK BRENNAN LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON PERRY GALEF CAHILL COOK BRENNAN LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON PERRY GALEF CAHILL COOK BRENNAN LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A2778-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A2778",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A2778"
                },
                "observedDateTime" : "2014-09-03T14:15:17.982",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "WEISENBERG MAYER SKARTADOS BRENNAN LENTOL RIVERA",
                        "observedData" : "WEISENBERG MAYER SKARTADOS BRENNAN GABRYSZAK LENTOL RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "WEISENBERG MAYER SKARTADOS BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "LENTOL RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG MAYER SKARTADOS BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG MAYER SKARTADOS BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG MAYER SKARTADOS BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3023-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3023",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3023"
                },
                "observedDateTime" : "2014-09-03T14:15:23.548",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COOK",
                        "observedData" : "COOK GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COOK"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3153-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3153",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3153"
                },
                "observedDateTime" : "2014-09-03T14:15:27.578",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "PERRY ENGLEBRIGHT GOTTFRIED",
                        "observedData" : "PERRY ENGLEBRIGHT GABRYSZAK GOTTFRIED",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "PERRY ENGLEBRIGHT "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GOTTFRIED"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY ENGLEBRIGHT "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY ENGLEBRIGHT "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY ENGLEBRIGHT "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3213-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3213",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3213"
                },
                "observedDateTime" : "2014-09-03T14:15:28.954",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_FULLTEXT_PAGE_COUNT",
                        "status" : "EXISTING",
                        "referenceData" : "{=3}",
                        "observedData" : "{=4}",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "{="
                        }, {
                            "operation" : "DELETE",
                            "text" : "3"
                        }, {
                            "operation" : "INSERT",
                            "text" : "4"
                        }, {
                            "operation" : "EQUAL",
                            "text" : "}"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "3"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "4"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "3"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "4"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "3"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "4"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3391-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3391",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3391"
                },
                "observedDateTime" : "2014-09-03T14:15:32.477",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAIA CORWIN FITZPATRICK PALMESANO CROUCH FINCH TENNEY MONTESANO MCKEVITT GRAF DUPREY HAWLEY BARCLAY BLANKENBUSH OAKS MCDONOUGH",
                        "observedData" : "RAIA CORWIN FITZPATRICK PALMESANO LOSQUADRO CROUCH FINCH TENNEY MONTESANO MCKEVITT GRAF DUPREY HAWLEY BARCLAY BLANKENBUSH OAKS MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAIA CORWIN FITZPATRICK PALMESANO "
                        }, {
                            "operation" : "INSERT",
                            "text" : "LOSQUADRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "CROUCH FINCH TENNEY MONTESANO MCKEVITT GRAF DUPREY HAWLEY BARCLAY BLANKENBUSH OAKS MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA CORWIN FITZPATRICK PALMESANO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "LOSQUADRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH TENNEY MONTESANO MCKEVITT GRAF DUPREY HAWLEY BARCLAY BLANKENBUSH OAKS MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA CORWIN FITZPATRICK PALMESANO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "LOSQUADRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH TENNEY MONTESANO MCKEVITT GRAF DUPREY HAWLEY BARCLAY BLANKENBUSH OAKS MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA CORWIN FITZPATRICK PALMESANO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "LOSQUADRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CROUCH FINCH TENNEY MONTESANO MCKEVITT GRAF DUPREY HAWLEY BARCLAY BLANKENBUSH OAKS MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3481-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3481",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3481"
                },
                "observedDateTime" : "2014-09-03T14:15:34.809",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "TITONE COOK NOLAN ABBATE ROBINSON GOTTFRIED MARKEY MCDONALD",
                        "observedData" : "TITONE COOK NOLAN ABBATE GABRYSZAK ROBINSON GOTTFRIED MARKEY MCDONALD",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "TITONE COOK NOLAN ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON GOTTFRIED MARKEY MCDONALD"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE COOK NOLAN ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MARKEY MCDONALD"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE COOK NOLAN ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MARKEY MCDONALD"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE COOK NOLAN ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MARKEY MCDONALD"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3576-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3576",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3576"
                },
                "observedDateTime" : "2014-09-03T14:15:37.448",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK ARROYO SCHIMEL BROOK-KRASNY GALEF COOK SWEENEY CERETTO",
                        "observedData" : "GLICK ARROYO SCHIMEL BROOK-KRASNY GALEF COOK SWEENEY GABRYSZAK CERETTO",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK ARROYO SCHIMEL BROOK-KRASNY GALEF COOK SWEENEY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "CERETTO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK ARROYO SCHIMEL BROOK-KRASNY GALEF COOK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK ARROYO SCHIMEL BROOK-KRASNY GALEF COOK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK ARROYO SCHIMEL BROOK-KRASNY GALEF COOK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3749-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3749",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3749"
                },
                "observedDateTime" : "2014-09-03T14:15:42.332",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_LAW_CODE_SUMMARY",
                        "status" : "EXISTING",
                        "referenceData" : "Amd S6542, Ed L Relates to the performance of medical services by physician assistants.",
                        "observedData" : "Amd S6542, Ed L Relates to the performance of medical services by physician assistants; permits a physician to employ and supervise up to six physician assistants.",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "Amd S6542, Ed L Relates to the performance of medical services by physician "
                        }, {
                            "operation" : "INSERT",
                            "text" : "assistants; permits a physician to employ and supervise up to six physician "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "assistants."
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "Amd S6542, Ed L Relates to the performance of medical services by physician "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "assistants; permits a physician to employ and supervise up to six physician "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "assistants."
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "Amd S6542, Ed L Relates to the performance of medical services by physician "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "assistants; permits a physician to employ and supervise up to six physician "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "assistants."
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "Amd S6542, Ed L Relates to the performance of medical services by physician "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "assistants; permits a physician to employ and supervise up to six physician "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "assistants."
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3759-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3759",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3759"
                },
                "observedDateTime" : "2014-09-03T14:15:42.085",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO CORWIN WALTER GIGLIO WEISENBERG STEC TENNEY CERETTO",
                        "observedData" : "ARROYO CORWIN WALTER GIGLIO WEISENBERG STEC GABRYSZAK TENNEY CERETTO",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO CORWIN WALTER GIGLIO WEISENBERG STEC "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "TENNEY CERETTO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO CORWIN WALTER GIGLIO WEISENBERG STEC "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO CORWIN WALTER GIGLIO WEISENBERG STEC "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO CORWIN WALTER GIGLIO WEISENBERG STEC "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY CERETTO"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3766-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3766",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3766"
                },
                "observedDateTime" : "2014-09-03T14:15:42.242",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON WEISENBERG ORTIZ GOTTFRIED HOOPER",
                        "observedData" : "COLTON WEISENBERG GABRYSZAK ORTIZ GOTTFRIED HOOPER",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON WEISENBERG "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ORTIZ GOTTFRIED HOOPER"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON WEISENBERG "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ GOTTFRIED HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON WEISENBERG "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ GOTTFRIED HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON WEISENBERG "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ GOTTFRIED HOOPER"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3821-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3821",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3821"
                },
                "observedDateTime" : "2014-09-03T14:15:43.925",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO SCHIMEL MCLAUGHLIN SALADINO CROUCH GUNTHER BRENNAN DUPREY CERETTO",
                        "observedData" : "ARROYO SCHIMEL MCLAUGHLIN SALADINO CROUCH GUNTHER BRENNAN DUPREY GABRYSZAK CERETTO",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO SCHIMEL MCLAUGHLIN SALADINO CROUCH GUNTHER BRENNAN DUPREY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "CERETTO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO SCHIMEL MCLAUGHLIN SALADINO CROUCH GUNTHER BRENNAN DUPREY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO SCHIMEL MCLAUGHLIN SALADINO CROUCH GUNTHER BRENNAN DUPREY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO SCHIMEL MCLAUGHLIN SALADINO CROUCH GUNTHER BRENNAN DUPREY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3875-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3875",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3875"
                },
                "observedDateTime" : "2014-09-03T14:15:45.069",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "O'DONNELL BROOK-KRASNY WEISENBERG",
                        "observedData" : "O'DONNELL BROOK-KRASNY WEISENBERG GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "O'DONNELL BROOK-KRASNY WEISENBERG"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "O'DONNELL BROOK-KRASNY WEISENBERG"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "O'DONNELL BROOK-KRASNY WEISENBERG"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "O'DONNELL BROOK-KRASNY WEISENBERG"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A3880-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A3880",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A3880"
                },
                "observedDateTime" : "2014-09-03T14:15:43.571",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON MAGEE LIFTON",
                        "observedData" : "COLTON MAGEE LIFTON GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON MAGEE LIFTON"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON MAGEE LIFTON"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON MAGEE LIFTON"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON MAGEE LIFTON"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4112-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4112",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4112"
                },
                "observedDateTime" : "2014-09-03T14:15:18.541",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "BRENNAN",
                        "observedData" : "BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4249-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4249",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4249"
                },
                "observedDateTime" : "2014-09-03T14:15:23.515",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "MCLAUGHLIN PALMESANO MILLER THIELE TENNEY GARBARINO DIPIETRO KOLB STEC DUPREY HAWLEY BARCLAY CERETTO OAKS",
                        "observedData" : "MCLAUGHLIN PALMESANO MILLER THIELE TENNEY GARBARINO CASTRO DIPIETRO KOLB STEC DUPREY HAWLEY BARCLAY CERETTO OAKS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "MCLAUGHLIN PALMESANO MILLER THIELE TENNEY GARBARINO "
                        }, {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "DIPIETRO KOLB STEC DUPREY HAWLEY BARCLAY CERETTO OAKS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "MCLAUGHLIN PALMESANO MILLER THIELE TENNEY GARBARINO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "DIPIETRO KOLB STEC DUPREY HAWLEY BARCLAY CERETTO OAKS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "MCLAUGHLIN PALMESANO MILLER THIELE TENNEY GARBARINO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "DIPIETRO KOLB STEC DUPREY HAWLEY BARCLAY CERETTO OAKS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "MCLAUGHLIN PALMESANO MILLER THIELE TENNEY GARBARINO "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "DIPIETRO KOLB STEC DUPREY HAWLEY BARCLAY CERETTO OAKS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4282-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4282",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4282"
                },
                "observedDateTime" : "2014-09-03T14:15:23.429",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON BRAUNSTEIN BRENNAN MARKEY",
                        "observedData" : "COLTON BRAUNSTEIN BRENNAN GABRYSZAK MARKEY",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON BRAUNSTEIN BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MARKEY"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON BRAUNSTEIN BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARKEY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON BRAUNSTEIN BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARKEY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON BRAUNSTEIN BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MARKEY"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4284-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4284",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4284"
                },
                "observedDateTime" : "2014-09-03T14:15:24.478",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RUSSELL FINCH ABBATE",
                        "observedData" : "RUSSELL FINCH ABBATE GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RUSSELL FINCH ABBATE"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RUSSELL FINCH ABBATE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RUSSELL FINCH ABBATE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RUSSELL FINCH ABBATE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4455-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4455",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4455"
                },
                "observedDateTime" : "2014-09-03T14:15:28.226",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "LOPEZ P PALMESANO TEDISCO MILLER THIELE CROUCH FINCH TENNEY MONTESANO MCKEVITT SALADINO WEISENBERG DUPREY HAWLEY BARCLAY CERETTO OAKS",
                        "observedData" : "LOPEZ P PALMESANO TEDISCO MILLER THIELE CROUCH FINCH TENNEY MONTESANO MCKEVITT CASTRO SALADINO WEISENBERG DUPREY HAWLEY BARCLAY CERETTO OAKS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "LOPEZ P PALMESANO TEDISCO MILLER THIELE CROUCH FINCH TENNEY MONTESANO MCKEVITT "
                        }, {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "SALADINO WEISENBERG DUPREY HAWLEY BARCLAY CERETTO OAKS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P PALMESANO TEDISCO MILLER THIELE CROUCH FINCH TENNEY MONTESANO MCKEVITT "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "SALADINO WEISENBERG DUPREY HAWLEY BARCLAY CERETTO OAKS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P PALMESANO TEDISCO MILLER THIELE CROUCH FINCH TENNEY MONTESANO MCKEVITT "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "SALADINO WEISENBERG DUPREY HAWLEY BARCLAY CERETTO OAKS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P PALMESANO TEDISCO MILLER THIELE CROUCH FINCH TENNEY MONTESANO MCKEVITT "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "SALADINO WEISENBERG DUPREY HAWLEY BARCLAY CERETTO OAKS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4471-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4471",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4471"
                },
                "observedDateTime" : "2014-09-03T14:15:28.862",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK PERRY WEISENBERG SWEENEY ABINANTI BRENNAN RA",
                        "observedData" : "GLICK PERRY WEISENBERG SWEENEY ABINANTI BRENNAN GABRYSZAK RA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK PERRY WEISENBERG SWEENEY ABINANTI BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY WEISENBERG SWEENEY ABINANTI BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY WEISENBERG SWEENEY ABINANTI BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY WEISENBERG SWEENEY ABINANTI BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4638-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4638",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4638"
                },
                "observedDateTime" : "2014-09-03T14:15:34.831",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK PERRY CROUCH NOLAN ABINANTI TENNEY HOOPER ARROYO BROOK-KRASNY GALEF DUPREY ABBATE CERETTO",
                        "observedData" : "GLICK PERRY CROUCH NOLAN ABINANTI TENNEY HOOPER ARROYO BROOK-KRASNY GALEF DUPREY ABBATE GABRYSZAK CERETTO",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK PERRY CROUCH NOLAN ABINANTI TENNEY HOOPER ARROYO BROOK-KRASNY GALEF DUPREY ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "CERETTO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY CROUCH NOLAN ABINANTI TENNEY HOOPER ARROYO BROOK-KRASNY GALEF DUPREY ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY CROUCH NOLAN ABINANTI TENNEY HOOPER ARROYO BROOK-KRASNY GALEF DUPREY ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK PERRY CROUCH NOLAN ABINANTI TENNEY HOOPER ARROYO BROOK-KRASNY GALEF DUPREY ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4681-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4681",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4681"
                },
                "observedDateTime" : "2014-09-03T14:15:35.983",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "TEDISCO MILLER THIELE CAMARA MAGNARELLI BROOK-KRASNY WEISENBERG CUSICK KOLB SWEENEY MILLMAN SCARBOROUGH LENTOL ORTIZ WRIGHT MCDONOUGH",
                        "observedData" : "TEDISCO MILLER THIELE CAMARA MAGNARELLI BROOK-KRASNY WEISENBERG CUSICK KOLB SWEENEY MILLMAN SCARBOROUGH GABRYSZAK LENTOL ORTIZ WRIGHT MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "TEDISCO MILLER THIELE CAMARA MAGNARELLI BROOK-KRASNY WEISENBERG CUSICK KOLB SWEENEY MILLMAN SCARBOROUGH "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "LENTOL ORTIZ WRIGHT MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TEDISCO MILLER THIELE CAMARA MAGNARELLI BROOK-KRASNY WEISENBERG CUSICK KOLB SWEENEY MILLMAN SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL ORTIZ WRIGHT MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TEDISCO MILLER THIELE CAMARA MAGNARELLI BROOK-KRASNY WEISENBERG CUSICK KOLB SWEENEY MILLMAN SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL ORTIZ WRIGHT MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TEDISCO MILLER THIELE CAMARA MAGNARELLI BROOK-KRASNY WEISENBERG CUSICK KOLB SWEENEY MILLMAN SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL ORTIZ WRIGHT MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4699-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4699",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4699"
                },
                "observedDateTime" : "2014-09-03T14:15:36.601",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "MCDONOUGH",
                        "observedData" : "CASTRO MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4733-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4733",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4733"
                },
                "observedDateTime" : "2014-09-03T14:15:37.896",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ROSENTHAL WEISENBERG JAFFEE",
                        "observedData" : "ROSENTHAL WEISENBERG JAFFEE GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ROSENTHAL WEISENBERG JAFFEE"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ROSENTHAL WEISENBERG JAFFEE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ROSENTHAL WEISENBERG JAFFEE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ROSENTHAL WEISENBERG JAFFEE"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4773-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4773",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4773"
                },
                "observedDateTime" : "2014-09-03T14:15:39.126",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAIA MCLAUGHLIN CROUCH GOODELL FINCH TENNEY GARBARINO MCKEVITT GRAF SALADINO STEC HAWLEY BARCLAY BLANKENBUSH CERETTO MCDONOUGH",
                        "observedData" : "RAIA MCLAUGHLIN CROUCH GOODELL FINCH TENNEY GARBARINO MCKEVITT GRAF SALADINO STEC HAWLEY BARCLAY GABRYSZAK BLANKENBUSH CERETTO MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAIA MCLAUGHLIN CROUCH GOODELL FINCH TENNEY GARBARINO MCKEVITT GRAF SALADINO STEC HAWLEY BARCLAY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "BLANKENBUSH CERETTO MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA MCLAUGHLIN CROUCH GOODELL FINCH TENNEY GARBARINO MCKEVITT GRAF SALADINO STEC HAWLEY BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "BLANKENBUSH CERETTO MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA MCLAUGHLIN CROUCH GOODELL FINCH TENNEY GARBARINO MCKEVITT GRAF SALADINO STEC HAWLEY BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "BLANKENBUSH CERETTO MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA MCLAUGHLIN CROUCH GOODELL FINCH TENNEY GARBARINO MCKEVITT GRAF SALADINO STEC HAWLEY BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "BLANKENBUSH CERETTO MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4789-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4789",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4789"
                },
                "observedDateTime" : "2014-09-03T14:15:38.764",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COOK ABINANTI MONTESANO",
                        "observedData" : "COOK ABINANTI GABRYSZAK MONTESANO",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COOK ABINANTI "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MONTESANO"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK ABINANTI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK ABINANTI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK ABINANTI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MONTESANO"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A4912-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A4912",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A4912"
                },
                "observedDateTime" : "2014-09-03T14:15:42.233",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CLARK MCLAUGHLIN SEPULVEDA MCDONOUGH",
                        "observedData" : "CLARK MCLAUGHLIN SEPULVEDA GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "CLARK MCLAUGHLIN SEPULVEDA "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK MCLAUGHLIN SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK MCLAUGHLIN SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK MCLAUGHLIN SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5096-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5096",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5096"
                },
                "observedDateTime" : "2014-09-03T14:15:15.513",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "TITONE ORTIZ",
                        "observedData" : "TITONE GABRYSZAK ORTIZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "TITONE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ORTIZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5122-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5122",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5122"
                },
                "observedDateTime" : "2014-09-03T14:15:16.025",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COOK RIVERA",
                        "observedData" : "COOK GABRYSZAK RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COOK "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5169-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5169",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5169"
                },
                "observedDateTime" : "2014-09-03T14:15:16.830",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SCHIMEL WEISENBERG THIELE SWEENEY MCDONOUGH",
                        "observedData" : "SCHIMEL WEISENBERG THIELE SWEENEY GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SCHIMEL WEISENBERG THIELE SWEENEY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL WEISENBERG THIELE SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL WEISENBERG THIELE SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL WEISENBERG THIELE SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5312-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5312",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5312"
                },
                "observedDateTime" : "2014-09-03T14:15:21.402",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "THIELE COOK BRENNAN RIVERA",
                        "observedData" : "THIELE COOK BRENNAN GABRYSZAK RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "THIELE COOK BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE COOK BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE COOK BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE COOK BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5318-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5318",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5318"
                },
                "observedDateTime" : "2014-09-03T14:15:21.063",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COOK JACOBS RIVERA",
                        "observedData" : "COOK GABRYSZAK JACOBS RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COOK "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "JACOBS RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5384-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5384",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5384"
                },
                "observedDateTime" : "2014-09-03T14:15:24.215",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK CLARK SWEENEY GOTTFRIED HOOPER JACOBS",
                        "observedData" : "GLICK CLARK SWEENEY GABRYSZAK GOTTFRIED HOOPER JACOBS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK CLARK SWEENEY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GOTTFRIED HOOPER JACOBS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED HOOPER JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED HOOPER JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED HOOPER JACOBS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5385-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5385",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5385"
                },
                "observedDateTime" : "2014-09-03T14:15:23.376",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "PERRY WEISENBERG MAGEE SWEENEY MILLMAN",
                        "observedData" : "PERRY WEISENBERG MAGEE SWEENEY MILLMAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "PERRY WEISENBERG MAGEE SWEENEY MILLMAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY WEISENBERG MAGEE SWEENEY MILLMAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY WEISENBERG MAGEE SWEENEY MILLMAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY WEISENBERG MAGEE SWEENEY MILLMAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5490-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5490",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5490"
                },
                "observedDateTime" : "2014-09-03T14:15:26.944",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK HEVESI MCDONOUGH",
                        "observedData" : "GLICK HEVESI GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK HEVESI "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK HEVESI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK HEVESI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK HEVESI "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5578-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5578",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5578"
                },
                "observedDateTime" : "2014-09-03T14:15:28.186",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "PERRY BRENNAN",
                        "observedData" : "PERRY BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "PERRY BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5688-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5688",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5688"
                },
                "observedDateTime" : "2014-09-03T14:15:31.951",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COOK CROUCH FAHY RIVERA",
                        "observedData" : "COOK CROUCH FAHY GABRYSZAK RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COOK CROUCH FAHY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK CROUCH FAHY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK CROUCH FAHY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK CROUCH FAHY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5689-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5689",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5689"
                },
                "observedDateTime" : "2014-09-03T14:15:32.214",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO BROOK-KRASNY PERRY SOLAGES RIVERA",
                        "observedData" : "ARROYO BROOK-KRASNY PERRY SOLAGES GABRYSZAK RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO BROOK-KRASNY PERRY SOLAGES "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO BROOK-KRASNY PERRY SOLAGES "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO BROOK-KRASNY PERRY SOLAGES "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO BROOK-KRASNY PERRY SOLAGES "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5718-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5718",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5718"
                },
                "observedDateTime" : "2014-09-03T14:15:31.181",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "LOPEZ P MCLAUGHLIN THIELE MAGEE KOLB DUPREY HAWLEY KATZ",
                        "observedData" : "LOPEZ P MCLAUGHLIN THIELE MAGEE KOLB DUPREY HAWLEY GABRYSZAK KATZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "LOPEZ P MCLAUGHLIN THIELE MAGEE KOLB DUPREY HAWLEY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "KATZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P MCLAUGHLIN THIELE MAGEE KOLB DUPREY HAWLEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P MCLAUGHLIN THIELE MAGEE KOLB DUPREY HAWLEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "LOPEZ P MCLAUGHLIN THIELE MAGEE KOLB DUPREY HAWLEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "KATZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5811-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5811",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5811"
                },
                "observedDateTime" : "2014-09-03T14:15:34.515",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "BRENNAN",
                        "observedData" : "BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5835-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5835",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5835"
                },
                "observedDateTime" : "2014-09-03T14:15:35.032",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK RAIA CLARK TITONE THIELE GOTTFRIED MCKEVITT SCHIMEL PAULIN BRINDISI SWEENEY BRENNAN LENTOL MARKEY",
                        "observedData" : "GLICK RAIA CLARK TITONE THIELE GOTTFRIED MCKEVITT SCHIMEL PAULIN BRINDISI SWEENEY BRENNAN GABRYSZAK LENTOL MARKEY",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK RAIA CLARK TITONE THIELE GOTTFRIED MCKEVITT SCHIMEL PAULIN BRINDISI SWEENEY BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "LENTOL MARKEY"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK RAIA CLARK TITONE THIELE GOTTFRIED MCKEVITT SCHIMEL PAULIN BRINDISI SWEENEY BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL MARKEY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK RAIA CLARK TITONE THIELE GOTTFRIED MCKEVITT SCHIMEL PAULIN BRINDISI SWEENEY BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL MARKEY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK RAIA CLARK TITONE THIELE GOTTFRIED MCKEVITT SCHIMEL PAULIN BRINDISI SWEENEY BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "LENTOL MARKEY"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5850-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5850",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5850"
                },
                "observedDateTime" : "2014-09-03T14:15:36.823",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COLTON WEISENBERG HEASTIE GOTTFRIED MARKEY WRIGHT MCKEVITT RIVERA",
                        "observedData" : "COLTON WEISENBERG HEASTIE GABRYSZAK GOTTFRIED MARKEY WRIGHT MCKEVITT RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COLTON WEISENBERG HEASTIE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GOTTFRIED MARKEY WRIGHT MCKEVITT RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON WEISENBERG HEASTIE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED MARKEY WRIGHT MCKEVITT RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON WEISENBERG HEASTIE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED MARKEY WRIGHT MCKEVITT RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COLTON WEISENBERG HEASTIE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED MARKEY WRIGHT MCKEVITT RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5909-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5909",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5909"
                },
                "observedDateTime" : "2014-09-03T14:15:38.240",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "WEISENBERG GUNTHER",
                        "observedData" : "WEISENBERG GUNTHER GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "WEISENBERG GUNTHER"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG GUNTHER"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG GUNTHER"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "WEISENBERG GUNTHER"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5914-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5914",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5914"
                },
                "observedDateTime" : "2014-09-03T14:15:37.943",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "HOOPER",
                        "observedData" : "CASTRO HOOPER",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "INSERT",
                            "text" : "CASTRO "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "HOOPER"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "HOOPER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "CASTRO "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "HOOPER"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5937-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5937",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5937"
                },
                "observedDateTime" : "2014-09-03T14:15:38.608",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK SCHIMEL COOK MAGEE MCDONALD RIVERA",
                        "observedData" : "GLICK SCHIMEL COOK MAGEE GABRYSZAK MCDONALD RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK SCHIMEL COOK MAGEE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONALD RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK SCHIMEL COOK MAGEE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONALD RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK SCHIMEL COOK MAGEE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONALD RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK SCHIMEL COOK MAGEE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONALD RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5941-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5941",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5941"
                },
                "observedDateTime" : "2014-09-03T14:15:38.659",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO BRENNAN",
                        "observedData" : "ARROYO BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A5965-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A5965",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A5965"
                },
                "observedDateTime" : "2014-09-03T14:15:38.194",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAIA CORWIN GIGLIO WEISENBERG THIELE SEPULVEDA MAGEE KOLB DUPREY FINCH",
                        "observedData" : "RAIA CORWIN GIGLIO WEISENBERG THIELE SEPULVEDA MAGEE KOLB DUPREY FINCH GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAIA CORWIN GIGLIO WEISENBERG THIELE SEPULVEDA MAGEE KOLB DUPREY FINCH"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA CORWIN GIGLIO WEISENBERG THIELE SEPULVEDA MAGEE KOLB DUPREY FINCH"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA CORWIN GIGLIO WEISENBERG THIELE SEPULVEDA MAGEE KOLB DUPREY FINCH"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA CORWIN GIGLIO WEISENBERG THIELE SEPULVEDA MAGEE KOLB DUPREY FINCH"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6003-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6003",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6003"
                },
                "observedDateTime" : "2014-09-03T14:15:40.457",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAIA TITONE BRONSON DENDEKKER CYMBROWITZ CAMARA MONTESANO GARBARINO MCKEVITT RA PRETLOW SALADINO WEISENBERG GALEF LUPINACCI COOK SIMOTAS SWEENEY MILLMAN CERETTO PEOPLES-STOKES MCDONOUGH",
                        "observedData" : "RAIA TITONE BRONSON DENDEKKER CYMBROWITZ CAMARA MONTESANO GARBARINO MCKEVITT RA PRETLOW SALADINO WEISENBERG GALEF LUPINACCI COOK SIMOTAS SWEENEY MILLMAN GABRYSZAK CERETTO PEOPLES-STOKES MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAIA TITONE BRONSON DENDEKKER CYMBROWITZ CAMARA MONTESANO GARBARINO MCKEVITT RA PRETLOW SALADINO WEISENBERG GALEF LUPINACCI COOK SIMOTAS SWEENEY MILLMAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "CERETTO PEOPLES-STOKES MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA TITONE BRONSON DENDEKKER CYMBROWITZ CAMARA MONTESANO GARBARINO MCKEVITT RA PRETLOW SALADINO WEISENBERG GALEF LUPINACCI COOK SIMOTAS SWEENEY MILLMAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO PEOPLES-STOKES MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA TITONE BRONSON DENDEKKER CYMBROWITZ CAMARA MONTESANO GARBARINO MCKEVITT RA PRETLOW SALADINO WEISENBERG GALEF LUPINACCI COOK SIMOTAS SWEENEY MILLMAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO PEOPLES-STOKES MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAIA TITONE BRONSON DENDEKKER CYMBROWITZ CAMARA MONTESANO GARBARINO MCKEVITT RA PRETLOW SALADINO WEISENBERG GALEF LUPINACCI COOK SIMOTAS SWEENEY MILLMAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CERETTO PEOPLES-STOKES MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6011-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6011",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6011"
                },
                "observedDateTime" : "2014-09-03T14:15:40.839",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CORWIN SALADINO LIFTON PEOPLES-STOKES",
                        "observedData" : "CORWIN SALADINO LIFTON GABRYSZAK PEOPLES-STOKES",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "CORWIN SALADINO LIFTON "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "PEOPLES-STOKES"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CORWIN SALADINO LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "PEOPLES-STOKES"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CORWIN SALADINO LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "PEOPLES-STOKES"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CORWIN SALADINO LIFTON "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "PEOPLES-STOKES"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6040-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6040",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6040"
                },
                "observedDateTime" : "2014-09-03T14:15:41.900",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SWEENEY TITUS",
                        "observedData" : "SWEENEY GABRYSZAK TITUS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SWEENEY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "TITUS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TITUS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TITUS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TITUS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6075-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6075",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6075"
                },
                "observedDateTime" : "2014-09-03T14:15:42.168",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "PERRY COOK BRENNAN BARCLAY JACOBS MCKEVITT",
                        "observedData" : "PERRY COOK BRENNAN BARCLAY GABRYSZAK JACOBS MCKEVITT",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "PERRY COOK BRENNAN BARCLAY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "JACOBS MCKEVITT"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY COOK BRENNAN BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS MCKEVITT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY COOK BRENNAN BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS MCKEVITT"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "PERRY COOK BRENNAN BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS MCKEVITT"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6125-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6125",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6125"
                },
                "observedDateTime" : "2014-09-03T14:15:44.075",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_FULLTEXT_PAGE_COUNT",
                        "status" : "EXISTING",
                        "referenceData" : "{=1}",
                        "observedData" : "{=2}",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "{="
                        }, {
                            "operation" : "DELETE",
                            "text" : "1"
                        }, {
                            "operation" : "INSERT",
                            "text" : "2"
                        }, {
                            "operation" : "EQUAL",
                            "text" : "}"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "1"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "2"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "1"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "2"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "1"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "2"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "}"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6231-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6231",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6231"
                },
                "observedDateTime" : "2014-09-03T14:14:42.718",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "THIELE COOK BRENNAN",
                        "observedData" : "THIELE COOK BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "THIELE COOK BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE COOK BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE COOK BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE COOK BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6232-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6232",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6232"
                },
                "observedDateTime" : "2014-09-03T14:14:41.960",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "TITONE ORTIZ",
                        "observedData" : "TITONE GABRYSZAK ORTIZ",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "TITONE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ORTIZ"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "TITONE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6430-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6430",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6430"
                },
                "observedDateTime" : "2014-09-03T14:14:47.198",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK CLARK PERRY SCARBOROUGH SKARTADOS ABBATE JACOBS",
                        "observedData" : "GLICK CLARK PERRY SCARBOROUGH SKARTADOS ABBATE GABRYSZAK JACOBS",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK CLARK PERRY SCARBOROUGH SKARTADOS ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "JACOBS"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK PERRY SCARBOROUGH SKARTADOS ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK PERRY SCARBOROUGH SKARTADOS ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK PERRY SCARBOROUGH SKARTADOS ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "JACOBS"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6445-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6445",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6445"
                },
                "observedDateTime" : "2014-09-03T14:14:47.592",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK HIKIND RUSSELL PERRY COOK CYMBROWITZ MAGEE ABBATE GOTTFRIED HOOPER MARKEY",
                        "observedData" : "GLICK HIKIND RUSSELL PERRY COOK CYMBROWITZ MAGEE ABBATE GABRYSZAK GOTTFRIED HOOPER MARKEY",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK HIKIND RUSSELL PERRY COOK CYMBROWITZ MAGEE ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "GOTTFRIED HOOPER MARKEY"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK HIKIND RUSSELL PERRY COOK CYMBROWITZ MAGEE ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED HOOPER MARKEY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK HIKIND RUSSELL PERRY COOK CYMBROWITZ MAGEE ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED HOOPER MARKEY"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK HIKIND RUSSELL PERRY COOK CYMBROWITZ MAGEE ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "GOTTFRIED HOOPER MARKEY"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6451-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6451",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6451"
                },
                "observedDateTime" : "2014-09-03T14:14:47.393",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "HIKIND BROOK-KRASNY WEISENBERG KELLNER COOK SWEENEY RIVERA",
                        "observedData" : "HIKIND BROOK-KRASNY WEISENBERG KELLNER COOK SWEENEY GABRYSZAK RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "HIKIND BROOK-KRASNY WEISENBERG KELLNER COOK SWEENEY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "HIKIND BROOK-KRASNY WEISENBERG KELLNER COOK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "HIKIND BROOK-KRASNY WEISENBERG KELLNER COOK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "HIKIND BROOK-KRASNY WEISENBERG KELLNER COOK SWEENEY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6482-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6482",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6482"
                },
                "observedDateTime" : "2014-09-03T14:14:48.450",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "MCDONOUGH",
                        "observedData" : "GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6536-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6536",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6536"
                },
                "observedDateTime" : "2014-09-03T14:14:50.087",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK CLARK TEDISCO THIELE DINOWITZ RA CURRAN CUSICK MILLMAN SCARBOROUGH ABBATE WRIGHT SCHIMMINGER",
                        "observedData" : "GLICK CLARK TEDISCO THIELE DINOWITZ RA CURRAN CUSICK MILLMAN SCARBOROUGH ABBATE GABRYSZAK WRIGHT SCHIMMINGER",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK CLARK TEDISCO THIELE DINOWITZ RA CURRAN CUSICK MILLMAN SCARBOROUGH ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "WRIGHT SCHIMMINGER"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK TEDISCO THIELE DINOWITZ RA CURRAN CUSICK MILLMAN SCARBOROUGH ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WRIGHT SCHIMMINGER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK TEDISCO THIELE DINOWITZ RA CURRAN CUSICK MILLMAN SCARBOROUGH ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WRIGHT SCHIMMINGER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK CLARK TEDISCO THIELE DINOWITZ RA CURRAN CUSICK MILLMAN SCARBOROUGH ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "WRIGHT SCHIMMINGER"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6785-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6785",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6785"
                },
                "observedDateTime" : "2014-09-03T14:14:55.935",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GLICK ARROYO SCHIMEL GALEF COOK GUNTHER SWEENEY BRENNAN",
                        "observedData" : "GLICK ARROYO SCHIMEL GALEF COOK GUNTHER SWEENEY BRENNAN GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GLICK ARROYO SCHIMEL GALEF COOK GUNTHER SWEENEY BRENNAN"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK ARROYO SCHIMEL GALEF COOK GUNTHER SWEENEY BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK ARROYO SCHIMEL GALEF COOK GUNTHER SWEENEY BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GLICK ARROYO SCHIMEL GALEF COOK GUNTHER SWEENEY BRENNAN"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6967-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6967",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6967"
                },
                "observedDateTime" : "2014-09-03T14:15:00.170",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "TENNEY MCDONOUGH",
                        "observedData" : "GABRYSZAK TENNEY MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "TENNEY MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A6976-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A6976",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A6976"
                },
                "observedDateTime" : "2014-09-03T14:15:00.494",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "THIELE GOODELL MCDONOUGH",
                        "observedData" : "THIELE GOODELL GABRYSZAK MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "THIELE GOODELL "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE GOODELL "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE GOODELL "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "THIELE GOODELL "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7086-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7086",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7086"
                },
                "observedDateTime" : "2014-09-03T14:15:05.257",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ROSENTHAL PERRY WEISENBERG COOK CROUCH SWEENEY MOSLEY BRENNAN ROBINSON GOTTFRIED MCDONOUGH",
                        "observedData" : "ROSENTHAL PERRY WEISENBERG COOK CROUCH SWEENEY MOSLEY BRENNAN GABRYSZAK ROBINSON GOTTFRIED MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ROSENTHAL PERRY WEISENBERG COOK CROUCH SWEENEY MOSLEY BRENNAN "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON GOTTFRIED MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ROSENTHAL PERRY WEISENBERG COOK CROUCH SWEENEY MOSLEY BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ROSENTHAL PERRY WEISENBERG COOK CROUCH SWEENEY MOSLEY BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ROSENTHAL PERRY WEISENBERG COOK CROUCH SWEENEY MOSLEY BRENNAN "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7183-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7183",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7183"
                },
                "observedDateTime" : "2014-09-03T14:15:08.045",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "GALEF MAGEE SWEENEY HAWLEY ABBATE TENNEY PEOPLES-STOKES MCDONOUGH",
                        "observedData" : "GALEF MAGEE SWEENEY HAWLEY ABBATE GABRYSZAK TENNEY PEOPLES-STOKES MCDONOUGH",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "GALEF MAGEE SWEENEY HAWLEY ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "TENNEY PEOPLES-STOKES MCDONOUGH"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GALEF MAGEE SWEENEY HAWLEY ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY PEOPLES-STOKES MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GALEF MAGEE SWEENEY HAWLEY ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY PEOPLES-STOKES MCDONOUGH"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "GALEF MAGEE SWEENEY HAWLEY ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "TENNEY PEOPLES-STOKES MCDONOUGH"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7205-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7205",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7205"
                },
                "observedDateTime" : "2014-09-03T14:15:08.748",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SCHIMEL COLTON THIELE COOK SWEENEY",
                        "observedData" : "SCHIMEL COLTON THIELE COOK SWEENEY GABRYSZAK",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SCHIMEL COLTON THIELE COOK SWEENEY"
                        }, {
                            "operation" : "INSERT",
                            "text" : " GABRYSZAK"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL COLTON THIELE COOK SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL COLTON THIELE COOK SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SCHIMEL COLTON THIELE COOK SWEENEY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : " GABRYSZAK"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7242-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7242",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7242"
                },
                "observedDateTime" : "2014-09-03T14:15:09.578",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "SEPULVEDA ROBINSON GOTTFRIED",
                        "observedData" : "SEPULVEDA GABRYSZAK ROBINSON GOTTFRIED",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "SEPULVEDA "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON GOTTFRIED"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7401-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7401",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7401"
                },
                "observedDateTime" : "2014-09-03T14:15:12.951",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CLARK PALMESANO WEISENBERG THIELE LALOR ABBATE ROBINSON MCKEVITT RA RIVERA",
                        "observedData" : "CLARK PALMESANO WEISENBERG THIELE LALOR ABBATE GABRYSZAK ROBINSON MCKEVITT RA RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "CLARK PALMESANO WEISENBERG THIELE LALOR ABBATE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON MCKEVITT RA RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK PALMESANO WEISENBERG THIELE LALOR ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON MCKEVITT RA RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK PALMESANO WEISENBERG THIELE LALOR ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON MCKEVITT RA RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK PALMESANO WEISENBERG THIELE LALOR ABBATE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON MCKEVITT RA RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7510-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7510",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7510"
                },
                "observedDateTime" : "2014-09-03T14:15:16.326",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO WEISENBERG MAGEE MOYA RIVERA",
                        "observedData" : "ARROYO WEISENBERG MAGEE GABRYSZAK MOYA RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO WEISENBERG MAGEE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "MOYA RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO WEISENBERG MAGEE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MOYA RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO WEISENBERG MAGEE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MOYA RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO WEISENBERG MAGEE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "MOYA RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7560-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7560",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7560"
                },
                "observedDateTime" : "2014-09-03T14:15:17.346",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RAMOS COLTON CORWIN FITZPATRICK PERRY MILLER CROUCH BUTLER HOOPER MCDONALD WALTER SALADINO BRINDISI HAWLEY ROBINSON SIMANOWITZ WRIGHT RAIA MCLAUGHLIN PALMESANO GIGLIO MALLIOTAKIS TEDISCO AUBRY RA GRAF CURRAN ABBATE BARCLAY ORTIZ CERETTO SCHIMMINGER",
                        "observedData" : "RAMOS COLTON CORWIN FITZPATRICK PERRY MILLER CROUCH BUTLER HOOPER MCDONALD WALTER SALADINO BRINDISI HAWLEY ROBINSON SIMANOWITZ WRIGHT RAIA MCLAUGHLIN PALMESANO GIGLIO MALLIOTAKIS TEDISCO AUBRY RA GRAF CURRAN ABBATE BARCLAY GABRYSZAK ORTIZ CERETTO SCHIMMINGER",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RAMOS COLTON CORWIN FITZPATRICK PERRY MILLER CROUCH BUTLER HOOPER MCDONALD WALTER SALADINO BRINDISI HAWLEY ROBINSON SIMANOWITZ WRIGHT RAIA MCLAUGHLIN PALMESANO GIGLIO MALLIOTAKIS TEDISCO AUBRY RA GRAF CURRAN ABBATE BARCLAY "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ORTIZ CERETTO SCHIMMINGER"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS COLTON CORWIN FITZPATRICK PERRY MILLER CROUCH BUTLER HOOPER MCDONALD WALTER SALADINO BRINDISI HAWLEY ROBINSON SIMANOWITZ WRIGHT RAIA MCLAUGHLIN PALMESANO GIGLIO MALLIOTAKIS TEDISCO AUBRY RA GRAF CURRAN ABBATE BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ CERETTO SCHIMMINGER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS COLTON CORWIN FITZPATRICK PERRY MILLER CROUCH BUTLER HOOPER MCDONALD WALTER SALADINO BRINDISI HAWLEY ROBINSON SIMANOWITZ WRIGHT RAIA MCLAUGHLIN PALMESANO GIGLIO MALLIOTAKIS TEDISCO AUBRY RA GRAF CURRAN ABBATE BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ CERETTO SCHIMMINGER"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RAMOS COLTON CORWIN FITZPATRICK PERRY MILLER CROUCH BUTLER HOOPER MCDONALD WALTER SALADINO BRINDISI HAWLEY ROBINSON SIMANOWITZ WRIGHT RAIA MCLAUGHLIN PALMESANO GIGLIO MALLIOTAKIS TEDISCO AUBRY RA GRAF CURRAN ABBATE BARCLAY "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ORTIZ CERETTO SCHIMMINGER"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7569-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7569",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7569"
                },
                "observedDateTime" : "2014-09-03T14:15:17.049",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "COOK SEPULVEDA RIVERA",
                        "observedData" : "COOK SEPULVEDA GABRYSZAK RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "COOK SEPULVEDA "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "COOK SEPULVEDA "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7589-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7589",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7589"
                },
                "observedDateTime" : "2014-09-03T14:15:18.083",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "ARROYO PERRY MOSLEY SCARBOROUGH ROBINSON GOTTFRIED MOYA PEOPLES-STOKES RIVERA",
                        "observedData" : "ARROYO PERRY MOSLEY SCARBOROUGH GABRYSZAK ROBINSON GOTTFRIED MOYA PEOPLES-STOKES RIVERA",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ARROYO PERRY MOSLEY SCARBOROUGH "
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "ROBINSON GOTTFRIED MOYA PEOPLES-STOKES RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO PERRY MOSLEY SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MOYA PEOPLES-STOKES RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO PERRY MOSLEY SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MOYA PEOPLES-STOKES RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ARROYO PERRY MOSLEY SCARBOROUGH "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "ROBINSON GOTTFRIED MOYA PEOPLES-STOKES RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A7836-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A7836",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A7836"
                },
                "observedDateTime" : "2014-09-03T14:15:24.278",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_MULTISPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "HIKIND CLARK COLTON FITZPATRICK THIELE COOK SWEENEY BRENNAN JAFFEE BENEDETTO PEOPLES-STOKES RIVERA",
                        "observedData" : "CLARK COLTON FITZPATRICK THIELE JAFFEE BENEDETTO RIVERA HIKIND COOK SWEENEY BRENNAN GABRYSZAK PEOPLES-STOKES",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "HIKIND "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "CLARK COLTON FITZPATRICK THIELE "
                        }, {
                            "operation" : "INSERT",
                            "text" : "JAFFEE BENEDETTO RIVERA HIKIND "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "COOK SWEENEY BRENNAN "
                        }, {
                            "operation" : "DELETE",
                            "text" : "JAFFEE BENEDETTO"
                        }, {
                            "operation" : "INSERT",
                            "text" : "GABRYSZAK"
                        }, {
                            "operation" : "EQUAL",
                            "text" : " PEOPLES-STOKES"
                        }, {
                            "operation" : "DELETE",
                            "text" : " RIVERA"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "HIKIND "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK COLTON FITZPATRICK THIELE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "JAFFEE BENEDETTO RIVERA HIKIND "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "COOK SWEENEY BRENNAN "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "JAFFEE BENEDETTO"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " PEOPLES-STOKES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "HIKIND "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK COLTON FITZPATRICK THIELE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "JAFFEE BENEDETTO RIVERA HIKIND "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "COOK SWEENEY BRENNAN "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "JAFFEE BENEDETTO"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " PEOPLES-STOKES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " RIVERA"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "HIKIND "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "CLARK COLTON FITZPATRICK THIELE "
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "JAFFEE BENEDETTO RIVERA HIKIND "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "COOK SWEENEY BRENNAN "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "JAFFEE BENEDETTO"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "GABRYSZAK"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : " PEOPLES-STOKES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " RIVERA"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A8035-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A8035",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A8035"
                },
                "observedDateTime" : "2014-09-03T14:15:27.863",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_LAW_CODE_SUMMARY",
                        "status" : "EXISTING",
                        "referenceData" : "Add S308-y, amd S325, County L Authorizes Monroe county to establish wireless surcharges.",
                        "observedData" : "",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "Add S308-y, amd S325, County L Authorizes Monroe county to establish wireless surcharges."
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "Add S308-y, amd S325, County L Authorizes Monroe county to establish wireless surcharges."
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "Add S308-y, amd S325, County L Authorizes Monroe county to establish wireless surcharges."
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "Add S308-y, amd S325, County L Authorizes Monroe county to establish wireless surcharges."
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A8069-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A8069",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A8069"
                },
                "observedDateTime" : "2014-09-03T14:15:28.095",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_COSPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "CRESPO ZEBROWSKI",
                        "observedData" : "",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "DELETE",
                            "text" : "CRESPO ZEBROWSKI"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "CRESPO ZEBROWSKI"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "CRESPO ZEBROWSKI"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "DELETE",
                                    "text" : "CRESPO ZEBROWSKI"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A8550-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A8550",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A8550"
                },
                "observedDateTime" : "2014-09-03T14:13:30.920",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_FULLTEXT_PAGE_COUNT",
                        "status" : "EXISTING",
                        "referenceData" : "{=749, B=752, D=745, E=755, A=752, C=755}",
                        "observedData" : "{=749, B=752, D=0, E=755, A=752, C=755}",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "{=749, B=752, D="
                        }, {
                            "operation" : "DELETE",
                            "text" : "745"
                        }, {
                            "operation" : "INSERT",
                            "text" : "0"
                        }, {
                            "operation" : "EQUAL",
                            "text" : ", E=755, A=752, C=755}"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{B=752, =749, C=755, D="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "745"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "0"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : ", A=752, E=755}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{C=755, A=752, B=752, D="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "745"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "0"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : ", =749, E=755}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{D="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "745"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "0"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : ", A=752, B=752, C=755, =749, E=755}"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A8553-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A8553",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A8553"
                },
                "observedDateTime" : "2014-09-03T14:15:42.839",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_FULLTEXT_PAGE_COUNT",
                        "status" : "EXISTING",
                        "referenceData" : "{=753, B=756, D=803, E=1022, A=753, C=902}",
                        "observedData" : "{=753, B=756, D=0, E=1022, A=753, C=902}",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "{=753, B=756, D="
                        }, {
                            "operation" : "DELETE",
                            "text" : "803"
                        }, {
                            "operation" : "INSERT",
                            "text" : "0"
                        }, {
                            "operation" : "EQUAL",
                            "text" : ", E=1022, A=753, C=902}"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{B=756, =753, C=902, D="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "803"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "0"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : ", A=753, E=1022}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{D="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "803"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "0"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : ", A=753, B=756, C=902, =753, E=1022}"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "{C=902, A=753, B=756, D="
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "803"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "0"
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : ", =753, E=1022}"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A9504-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A9504",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A9504"
                },
                "observedDateTime" : "2014-09-03T14:14:35.004",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_ACTION",
                        "status" : "EXISTING",
                        "referenceData" : "ASSEMBLY - 05/05/14 REFERRED TO REAL PROPERTY TAXATION\nASSEMBLY - 05/28/14 REPORTED REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/02/14 REPORTED REFERRED TO RULES\nASSEMBLY - 06/09/14 REPORTED\nASSEMBLY - 06/09/14 RULES REPORT CAL.95\nASSEMBLY - 06/09/14 ORDERED TO THIRD READING RULES CAL.95\nASSEMBLY - 06/09/14 HOME RULE REQUEST\nASSEMBLY - 06/09/14 PASSED ASSEMBLY\nASSEMBLY - 06/09/14 DELIVERED TO SENATE\nSENATE - 06/09/14 REFERRED TO LOCAL GOVERNMENT\nSENATE - 06/11/14 SUBSTITUTED FOR S7109\nSENATE - 06/11/14 3RD READING CAL.1268\nSENATE - 06/11/14 HOME RULE REQUEST\nSENATE - 06/11/14 PASSED SENATE\nSENATE - 06/11/14 RETURNED TO ASSEMBLY\nASSEMBLY - 08/25/14 DELIVERED TO GOVERNOR\nASSEMBLY - 08/26/14 SIGNED CHAP.329",
                        "observedData" : "ASSEMBLY - 05/05/14 REFERRED TO REAL PROPERTY TAXATION\nASSEMBLY - 05/28/14 REPORTED REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/02/14 REPORTED REFERRED TO RULES\nASSEMBLY - 06/09/14 REPORTED\nASSEMBLY - 06/09/14 RULES REPORT CAL.95\nASSEMBLY - 06/09/14 ORDERED TO THIRD READING RULES CAL.95\nASSEMBLY - 06/09/14 HOME RULE REQUEST\nASSEMBLY - 06/09/14 PASSED ASSEMBLY\nASSEMBLY - 06/09/14 DELIVERED TO SENATE\nSENATE - 06/09/14 REFERRED TO LOCAL GOVERNMENT\nSENATE - 06/11/14 SUBSTITUTED FOR S7109\nSENATE - 06/11/14 3RD READING CAL.1268\nSENATE - 06/11/14 HOME RULE REQUEST\nSENATE - 06/11/14 PASSED SENATE\nSENATE - 06/11/14 RETURNED TO ASSEMBLY\nASSEMBLY - 08/25/14 DELIVERED TO GOVERNOR",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "ASSEMBLY - 05/05/14 REFERRED TO REAL PROPERTY TAXATION\nASSEMBLY - 05/28/14 REPORTED REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/02/14 REPORTED REFERRED TO RULES\nASSEMBLY - 06/09/14 REPORTED\nASSEMBLY - 06/09/14 RULES REPORT CAL.95\nASSEMBLY - 06/09/14 ORDERED TO THIRD READING RULES CAL.95\nASSEMBLY - 06/09/14 HOME RULE REQUEST\nASSEMBLY - 06/09/14 PASSED ASSEMBLY\nASSEMBLY - 06/09/14 DELIVERED TO SENATE\nSENATE - 06/09/14 REFERRED TO LOCAL GOVERNMENT\nSENATE - 06/11/14 SUBSTITUTED FOR S7109\nSENATE - 06/11/14 3RD READING CAL.1268\nSENATE - 06/11/14 HOME RULE REQUEST\nSENATE - 06/11/14 PASSED SENATE\nSENATE - 06/11/14 RETURNED TO ASSEMBLY\nASSEMBLY - 08/25/14 DELIVERED TO GOVERNOR"
                        }, {
                            "operation" : "DELETE",
                            "text" : "\nASSEMBLY - 08/26/14 SIGNED CHAP.329"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ASSEMBLY - 05/05/14 REFERRED TO REAL PROPERTY TAXATION\nASSEMBLY - 05/28/14 REPORTED REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/02/14 REPORTED REFERRED TO RULES\nASSEMBLY - 06/09/14 REPORTED\nASSEMBLY - 06/09/14 RULES REPORT CAL.95\nASSEMBLY - 06/09/14 ORDERED TO THIRD READING RULES CAL.95\nASSEMBLY - 06/09/14 HOME RULE REQUEST\nASSEMBLY - 06/09/14 PASSED ASSEMBLY\nASSEMBLY - 06/09/14 DELIVERED TO SENATE\nSENATE - 06/09/14 REFERRED TO LOCAL GOVERNMENT\nSENATE - 06/11/14 SUBSTITUTED FOR S7109\nSENATE - 06/11/14 3RD READING CAL.1268\nSENATE - 06/11/14 HOME RULE REQUEST\nSENATE - 06/11/14 PASSED SENATE\nSENATE - 06/11/14 RETURNED TO ASSEMBLY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "\nASSEMBLY - 08/25/14 DELIVERED TO GOVERNOR"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "REGRESSION",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ASSEMBLY - 05/05/14 REFERRED TO REAL PROPERTY TAXATION\nASSEMBLY - 05/28/14 REPORTED REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/02/14 REPORTED REFERRED TO RULES\nASSEMBLY - 06/09/14 REPORTED\nASSEMBLY - 06/09/14 RULES REPORT CAL.95\nASSEMBLY - 06/09/14 ORDERED TO THIRD READING RULES CAL.95\nASSEMBLY - 06/09/14 HOME RULE REQUEST\nASSEMBLY - 06/09/14 PASSED ASSEMBLY\nASSEMBLY - 06/09/14 DELIVERED TO SENATE\nSENATE - 06/09/14 REFERRED TO LOCAL GOVERNMENT\nSENATE - 06/11/14 SUBSTITUTED FOR S7109\nSENATE - 06/11/14 3RD READING CAL.1268\nSENATE - 06/11/14 HOME RULE REQUEST\nSENATE - 06/11/14 PASSED SENATE\nSENATE - 06/11/14 RETURNED TO ASSEMBLY"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "\nASSEMBLY - 08/25/14 DELIVERED TO GOVERNOR\nASSEMBLY - 08/26/14 SIGNED CHAP.329"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "RESOLVED",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "ASSEMBLY - 05/05/14 REFERRED TO REAL PROPERTY TAXATION\nASSEMBLY - 05/28/14 REPORTED REFERRED TO WAYS AND MEANS\nASSEMBLY - 06/02/14 REPORTED REFERRED TO RULES\nASSEMBLY - 06/09/14 REPORTED\nASSEMBLY - 06/09/14 RULES REPORT CAL.95\nASSEMBLY - 06/09/14 ORDERED TO THIRD READING RULES CAL.95\nASSEMBLY - 06/09/14 HOME RULE REQUEST\nASSEMBLY - 06/09/14 PASSED ASSEMBLY\nASSEMBLY - 06/09/14 DELIVERED TO SENATE\nSENATE - 06/09/14 REFERRED TO LOCAL GOVERNMENT\nSENATE - 06/11/14 SUBSTITUTED FOR S7109\nSENATE - 06/11/14 3RD READING CAL.1268\nSENATE - 06/11/14 HOME RULE REQUEST\nSENATE - 06/11/14 PASSED SENATE\nSENATE - 06/11/14 RETURNED TO ASSEMBLY"
                                }, {
                                    "operation" : "INSERT",
                                    "text" : "\nASSEMBLY - 08/25/14 DELIVERED TO GOVERNOR"
                                } ]
                            } ],
                            "size" : 3
                        }
                    }, {
                        "mismatchType" : "BILL_LAW_CODE_SUMMARY",
                        "status" : "EXISTING",
                        "referenceData" : "Provides for a property taxpayer assistance authorization for households in  the town of Henrietta, county of Monroe.",
                        "observedData" : "Provides for a taxpayer assistance authorization for households in the town of Henrietta, county of Monroe.",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "Provides for a "
                        }, {
                            "operation" : "DELETE",
                            "text" : "property "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "taxpayer assistance authorization for households in "
                        }, {
                            "operation" : "DELETE",
                            "text" : " "
                        }, {
                            "operation" : "EQUAL",
                            "text" : "the town of Henrietta, county of Monroe."
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "Provides for a "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : "property "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "taxpayer assistance authorization for households in "
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " "
                                }, {
                                    "operation" : "EQUAL",
                                    "text" : "the town of Henrietta, county of Monroe."
                                } ]
                            } ],
                            "size" : 1
                        }
                    } ],
                    "size" : 2
                }
            },
            "A10144-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A10144",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A10144"
                },
                "observedDateTime" : "2014-09-03T14:13:50.017",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_SPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RULES (O'Donnell)",
                        "observedData" : "RULES",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RULES"
                        }, {
                            "operation" : "DELETE",
                            "text" : " (O'Donnell)"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RULES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " (O'Donnell)"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RULES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " (O'Donnell)"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RULES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " (O'Donnell)"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            },
            "A10145-2013" : {
                "refDateTime" : "2014-08-30T00:00",
                "key" : {
                    "basePrintNo" : "A10145",
                    "session" : {
                        "year" : 2013
                    },
                    "version" : "DEFAULT",
                    "printNo" : "A10145"
                },
                "observedDateTime" : "2014-09-03T14:13:50.703",
                "mismatches" : {
                    "items" : [ {
                        "mismatchType" : "BILL_SPONSOR",
                        "status" : "EXISTING",
                        "referenceData" : "RULES (Benedetto)",
                        "observedData" : "RULES",
                        "notes" : "",
                        "diff" : [ {
                            "operation" : "EQUAL",
                            "text" : "RULES"
                        }, {
                            "operation" : "DELETE",
                            "text" : " (Benedetto)"
                        } ],
                        "prior" : {
                            "items" : [ {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-09-03T00:24:36.376"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RULES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " (Benedetto)"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-30T15:27:21.052"
                                },
                                "status" : "EXISTING",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RULES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " (Benedetto)"
                                } ]
                            }, {
                                "reportId" : {
                                    "referenceType" : "LBDC_DAYBREAK",
                                    "reportDateTime" : "2014-08-28T22:23:40.309"
                                },
                                "status" : "NEW",
                                "diff" : [ {
                                    "operation" : "EQUAL",
                                    "text" : "RULES"
                                }, {
                                    "operation" : "DELETE",
                                    "text" : " (Benedetto)"
                                } ]
                            } ],
                            "size" : 3
                        }
                    } ],
                    "size" : 1
                }
            }
        },
        "totalMismatches" : 138
    }
}