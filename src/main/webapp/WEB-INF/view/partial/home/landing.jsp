<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("ctxPath", request.getContextPath()); %>

<section ng-controller="LandingCtrl">
  <section layout="row" layout-align="center center" style="rgb(243, 243, 243);">
      <div hide show-gt-lg style="width:300px;" class="padding-10">
        <img src="${ctxPath}/static/img/capitol_square.jpg" style="width:100%"/>
      </div>
    <h1 flex class="slogan no-margin">Search, explore, and
        <a target="_blank" href="${ctxPath}/docs" class="slogan-link">integrate</a> legislative information from the
        <a class="slogan-link" href="http://www.nysenate.gov">New&nbsp;York&nbsp;State Senate</a>.
    </h1>
  </section>
  <section class="padding-20" style="background:rgb(238, 238, 238);">
    <h2 class="thin-300">Data we currently provide</h2>
      <md-divider/>
    <md-list layout-gt-sm="row" layout-wrap>
      <md-item flex="50" class="provide-data-container" ng-repeat="provideData in dataWeProvide">
        <md-item-content ng-click="go(provideData.url)">
          <div class="md-tile-left">
            <div class="provide-data-icon">
              <i ng-class="provideData.icon"></i>
            </div>
          </div>
          <div class="md-tile-content margin-left-20">
            <h3 class="no-margin bold">{{provideData.type}}</h3>
            <h4>{{provideData.blurb}}</h4>
          </div>
        </md-item-content>
      </md-item>
    </md-list>
    <md-divider></md-divider>
    <h4 class="thin-300">The canonical data provider is the Legislative Bill Drafting Commission. Raw data feeds are processed by
    OpenLegislation continuously to collate and re-distribute the data using a REST API to various end-points including
    the <a href="http://www.nysenate.gov" class="slogan-link">nysenate.gov site.</a> </h4>
  </section>
  <section class="padding-20 white" style="background:rgb(116, 156, 77);">
    <h2>Integrate NYS legislative data with your App</h2>
    <h3>Sign up for a free API Key</h3>
    <hr/>
     <form action="${ctxPath}/register/signup" method="post">
    <div layout="row" layout-sm="column" layout-align="center center">
        <md-input-container class="margin-right-20">
            <label style="color:white;">Name</label>
            <input type="text" name="name" style="color:white;border-color:white;"/>
        </md-input-container>
        <md-input-container class="margin-right-20">
            <label style="color:white">Email</label>
            <input type="email" name="email" style="color:white;border-color:white;"/>
        </md-input-container>
        <md-button style="width: 160px;color:white;" class="bold md-accent md-raised md-hue-3">Signup</md-button>
    </div>
    </form>
    <hr/>
    <h3>
      <a class="white" target="_blank" href="${ctxPath}/docs">View the JSON API Documentation</a>
    </h3>
  </section>
  <section class="padding-20 " style="background:rgb(238, 238, 238);">
    <h2 class="thin-300">Built on Open Source</h2>
    <h3 class="thin-300">OpenLegislation is developed using several open-source packages and frameworks.
     Source code is <a class="slogan-link" href="http://www.github.com/nysenate/OpenLegislation">published on GitHub</a>.
     Feel free to open any tickets with issues you are having or contact the development team at senatedev@nysenate.gov.
    </h3>
  </section>
  <section class="padding-20 white" style="background:#333;">
      <img src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png"/>
      <p class="text-medium">This content is licensed under Creative Commons BY-NC-ND 3.0.
          The software and services provided under this site are offered under the BSD License and the GPL v3 License.</p>
  </section>
</section>