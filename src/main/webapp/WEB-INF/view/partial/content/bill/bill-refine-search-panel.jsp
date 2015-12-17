<!-- We set the statusTypes here to make it easy to create a select menu out of the status types. -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="open" tagdir="/WEB-INF/tags/component" %>

<%@ page import="gov.nysenate.openleg.model.bill.BillStatusType" %>
<c:set var="statusTypes" value="<%=BillStatusType.values()%>"/>

<div layout-gt-sm="row" layout="column" class="search-refine-panel">
  <div flex="25" class="refine-controls margin-right-20">
    <label for="refine_chamber" ng-class="{lit: searchParams.chamber}">Chamber</label>
    <select id="refine_chamber" ng-model="searchParams.chamber">
      <option value="">Any</option><option value="SENATE">Senate</option><option value="ASSEMBLY">Assembly</option>
    </select>

    <label for="refine_type" ng-class="{lit: searchParams.isRes}">Bill/Resolution</label>
    <select id="refine_type" name="isRes" ng-model="searchParams.isRes">
      <option value="">Any</option>
      <option value="false">Bill</option>
      <option value="true">Resolution</option>
    </select>

    <label for="refine_sponsor" ng-class="{lit: searchParams.sponsor}">Primary Sponsor</label>
    <select id="refine_sponsor" name="sponsor" ng-model="searchParams.sponsor">
      <open:member-select-menu showSenators="true" showAssembly="true"/>
    </select>

    <label for="refine_status" ng-class="{lit: searchParams.status}">Current Status</label>
    <select id="refine_status" name="status" ng-model="searchParams.status">
      <option value="">Any</option>
      <c:forEach items="${statusTypes}" var="status">
        <option value="${status.name()}">${status.desc}</option>
      </c:forEach>
    </select>
  </div>
  <div flex="50" layout="column" class="refine-controls margin-right-20">
    <div layout="row">
      <div flex class="margin-right-10">
        <label ng-class="{lit: searchParams.printNo}">Print No</label>
        <input type="text" name="printNo" ng-model="searchParams.printNo" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. S1234"/>
      </div>
      <div flex>
        <label ng-class="{lit: searchParams.title}">Title</label>
        <input type="text" name="title" ng-model="searchParams.title" ng-model-options="{ debounce: 400 }"
               placeholder="Title of the bill/reso"/>
      </div>
    </div>
    <div layout="row">
      <div flex class="margin-right-10">
        <label ng-class="{lit: searchParams.memo}">Memo</label>
        <input type="text" name="memo" ng-model="searchParams.memo" ng-model-options="{ debounce: 400 }"/>
      </div>
      <div flex>
        <label ng-class="{lit: searchParams.fullText}">Full Text</label>
        <input type="text" name="fullText" ng-model="searchParams.fullText" ng-model-options="{ debounce: 400 }"/>
      </div>
    </div>
    <div layout="row">
      <div flex class="margin-right-10">
        <label ng-class="{lit: searchParams.actionText}">Contains Action Text</label>
        <input type="text" name="actionText" ng-model="searchParams.actionText" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. Substituted For *"/>
      </div>
      <div flex>
        <label ng-class="{lit: searchParams.committeeName}">In Committee (Name)</label>
        <input type="text" name="committeeName" ng-model="searchParams.committeeName" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. Aging"/>
      </div>
    </div>
    <div layout="row">
      <div flex class="margin-right-10">
        <label ng-class="{lit: searchParams.billCalNo}">Bill Calendar Number</label>
        <input type="text" name="billCalNo" ng-model="searchParams.billCalNo" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. 123"/>
      </div>
      <div flex>
        <label ng-class="{lit: searchParams.agendaNo}">Agenda Number</label>
        <input type="text" name="agendaNo" ng-model="searchParams.agendaNo" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. 4"/>
      </div>
    </div>
    <div layout="row">
      <div flex class="margin-right-10">
        <label ng-class="{lit: searchParams.lawSection}">Law Section</label>
        <input type="text" name="lawSection" ng-model="searchParams.lawSection" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. Education"/>
      </div>
      <div flex>
        <label ng-class="{lit: searchParams.lawCode}">Law Code</label>
        <input type="text" name="lawCode" ng-model="searchParams.lawCode" ng-model-options="{ debounce: 400 }"
               placeholder="e.g. 236 Town L"/>
      </div>
    </div>
  </div>
  <div flex="25" layout="column" layout-align="center start" class="refine-controls margin-right-20">
    <md-checkbox ng-model="searchParams.signed" ng-class="{lit: searchParams.signed}">
      Is Signed / Adopted
    </md-checkbox>
    <md-checkbox ng-model="searchParams.isGov" ng-false-value="" ng-class="{lit: searchParams.isGov}">
      Governor's Bill
    </md-checkbox>
    <md-checkbox ng-model="searchParams.hasVotes" ng-false-value="" ng-class="{lit: searchParams.hasVotes}">
      Has Votes
    </md-checkbox>
    <md-checkbox ng-model="searchParams.hasApVetoMemo" ng-false-value="" ng-class="{lit: searchParams.hasApVetoMemo}">
      Has Appr/Veto Memo
    </md-checkbox>
    <md-checkbox ng-model="searchParams.isSubstituted" ng-false-value="" ng-class="{lit: searchParams.isSubstituted}">
      Is Substituted By
    </md-checkbox>
    <md-checkbox ng-model="searchParams.isUni" ng-false-value="" ng-class="{lit: searchParams.isUni}">
      Is Uni Bill
    </md-checkbox>
    <md-checkbox ng-model="searchParams.isBudget" ng-false-value="" ng-class="{lit: searchParams.isBudget}">
      Budget Bill
    </md-checkbox>
    <md-checkbox ng-model="searchParams.isRulesSponsor" ng-false-value="" ng-class="{lit: searchParams.isRulesSponsor}">
      Rules Sponsored
    </md-checkbox>
  </div>
</div>
