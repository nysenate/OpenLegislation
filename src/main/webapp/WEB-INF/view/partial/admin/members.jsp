<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div ng-controller="MembersCtrl" ng-init="init()">
    <div class="member-header-section">
        <h1 class="member-header">{{unverifiedOnly ? " Unverified Members" : "All Members"}}<p class="member-total-results grey">(Results: {{pagination.totalItems}})</p></h1>
        <a class="member-toggle" ng-click="toggleVerified()">{{unverifiedOnly ? "View All Members" : "View Unverified Memebers"}}</a>
    </div>

    <input class="content-section" style="width:100%; display:block" ng-model="searchInput" placeholder="Search"></input>

    <md-card class="member-content-section">
        <md-card-content ng-if="loadingMembers === false">
            <p class="member-text-center" ng-show="pagination.totalItems == 0">No results found.</p>
            <div dir-paginate="member in membersList | itemsPerPage: pagination.itemsPerPage"
                 total-items="pagination.totalItems" current-page="pagination.currPage"
                 pagination-id="paginationId"
                 class="member-table-row">
                <p class="member-table-item">{{member.firstName}} {{member.lastName}}</p>
                <p class="member-table-item grey">{{member.chamber}} {{member.sessionYear}}</p>
                <p class="member-table-item red" ng-if="!member.verified">(Unverified)</p>
                <md-button class="md-raised member-table-button" ng-if="!member.verified" href="${ctxPath}/admin/members/verify/{{member.memberId}}">Verify</md-button>
                <md-button class="md-raised member-table-button" ng-if="member.verified" href="${ctxPath}/admin/member/{{member.memberId}}">Details</md-button>
            </div>
            <dir-pagination-controls class="member-text-center" pagination-id="paginationId" boundary-links="true"
                                     on-page-change="onPageChange(newPageNumber, type)" max-size="12">
            </dir-pagination-controls>
            <!--
            <div class="spotcheck-table-goto" ng-show="showGoto">
                Go to:
                <input ng-model="pagination.currPage" ng-change="onPageChange(pagination.currPage, type)" type="text">
            </div>
            -->
        </md-card-content>
    </md-card>
</div>