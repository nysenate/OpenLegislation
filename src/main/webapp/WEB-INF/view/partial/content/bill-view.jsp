<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
  <section ng-controller="BillViewCtrl">
    <md-toolbar class="md-toolbar-tools">
      <h6>{{bill.title}}</h6>
    </md-toolbar>
    <md-tabs md-selected="selectedView">
      <md-tab label="Info">
        <md-toolbar ng-if="bill.amendments.size > 1" class="md-toolbar-tools auto-height md-hue-2">
          <md-radio-group layout="row" layout-sm="column" ng-model="amdVersion">
            <md-radio-button ng-repeat="amd in bill.amendments.items" class="md-accent md-hue-1"
                             ng-value="amd.version" ng-aria-label="amd.version">
              <span ng-if="$first">Initial</span>
              <span ng-if="!$first">Revision {{amd.version}}</span>
            </md-radio-button>
          </md-radio-group>
        </md-toolbar>

        <md-card class="md-whiteframe-z0 white-bg padding-10">
          <md-subheader>Status</md-subheader>
          <md-content style="margin-left:16px;">
            <md-progress-linear class="md-accent" md-mode="determinate" ng-value="(bill.milestones.size / 8) * 100"></md-progress-linear>
            <p class="text-medium">{{getStatusDesc(bill.status)}} - {{bill.status.statusDate}}</p>
          </md-content>
        </md-card>

        <md-card class="md-whiteframe-z0 white-bg padding-10">
          <md-subheader>Enacting Clause</md-subheader>
          <md-content style="margin-left:16px;">
            <p class="text-medium">{{bill.amendments.items[amdVersion].actClause}}</p>
          </md-content>
        </md-card>

        <md-card class="md-whiteframe-z0 white-bg padding-10">
          <md-subheader>Summary of Bill</md-subheader>
          <md-content style="margin-left:16px;">
            <p class="text-medium">{{bill.summary}}</p>
          </md-content>
        </md-card>

        <md-card layout="row" layout-sm="column" layout-align="start start" class="md-whiteframe-z0 white-bg padding-10">
          <section flex class="margin-right-20">
            <md-subheader>Sponsor(s)</md-subheader>
            <md-divider/>
            <md-content flex layout-align="center center" class="padding-10" style="margin-left:16px;">
              <md-list>
                <md-item>
                  <md-item-content>
                    <div class="md-tile-left">
                      <img class="margin-right-10" src="${ctxPath}/static/img/business_assets/stakeholders/2.jpg"
                           style="border-radius:100%;height: 100px;width:100px;"/>
                    </div>
                    <div class="md-tile-content">
                      <span class="text-medium bold">{{bill.sponsor.member.fullName}}</span><br/>
                      <label class="text-medium bold gray10">Senate District {{bill.sponsor.member.districtCode}}</label>
                    </div>
                  </md-item-content>
                </md-item>
              </md-list>
            </md-content>
          </section>
          <section flex ng-if="bill.amendments.items[amdVersion].coSponsors.size > 0">
            <md-subheader>{{bill.amendments.items[amdVersion].coSponsors.size}} Co Sponsor(s)</md-subheader>
            <md-divider/>
            <md-content style="margin-left:16px;max-height: 200px;" class="padding-10">
              <md-list>
                <md-item ng-repeat="coSponsor in bill.amendments.items[amdVersion].coSponsors.items">
                  <md-item-content>
                    <div class="md-tile-left">
                      <img class="margin-right-10" ng-src="http://lorempixel.com/50/50/food/{{$index}}"
                           style="height: 50px;width:50px;"/>
                    </div>
                    <div class="md-tile-content">
                      <span class="text-medium">{{coSponsor.fullName}} (D)</span>
                    </div>
                  </md-item-content>
                </md-item>
              </md-list>
            </md-content>
          </section>
        </md-card>
      </md-tab>
      <md-tab label="Bill Text">
        <md-content class="margin-10 padding-20">
          <pre style="font-size:0.9rem;white-space: pre;font-family: monospace, monospace">
             {{bill.amendments.items[amdVersion].fullText}}
          </pre>
        </md-content>
      </md-tab>
      <md-tab label="Update History">

      </md-tab>
  </section>
</section>