<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="CacheCtrl" ng-init="init();">
  <div layout="row" layout-wrap>
    <md-card ng-repeat="cache in caches" flex="30">
      <md-toolbar>
        <h4 class="margin-left-16 text-medium">{{cache.cacheName}}</h4>
      </md-toolbar>
      <md-progress-linear class="md-accent md-hue-1" md-mode="{{(loading[cache.cacheName]) ? 'query' : ''}}"></md-progress-linear>
      <md-list>
        <md-list-item>
          Memory Used: {{cache.heapSizeMb}} MB
        </md-list-item>
        <md-list-item>
          Entries: {{cache.size}}
        </md-list-item>
        <md-list-item>
          Hits: {{cache.hitCount}}
        </md-list-item>
        <md-list-item>
          <div layout="row">
            <md-button class="md-raised md-hue-3" ng-click="evictCache(cache.cacheName)">
              <span class="red1">Clear</span>
            </md-button>
            <md-button class="md-raised md-hue-2" ng-click="warmCache(cache.cacheName)">
              <span class="blue3">Warm</span>
            </md-button>
          </div>
        </md-list-item>
      </md-list>
    </md-card>
  </div>
</div>