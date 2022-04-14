import React from "react"
import Input from "app/shared/Input";
import { changePassword } from "app/apis/adminAccountApi";
import ErrorMessage from "app/shared/ErrorMessage";


const registerReducer = function (state, action) {

  switch (action.type) {
    case "onChange":
      return {
        ...state,
        ...action.payload,
        successMsg: ""
      }
    case "error":
      return {
        ...state,
        error: true,
        errorMsg: action.payload,
        successMsg: ""
      }
    case "onSubmit":
      return {
        ...state,
        error: false,
        errorMsg: "",
        successMsg: ""
      }
    case "success":
      return {
        ...state,
        successMsg: "Successfully changed password."
      }
    default:
      return state
  }
}

const initialState = {
  password: "",
  repeatPassword: "",
  error: false,
  errorMsg: "",
  successMsg: ""
}

export default function ChangePassword() {
  const [ state, dispatch ] = React.useReducer(registerReducer, initialState)

  const onSubmit = (e) => {
    e.preventDefault()

    if (state.password.length < 5) {
      dispatch({
        type: "error",
        payload: "Your password must be at least 5 characters."
      })
    }
    else if (state.password !== state.repeatPassword) {
      dispatch({
        type: "error",
        payload: "Your passwords must match."
      })
    }
    else {
      dispatch({
        type: "onSubmit"
      })
      changePassword(state.password)
        .then((res) => {
          dispatch({
            type: "success",
          })
        })
        .catch((err) => {
          dispatch({
            type: "error",
            payload: err.message
          })
        })
    }
  }

  return (
    <div className="p-3">
      <h3 className="h3 mb-3">Change Password</h3>
      <form className="flex items-end gap-6" onSubmit={onSubmit}>
        <Input label="New Password"
               value={state.password}
               onChange={(e) => dispatch({ type: "onChange", payload: { password: e.target.value } })}
               type="password"
               name="newPassword"
        />
        <Input label="Repeat Password"
               value={state.repeatPassword}
               onChange={(e) => dispatch({ type: "onChange", payload: { repeatPassword: e.target.value } })}
               type="password"
               name="newPassword"
        />
        <button type="submit" className="btn btn--primary px-3">Change Password</button>
      </form>
      <div className="my-3">
        {state.error &&
          <ErrorMessage>{state.errorMsg}</ErrorMessage>
        }
        {state.successMsg &&
          <p className="text text-green-700">{state.successMsg}</p>
        }
      </div>
    </div>
  )
}
