<!-- Pagination Template -->
<script type="text/ng-template" id="st-pagination-template">
  <span class="st-pagination" ng-if="pages.length > 1">
    <a href="#" ng-click="selectPage(currentPage - 1)" ng-class="{'inactive-link': currentPage === 1}">&laquo;</a>
    <a href="#" ng-click="selectPage(currentPage + 1)" ng-class="{'inactive-link': currentPage === numPages}">&raquo;</a>
    <a href="#" ng-click="selectPage(1)" ng-class='{"inactive-link": currentPage === 1}'>1</a>
    <a href="#" ng-repeat="page in pages" ng-bind="page" ng-if="page > 1 && page < numPages"
       ng-click="selectPage(page)" ng-class='{"inactive-link": currentPage === page}'></a>
    <a href="#" ng-click="selectPage(numPages)" ng-bind="numPages"
       ng-class='{"inactive-link": currentPage === numPages}'></a>
  </span>
</script>