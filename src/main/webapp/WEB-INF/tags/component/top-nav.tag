<%@ tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="components" tagdir="/WEB-INF/tags/component" %>
<%@ attribute name="activeLink" required="true" %>

<header class="top-bar-wrapper" ng-controller="TopNavCtrl">
    <nav class="top-bar" data-topbar role="navigation">
        <section class="show-for-large-up">
            <ul class="title-area">
                <li class="name">
                    <h1>
                        <img src="<%= request.getContextPath() %>/static/img/NYSS_seal_transp.png"/>
                        <a style="display:inline-block" href="#"><span style='color:#008cba'>Open </span> Legislation</a>
                    </h1>
                </li>
            </ul>
            <section class="top-bar-section">
                <!-- Left Nav Section -->
                <components:top-nav-links activeLink="${activeLink}" ulClass="left"/>
            </section>
        </section>
        <section class="show-for-medium-down">
            <div class="row">
                <div ng-click="showMobileMenu=!showMobileMenu" class="name columns small-12">
                    <a class="left mobile-menu-toggle icon-list2 prefix-icon"></a>
                    <h1>
                        <a class="noselect text-center"><span style='color:#008cba'>Open </span>Legislation</a>
                    </h1>
                </div>
            </div>
        </section>
    </nav>
    <div class="hide-for-large-up text-center" role="navigation">
        <div ng-show="showMobileMenu">
            <components:top-nav-links activeLink="${activeLink}" ulClass="mobile-menu-list side-nav"/>
        </div>
    </div>
</header>