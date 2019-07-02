<%--
  Created by IntelliJ IDEA.
  User: angelina
  Date: 6/25/19
  Time: 11:42 AM
  To change this template use File | Settings | File Templates.
--%>

<div class="batch-email-content-section" ng-controller="EmailCtrl">
  <div class="batch-email-header">
    <h1>{{title}}</h1>
  </div>
  <div class="batch-email-main-box" layout="column">
    <div>
      <h2 id="batch-email-send-error">{{error}}</h2>
    </div>
    <div class="batch-email-subscriptions layout-align-left" layout="row" ng-show="!sending && !sent">
      <h4 ng-class="{'batch-email-error-header':invalidSubs}">Groups:</h4>
      <div class="checkbox-container layout-align-center">
        <md-list class="batch-email-subs-checkbox-list-updater" layout-align="left"
                 layout="column" layout-sm="row" layout-wrap>
          <md-list-item ng-repeat="sub in subscriptionsAvailable">
            <div>
              <md-checkbox class="blue" ng-model="sub.checked" aria-label="sub.desc">
                {{sub.desc}}
              </md-checkbox>
            </div>
          </md-list-item>
        </md-list>
      </div>
    </div>
    <div class="batch-email-subject-line" layout="row" ng-show="!sending && !sent">
      <h4 ng-class="{'batch-email-error-header':invalidSubject}">Subject:</h4>
      <textarea id="batch-email-subject-input" ng-model="subject"></textarea>
    </div>
    <div class="batch-email-message-body" layout="row" ng-show="!sending && !sent">
      <h4 ng-class="{'batch-email-error-header':invalidBody}">Body:</h4>
      <textarea id="batch-email-body-text" ng-model="body"></textarea>
      <div class="batch-email-validation-message" ng-show="displayInvalidMessage">
        <p><pre>{{invalidMessage}}</pre></p>
      </div>
    </div>
    <div class="batch-email-submit-box" layout="row" layout-align="center" ng-show="!sending && !sent">
      <md-button class="bold md-raised" ng-click="enterPreview()">
        PREVIEW
      </md-button>
      <md-button class="primary bold md-raised" ng-click="submit()">
        SEND
      </md-button>
    </div>
    <div ng-show="sending">
      <h3>Message is sending...</h3>
    </div>
    <div ng-show="sent">
      <h2>Message sent successfully!</h2>
      <h3>To send another message, <a href="" onclick="location.reload()">click here.</a></h3>
    </div>
    <div id="batch-email-preview-div" ng-show="previewOn">
      <h2>Preview of message:</h2>
      <div id="batch-email-preview">
        <text ng-bind-html="bodyHtml"></text>
      </div>
      <md-button class="bold md-raised" ng-click="exitPreview()">
        Exit Preview
      </md-button>
    </div>
  </div>
</div>

