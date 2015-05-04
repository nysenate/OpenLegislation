<%@ page import="gov.nysenate.openleg.model.notification.NotificationType" %>
<%@ page import="gov.nysenate.openleg.util.OutputUtils" %>
<%@ page import="gov.nysenate.openleg.model.notification.NotificationTarget" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccountSettingsCtrl" ng-init="setHeaderVisible(true)">
<md-tabs class="md-primary" md-selected="selectedIndex" ng-init="setHeaderText('Admin Account Settings')">

<!-- Change Password -->

<md-tab label="Change Password">
<md-card ng-controller="PassChangeCtrl">
  <md-toolbar>
    <h5 class="md-toolbar-tools">Change Password</h5>
  </md-toolbar>
  <md-card-content>
    <div >
      <form name="passwordForm" layout layout-sm="column">
        <md-input-container flex>
          <label>New Password</label>
          <input name=newPassInput minlength="5" type="password" ng-model="newPass">
        </md-input-container>
        <div flex>
          <md-button ng-click="submitNewPass()" class="md-primary md-raised flex-center-vertically" aria-label="change password">
            Change Pass
          </md-button>
        </div>
      </form>
    </div>
  </md-card-content>
</md-card>
</md-tab>

<!-- Notification Settings -->

<%
  String notifTypeHierarchy = OutputUtils.toJson(NotificationType.getHierarchy());
  String notifTargets = OutputUtils.toJson(NotificationTarget.values());
%>

<md-tab label="Notification Settings">
<md-card ng-controller="NotificationSubCtrl"
    ng-init='init(<%=notifTypeHierarchy%>, <%=notifTargets%>)'>
  <md-toolbar>
    <h5 class="md-toolbar-tools inline-block">Manage Notification Subscriptions</h5>
  </md-toolbar>
  <md-card-content>
    <md-button class="md-primary" aria-label="Add new subscription" ng-click="toggleNewSubscription()"
               ng-class="{'md-raised': !newSubscriptionShown}">
      New
    </md-button>
    <md-button class="md-primary md-raised" ng-disabled="selectedSubs == 0" ng-click="unsubscribeSelected()"
               aria-label="Remove selected subscriptions">
      Unsubscribe
    </md-button>
    <md-list class="notification-sub-list">
      <md-item>
        <md-item-content layout="row">
          <div>
            <md-checkbox  class="md-primary" ng-model="selectAll" ng-click="applySelectAll()" aria-label=":P"></md-checkbox>
          </div>
          <div><h4>Notification Type</h4></div>
          <div><h4>Target Type</h4></div>
          <div><h4>Target Address</h4></div>
        </md-item-content>
        <md-divider class="md-defualt-theme"></md-divider>
      </md-item>
      <md-item ng-show="newSubscriptionShown">
        <form>
        <md-item-content layout="row">
          <div></div>
          <div>
            <select ng-options="type.trim() as type for type in notificationTypes" ng-model="newSubscription.type"
                    class="margin-right-10"></select>
          </div>
          <div>
            <select ng-options="target for target in notificationTargets" ng-model="newSubscription.target"
                    class="margin-right-10"></select>
          </div>
          <div class="margin-right-10">
            <md-input-container ng-if="['EMAIL', 'EMAIL_SIMPLE'].indexOf(newSubscription.target)>=0">
              <label>Address</label>
              <input type="email" required ng-model="newSubscription.address">
            </md-input-container>
            <md-input-container ng-if="['EMAIL', 'EMAIL_SIMPLE'].indexOf(newSubscription.target)==-1">
              <label>Address</label>
              <input type="text" ng-model="newSubscription.address">
            </md-input-container>
          </div>
          <div>
            <md-button class="md-primary md-raised" ng-click="registerNewSubscription()" aria-label="o_o">Subscribe</md-button>
          </div>
        </md-item-content>
        </form>
        <md-divider md-inset class="md-defualt-theme"></md-divider>
      </md-item>
      <md-item ng-repeat="subscription in subscriptions">
        <md-item-content layout="row">
          <div>
            <md-checkbox ng-click="tallySelectedSubs()" ng-model="subscription.selected" aria-label=":P"
                         class="md-primary""></md-checkbox>
          </div>
          <div><span ng-bind="subscription.type"></span></div>
          <div><span ng-bind="subscription.target"></span></div>
          <div><span ng-bind="subscription.address"></span></div>
        </md-item-content>
        <md-divider class="md-defualt-theme"></md-divider>
      </md-item>
    </md-list>
  </md-card-content>
</md-card>
</md-tab>

</md-tabs>
</section>