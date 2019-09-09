<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>

<open-layout:head title="New York State Senate - Open Legislation">
  <base href="/">
  <script type="application/javascript" src="${ctxPath}/static/js/src/subscription.js"></script>
</open-layout:head>
<open-layout:body appName="open-public">
  <div class="public-home-page ng-scope" ng-controller="SubscriptionCtrl">
    <section class="hero-container-subscriptions">
      <h1 ng-bind="title"></h1>
    </section>
    <div id="subscription-page-pop-out" class="pop-out-container">
      <div class="project-desc" ng-hide="submitted || invalidKey || ! pageLoaded || processing">
        <p class="unsubscribeInstructions">{{instructions}}</p>
        <form name="subscriptionForm" method="post" class="update-subscription-form">
          <div class="checkbox-container layout-align-center-center">
            <md-list class="subscription-checkbox-list-updater" layout="column" layout-sm="row" layout-wrap>
              <md-list-item ng-repeat="sub in subscriptionsAvailable">
                <div>
                  <md-checkbox class="md-primary" ng-model="sub.checked"
                               aria-label="sub.desc" ng-init="sub.checked">
                    {{sub.desc}}
                  </md-checkbox>
                </div>
              </md-list-item>
            </md-list>
          </div>
          <div class="submit-button" layout="column">
            <md-button ng-click="updateSubscriptions()" class="bold md-primary md-raised">
              Update Subscriptions
            </md-button>
            <p><strong>OR</strong></p>
            <md-button ng-click="uncheckAll()" class="bold md-primary md-raised">
              Unsubscribe From All
            </md-button>
          </div>
          <div class="signup-err" ng-if="errmsg">
            <h4><i class="icon-warning prefix-icon"></i>{{errmsg}}</h4>
          </div>
        </form>
        <hr>
        <div class="signup-err" ng-if="emailErr">
          <h4><i  class="icon-warning prefix-icon"></i>{{emailErr}}</h4>
        </div>
        <form name="updateEmailForm" method="post" ng-class="{'update-email-already-used-error':emailErr, 'update-email-form':true }">
          <div class="email-input-container" layout="column">
            <p>Update the email you want to use for subscriptions:</p>
            <md-input-container>
              <label>you@example.com</label>
              <input name="email" ng-model="emailInput" type="email" ng-change="turnOffSubmitMessage()"/>
            </md-input-container>
            <md-button ng-click="updateEmail()" class="bold md-primary md-raised">
              Update Email
            </md-button>
          </div>
          <p id="email-updated-success-message" ng-show="emailSubmitted">Your email has been updated!</p>
          <p class="valid-email-error-message" ng-show="validEmailMessageOn">Please enter a valid email.</p>
        </form>
      </div>
      <div class="project-desc" ng-show="submitted">
        <h3>{{submitMessage}}</h3>
        <a id="back-link" onclick="open(link)" target="_self">Go back to subscription preferences</a>
      </div>
      <div class="project-desc" ng-show="processing">
        <h3>{{processingMessage}}</h3>
      </div>
      <div class="project-desc" ng-hide="! invalidKey">
        <h3>{{errmsg}}</h3>
      </div>
    </div>
  </div>
</open-layout:body>