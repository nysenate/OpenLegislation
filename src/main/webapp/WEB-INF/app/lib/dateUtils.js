
/**
 * Convert a year into its session year.
 * @param year The year to convert, defaults to the current year.
 */
export function sessionYear(year = new Date().getFullYear()) {
  return (year % 2 === 0) ? year - 1 : year
}

