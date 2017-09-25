
<%-- Intended for use within SpotcheckReportCtrl --%>
<div class="spotcheck-report-control-bar spotcheck-report-inner-controls">
  <select ng-model="mismatchStatusSummary.selected" ng-change="onStatusChange()">
    <option value="NEW" ng-disabled="mismatchStatusSummary.summary.items.NEW == 0">
      New Issues ({{mismatchStatusSummary.summary.items.NEW}})
    </option>
    <option value="OPEN" ng-disabled="mismatchStatusSummary.summary.items.OPEN == 0">
      Open Issues ({{mismatchStatusSummary.summary.items.OPEN}})
    </option>
    <option value="RESOLVED" ng-disabled="mismatchStatusSummary.summary.items.RESOLVED == 0">
      Resolved Issues ({{mismatchStatusSummary.summary.items.RESOLVED}})
    </option>
  </select>

  <select  ng-model="mismatchTypeSummary.selected"
           ng-change="onMismatchTypeChange()"
           ng-options="type as mismatchTypeLabel(type, count) disable when count == 0 for (type, count) in mismatchTypeSummary.summary">
  </select>
</div>
