<md-card ng-controller="ReportsCtrl">
    <md-card-content>
        <md-subheader>Run Reports or Add to Scrape Queue</md-subheader>
        <md-list class = "reportlist">
            <md-list-item>
                <p>Calendar Report</p>
                <md-select ng-model="year" placeholder="2018">
                    <md-optgroup label="Select A Year">
                        <md-option ng-repeat="year in years" ng-value="year" value="{{year}}">{{year}}</md-option>
                    </md-optgroup>
                </md-select>
                <md-button class="md-raised md-hue-2" ng-click="runInvervalReport(year)">
                    <span class="blue3">Run Report</span>
                </md-button>
            </md-list-item>
            <md-list-item>
                <p>Agenda Report</p>
                <md-select ng-model="year2" placeholder="2018">
                    <md-optgroup label="Select A Year">
                        <md-option ng-repeat="year in years" ng-value="year" value="{{year}}">{{year}}</md-option>
                    </md-optgroup>
                </md-select>
                <md-button class="md-raised md-hue-2" ng-click="runInvervalReport(year2)">
                    <span class="blue3">Run Report</span>
                </md-button>
            </md-list-item>
            <md-list-item>
                <p>Add to Scrape Queue</p>
                <md-input-container>
                    <label>Session Year</label>
                    <input id="sessionYear" required name="sessionYear" ng-model="sessionYear">
                    <div ng-messages="sessionYear.$error">
                        <div ng-message="required">This is required.</div>
                    </div>
                </md-input-container>
                <md-input-container>
                    <label>Print Number</label>
                    <input id="printNo"  required name="printNo" ng-model="printNo">
                    <div ng-messages="printNo.$error">
                        <div ng-message="required">This is required.</div>
                    </div>
                </md-input-container>
                <md-button class="md-raised" ng-click="addToScrapeQueue(sessionYear, printNo)">
                    <span class="blue3">Add to Queue</span>
                </md-button>
            </md-list-item>
    </md-card-content>
</md-card>

