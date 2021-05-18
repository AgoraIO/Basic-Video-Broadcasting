import React, { useMemo, useState, useEffect } from 'react'
import clsx from 'clsx'
import Tooltip from '@material-ui/core/Tooltip'
import { makeStyles } from '@material-ui/core/styles'
import { useGlobalState, useGlobalMutation } from '../../utils/container'

const useStyles = makeStyles({
  menu: {
    height: '150px',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
  },
  customBtn: {
    width: '50px',
    height: '50px',
    marginLeft: '20px',
    borderRadius: '26px',
    backgroundColor: 'rgba(0, 0, 0, 0.4)',
    backgroundSize: '50px',
    cursor: 'pointer'
  },
  leftAlign: {
    display: 'flex',
    flex: '1',
    justifyContent: 'space-evenly'
  },
  rightAlign: {
    display: 'flex',
    flex: '1',
    justifyContent: 'center'
  },
  menuContainer: {
    width: '100%',
    height: '100%',
    position: 'absolute',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'flex-end',
    zIndex: '2'
  }
})

export default function StreamMenu (props) {
  const { muteVideo, muteAudio } = props

  const stateCtx = useGlobalState()
  const classes = useStyles()

  function toggleVideo () {
    props.toggleVideo()
  }

  function toggleAudio () {
    props.toggleAudio()
  }

  function toggleShareScreen () {
    props.toggleShareScreen()
  }

  return (
    <div className={classes.menuContainer}>
      <div className={classes.menu}>
        <Tooltip title={muteVideo ? 'mute-video' : 'unmute-video'}>
          <i
            onClick={toggleVideo}
            className={clsx(
              classes.customBtn,
              muteVideo ? 'mute-video' : 'unmute-video'
            )}
          />
        </Tooltip>
        <Tooltip title={muteAudio ? 'mute-audio' : 'unmute-audio'}>
          <i
            onClick={toggleAudio}
            className={clsx(
              classes.customBtn,
              muteAudio ? 'mute-audio' : 'unmute-audio'
            )}
          />
        </Tooltip>
        <Tooltip title={stateCtx.screen ? 'stop-screen-share' : 'start-screen-share'}>
          <i
            onClick={toggleShareScreen}
            className={clsx(
              classes.customBtn,
              stateCtx.screen
                ? 'start-screen-share'
                : 'stop-screen-share'
            )}
          />
        </Tooltip>
      </div>
    </div>
  )
}
