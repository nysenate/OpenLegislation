import React from 'react'
import useWindowSize from "app/shared/useWindowSize";
import { List } from "phosphor-react";

/**
 * Renders tabs responsively. On screens >= 768px a typical tab like component is rendered, on smaller screens
 * an alternative select element is rendered.
 * @param tabs An array of tabs, each tab should be an object with 'name', 'quantity', 'isDisable',
 * and 'component' fields. The 'component' is the component to render when this tab is active.
 * @param activeTab The 'name' of the currently active tab.
 * @param setActiveTab A callback which is executed whenever a users selects a tab.
 *                     It is passed the name of the selected tab.
 * @param showZeroQuantity If true a count will be shown even if it is zero.
 */
export default function Tabs({ tabs, activeTab, setActiveTab, showZeroQuantity = false }) {
  const [ tabComponent, setTabComponent ] = React.useState()
  const windowSize = useWindowSize()

  React.useEffect(() => {
    tabs.forEach((tab) => {
      if (tab.name === activeTab) {
        setTabComponent(tab.component)
      }
    })
  }, [ tabs, activeTab ])

  if (windowSize[0] >= 768) {
    return (
      <DefaultTabs tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab} showZeroQuantity={showZeroQuantity}>
        {tabComponent}
      </DefaultTabs>
    )
  } else {
    return (
      <MobileTabs tabs={tabs} activeTab={activeTab} setActiveTab={setActiveTab} showZeroQuantity={showZeroQuantity}>
        {tabComponent}
      </MobileTabs>
    )
  }
}

/**
 * "Tab" component for mobile or small displays
 */
function MobileTabs({ tabs, activeTab, setActiveTab, showZeroQuantity, children }) {
  return (
    <React.Fragment>
      <div className="mx-5">
        <label className="label label--top font-semibold">
          Go to
        </label>
        <div className="flex items-center border-2 border-blue-500 rounded">
          <List size="1.5rem" className="mx-2" />
          <select value={activeTab} onChange={(e) => setActiveTab(e.target.value)} className="py-1 w-full">
            {tabs.map((tab) =>
              <option key={tab.name} value={tab.name} disabled={tab.isDisabled}>
                {tabLabel(tab, showZeroQuantity)}
              </option>
            )}
          </select>
        </div>
      </div>
      {children}
    </React.Fragment>
  )
}

/**
 * The default tab component, rendered on medium to large size screens.
 */
function DefaultTabs({ tabs, activeTab, setActiveTab, showZeroQuantity, children }) {
  return (
    <React.Fragment>
      <div className="flex pl-5 border-b-1 border-blue-600">
        {tabs.map((tab) => {
          return (
            <Tab key={tab.name}
                 tab={tab}
                 isActive={tab.name === activeTab}
                 setActiveTab={setActiveTab}
                 showZeroQuantity={showZeroQuantity} />
          )
        })}
      </div>
      {children}
    </React.Fragment>
  )
}

function Tab({ tab, isActive, setActiveTab, showZeroQuantity }) {
  let tabClass = "px-3 py-1 mr-3 whitespace-nowrap border-t-1 border-l-1 border-r-1"

  if (tab.isDisabled) {
    tabClass += " cursor-default bg-gray-50 text-gray-400 font-extralight border-gray-50"
  } else if (isActive) {
    tabClass += " text-blue-600 font-semibold bg-white border-blue-600 -mb-px"
  } else {
    tabClass += " text-gray-500 font-light cursor-pointer bg-gray-100"
  }

  return (
    <div className={tabClass}
         onClick={tab.isDisabled ? undefined : () => setActiveTab(tab.name)}>
      {tabLabel(tab, showZeroQuantity)}
    </div>
  )
}

const tabLabel = (tab, showZeroQuantity) => {
  if (!showZeroQuantity) {
    return tab.name + (tab.quantity ? ` (${tab.quantity})` : "")
  }
  return tab.name + ((tab.quantity || tab.quantity === 0) ? ` (${tab.quantity})` : "")
}
