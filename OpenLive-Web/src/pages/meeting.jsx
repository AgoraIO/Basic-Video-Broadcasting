import React, {useEffect, useMemo} from 'react';
import clsx from 'clsx';
import {useGlobalState, useGlobalMutation} from '../utils/container';
import {makeStyles} from '@material-ui/core/styles';
import userRouter from '../utils/use-router';
import useStream from '../utils/use-stream';
import RTCClient from '../rtc-client';
import StreamPlayer from './meeting/stream-player';

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
    borderRadius: '26px',
    backgroundColor: 'rgba(0, 0, 0, 1)',
    opacity: 0.4,
    backgroundSize: '50px',
    margin: '0 19px',
    cursor: 'pointer',
  },
  leftAlign: {
    display: 'flex',
    flex: '1',
    justifyContent: 'space-evenly',
  },
  rightAlign: {
    display: 'flex',
    flex: '1',
    justifyContent: 'center'
  },
  muteVideo: {
    '&:hover': {
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-camera.png")',
  },
  unmuteVideo: {
    '&:hover': {
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-camera-off.png")',
  },
  muteAudio: {
    '&:hover': {
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-microphone.png")',
  },
  unmuteAudio: {
    '&:hover': {
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-microphone-off.png")',
  },
  startScreenShare: {
    '&:hover': {
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-share.png")',
  },
  stopScreenShare: {
    '&:hover': {
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-share.png")',
    '&::before': {
      backgroundColor: 'rgba(0, 0, 0, 1)',
      opacity: 0.2,
    }
  },
  showProfile: {
    '&:hover': {
      backgroundImage: 'url("/icon-text-actived.png")',
      backgroundColor: '#44A2FC',
      opacity: 1.0,
    },
    backgroundImage: 'url("/icon-text.png")',
  },
  menuContainer: {
    width: '100%',
    height: '100%',
    position: 'absolute',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'flex-end',
    zIndex: '2',
  },
  // streamContainer: {
  //   left: '30px',
  //   top: '91px',
  //   height: '660px',
  //   width: '233px',
  //   position: 'absolute',
  //   display: 'flex',
  //   flexDirection: 'column',
  //   justifyContent: 'flex-end',
  //   zIndex: '2',
  // }
});

const MeetingPage = () => {
  const classes = useStyles();

  const routerCtx = userRouter();
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const localClient = useMemo(() => {
    const client = new RTCClient();
    client.createClient({codec: stateCtx.codec, mode: stateCtx.mode});
    return client;
  }, []);
  const [localStream, streamList] = useStream(localClient);
  useEffect(() => {
    console.log('localStream', localStream);
    if (!stateCtx.config.channelName) {
      routerCtx.history.push('/');
    }
    localClient.join({
      token: null,
      channel: stateCtx.config.channelName,
      uid: 0,
      microphoneId: stateCtx.config.microphoneId,
      cameraId: stateCtx.config.cameraId,
      resolution: stateCtx.config.resolution
    }).then(() => {
      console.log('localStream', localStream);
      mutationCtx.stopLoading();
    })
    return () => {
      localClient.destroy();
    }
  }, [0])

  const handleClick = (name) => {
    return (evt) => {
      switch (name) {
        case 'video': {
          stateCtx.video ? localStream.muteVideo() : localStream.unmuteVideo()
          mutationCtx.setVideo(!stateCtx.video)
          break;
        }
        case 'audio': {
          stateCtx.audio ? localStream.muteAudio() : localStream.unmuteAudio()
          mutationCtx.setAudio(!stateCtx.audio)
          break;
        }
        case 'screen': {
          if (stateCtx.screen) {
            localClient.createRTCStream({
              token: null,
              channel: stateCtx.config.channelName,
              microphoneId: stateCtx.config.microphoneId,
              resolution: stateCtx.config.resolution
            }).then(() => {
              mutationCtx.setScreen(false)
              console.log('start rtc stream')
            })
          } else {
            localClient.createScreenSharingStream({
              token: null,
              channel: stateCtx.config.channelName,
              microphoneId: stateCtx.config.microphoneId,
              cameraId: stateCtx.config.cameraId,
              resolution: stateCtx.config.resolution
            }).then(() => {
              mutationCtx.setScreen(true)
              console.log('start rtc stream')
            })
          }
          break;
        }
        case 'profile': {
          break;
        }
        default:
          throw new Error(`Unknown click handler, name: ${name}`);
      }
    }
  }

  return (
    <div className="meeting">
      <div className="local-view">
        {localStream ?
          <StreamPlayer stream={localStream}>
            <div className={classes.menuContainer}>
              <div className={classes.menu}>
                <i onClick={handleClick('video')} className={clsx(classes.customBtn, stateCtx.video ? classes.muteVideo : classes.unmuteVideo)}/>
                <i onClick={handleClick('audio')} className={clsx(classes.customBtn, stateCtx.audio ? classes.muteAudio : classes.unmuteAudio)}/>
                <i onClick={handleClick('screen')} className={clsx(classes.customBtn, stateCtx.screen ? classes.startScreenShare : classes.stopScreenShare)}/>
                <i onClick={handleClick('profile')} className={clsx(classes.customBtn, classes.showProfile)}/>
              </div>
            </div>
            <div className="quit" onClick={() => {
              localClient.leave().then(() => {
                routerCtx.history.push('/');
              })
            }}></div>
            <div className="stream-container">
              <div className="avatar-container">
                <div className="default-avatar"></div>
                <div className="avatar-uid">{localStream.getId()}</div>
                <div className="like"></div>
              </div>
              {streamList.filter((stream) => 
                (stream.getId() !== localStream.getId())
              ).map((stream, index) => (
                <StreamPlayer key={index} stream={stream}>
                  <div className='stream-uid'>UID: {stream.getId()}</div>
                </StreamPlayer>
              )).slice(0, 4)}
            </div>
          </StreamPlayer> : null}
      </div>
    </div>
  )
};

// const MeetingPage = Meeting(props);

export default MeetingPage;