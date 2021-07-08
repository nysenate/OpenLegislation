import React from "react"

export default function MemberThumbnail({ member }) {
  return (
    <img className="h-20 md:h-24 mr-3" alt={`Member ${member.fullName}`} src={`/static/img/business_assets/members/mini/${member.imgName}`} />
  )
}