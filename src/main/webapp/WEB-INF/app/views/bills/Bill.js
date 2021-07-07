import React, { useEffect } from 'react';
import billGetApi from "app/apis/billGetApi";

export default function Bill({ setTitle, match }) {

  const [ bill, setBill ] = React.useState({})
  const [ loading, setLoading ] = React.useState(true)

  useEffect(() => {
    billGetApi(match.params.sessionYear, match.params.printNo)
      .then((bill) => {
        setBill(bill)
        setLoading(false)
      })
  }, [])

  useEffect(() => {
    if (bill && bill.billType) {
      setTitle(formatTitle(bill))
    }
  }, [ bill ])

  function formatTitle(bill) {
    const type = bill.billType.resolution ? 'Resolution' : 'Bill'
    return `NYS ${bill.billType.desc} ${type} ${bill.printNo}-${bill.session}`
  }

  if (loading) {
    return (<div>Loading ...</div>)
  }

  return (
    <div className="p-5">
      <h3 className="h3 px-3 pb-5">{bill.title}</h3>
      <div className="flex">
        <img className="h-24 mr-3" src={`/static/img/business_assets/members/mini/${bill.sponsor.member.imgName}`} />
        <div className="flex flex-col">
          <span className="text">Sponsored By</span>
          <span className="text text--large font-medium">{bill.sponsor.member.fullName}</span>
          <span className="text text--small">District {bill.sponsor.member.districtCode}</span>
        </div>
      </div>
    </div>
  )
}