<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section ng-controller="AgendaCtrl">
  <section class="content-section">
    <md-tabs md-selected="selectedView" class="md-primary">
      <md-tab>
        <md-tab-label><i class="icon-calendar prefix-icon2"></i>Browse</md-tab-label>

        <section ng-if="selectedView === 0" ng-controller="AgendaBrowseCtrl">
          <p class="text-medium margin-left-10 gray10">
            <i class="prefix-icon2 icon-info"></i>Repeated meetings typically have notes associated with them to indicate changes to the time/location.
          </p>
          <md-card class="content-card">
            <md-card-content id="agenda-date-picker" ui-calendar="calendarConfig" ng-model="meetingEventSources"></md-card-content>
          </md-card>
        </section>
      </md-tab>
      <md-tab>
        <md-tab-label>
          <md-tab-label><i class="icon-search prefix-icon2"></i>Search</md-tab-label>
        </md-tab-label>
        <section ng-if="selectedView === 1" ng-controller="AgendaSearchCtrl">
          <section class="margin-top-10">
            <form>
              <md-content class="padding-20">
                <md-input-container class="md-primary">
                  <label><i class="prefix-icon2 icon-search"></i>Search for committee agendas</label>
                  <input tabindex="1" style="font-size:1.4rem;" name="term"
                         ng-model="agendaSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">
                </md-input-container>
              </md-content>
              <md-divider></md-divider>
              <md-subheader class="margin-10 md-warn md-whiteframe-z0">
                <h4>No search results were found for </h4>
              </md-subheader>
              <md-subheader ng-show="" class="margin-10 md-warn md-whiteframe-z0">
                <h4></h4>
              </md-subheader>
            </form>
            <md-card>
              <md-content>
                <code style="max-width: 400px;">{{agendaSearch.results}}</code>

              </md-content>
            </md-card>
          </section>
          <section>
            <toggle-panel open="true" label="Quick search for Agendas" extra-classes="content-card">
              <div class="padding-20">
                <p class="text-medium">Senate committee agendas contain meeting details such as where the meeting took place and
                  which bills were discussed/voted on. The committee agendas are grouped into a <strong>weekly agenda</strong>.
                  Each weekly agenda is identified by an agenda number and a calendar year, e.g. 2-2015, where 2 is the agenda number
                  and 2015 is the calendar year.
                </p>
                <p class="text-medium"></p>
              </div>
            </toggle-panel>
          </section>
        </section>
      </md-tab>
      <md-tab>
        <md-tab-label>
          <md-tab-label><i class="icon-flag prefix-icon2"></i>Updates</md-tab-label>
        </md-tab-label>
        <section ng-if="selectedView === 2" ng-controller="AgendaUpdatesCtrl">

        </section>
      </md-tab>
    </md-tabs>
  </section>
</section>