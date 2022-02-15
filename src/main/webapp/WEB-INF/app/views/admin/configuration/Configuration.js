import React from "react";
import ContentContainer from "app/shared/ContentContainer";
import {
  fetchEnvironmentVariables,
  setEnvironmentVariable
} from "app/apis/environmentApi";


const registerReducer = function (state, action) {
  switch (action.type) {
    case "setVar":
      return {
        ...state,
        [action.name]: { ...state[action.name], value: action.value }
      }
    case "init":
      return action.value.reduce(function (map, obj) {
        map[obj.name] = obj
        return map
      }, {})
  }
}

export default function Configuration({setHeaderText}) {
  // Object which maps variable name to the variable object.
  const [ vars, dispatch ] = React.useReducer(registerReducer, {})

  React.useEffect(() => {
    setHeaderText("Manage Configuration")
    fetchEnvironmentVariables()
      .then((res) => dispatch({ type: "init", value: res.result.items }))
  }, [])

  return (
    <ContentContainer>
      <div className="p-3">
        <table className="table table--stripe w-full">
          <tbody>
          <tr>
            <th className="p-3" colSpan="2"><span className="text-center">Mutable Variables</span></th>
          </tr>
          {Object.values(vars).filter((v) => v.mutable)
            .map((v) => <MutableVariableRow variable={v} dispatch={dispatch} key={v.name} />)}
          <tr>
            <th className="p-3" colSpan="2">Static Variables</th>
          </tr>
          {Object.values(vars).filter((v) => !v.mutable)
            .map((v) => <StaticVariableRow variable={v} key={v.name} />)}
          </tbody>
        </table>
      </div>
    </ContentContainer>
  )
}

function MutableVariableRow({ variable, dispatch }) {

  const onVarChange = (e) => {
    const value = e.target.checked
    setEnvironmentVariable(variable.name, value)
      .then((res) => {
        dispatch({
          type: "setVar",
          name: res.result.name,
          value: res.result.value
        })
      })
      .catch((error) => {
        console.error("Error updating variable: " + error)
      })
  }

  return (
    <tr>
      <td className="p-3">{removeCamelCase(variable.name)}</td>
      <td className="text-right"><input type="checkbox"
                                        checked={variable.value}
                                        onChange={(e) => onVarChange(e)} /></td>
    </tr>
  )
}

function StaticVariableRow({ variable }) {
  return (
    <tr>
      <td className="p-3">{removeCamelCase(variable.name)}</td>
      <td className="text-right">{variable.value.toString()}</td>
    </tr>
  )
}

const removeCamelCase = (name) => {
  const capitalized = name.charAt(0).toUpperCase() + name.slice(1)
  return capitalized.replace(/([A-Z])/g, " $1")
}