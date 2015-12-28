<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="content-section" ng-controller="LogsCtrl">
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
        <th>Request Time</th>
        <th>IP Address</th>
        <th>Api Key</th>
        <th>Request Method</th>
        <th>Status Code</th>
        <th>Latency</th>
        <th>Url</th>
        </thead>
        <tbody>
        <tr ng-repeat="event in newApiRequests | filter:logFilter" ng-init="baseRequest = event.apiResponse.baseRequest">
          <td>{{baseRequest.requestTime.monthValue}}/{{baseRequest.requestTime.dayOfMonth}}/{{baseRequest.requestTime.year}}
            {{baseRequest.requestTime.hour}}:{{baseRequest.requestTime.minute}}:{{baseRequest.requestTime.second}}.{{baseRequest.requestTime.nano / 100000}}</td>
          <td>{{baseRequest.ipAddress}}</td>
          <td>{{baseRequest.apiKey | default:'None'}}</td>
          <td>{{baseRequest.requestMethod}}</td>
          <td ng-class="{'red1': event.apiResponse.statusCode != 200}">{{event.apiResponse.statusCode}}</td>
          <td ng-class="{'red1': event.apiResponse.processTime > 1000}">{{event.apiResponse.processTime}} ms</td>
          <td class="url-td"><a ng-href="{{baseRequest.url}}" target="_blank">{{baseRequest.url | limitTo:100}}</a></td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <md-divider></md-divider>
  <div flex class="listing-filter">
    <label class="margin-bottom-10 margin-right-10">
      Search Api Requests
    </label>
    <input class="padding-5" ng-change="searchLogs()" ng-model-options="{debounce: 200}" ng-model="term" placeholder=""/>
  </div>
  <div flex layout-padding class="api-request-table-container">
    <table class="api-request-table" ng-if="lawSearchResp.total > 0">
      <thead>
      <th>Request Time</th>
      <th>IP Address</th>
      <th>Api Key</th>
      <th>Request Method</th>
      <th>Status Code</th>
      <th>Latency</th>
      <th>Url</th>
      </thead>
      <tbody>
      <tr ng-repeat="req in lawSearchResults" >
        <td>{{req.result.requestTime}}</td>
        <td>{{req.result.ipAddress}}</td>
        <td>{{req.result.apiKey | default:'None'}}</td>
        <td>{{req.result.requestMethod}}</td>
        <td ng-class="{'red1': req.result.statusCode != 200}">{{req.result.statusCode}}</td>
        <td ng-class="{'red1': req.result.processTime > 1000}">{{req.result.processTime}} ms</td>
        <td class="url-td"><a ng-href="{{req.result.url}}" target="_blank">{{req.result.url | limitTo:100}}</a></td>
      </tr>
      </tbody>
    </table>
  </div>
</div>