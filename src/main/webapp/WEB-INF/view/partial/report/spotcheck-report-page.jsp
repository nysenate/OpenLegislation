<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType" %>
<%@ page import="gov.nysenate.openleg.model.spotcheck.SpotCheckRefType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<%
  String refTypeMap = SpotCheckRefType.getRefJsonMap();
  String refTypeDisplayMap = SpotCheckRefType.getDisplayJsonMap();
  String mismatchMap = SpotCheckMismatchType.getJsonMap();
  String daybreakInitArgs = refTypeMap + ", " + refTypeDisplayMap + ", " + mismatchMap;
%>

<section ng-controller="SpotcheckReportCtrl"
         ng-init='init(<%=daybreakInitArgs%>)'
         class="padding-20">
  <div>
    <div>
      <h2>Report Date: {{date}}</h2>
    </div>
    <div layout="row" layout-align="space-between center">
      <div>
        <select ng-model="datasource">
          <option value="OPENLEG">LBDC - OpenLegislation</option>
          <option value="NYSENATE_DOT_GOV">OpenLegislation - NYSenate.gov</option>
        </select>
      </div>

      <div>
        <select ng-model="status" ng-change="onStatusChange()">
          <option value="OPEN">Open Issues ({{mismatchSummary.openCount}})</option>
          <option value="NEW">New Issues ({{mismatchSummary.newCount}})</option>
          <option value="RESOLVED">Resolved Issues</option>
        </select>
      </div>
    </div>
  </div>

  <div>
    <md-content>
      <md-card class="content-card">
        <md-tabs md-dynamic-height md-border-bottom>
          <md-tab label="Bills">
            <table style="width: 100%">
              <thead>
              <th>Status</th>
              <th>Bill</th>
              <th>Type</th>
              <th>Date</th>
              <th>Issue</th>
              <th>Source</th>
              <th style="">Diff</th>
              <th>Ignore</th>
              </thead>
            </table>
            <%--<md-content>--%>
                <%--<md-list layout="row">--%>
                  <%--<md-list-item ng-repeat="category in billCategories" flex>{{category}}</md-list-item>--%>

                  <%--<md-button flex></md-button>--%>
                  <%--<md-button flex></md-button>--%>
                <%--</md-list>--%>

                <%--<md-divider></md-divider>--%>

                <%--<md-list layout="row">--%>
                  <%--<md-list-item ng-repeat="mismatch in mismatches" flex>--%>
                    <%--{{mismatch.status}} {{mismatch.bill}}--%>
                  <%--</md-list-item>--%>

                  <%--<md-button class="md-raised" flex>Diff</md-button>--%>
                  <%--<md-button class="md-accent md-raised" flex>Ignore</md-button>--%>
                <%--</md-list>--%>
            <%--</md-content>--%>
          </md-tab>

          <md-tab label="Calendars">
            <md-content class="md-padding">
              <md-list>
                <md-list layout="row">
                  <md-list-item ng-repeat="data in calendarCategories" flex>{{data}}</md-list-item>
                </md-list>
              </md-list>
            </md-content>
          </md-tab>

          <md-tab label="Agendas">
            <md-content class="md-padding">
              <md-list>
                <md-list layout="row">
                  <md-list-item ng-repeat="data in agendaCategories" flex>{{data}}</md-list-item>
                </md-list>
              </md-list>
            </md-content>
          </md-tab>
        </md-tabs>
      </md-card>
    </md-content>
  </div>
</section>
