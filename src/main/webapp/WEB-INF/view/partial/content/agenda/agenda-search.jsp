<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AgendaCtrl">
  <section>
    <md-tabs md-selected="curr.selectedView" class="md-primary">
      <md-tab>
        <md-tab-label>
          <md-tab-label><i class="icon-search prefix-icon2"></i>Search</md-tab-label>
        </md-tab-label>
        <section ng-controller="AgendaSearchCtrl">
          <section class="margin-top-10">
            <form>
              <md-content class="padding-20">
                <md-input-container class="md-primary">
                  <label><i class="prefix-icon2 icon-search"></i>Search for committee agendas</label>
                  <input tabindex="1" style="font-size:1.4rem;" name="quick-term"
                         ng-model="agendaSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">
                </md-input-container>
              </md-content>
              <md-divider></md-divider>
              <md-subheader ng-show="billSearch.searched && billSearch.term && !billSearch.error && curr.pagination.totalItems === 0"
                            class="margin-10 md-warn md-whiteframe-z0">
                <h4>No search results were found for '{{billSearch.term}}'</h4>
              </md-subheader>
              <md-subheader ng-show="billSearch.searched && billSearch.term && billSearch.error"
                            class="margin-10 md-warn md-whiteframe-z0">
                <h4>{{billSearch.error.message}}</h4>
              </md-subheader>
            </form>
          </section>
          <section>
            <md-card class="content-card">
              <md-subheader><strong>Quick search for Agendas</strong></md-subheader>
              <div class="padding-20">
                <p class="text-medium">Senate committee agendas contain meeting details such as where the meeting took place and
                  which bills were discussed/voted on. The committee agendas are grouped into a <strong>weekly agenda</strong>.
                  Each weekly agenda is identified by an agenda number and a calendar year, e.g. 2-2015, where 2 is the agenda number
                  and 2015 is the calendar year.
                </p>
                <p class="text-medium"></p>
              </div>
            </md-card>
          </section>
        </section>
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
