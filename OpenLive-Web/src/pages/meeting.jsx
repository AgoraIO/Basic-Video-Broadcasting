import React, { useEffect, useState, useMemo } from 'react'
import clsx from 'clsx'
import { useGlobalState, useGlobalMutation } from '../utils/container'
import { makeStyles } from '@material-ui/core/styles'
import useRouter from '../utils/use-router'
import RTCClient from '../rtc-client'
import Tooltip from '@material-ui/core/Tooltip'
import StreamPlayer from './meeting/stream-player'
import StreamMenu from './meeting/stream-menu'

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

const MeetingPage = () => {
  const classes = useStyles()

  const routerCtx = useRouter()
  const stateCtx = useGlobalState()
  const mutationCtx = useGlobalMutation()

  const onUserPublished = (remoteUser, mediaType) => {
    // remoteUser:
    // mediaType: "audio" | "video" | "all"
    localClient.subscribe(remoteUser)
      .then(mRemoteTrack => {

      })
      .catch(err => {
        mutationCtx.toastError(
          `stream ${remoteUser.getId()} subscribe failed: ${err}`
        )
      })

    if (mediaType === 'video' || mediaType === 'all') {

    }

    if (mediaType === 'audio' || mediaType === 'all') {

    }
  }

  const onUserUnPublished = (remoteUser, mediaType) => {
    // remoteUser:
    // mediaType: "audio" | "video" | "all"
    if (mediaType === 'video' || mediaType === 'all') {

    }

    if (mediaType === 'audio' || mediaType === 'all') {

    }
  }

  const localClient = useMemo(() => {
    const client = new RTCClient()
    client.createClient({ codec: stateCtx.codec, mode: stateCtx.mode })

    client.on('connection-state-change', mutationCtx.connectionStateChanged)
    client.on('user-published', onUserPublished)
    client.on('user-unpublished', onUserUnPublished)

    return client
  }, [stateCtx.codec, stateCtx.mode])

  const [muteVideo, setMuteVideo] = useState(stateCtx.muteVideo)
  const [muteAudio, setMuteAudio] = useState(stateCtx.muteAudio)

  const config = useMemo(() => {
    return {
      token: stateCtx.config.token,
      channel: stateCtx.config.channelName,
      microphoneId: stateCtx.config.microphoneId,
      cameraId: stateCtx.config.cameraId,
      resolution: stateCtx.config.resolution,
      muteVideo: muteVideo,
      muteAudio: muteAudio,
      uid: stateCtx.config.uid,
      host: stateCtx.config.host
      // beauty: stateCtx.beauty
    }
  }, [stateCtx, muteVideo, muteAudio])

  useEffect(() => {
    return () => {
      localClient && localClient.leave(() => mutationCtx.clearAllStream())
    }
  }, [localClient])

  const history = routerCtx.history

  const params = new URLSearchParams(window.location.search)

  useEffect(() => {
    const roleParams = params.get('role')
    if (!config.channel && roleParams !== 'audience') {
      history.push('/')
    }
  }, [config.channel, history, params])

  useEffect(() => {
    if (
      config.channel &&
      localClient._created &&
      localClient._joined == false &&
      localClient._leave == false
    ) {
      localClient.setClientRole(config.host ? 'host' : 'audience')
      localClient
        .join(config)
        .then((uid) => {
          if (config.host) {
            localClient.startLive(config.microphoneId, config.cameraId)
              .then(() => {

              })
          }
          mutationCtx.updateConfig({ uid })
          mutationCtx.stopLoading()
        })
        .catch((err) => {
          mutationCtx.toastError(`join error: ${err.info}`)
          routerCtx.history.push('/')
        })
    }
  }, [localClient, mutationCtx, config, routerCtx])

  const toggleVideo = () => {
    setMuteVideo(!muteVideo)
    console.debug('toggleVideo')
  }

  const toggleAudio = () => {
    setMuteAudio(!muteAudio)
    console.debug('toggleAudio')
  }

  const toggleShareScreen = () => {

  }

  const handleClick = (name) => {
    return (evt) => {
      evt.stopPropagation()
      switch (name) {
        case 'video': {
          muteVideo
            ? localClient.mLocalVideoTrack.play(`stream-player-${localClient.uid}`)
            : localClient.mLocalVideoTrack.stop()
          setMuteVideo(!muteVideo)
          break
        }
        case 'audio': {
          muteAudio
            ? localClient.mLocalAudioTrack.play()
            : localClient.mLocalAudioTrack.stop()
          setMuteAudio(!muteAudio)
          break
        }
        // case 'screen': {
        //   if (stateCtx.screen) {
        //     localClient
        //       .createRTCStream({
        //         token: null,
        //         channel: stateCtx.config.channelName,
        //         microphoneId: stateCtx.config.microphoneId,
        //         resolution: stateCtx.config.resolution,
        //         muteVideo: muteVideo,
        //         muteAudio: muteAudio
        //         // beauty: stateCtx.beauty,
        //       })
        //       .then(() => {
        //         localClient.publish()
        //         mutationCtx.setScreen(false)
        //       })
        //       .catch((err) => {
        //         console.log(err)
        //         mutationCtx.toastError(`Media ${err.info}`)
        //         routerCtx.history.push('/')
        //       })
        //   } else {
        //     localClient
        //       .createScreenSharingStream({
        //         token: null,
        //         channel: stateCtx.config.channelName,
        //         microphoneId: stateCtx.config.microphoneId,
        //         cameraId: stateCtx.config.cameraId,
        //         resolution: stateCtx.config.resolution
        //       })
        //       .then(() => {
        //         localClient.publish()
        //         mutationCtx.setScreen(true)
        //       })
        //       .catch((err) => {
        //         console.log(err)
        //         mutationCtx.toastError(`Media ${err.info}`)
        //       })
        //   }
        //   break
        // }
        case 'profile': {
          break
        }
        default:
          throw new Error(`Unknown click handler, name: ${name}`)
      }
    }
  }

  const doLeave = () => {
    localClient.leave().then(() => {
      localClient.stopLive()
      localClient.destroy()
      mutationCtx.clearAllStream()
      // mutationCtx.resetState()
      routerCtx.history.push('/')
    })
  }

  const otherStreams = useMemo(() => {
    return stateCtx.streams.filter(
      (it) => true
      // (it) => it.getId() !== currentStream.getId()
    )
  }, [stateCtx.streams])

  return (
    <div className="meeting">
      <div className="current-view">
        <div className="nav">
          <div className="avatar-container">
            <div className="default-avatar"></div>
            <div className="avatar-text">Agora Test</div>
            <div className="like"></div>
          </div>
          <Tooltip title="quit">
            <div
              className="quit"
              onClick={doLeave}
            ></div>
          </Tooltip>
        </div>
        <StreamPlayer
          className={'main-stream-profile'}
          uid={stateCtx.config.uid}
          isLocal={true}
          videoTrack={localClient.mLocalVideoTrack}
          audioTrack={localClient.mLocalAudioTrack}
          muteAudio={muteAudio}
          muteVideo={muteVideo}
          showInfo={stateCtx.profile}
          rtcClient={localClient._client}
        >
          <StreamMenu
            muteAudio={muteAudio}
            muteVideo={muteVideo}
            toggleVideo={toggleVideo}
            toggleAudio={toggleAudio}
            toggleShareScreen={toggleShareScreen}
          />
        </StreamPlayer>
        <div className="stream-container">
          {/* {stateCtx.otherStreams.map((stream, index) => (
            <StreamPlayer
              className={'stream-profile'}
              uid={stateCtx.config.uid}
              videoTrack={localClient.mLocalVideoTrack}
              audioTrack={localClient.mLocalAudioTrack}
              muteAudio={muteAudio}
              muteVideo={muteVideo}
              showInfo={stateCtx.profile}

              showProfile={stateCtx.profile}
              local={false}
              key={`${index}${stream.getId()}`}
              stream={stream}
              isPlaying={stream.isPlaying()}
              uid={stream.getId()}
              domId={`stream-player-${stream.getId()}`}
              onDoubleClick={handleDoubleClick}
              showUid={true}
            ></StreamPlayer>
          ))} */}
        </div>
      </div>
    </div>
  )
}

export default React.memo(MeetingPage)
