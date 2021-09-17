import React from 'react'

export default function ContentContainer({ children }) {
  return (
    <section className="flex justify-center">
      <div className="w-full md:w-11/12 xl:w-10/12 bg-white">
        {children}
      </div>
    </section>
  )
}
