<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("ctxPath", request.getContextPath()); %>

<section class="content-section" ng-controller="LandingCtrl">
  <section class="slogan-container">
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
  <section class="padding-20 margin-20">
      <img src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png"/>
      <p class="text-medium">This content is licensed under Creative Commons BY-NC-ND 3.0.
          The software and services provided under this site are offered under the BSD License and the GPL v3 License.</p>
  </section>
</section>