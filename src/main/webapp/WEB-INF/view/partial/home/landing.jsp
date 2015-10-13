<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("ctxPath", request.getContextPath()); %>

<section class="content-section" ng-controller="LandingCtrl">
  <section class="slogan-container">
    <h1 class="slogan">Search, explore, and
      <a target="_blank" href="${ctxPath}/docs" class="slogan-link">integrate</a> legislative information from the
      <a class="slogan-link" href="http://www.nysenate.gov">New&nbsp;York State Senate</a>
    </h1>
    <md-divider/>
    <md-list layout-gt-sm="row" layout-wrap>
      <md-list-item flex="50" class="provide-data-container md-3-line" ng-repeat="provideData in dataWeProvide" ng-click="go(provideData.url)">
        <div class="provide-data-icon" ng-class="provideData.bgclass">
          <i ng-class="provideData.icon" class="white"></i>
        </div>
        <div class="margin-left-20 md-list-item-text">
          <h3 class="no-margin bold provide-data-type">{{provideData.type}}</h3>
          <p style="color:#444">{{provideData.blurb}}</p>
        </div>
      </md-list-item>
    </md-list>
    <md-divider></md-divider>
    <h4 class="thin-300">*The canonical data provider is the Legislative Bill Drafting Commission. Raw data feeds are processed by
    OpenLegislation continuously to collate and re-distribute the data using a REST API to various end-points including
    the <a href="http://www.nysenate.gov" class="slogan-link">nysenate.gov site.</a> </h4>
  </section>
  <section class="padding-20 margin-20 white-bg">
    <h2 class="thin-300"><i class="icon-key prefix-icon2"></i>Access NYS legislative data through a JSON API</h2>
    <hr/>
    <section ng-hide="signedup">
        <p>Sign up for a free API Key</p>
        <hr/>
        <form method="post">
            <div layout="row" layout-sm="column" layout-align="center center">
                <md-input-container class="margin-right-20">
                    <label ng-required ng-trim>Name</label>
                    <input type="text" name="name" ng-model="name"/>
                </md-input-container>
                <md-input-container class="margin-right-20">
                    <label>Email</label>
                    <input ng-required ng-trim type="email" name="email" ng-model="email"/>
                </md-input-container>
                <md-button ng-click="signup()" style="width: 160px;" class="bold md-primary md-raised">Signup</md-button>
            </div>
            <div class="signup-err" layout="row" ng-if="errmsg">
                <h4>{{errmsg}}</h4>
            </div>
        </form>
    </section>
    <section ng-show="signedup">
        <h3>Thanks for signing up, please check your email to receive your API key.</h3>
    </section>
    <section ng-show="processing">
        <h3>Your API key is being created, one sec.</h3>
    </section>
    <hr/>
    <h3>
      <a class="blue3" target="_blank" href="${ctxPath}/docs">View the JSON API Documentation</a>
    </h3>
  </section>
  <section class="padding-20 margin-20 white-bg">
    <h2 class="thin-300"><i class="icon-shareable prefix-icon2"></i>Built on Open Source</h2>
    <h3 class="thin-300">OpenLegislation is developed using several open-source packages and frameworks.
     Source code is <a class="slogan-link" href="http://www.github.com/nysenate/OpenLegislation">published on GitHub</a>.
     Feel free to open any tickets with issues you are having or contact the development team at senatedev@nysenate.gov.
    </h3>
  </section>
  <section class="padding-20 margin-20">
      <img src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png"/>
      <p class="text-medium">This content is licensed under Creative Commons BY-NC-ND 3.0.
          The software and services provided under this site are offered under the BSD License and the GPL v3 License.</p>
  </section>
</section>