<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-component" tagdir="/WEB-INF/tags/component" %>

<section ng-controller="BillCtrl">
    <form name="bill-search-form">
      <md-tabs md-selected="selectedView">
        <md-tab label="Overview">
        </md-tab>
        <md-tab label="Quick Search">
          <section ng-controller="BillSearchCtrl">
            <md-content class="padding-10 margin-10">
              <md-input-container>
                <label><i class="prefix-icon2 icon-search"></i>Search for a term or print no</label>
                <input style="font-size:1.5rem;" required name="description" ng-model="searchTerm">
              </md-input-container>
            </md-content>
            <md-content class="padding-10 margin-10 md-whiteframe-z0">
              <span class="md-primary">some sufff</span>
            </md-content>
          </section>
        </md-tab>
        <md-tab label="Advanced Search">
          <md-content class="md-padding">
            <md-input-container flex>
              <label><i class="prefix-icon2 icon-search"></i>Search for a term or print no</label>
              <input required name="description">
            </md-input-container>
            <md-input-container flex>
              <label><i class="prefix-icon2 icon-search"></i>Sponsor</label>
              <input required name="sponsor">
            </md-input-container>
          </md-content>
        </md-tab>
      </md-tabs>

    </form>
  </md-content>
</section>