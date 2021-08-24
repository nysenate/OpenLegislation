import React from 'react'
import MemberThumbnail from "app/shared/MemberThumbnail";

/**
 * Displays information about a single member
 * @param member
 */
export default function MemberListing({ member }) {
  return (
    <div className="flex items-center">
      <div className="w-12 mr-2">
      <MemberThumbnail member={member} />
      </div>
      <div>{member.fullName} - District {member.districtCode}</div>
    </div>
  )
}