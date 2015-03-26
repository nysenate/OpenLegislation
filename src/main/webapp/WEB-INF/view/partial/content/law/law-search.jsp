<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section ng-controller="LawCtrl">
  <section class="content-section" ng-controller="LawListingCtrl">
    <md-tabs md-selected="curr.selectedView" class="md-primary">
      <md-tab>
        <md-tab-label>
          <i class="icon-list prefix-icon2"></i>Listings
        </md-tab-label>
        <md-card class="content-card">
          <md-content>
            <md-input-container>
              <label>Filter law listing</label>
              <input ng-model-options="{debounce: 200}" ng-model="lawFilter"/>
            </md-input-container>
          </md-content>
        </md-card>
        <md-card class="no-margin">
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
      <md-tab>
        <md-tab-label><i class="icon-search prefix-icon2"></i>Search</md-tab-label>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
      </md-tab>
    </md-tabs>
  </section>
</section>