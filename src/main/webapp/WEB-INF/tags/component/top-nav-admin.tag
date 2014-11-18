<%@ tag description="Top navigation menu" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="components" tagdir="/WEB-INF/tags/component" %>

<header class="top-bar-wrapper">
    <nav class="top-bar" data-topbar role="navigation">
        <section>
            <ul class="title-area">
                <li class="name">
                    <h1>
                        <img class="nyss-seal" src="<%= request.getContextPath() %>/static/img/NYSS_seal_transp.png"/>
                        <a style="display:inline-block" href="${ctxPath}/admin"><span class="red1">Open </span> Legislation Admin</a>
                    </h1>
                </li>
            </ul>
            <section class="top-bar-section">
                <!-- Left Nav Section -->
                <components:top-nav-admin-links ulClass="left"/>
            </section>
        </section>
    </nav>
</header>