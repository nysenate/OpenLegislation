import React from "react"


export default function UpdateFieldTable({ updateFields }) {
  if (!updateFields) {
    return null
  }

  return (
    <table className="table table--stripe">
      <thead>
      <tr>
        <th>Field Name</th>
        <th>Data</th>
      </tr>
      </thead>
      <tbody>
      {Object.entries(updateFields).map(([ key, value ]) => {
        return (
          <tr key={key}>
            <td>{key}</td>
            <td>
              <pre className="whitespace-pre-wrap">{value}</pre>
            </td>
          </tr>
        )
      })}
      </tbody>
    </table>
  )
}