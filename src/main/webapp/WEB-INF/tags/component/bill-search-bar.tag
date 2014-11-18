<%@ tag description="Responsive Bill Search Bar" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="components" tagdir="/WEB-INF/tags/component" %>

<form id="billSearchForm" ng-submit="search()">
    <div class="relative collapsed">
        <div id="searchBarIcon">
            <i class="icon-search"></i>
        </div>
        <div class="columns large-10 no-padding" style="padding-left:50px;">
            <div class="columns small-4 medium-2 no-padding">
                <select class="no-bottom-margin" style="height: 42px;">
                    <option>All Sessions</option>
                    <option>2013-2014</option>
                    <option>2011-2012</option>
                    <option>2009-2010</option>
                </select>
            </div>
            <div class="columns small-8 medium-10 no-padding">
                <input id="billSearchBar" type="text" ng-model="searchTerm" placeholder="Search for NYS Bills and Resolutions."/>
            </div>
        </div>
        <div id="billAdvancedSearchToggle" ng-click="search()" class="columns large-2 hide-for-medium-down noselect">
            <span class="bold-span-1">Search</span>
        </div>
    </div>
</form>