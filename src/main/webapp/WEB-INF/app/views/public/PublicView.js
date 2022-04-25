import React from 'react'
import BrowseLegislation from "./components/BrowseLegislation";
import DataProvided from "./components/DataProvided"
import SignUp from "./components/SignUp";
import OpenSource from "./components/OpenSource";
import Documentation from "./components/Documentation";
import Footer from "./components/Footer"
import PublicHeader from "./components/PublicHeader";

export default function PublicView() {
  return (
    <div className="bg-gray-100 overflow-auto">
      <PublicHeader />
      <main>
        <div className="max-w-sm sm:max-w-lg md:max-w-xl lg:max-w-4xl mx-auto">
          <About />
          <BrowseLegislation />
          <DataProvided />
          <SignUp />
          <OpenSource />
          <Documentation />
          <Footer />
        </div>
      </main>
    </div>
  )
}

function About() {
  return (
    <section className="card mt-12 p-6 lg:p-12 relative -mt-28">
      <p className="text text--large text-left md:text-center">
        Developed in-house at the <a href="http://www.nysenate.gov" className="link">New York State Senate</a>,
        Open Legislation is a public web service that provides access to NYS legislative data
        from the Legislative Bill Drafting Commission system through a JSON API.
      </p>
    </section>
  )
}
