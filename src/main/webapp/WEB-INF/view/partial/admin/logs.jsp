<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="content-section" ng-controller="LogsCtrl">
  <md-tabs md-dynamic-height="true" class="md-hue-2" md-selected="view">
    <md-tab label="API Monitor">
      <md-toolbar>
        <div layout="row" layout-align="space-between center">
          <div>
            <p class="margin-left-16">{{newApiRequestsCount}} API Requests since {{now | moment:'llll'}}</p>
          </div>
          <div>
            <md-button class="md-accent" ng-click="resetRunningLog()">Reset</md-button>
          </div>
        </div>
      </md-toolbar>
      <div ng-if="newApiRequestsCount > 0">
        <div flex class="listing-filter">
          <label class="margin-bottom-10 margin-right-10">
            Filter incoming Api Requests
          </label>
          <input class="padding-5" ng-model-options="{debounce: 200}" ng-model="logFilter" placeholder=""/>
        </div>
        <div flex layout-padding class="api-request-table-container">
          <table class="api-request-table">
            <thead>
            <th>Time</th>
            <th>IP</th>
            <th>User</th>
            <th>Method</th>
            <th>Status</th>
            <th>Latency</th>
            <th>Url</th>
            </thead>
            <tbody>
            <tr class="new-request" ng-repeat="event in newApiRequests | filter:logFilter" ng-init="baseRequest = event.apiResponse.baseRequest">
              <td class="time-td">{{baseRequest.requestTime.monthValue}}/{{baseRequest.requestTime.dayOfMonth}}/{{baseRequest.requestTime.year}}
                {{baseRequest.requestTime.hour}}:{{baseRequest.requestTime.minute}}:{{baseRequest.requestTime.second}}.{{baseRequest.requestTime.nano / 100000}}</td>
              <td>{{baseRequest.ipAddress}}</td>
              <td>{{baseRequest.apiUser.name | default:'None'}}</td>
              <td>{{baseRequest.requestMethod}}</td>
              <td ng-class="{'red1': event.apiResponse.statusCode != 200}">{{event.apiResponse.statusCode}}</td>
              <td ng-class="{'red1': event.apiResponse.processTime > 1000}">{{event.apiResponse.processTime}} ms</td>
              <td class="url-td"><a ng-href="{{baseRequest.url}}" target="_blank">{{baseRequest.url | limitTo:100}}</a></td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </md-tab>
    <md-tab label="API Log Search">
      <div flex layout="column" layout-gt-sm="row" layout-align="start center" class="listing-filter">
        <div flex="none" class="margin-right-20">
          <label class="margin-bottom-10 margin-right-10">
            Search Api Requests
          </label>
          <input class="padding-5" ng-change="searchLogs()" ng-model-options="{debounce: 200}" ng-model="apiLogTerm" placeholder=""/>
        </div>
        <div flex>
          <label>Requests During</label>
          <md-datepicker ng-model="apiLogFromDate" md-max-date="apiLogToDate" ng-change="searchLogs()"></md-datepicker>
          <md-datepicker ng-model="apiLogToDate" md-min-date="apiLogFromDate" ng-change="searchLogs()"></md-datepicker>
        </div>
      </div>
      <div flex layout-padding class="api-request-table-container">
        <div ng-show="lawSearchResp.total == 0">
          <h3 class="red1">No matching requests found.</h3>
        </div>
        <div ng-show="lawSearchResp.total > 0" layout="row" layout-align="space-between center">
          <div>
            <h3>{{lawSearchResp.total}} requests</h3>
          </div>
          <div>
            <dir-pagination-controls
                    class="text-align-center" pagination-id="api-log-search" boundary-links="true"
                    on-page-change="apiLogSearchPageChange(newPageNumber)" max-size="10">
            </dir-pagination-controls>
          </div>
          <div>
            <label class="margin-right-10">Sort By</label>
            <select ng-model="apiLogSort" ng-change="searchLogs()">
              <option value="requestTime:desc">Recent Requests</option>
              <option value="requestTime:asc">Older Requests</option>
              <option value="processTime:desc">Longest Latency</option>
              <option value="processTime:asc">Shortest Latency</option>
              <option value="statusCode:desc">Status Code Desc</option>
              <option value="statusCode:asc">Status Code Asc</option>
            </select>
          </div>
        </div>
        <table class="api-request-table" ng-if="lawSearchResp.total > 0">
          <thead>
          <th>Time</th>
          <th>IP</th>
          <th>User</th>
          <th>Method</th>
          <th>Status</th>
          <th>Latency</th>
          <th>Url</th>
          </thead>
          <tbody>
          <tr dir-paginate="req in lawSearchResults | itemsPerPage: apiLogSearchPagination.itemsPerPage"
              total-items="lawSearchResp.total" current-page="apiLogSearchPagination.currPage"
              pagination-id="api-log-search">
            <td class="time-td">{{req.result.requestTime}}</td>
            <td>{{req.result.ipAddress}}</td>
            <td>{{req.result.apiUserName | default:'None'}}</td>
            <td>{{req.result.requestMethod}}</td>
            <td ng-class="{'red1': req.result.statusCode != 200}">{{req.result.statusCode}}</td>
            <td ng-class="{'red1': req.result.processTime > 1000}">{{req.result.processTime}} ms</td>
            <td class="url-td"><a ng-href="{{req.result.url}}" target="_blank">{{req.result.url | limitTo:100}}</a></td>
          </tr>
          </tbody>
        </table>
      </div>
    </md-tab>
  </md-tabs>
</div>