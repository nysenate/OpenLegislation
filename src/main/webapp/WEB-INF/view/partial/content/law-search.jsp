<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="LawCtrl">
  <section ng-controller="LawListingCtrl">
    <md-tabs md-selected="curr.selectedView">
      <md-tab label="Listings">
        <md-card class="padding-10">
          <md-input-container>
            <label>Filter law listing</label>
            <input ng-model-options="{debounce: 200}" ng-model="lawFilter"/>
          </md-input-container>
        </md-card>
        <md-card>
          <md-content class="text-medium">
            <md-list>
              <md-item ng-repeat="law in lawListing | filter:lawFilter | limitTo:curr.listingLimit">
                <md-item-content class="law-node" ng-click="go('${ctxPath}/laws/' + law.lawId)">
                  <div class="md-tile-left">
                    <strong>{{law.lawId}}</strong>
                  </div>
                  <div class="md-tile-content">
                    <h3>{{law.name}}</h3>
                    <h4>{{law.lawType}} | Chapter {{law.chapter}}</h4>
                  </div>
                </md-item-content>
                <md-divider></md-divider>
              </md-item>
            </md-list>
            <div infinite-scroll="keepScrolling()" infinite-scroll-distance="1"></div>
          </md-content>
        </md-card>
      </md-tab>
        <md-tab label="Search"></md-tab>
    </md-tabs>
  </section>
</section>