<%@ tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="components" tagdir="/WEB-INF/tags/component" %>

<header class="top-bar-wrapper" ng-controller="TopNavCtrl">
    <nav class="top-bar" data-topbar role="navigation">
        <section class="show-for-large-up">
            <ul class="title-area">
                <li class="name">
                    <h1>
                        <img class="nyss-seal" src="<%= request.getContextPath() %>/static/img/NYSS_seal_transp.png"/>
                        <a style="display:inline-block" href="${ctxPath}"><span class="blue3">Open </span> Legislation</a>
                    </h1>
                </li>
            </ul>
            <section class="top-bar-section">
                <!-- Left Nav Section -->
                <components:top-nav-links ulClass="left"/>
            </section>
        </section>
        <section class="show-for-medium-down">
            <div class="row">
                <div ng-click="showMobileMenu=!showMobileMenu" class="name columns small-12">
                    <a class="left mobile-menu-toggle icon-list2 prefix-icon"></a>
                    <h1 class="text-center">
                        <img class="nyss-seal" src="<%= request.getContextPath() %>/static/img/NYSS_seal_transp.png"/>
                        <a style="display: inline" class="noselect"><span class="blue3">Open </span>Legislation</a>
                    </h1>
                </div>
            </div>
        </section>
    </nav>
    <div class="hide-for-large-up text-center" role="navigation">
        <div ng-show="showMobileMenu">
            <components:top-nav-links ulClass="mobile-menu-list side-nav"/>
        </div>
    </div>
</header>