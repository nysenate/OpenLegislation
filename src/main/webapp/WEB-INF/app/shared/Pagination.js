import React from 'react'
import ReactPaginate from 'react-paginate'

/**
 * A common Pagination component to be used by Open Legislation.
 * This wraps react-paginate but also adds custom styling and a custom return object.
 *
 * Fixes some inconvenient aspects or react-paginate, mainly page numbers being 0 indexed.
 */
export default function Pagination({ currentPage = 1, limit, total, onPageChange }) {

  const pageCount = () => {
    return Math.ceil(total / limit)
  }

  /**
   * Generate the object returned to onPageChange when a new page is clicked.
   *
   * @param page
   * @returns {*} An object with the newly selected page number and the limit and offset values
   * needed to query that page from our API.
   */
  const onPageChangeWrapper = (page) => {
    const page1Indexed = page.selected + 1 // Converts the 0 indexed page from react-paginate to be 1 indexed.
    return onPageChange({
      selectedPage: page1Indexed,
      limit: limit,
      offset: offsetForPage(page1Indexed)
    })
  }

  const offsetForPage = (page) => {
    return (page - 1) * limit + 1
  }

  const itemClassName = "hover:bg-gray-100 rounded cursor-pointer"
  const linkClassName = "px-2 py-1 text-gray-500 border-none"

  return (
    <ReactPaginate
      pageCount={pageCount()}
      pageRangeDisplayed={5}
      onPageChange={onPageChangeWrapper}
      marginPagesDisplayed={1}
      initialPage={currentPage - 1} // react-paginate pages are 0 indexed.
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

