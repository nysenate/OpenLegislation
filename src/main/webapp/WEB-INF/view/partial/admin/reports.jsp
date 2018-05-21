<md-card ng-controller="ReportsCtrl">
    <md-subheader>Run Reports or Add to Scrape Queue</md-subheader>
    <md-card-content>
        <div class="spotcheck-table-row">
            <p>Select a Spotcheck Report to Run:</p>
            <md-select ng-model="defaultReportType" id="reportType" placeholder="Both">
                <md-optgroup label="What report would you like to Run?">
                    <md-option ng-repeat="report in reportType" ng-value="report" value="{{report}}">{{report}}</md-option>
                </md-optgroup>
            </md-select>
            <md-select ng-model="year" id="year" placeholder={{years[0]}}>
                <md-optgroup label="Select A Year">
                    <md-option ng-repeat="year in years" ng-value="year" value="{{year}}">{{year}}</md-option>
                </md-optgroup>
            </md-select>
            <md-button class="md-raised md-hue-2" ng-click="runReports(defaultReportType, year)">
                <span class="blue3">Run Report</span>
            </md-button>
        </div>
        <div class="spotcheck-table-row">
            <p>Add to Scrape Queue:</p>
            <md-input-container>
                <label>Print Number</label>
                <input id="printNo"  required name="printNo" ng-model="printNo">
                <span ng-messages="printNo.$error">
                    <span ng-message="required">This is required.</span>
                </span>
            </md-input-container>
            <md-select ng-model="sessionYear" class="sessionYear" placeholder={{sessionYears[0]}}>
                <md-optgroup label="Select A Year">
                    <md-option ng-repeat="sessionYear in sessionYears" ng-value="sessionYear" value="{{sessionYear}}">{{sessionYear}}</md-option>
                </md-optgroup>
            </md-select>
            <md-button class="md-raised" ng-click="addToScrapeQueue(sessionYear, printNo)">
                <span class="blue3">Add to Queue</span>
            </md-button>
        </div>
        <div class="spotcheck-table-row">
            <p>Remove Bill Scrape Queue:</p>
            <md-input-container>
                <label>Print Number</label>
                <input id="printNoD"  required name="printNoD" ng-model="printNoD">
                <div ng-messages="printNo.$error">
                    <div ng-message="required">This is required.</div>
                </div>
            </md-input-container>
            <md-select ng-model="dSessionYear" class="sessionYear" placeholder={{sessionYears[0]}}">
                <md-optgroup label="Select A Year">
                    <md-option ng-repeat="dSessionYear in sessionYears" ng-value="dSessionYear" value="{{dSessionYear}}">{{dSessionYear}}</md-option>
                </md-optgroup>
            </md-select>
            <md-button class="md-raised" ng-click="deleteFromScrapeQueue(dSessionYear, printNoD)">
                <span class="blue3">Delete Queue</span>
            </md-button>
        </div>
    </md-card-content>
</md-card>

