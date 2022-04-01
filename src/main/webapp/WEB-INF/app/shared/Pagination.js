import React from 'react'
import ReactPaginate from 'react-paginate'


/**
 * A common Pagination component to be used by Open Legislation.
 * This wraps react-paginate but also adds custom styling and a custom return object.
 *
 * Fixes some inconvenient aspects or react-paginate, mainly page numbers being 0 indexed.
 *
 * @param {Object} params
 * @param {number} params.currentPage - The currently active page, this page will be selected.
 * @param {number} params.limit - The number of results to show per page.
 * @param {number} params.total - The total number of results.
 * @param {callback} params.onPageChange - A callback function called on page change.
 *                                         Its given a PageLimOff representing the selected page.
 * @return {PageParams} Limit offset info for the selected page.
 */
export default function Pagination({ currentPage = 1, limit, total, onPageChange }) {
  if (!total) {
    return null;
  }

  const pageCount = () => {
    return Math.ceil(total / limit)
  }

  const onPageChangeWrapper = (page) => {
    const page1Indexed = page.selected + 1 // Converts the 0 indexed page from react-paginate to be 1 indexed.
    return onPageChange(new PageParams(page1Indexed, limit))
  }

  const itemClassName = "hover:bg-gray-100 rounded cursor-pointer"
  const linkClassName = "px-2 py-1 text-gray-500 border-none"

  return (
    <ReactPaginate
      pageCount={pageCount()}
      pageRangeDisplayed={5}
      onPageChange={onPageChangeWrapper}
      marginPagesDisplayed={1}
      forcePage={currentPage - 1} // react-paginate pages are 0 indexed.
      disableInitialCallback={true}
      nextLabel=">"
      previousLabel="<"
      containerClassName="flex space-x-0 md:space-x-2 justify-center m-3"
      pageClassName={itemClassName}
      pageLinkClassName={linkClassName}
      activeClassName="border-solid border-1 border-blue-500 rounded"
      activeLinkClassName="text-blue-500"
      previousClassName={itemClassName}
      previousLinkClassName={linkClassName}
      nextClassName={itemClassName}
      nextLinkClassName={linkClassName}
      breakClassName={itemClassName}
      breakLinkClassName={linkClassName}
      disabledClassName=""
    />
  )
}

/**
 * Limit, offset, and page info on a page. Useful for querying that page from an API.
 * @param selectedPage {number} The selected page.
 * @param limit {number} The current limit being used.
 * @returns {{selectedPage, offset, limit}}
 * @constructor
 */
export function PageParams(selectedPage, limit) {
  return {
    selectedPage: selectedPage,
    limit: limit,
    offset: (selectedPage - 1) * limit + 1
  }
}
