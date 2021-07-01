import React from 'react'


export default function PublicHeader() {

  return (
    <div className="bg-blue-500 h-64 md:h-72 lg:h-80">
      <div className="pt-14 flex items-center space-x-4 mx-auto justify-center">
        <div>
          <img className="h-14 md:h-24 lg:h-28" src="/static/img/nys_logo224x224.png" alt="Senate Seal" />
        </div>
        <div>
          <h1 className="h1 text-white">Open Legislation</h1>
        </div>
      </div>
    </div>
  )
}
