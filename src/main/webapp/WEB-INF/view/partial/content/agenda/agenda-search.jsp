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
            <md-content layout="row">
              <div flex>
                <label>Select month/year: </label>
                <select ng-model="curr.month" ng-change="updateSelectedDate()" class="margin-left-10">
                  <option value="0">January</option>
                  <option value="1">February</option>
                  <option value="2">March</option>
                  <option value="3">Apr</option>
                  <option value="4">May</option>
                  <option value="5">June</option>
                  <option value="6">July</option>
                  <option value="7">August</option>
                  <option value="8">September</option>
                  <option value="9">October</option>
                  <option value="10">November</option>
                  <option value="11">December</option>
                </select>
                <select ng-model="curr.year" ng-change="updateSelectedDate()" class="margin-left-10">
                  <option>2015</option>
                  <option>2014</option>
                  <option>2013</option>
                  <option>2012</option>
                  <option>2011</option>
                  <option>2010</option>
                  <option>2009</option>
                </select>
              </div>
            </md-content>
          </md-card>
          <span ng-show="loading"><i class="icon-hourglass prefix-icon2"></i>Loading Meetings...</span>
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
            <md-card class="content-card text-medium">
              <form class="agenda-search">
                <div>
                  <label>Agenda No</label>
                  <select></select>
                  <label>Agenda Year</label>
                  <select></select>
                </div>
                <md-divider></md-divider>
                <div>
                  <label>Committee</label>
                  <select></select>
                  <label>Meeting Start Date</label>
                  <input type="date">
                  <label>Meeting End Date</label>
                  <input type="date">
                </div>
                <md-divider></md-divider>
                <div>
                  <label>Contains Bill Print No</label>
                  <input type="text">
                </div>
              </form>
            </md-card>
              <%--<md-content class="padding-20">--%>
                <%--<md-input-container class="md-primary">--%>
                  <%--<label>Agenda Number</label>--%>
                  <%--<select></select>--%>
                  <%--<label>Year</label>--%>
                  <%--<select></select>--%>
                  <%--&lt;%&ndash;<input tabindex="1" style="font-size:1.4rem;" name="term"&ndash;%&gt;--%>
                         <%--&lt;%&ndash;ng-model="agendaSearch.term" ng-model-options="{debounce: 300}" ng-change="simpleSearch(true)">&ndash;%&gt;--%>
                <%--</md-input-container>--%>
              <%--</md-content>--%>
            <md-divider></md-divider>
            <md-subheader class="margin-10 md-warn md-whiteframe-z0">
              <h4>No search results were found for </h4>
            </md-subheader>
            <md-subheader ng-show="" class="margin-10 md-warn md-whiteframe-z0">
              <h4></h4>
            </md-subheader>
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
          <md-card class="content-card">
            <md-subheader>Show agenda updates during the following date range</md-subheader>
            <div layout="row" class="padding-20 text-medium">
              <div flex>
                <label>From</label>
                <input class="margin-left-10" ng-model="curr.fromDate" type="datetime-local">
              </div>
              <div flex>
                <label>To</label>
                <input class="margin-left-10" ng-model="curr.toDate" type="datetime-local">
              </div>
            </div>
            <md-divider></md-divider>
            <div layout="row" class="padding-20 text-medium">
              <div flex>
                <label>With </label>
                <select class="margin-left-10" ng-model="curr.type">
                  <option value="processed">Processed Date</option>
                  <option value="published">Published Date</option>
                </select>
              </div>
              <div flex>
                <label>Sort </label>
                <select class="margin-left-10" ng-model="curr.sortOrder">
                  <option value="desc" selected>Newest First</option>
                  <option value="asc">Oldest First</option>
                </select>
              </div>
              <div flex>
                <md-checkbox class="md-hue-3 no-margin" ng-model="curr.detail" aria-label="detail">Show Detail</md-checkbox>
              </div>
            </div>
          </md-card>
          <div ng-if="agendaUpdates.fetching" class="text-medium text-align-center">Fetching updates, please wait.</div>
          <update-list ng-show="!agendaUpdates.fetching && agendaUpdates.response.success === true"
                       update-response="agendaUpdates.response"
                       from-date="curr.fromDate" to-date="curr.toDate"
                       pagination="pagination" show-details="curr.detail">
          </update-list>
          <md-card class="content-card" ng-if="agendaUpdates.response.success === false">
            <md-subheader class="margin-10 md-warn">
              <h4>{{agendaUpdates.errMsg}}</h4>
            </md-subheader>
          </md-card>
        </section>
      </md-tab>
    </md-tabs>
  </section>
</section>