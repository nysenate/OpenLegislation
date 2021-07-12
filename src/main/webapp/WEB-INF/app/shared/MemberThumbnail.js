import React from "react"

export default function MemberThumbnail({ member }) {
  const imgName = member ? member.imgName : "no_image.jpg"
  const alt = (member && member.fullName) ? member.fullName : "Rules Committee"
  return (
    <img className="h-20 md:h-24 mr-3"
         alt={`Member ${alt}`}
         src={`/static/img/business_assets/members/mini/${imgName}`} />
  )
}