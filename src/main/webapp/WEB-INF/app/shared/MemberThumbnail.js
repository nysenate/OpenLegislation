import React from "react"

export default function MemberThumbnail({ member }) {
  const imgName = member ? member.imgName : "no_image.jpg"
  const alt = (member && member.fullName) ? member.fullName : "Rules Committee"
  return (
    <div>
      <img className="w-12 md:w-16 mr-3"
           alt={`Member ${alt}`}
           src={`/static/img/business_assets/members/mini/${imgName}`} />
    </div>
  )
}