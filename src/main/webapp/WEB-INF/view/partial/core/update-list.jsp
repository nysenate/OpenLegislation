<md-list>
  <md-item ng-show="pagination.totalItems > pagination.itemsPerPage">
    <md-item-content>
      <div class="md-tile-content" layout="row" layout-sm="column" layout-align="space-between center">
        <div flex> {{pagination.totalItems}} updates. Viewing page {{pagination.currPage}} of {{pagination.lastPage}}.  </div>
        <div flex style="text-align: right;"><dir-pagination-controls boundary-links="true"></dir-pagination-controls></div>
      </div>
    </md-item-content>
  </md-item>
  <md-item dir-paginate="update in updateResponse.result.items | itemsPerPage: updateResponse.limit"
         current-page="pagination.currPage" total-items="updateResponse.total">
    <md-item-content>
      <div class="md-tile-content" style="padding:0;">
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
  <md-item ng-show="pagination.totalItems > pagination.itemsPerPage">
    <md-item-content>
      <div class="md-tile-content" layout="row" layout-sm="column" layout-align="space-between center">
        <div flex> {{pagination.totalItems}} updates. Viewing page {{pagination.currPage}} of {{pagination.lastPage}}.  </div>
        <div flex style="text-align: right;"><dir-pagination-controls boundary-links="true"></dir-pagination-controls></div>
      </div>
    </md-item-content>
  </md-item>
</md-list>
