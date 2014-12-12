<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="LawCtrl">
  <section ng-controller="LawViewCtrl">
    <h3 style="color:white;" class="green1-bg no-bottom-margin padding-20">
      <span class="bold">{{lawTree.info.name}}</span>
    </h3>

    <!-- Recursive ng-repeat templates (pretty cool huh) -->
    <script type="text/ng-template" id="law-tree-snippet.html">
      <div class="margin-bottom-20 text-medium">
        <a class="bold-span-1">{{doc.docType}} {{doc.docLevelId}}</a>
        <span ng-if="doc.docType == 'SECTION'">
           {{doc.title}}
        </span>
        <hr ng-if="doc.documents.size > 0" class="margin-top-5"/>
        <ul class="no-bullet" ng-if="doc.documents.size > 0">
          <li ng-repeat="doc in doc.documents.items" ng-include="'law-tree-snippet.html'"></li>
        </ul>
      </div>
    </script>

    <a ng-repeat="doc in lawTree.documents.documents.items" class="bold-span-1">{{doc.docType}} {{doc.docLevelId}}</a>
    <hr/>


  </section>
</section>