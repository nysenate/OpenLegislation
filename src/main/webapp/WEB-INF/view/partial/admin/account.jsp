<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="gov.nysenate.openleg.model.notification.NotificationType" %>
<%@ page import="gov.nysenate.openleg.util.OutputUtils" %>
<%@ page import="gov.nysenate.openleg.model.notification.NotificationTarget" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AccountSettingsCtrl" ng-init="setHeaderVisible(true)" class="content-section">

  <shiro:hasPermission name="admin:account:modify">
    <span ng-init="isMaster = true"></span>
  </shiro:hasPermission>

  <md-tabs md-selected="selectedIndex" md-dynamic-height="true" class="md-hue-2"
           ng-init="setHeaderText('Admin Account Settings')">

    <!-- Change Password -->

    <md-tab label="Change Password">
      <md-card ng-controller="PassChangeCtrl">
        <md-toolbar>
          <h5 class="md-toolbar-tools">Change Password</h5>
        </md-toolbar>
        <md-card-content>
          <div >
            <form name="passwordForm" layout="column">
              <div>
                <md-input-container>
                  <label>New Password</label>
                  <input name=newPassInput minlength="5" type="password" ng-model="newPass">
                </md-input-container>
                <md-input-container md-is-error="newPassRepeat && newPassRepeat !== newPass">
                  <label>Repeat Password</label>
                  <input name=newPassInput type="password" ng-model="newPassRepeat">
                </md-input-container>
              </div>
              <div>
                <md-button ng-click="submitNewPass()"
                           class="md-primary md-raised flex-center-vertically" aria-label="change password">
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
          <md-button class="md-primary" aria-label="Add new subscription" ng-click="newSubscriptionShown = !newSubscriptionShown"
                     ng-class="{'md-raised': !newSubscriptionShown}">
            New
          </md-button>
          <md-button class="md-primary md-raised" ng-disabled="selectedSubs == 0" ng-click="unsubscribeSelected()"
                     aria-label="Remove selected subscriptions">
            Unsubscribe
          </md-button>
          <md-list>
            <md-list-item layout="row">
                <div class="notsub-check-column">
                  <md-checkbox  class="md-primary" ng-model="selectAll"
                                ng-click="applySelectAll()" aria-label=":P"></md-checkbox>
                </div>
                <div class="notsub-type-column"><h4>Notification Type</h4></div>
                <div class="notsub-target-column"><h4>Target Type</h4></div>
                <div><h4>Target Address</h4></div>
              <md-divider class="md-defualt-theme"></md-divider>
            </md-list-item>
            <md-list-item ng-show="newSubscriptionShown">
              <form layout="row" class="notification-list-row">
                <div class="notsub-check-column"></div>
                <div class="notsub-type-column" layout="row" layout-align="start center">
                  <select ng-options="type.trim() as type for type in notificationTypes" ng-model="newSubscription.type"
                          class="margin-right-10"></select>
                </div>
                <div class="notsub-target-column" layout="row" layout-align="start center">
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
                  <md-button class="md-primary md-raised" ng-click="registerNewSubscription()" aria-label="o_o">
                    Subscribe
                  </md-button>
                </div>
              </form>
              <md-divider md-inset class="md-defualt-theme"></md-divider>
            </md-list-item>
            <md-list-item ng-repeat-start="subscription in subscriptions" layout="row" class="notification-list-row">
              <div class="notsub-check-column">
                <md-checkbox ng-click="tallySelectedSubs()" ng-model="subscription.selected" aria-label=":P"
                             class="md-primary""></md-checkbox>
              </div>
              <div class="notsub-type-column"><span ng-bind="subscription.type"></span></div>
              <div class="notsub-target-column"><span ng-bind="subscription.target"></span></div>
              <div><span ng-bind="subscription.address"></span></div>
            </md-list-item>
            <md-divider ng-repeat-end ng-if="!$last" class="md-default-theme"></md-divider>
          </md-list>
        </md-card-content>
      </md-card>
    </md-tab>

    <md-tab label="manage admin users" ng-disabled="!isMaster">
      <md-card ng-controller="ManageAdminUsersCtrl" ng-init="init()">
        <md-card-content id="admin-management">
          <md-toolbar>
            <h5 class="md-toolbar-tools inline-block">Manage Admin Users</h5>
          </md-toolbar>
          <md-button ng-click="newAccountShown = !newAccountShown" ng-class="{'md-raised': !newAccountShown}"
              class="md-primary" aria-label="create new account" style="margin-top: 22px; margin-bottom: 22px">
            New Account
          </md-button>
          <form layout-gt-sm="row" ng-show="newAccountShown">
            <md-input-container class="margin-left-20" flex>
              <label>Email</label>
              <input type="email" ng-model="newAccount.username" aria-label="username">
            </md-input-container>
            <md-checkbox ng-model="newAccount.master" flex>Master admin</md-checkbox>
            <span flex>
              <md-button ng-click="createAccount()" class="md-primary md-raised" aria-label="create account">Create</md-button>
            </span>
          </form>
          <md-divider></md-divider>
          <div ng-show="loadingAccounts || creatingAccount || removingAccount">
            <h4>Loading accounts...</h4>
            <md-progress-linear md-mode="indeterminate" class="md-hue-2"></md-progress-linear>
          </div>
          <md-list ng-if="!(loadingAccounts || creatingAccount || removingAccount)">
            <md-list-item ng-repeat="account in accounts" ng-class="{'master-admin': account.master}">
              <md-button ng-click="deletePrompt(account.username)" class="md-raised" aria-label="delete account">
                <span class="icon-trash"></span>
              </md-button>
              <p ng-bind="account.username"></p>
              <p ng-show="account.master"><span class="icon-shield"></span>Master Admin</p>
              <md-divider ng-if="!$last"></md-divider>
            </md-list-item>
          </md-list>
        </md-card-content>
      </md-card>
    </md-tab>

  </md-tabs>
</section>