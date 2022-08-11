import React from 'react'


export default function Note({ children }) {

  return (
    <div className="bg-yellow-50 py-2 rounded">
      {children}
    </div>
  )
}
