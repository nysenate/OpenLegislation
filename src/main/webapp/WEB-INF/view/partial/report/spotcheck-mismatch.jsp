<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getRefJsonMap();
  String refTypeDisplayMap = SpotCheckRefType.getDisplayJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + refTypeDisplayMap + ", " + mismatchMap;
%>

<section ng-controller="SpotcheckCtrl" ng-init='init(<%=daybreakInitArgs%>)' class="content-section">
  <md-card ng-controller="SpotcheckMismatchCtrl" ng-init='init()' class="content-card">
    <md-card-content>
      <div class="margin-bottom-10">
        <form>
          <h2 class="no-margin">
            <span class="icon-clipboard blue-title-icon"></span>
            Open Mismatches from {{reportType | reportTypeLabel}} Reports
          </h2>
        </form>
      </div>
      <md-divider class="margin-bottom-10"></md-divider>
      <div ng-show="lastReceived > 0 && !parameterError && !requestError">
        <mismatch-view mismatches="mismatchRows" summary="summary" filter="filter"
                       loading="{{lastReceived < requestCount}}" no-status-filter></mismatch-view>
      </div>
      <div ng-show="lastReceived === 0 && requestCount > 0">
        <h3>Loading Mismatches</h3>
        <md-progress-linear md-mode="indeterminate" class="md-hue-2"></md-progress-linear>
      </div>
      <div ng-show="parameterError" style="color: red">
        Invalid parameter: {{parameterErrorVal}}
      </div>
      <div ng-show="requestError" style="color: red">
        There was an error while retrieving the requested mismatches
      </div>
    </md-card-content>
  </md-card>
</section>

<jsp:include page="spotcheck-detail-window.jsp"/>
