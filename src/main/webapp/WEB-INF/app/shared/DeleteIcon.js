import { Trash } from "phosphor-react";
import React from "react";

export default function DeleteIcon({ onClick }) {
  return (
    <Trash onClick={() => onClick()}
           className="text-red-600 hover:cursor-pointer inline mr-3"
           size="1.2rem"
           weight="bold" />
  )
}
