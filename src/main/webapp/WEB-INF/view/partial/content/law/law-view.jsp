<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="LawCtrl">
  <section class="content-section" ng-controller="LawViewCtrl">
    <md-tabs md-selected="selectedView" class="md-primary">
      <md-tab md-on-select="backToListings()">
        <i class="icon-list prefix-icon2"></i>Listings
      </md-tab>
      <md-tab label="{{curr.lawId}} Directory">
        <pre ng-show="curr.nodes[depth].docType === 'SECTION'"
             class="margin-left-20 bill-full-text" style="white-space: pre-line;">
          {{curr.lawText}}
        </pre>
        <section class="margin-top-10 gray1-bg" layout="row" layout-padding>
          <div flex>
            <label class="gray10 text-medium">Navigate by section number</label>
            <md-autocomplete class="margin-top-10 margin-bottom-10 white-bg"
                             md-selected-item="selectedItem" md-search-text="filterText"
                             md-items="item in getFilterResults(filterText)" md-no-cache="true"
                             placeholder="e.g 32.01" md-autoselect
                             md-selected-item-change="navigateToLawDoc(item.result)" md-delay="50">
              <span><strong>{{item.result.docType}} {{item.result.docLevelId}}</strong> {{item.result.title}}</span>
            </md-autocomplete>
          </div>
        </section>
        <md-divider></md-divider>

        <div style="position:fixed;top:300px;right: 100px;z-index: 100" class="padding-20" ng-show="loading">
          <md-progress-circular md-mode="indeterminate"></md-progress-circular>
        </div>

        <md-button ng-show="!loading" ng-click="collapseNodesBelow(curr.lawRoot.documents);">
          <span class="text-small">
            <i class="icon-chevron-up prefix-icon2"></i>Collapse open documents
          </span>
        </md-button>

        <!-- Recursive ng-repeat template to render the law tree -->
        <script type="text/ng-template" id="law-tree-snippet.html">
          <div>
            <md-item id="{{doc.locationId}}" ng-click="toggleLawNode(doc)">
              <md-item-content class="law-node" ng-class="{active: curr.showNested[doc.locationId]}">
                <div class="md-tile-left law-node-toggle-icon" style="min-width:30px;margin-right:0;">
                  <i ng-show="!curr.showNested[doc.locationId]" class="hide icon-arrow-down5"></i>
                  <i ng-show="curr.showNested[doc.locationId]" class="hide icon-arrow-up4"></i>
                </div>
                <div class="md-tile-left" style="width:130px;">
                  <h4><span ng-switch="doc.docType">
                          <span ng-switch-when="SECTION">&sect;</span>
                          <span ng-switch-default>{{doc.docType}}</span>
                       </span> {{doc.docLevelId}}
                  </h4>
                </div>
                <div class="md-tile-content">
                  <h4 class="bold" ng-hide="doc.docType === 'SECTION'">Sections (&sect;{{doc.fromSection}} - &sect;{{doc.toSection}})</h4>
                  <h4><span ng-if="doc.title">{{doc.title}}</span>
                    <span class="red1" ng-if="!doc.title">Title not available</span>
                  </h4>
                  <p class="green2" ng-show="doc.docType === 'SECTION' && doc.activeDate !== '2014-09-22'">
                    Updated on {{doc.activeDate | moment:'MM/DD/YYYY'}}
                  </p>
                  <small>Location Id: {{doc.locationId}}</small>
                </div>
              </md-item-content>
              <md-divider></md-divider>
            </md-item>
            <md-item ng-if="curr.showNested[doc.locationId]">
              <div class="padding-10 gray2-bg">
                <md-button ng-if="doc.docType !== 'SECTION'" ng-click="toggleNodeText(doc)" class="md-primary md-hue-2" style="font-size:0.8rem;">
                  <i class="icon-text prefix-icon2"></i>
                  <span ng-show="!curr.showDoc[doc.locationId]">Show</span>
                  <span ng-show="curr.showDoc[doc.locationId]">Hide</span> text for {{doc.docType}} {{doc.docLevelId}}
                </md-button>
                <md-button ng-if="doc.documents.size > 0" class="md-primary md-hue-2" ng-click="expandNodesBelow(doc);">
                  <span class="text-small">
                    <i class="icon-chevron-down prefix-icon2"></i>Expand
                  </span>
                </md-button>
                <md-button class="md-primary md-hue-2" ng-click="collapseNodesBelow(doc);">
                  <span class="text-small">
                    <i class="icon-chevron-up prefix-icon2"></i>Collapse
                  </span>
                </md-button>
                <md-button class="md-primary md-hue-2" ng-click="setLink(doc.locationId)">
                  <span class="text-small">
                    <i class="icon-link prefix-icon2"></i>PermaLink
                  </span>
                </md-button>
              </div>
              <md-divider></md-divider>
              <div class="law-text" ng-if="curr.showDoc[doc.locationId]" ng-bind-html="curr.lawText[doc.locationId]"></div>
            </md-item>
            <md-item ng-if="curr.showNested[doc.locationId]">
              <md-item-content style="background: #f5f5f5">
                <md-list style="width:100%;margin-left:20px;" class="no-padding" ng-if="doc.docType !== 'SECTION'">
                  <div ng-repeat="doc in doc.documents.items" ng-include="'law-tree-snippet.html'"></div>
                </md-list>
                <div class="law-text" ng-if="doc.docType === 'SECTION'" ng-bind-html="curr.lawText[doc.locationId]"></div>
              </md-item-content>
            </md-item>
          </div>
        </script>

        <!-- Invoke the law tree template using the first law tree level -->
        <md-card class="law-container">
          <md-content>
            <md-list>
              <div ng-repeat="doc in curr.lawTree | limitTo:listingLimit" ng-include="'law-tree-snippet.html'"></div>
            </md-list>
            <div infinite-scroll="keepScrolling()" infinite-scroll-distance="1"></div>
          </md-content>
        </md-card>
      </md-tab>
      <md-tab label="{{curr.lawId}} Updates" md-on-select="getUpdates()">
        <md-card class="content-card">
          <md-content layout="row" layout-sm="column">
            <div flex>
              <label>Sort By: </label>
              <select ng-model="curr.updateOrder" ng-change="getUpdates()" class="margin-left-10">
                <option value="desc">Newest First</option>
                <option value="asc">Oldest First</option>
              </select>
            </div>
          </md-content>
        </md-card>
        <update-list update-response="updatesResponse" pagination="updatesPagination" show-details="true"></update-list>
      </md-tab>
    </md-tabs>
  </section>
</section>