import React, { useMemo, useState, useEffect } from 'react'
import PropTypes from 'prop-types'

StreamPlayer.propTypes = {
  uid: PropTypes.string.isRequired
}

export default function StreamPlayer (props) {
  const { uid, isLocal, videoTrack, audioTrack, muteVideo, muteAudio, showInfo, rtcClient } = props

  useMemo(() => {
    if (videoTrack != null) {
      if (muteVideo == true) {
        videoTrack.stop()
      } else if (muteVideo == false) {
        videoTrack.play(`stream-player-${uid}`)
      }
    }
  }, [muteVideo, videoTrack])

  useMemo(() => {
    if (videoTrack != null) {
      if (muteAudio == true) {
        audioTrack.stop()
      } else if (muteAudio == false) {
        audioTrack.play()
      }
    }
  }, [muteAudio, audioTrack])

  const [state, setState] = useState({
    accessDelay: 0,
    fps: 0,
    resolution: 0
  })

  const analytics = useMemo(() => state, [state])

  useEffect(() => {
    const timer = setInterval(() => {
      if (isLocal) {
        const stats = rtcClient.getLocalVideoStats()
        const width = stats.captureResolutionWidth
        const height = stats.captureResolutionHeight
        const fps = stats.captureFrameRate
        setState({
          accessDelay: `${stats.accessDelay ? stats.accessDelay : 0}`,
          resolution: `${width}x${height}`,
          fps: `${fps || 0}`
        })
      } else {
        const stats = rtcClient.getRemoteVideoStats()
        const width = stats.captureResolutionWidth
        const height = stats.captureResolutionHeight
        const fps = stats.captureFrameRate
        setState({
          accessDelay: `${stats.accessDelay ? stats.accessDelay : 0}`,
          resolution: `${width}x${height}`,
          fps: `${fps || 0}`
        })
      }
    }, 500)

    return () => {
      clearInterval(timer)
    }
  }, [isLocal, rtcClient])

  return (
    <div
      className={'stream-player'}
      id={`stream-player-${uid}`}
    >
      {props.children}
      {showInfo ? (
        <div>
          <div className={props.className}>
            <span>SD-RTN delay: {analytics.accessDelay}ms</span>
            <span>
              Video: {analytics.fps}fps {analytics.resolution}
            </span>
          </div>
          <div className="stream-uid">UID: {uid}</div>
        </div>
      ) : null}
    </div>
  )
}
