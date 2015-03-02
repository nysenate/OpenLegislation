<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AgendaCtrl">
  <section>
    <md-tabs md-selected="curr.selectedView" class="md-primary">
      <md-tab>
        <md-tab-label>
          <md-tab-label><i class="icon-search prefix-icon2"></i>Search</md-tab-label>
        </md-tab-label>
      </md-tab>
      <md-tab>
        <md-tab-label><i class="icon-archive prefix-icon2"></i>Browse</md-tab-label>
      </md-tab>
      <md-tab>
        <md-tab-label>
          <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        </md-tab-label>
      </md-tab>
      <md-tab>
        <md-tab-label>
          <md-tab-label><i class="icon-question prefix-icon2"></i>About</md-tab-label>
        </md-tab-label>
      </md-tab>
    </md-tabs>
  </section>
</section>
