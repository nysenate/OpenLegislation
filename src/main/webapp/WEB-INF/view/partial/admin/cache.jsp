<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="CacheCtrl" ng-init="init();">
  <div layout="row" layout-wrap>
    <md-card ng-repeat="cache in caches" flex="30">
      <md-toolbar>
        <h4 class="margin-left-16 text-medium">{{cache.cacheName}}</h4>
      </md-toolbar>
      <md-progress-linear class="md-accent md-hue-1" md-mode="{{(loading[cache.cacheName]) ? 'query' : ''}}"></md-progress-linear>
      <div class="padding-20">
        <p class="no-margin">Memory Used: {{cache.heapSizeMb}} MB</p>
        <p class="no-margin">Entries: {{cache.size}}</p>
        <p class="no-margin">Hits: {{cache.hitCount}}</p>
      </div>
      <div layout="row" layout-align="center center" class="gray3-bg">
        <md-button class="md-raised md-hue-3" ng-click="evictCache(cache.cacheName)">
          <span class="red1">Clear</span>
        </md-button>
        <md-button class="md-raised md-hue-2" ng-click="warmCache(cache.cacheName)">
          <span class="blue3">Warm</span>
        </md-button>
      </div>
    </md-card>
  </div>
</div>