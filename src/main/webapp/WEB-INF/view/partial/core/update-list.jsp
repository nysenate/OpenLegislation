<md-list ng-repeat="update in updates">
  <md-item>
    <md-item-content>
      <div class="md-tile-content">
        <md-card class="content-card">
          <md-subheader>
            <span class="capitalize">
              <span ng-if="showDetails && update.action && update.scope">
                {{update.action | lowercase}} - {{update.scope}}
              </span>
              <span ng-if="showDetails && showId">-</span>
              <span ng-if="showId">{{update | updateId}}</span>
            </span>
          </md-subheader>
          <md-content>
            <h4>Published Date - {{update.sourceDateTime | moment:'MMM DD, YYYY HH:mm:ss'}}</h4>
            <h4>Processed Date - {{update.processedDateTime | moment:'MMM DD, YYYY HH:mm:ss'}}</h4>
            <h4>Source - <a>{{update.sourceId}}</a></h4>
            <table class="bill-updates-table" ng-if="showDetails && update.fields">
              <thead>
              <tr>
                <th style="width:150px;">Field Name</th>
                <th>Data</th>
              </tr>
              </thead>
              <tbody>
              <tr ng-repeat="(field, value) in update.fields">
                <td>{{field}}</td>
                <td><pre style="max-height:300px;">{{value}}</pre></td>
              </tr>
              </tbody>
            </table>
          </md-content>
        </md-card>
      </div>
    </md-item-content>
  </md-item>
</md-list>
