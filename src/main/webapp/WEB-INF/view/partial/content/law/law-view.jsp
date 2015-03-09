<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="LawCtrl">
  <section ng-controller="LawViewCtrl">
    <md-tabs md-selected="curr.selectedView" class="md-primary">
      <md-tab md-on-select="backToListings()">
        <i class="prefix-icon2 icon-arrow-left5"></i>Back to Listings
      </md-tab>
      <md-tab label="{{curr.lawId}}">
        <pre ng-show="curr.nodes[depth].docType === 'SECTION'"
             class="margin-left-20 bill-full-text" style="white-space: pre-line;">
          {{curr.lawText}}
        </pre>
        <!-- Recursive ng-repeat template to render the law tree -->
        <script type="text/ng-template" id="law-tree-snippet.html">
          <div>
            <md-item ng-click="toggleLawNode(doc)">
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
                  <h4><span ng-if="doc.title">{{doc.title}}</span>
                      <span class="red1" ng-if="!doc.title">Title not available</span>
                  </h4>
                  <h4 ng-hide="doc.docType === 'SECTION'">Sections (&sect;{{doc.fromSection}} - &sect;{{doc.toSection}})</h4>
                  <p class="green2" ng-show="doc.docType === 'SECTION' && doc.activeDate !== '2014-09-22'">
                    Updated on {{doc.activeDate | moment:'MM/DD/YYYY'}}
                  </p>
                </div>
              </md-item-content>
              <md-divider></md-divider>
            </md-item>
            <md-item ng-if="doc.docType !== 'SECTION' && curr.showNested[doc.locationId]">
              <div class="padding-10 gray2-bg">
                <md-button ng-click="toggleNodeText(doc)" class="md-primary md-hue-2" style="font-size:0.8rem;">
                  <i class="icon-text prefix-icon2"></i>
                  <span ng-show="!curr.showDoc[doc.locationId]">Show</span>
                  <span ng-show="curr.showDoc[doc.locationId]">Hide</span> text for {{doc.docType}} {{doc.docLevelId}}
                </md-button>
              </div>
              <md-divider></md-divider>
              <div class="law-text" ng-if="curr.showDoc[doc.locationId]" ng-bind-html="curr.lawText[doc.locationId]"></div>
            </md-item>
            <md-item ng-if="curr.showNested[doc.locationId]">
              <md-item-content>
                <md-list style="width:100%;" class="no-padding" ng-if="doc.docType !== 'SECTION'">
                  <div ng-repeat="doc in doc.documents.items" class="margin-left-20" ng-include="'law-tree-snippet.html'"></div>
                </md-list>
                <div class="law-text" ng-if="doc.docType === 'SECTION'" ng-bind-html="curr.lawText[doc.locationId]"></div>
              </md-item-content>
            </md-item>
          </div>
        </script>

        <!-- Invoke the law tree template using the first law tree level -->
        <md-card class="no-margin">
          <md-content class="gray10-bg">
            <md-list>
              <div ng-repeat="doc in curr.lawTree" ng-include="'law-tree-snippet.html'"></div>
            </md-list>
          </md-content>
        </md-card>
      </md-tab>
    </md-tabs>
  </section>
</section>