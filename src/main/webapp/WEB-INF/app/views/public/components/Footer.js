import React from 'react'
import {
  Center,
  TextSmall,
} from "../style"

export default function Footer() {
  return (
    <footer>
      <Center>
        <img src="//licensebuttons.net/l/by-nc-nd/3.0/us/88x31.png"/>
        <p>
          <TextSmall>
            This content is licensed under Creative Commons BY-NC-ND 3.0.
            The software and services provided under this site are offered under the BSD License and the GPL v3 License.
          </TextSmall>
        </p>
      </Center>
    </footer>
  )
}