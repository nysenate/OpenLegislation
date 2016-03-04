<!-- Template for displaying generic paginated content updates -->
<div class="content-card">
  <h3 class="text-normal"><strong>{{updateResponse.total}}</strong> <span ng-show="showDetails"> granular </span> updates
    <span ng-show="fromDate && toDate">between {{fromDate | moment:'lll'}} and {{toDate | moment:'lll'}}.</span>
    <span ng-show="pagination.totalItems > 0">Viewing page {{pagination.currPage}} of {{pagination.lastPage}}. </span>
  </h3>
  <div class="subheader" ng-if="pagination.totalItems > pagination.itemsPerPage">
    <div>
      <dir-pagination-controls max-size="10" class="text-align-center" boundary-links="true"></dir-pagination-controls>
    </div>
  </div>
</div>
<section>
  <div dir-paginate="update in updateResponse.result.items | itemsPerPage: updateResponse.limit"
       current-page="pagination.currPage" total-items="updateResponse.total">
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
        <p class="text-medium">Published Date - {{update.sourceDateTime | moment:'MMM DD, YYYY h:mm:ss A'}}</p>
        <p class="text-medium">Processed Date - {{update.processedDateTime | moment:'MMM DD, YYYY h:mm:ss A'}}</p>
        <p class="text-medium">Source - <a>{{update.sourceId}}</a></p>
        <table class="bill-updates-table" ng-if="showDetails && update.fieldCount > 0">
          <thead>
          <tr>
            <th style="width:150px;">Field Name</th>
            <th>Data</th>
          </tr>
          </thead>
          <tbody>
          <tr ng-repeat="(field, value) in update.fields">
            <td class="blue4">{{field}}</td>
            <td><pre style="max-height:300px;">{{value}}</pre></td>
          </tr>
          </tbody>
        </table>
      </md-content>
    </md-card>
  </div>
</section>
<div ng-if="pagination.totalItems > pagination.itemsPerPage">
  <dir-pagination-controls  max-size="10" class="text-align-center" boundary-links="true"></dir-pagination-controls>
</div>
