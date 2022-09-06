import React from 'react'

/**
 * Returns a 2 element array, the first element is the width of the screen, second is the height. i.e. [x, y]
 * This hook will trigger a rerender whenever the size of the screen changes.
 */
export default function useWindowSize() {
  const [ size, setSize ] = React.useState([ 0, 0 ]);
  React.useLayoutEffect(() => {
    function updateSize() {
      setSize([ window.innerWidth, window.innerHeight ]);
    }

    window.addEventListener('resize', updateSize);
    updateSize();
    return () => window.removeEventListener('resize', updateSize);
  }, []);
  return size;
}
