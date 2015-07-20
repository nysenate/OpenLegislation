<!-- Pagination Template -->
<script type="text/ng-template" id="st-pagination-template">
  <span class="st-pagination" ng-class="{'vis-hidden' : pages.length < 2}">
    Page:
    <a href="#" ng-click="selectPage(currentPage - 1)" ng-class="{'inactive-link': currentPage === 1}">&laquo;</a>
    <a href="#" ng-click="selectPage(currentPage + 1)" ng-class="{'inactive-link': currentPage === numPages}">&raquo;</a>
    <st-pagination-input class="margin-left-20"></st-pagination-input>
    of {{numPages}}
  </span>
</script>