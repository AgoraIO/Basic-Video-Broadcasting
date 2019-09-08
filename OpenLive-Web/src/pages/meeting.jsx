import React, {useEffect, useMemo, useState} from 'react';
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
    backgroundColor: 'rgba(0, 0, 0, 0.4)',
    backgroundSize: '50px',
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
  menuContainer: {
    width: '100%',
    height: '100%',
    position: 'absolute',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'flex-end',
    zIndex: '2',
  },
});

const MeetingPage = () => {
  const classes = useStyles();

  const routerCtx = userRouter();
  const stateCtx = useGlobalState();
  const mutationCtx = useGlobalMutation();

  const localClient = useMemo(() => {
    const client = new RTCClient();
    if (!client._created) {
      client.createClient({codec: stateCtx.codec, mode: stateCtx.mode});
      client._created = true;
    }
    return client;
  }, [stateCtx.codec, stateCtx.mode]);

  const [localStream, currentStream, streamList] = useStream(localClient);

  const config = useMemo(() => {
    return {
      token: null,
      channel: stateCtx.config.channelName,
      microphoneId: stateCtx.config.microphoneId,
      cameraId: stateCtx.config.cameraId,
      resolution: stateCtx.config.resolution,
      muteVideo: stateCtx.muteVideo,
      muteAudio: stateCtx.muteAudio,
      uid: 0,
      host: stateCtx.config.host
    }
  }, [stateCtx]);

  const history = routerCtx.history;

  useEffect(() => {
    console.log("meeting-page >>>> ");
    if (!config.channel) {
      history.push('/');
    }
  }, [config.channel, history]);

  useEffect(() => {
    if (config.channel && localClient._created && localClient._joined === false) {
      localClient.join(config).then(() => {
        config.host ? 
          localClient.setClientRole('host') :
          localClient.setClientRole('audience')
        if (config.host) {
          localClient.publish();
        }
        mutationCtx.stopLoading();
      }).catch((err) => {
        mutationCtx.toastError(`Media ${err.info}`);
        routerCtx.history.push('/');
      })
    }
  }, [localClient, mutationCtx, config, routerCtx]);

  const handleClick = (name) => {
    return (evt) => {
      switch (name) {
        case 'video': {
          stateCtx.muteVideo ? localStream.muteVideo() : localStream.unmuteVideo();
          mutationCtx.setVideo(!stateCtx.muteVideo);
          break;
        }
        case 'audio': {
          stateCtx.muteAudio ? localStream.muteAudio() : localStream.unmuteAudio();
          mutationCtx.setAudio(!stateCtx.muteAudio);
          break;
        }
        case 'screen': {
          if (stateCtx.screen) {
            localClient.createRTCStream({
              token: null,
              channel: stateCtx.config.channelName,
              microphoneId: stateCtx.config.microphoneId,
              resolution: stateCtx.config.resolution,
              video: stateCtx.video,
              audio: stateCtx.audio,
            }).then(() => {
              localClient.publish();
              mutationCtx.setScreen(false)
            }).catch((err) => {
              console.log(err)
              mutationCtx.toastError(`Media ${err.info}`);
              routerCtx.history.push('/');
            });
          } else {
            localClient.createScreenSharingStream({
              token: null,
              channel: stateCtx.config.channelName,
              microphoneId: stateCtx.config.microphoneId,
              cameraId: stateCtx.config.cameraId,
              resolution: stateCtx.config.resolution
            }).then(() => {
              localClient.publish();
              mutationCtx.setScreen(true)
            }).catch((err) => {
              console.log(err)
              mutationCtx.toastError(`Media ${err.info}`);
              routerCtx.history.push('/');
            });
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

  // const [otherStreams, updateOtherStreams] = useState

  // const otherStreams = stateCtx.streams
        // .filter((stream) => (stream.getId() !== currentStream.getId()));

  const handleDoubleClick = (evt) => {
    console.log("handle double click >>>>>")
    evt.stopPropagation();
    const index = streamList.findIndex((stream) => stream === currentStream);
    const targetIndex = (index+1) % streamList.length;
    mutationCtx.setCurrentStream(streamList[targetIndex]);
  }

  return (
    <div className="meeting">
      cur: {currentStream && currentStream.getId()}
      streams: {stateCtx.otherStreams.length && stateCtx.otherStreams.map(it => it.getId())}
      <div className="local-view">
        {stateCtx.currentStream ?
          <StreamPlayer stream={stateCtx.currentStream} onDoubleClick={handleDoubleClick}>
            <div className={classes.menuContainer}>
              {config.host && <div className={classes.menu}>
                  <i onClick={handleClick('video')} className={clsx(classes.customBtn, 'margin-right-19', stateCtx.muteVideo ? 'mute-video' : 'unmute-video')}/>
                  <i onClick={handleClick('audio')} className={clsx(classes.customBtn, stateCtx.muteAudio ? 'mute-audio' : 'unmute-audio')}/>
                {/* <i onClick={handleClick('screen')} className={clsx(classes.customBtn, stateCtx.screen ? 'start-screen-share' : 'stop-screen-share)}/> */}
                {/* <i onClick={handleClick('profile')} className={clsx(classes.customBtn, 'show-profile')}/> */}
              </div>}
            </div>
            <div className="nav">
              <div className="avatar-container">
                <div className="default-avatar"></div>
                <div className="avatar-text">Agora Test</div>
                <div className="like"></div>
              </div>
              <div className="quit" onClick={() => {
                localClient.leave().then(() => {
                  mutationCtx.clearAllStream();
                  routerCtx.history.push('/');
                });
              }}></div>
            </div>
            <div className="stream-container">
              {/* {stateCtx.streams
                .filter((stream) => (stream.getId() !== currentStream.getId()))
                .map((stream, index) => (
                <StreamPlayer key={index} stream={stream} onDoubleClick={handleDoubleClick}>
                  <div className='stream-uid'>UID: {stream.getId()}</div>
                </StreamPlayer>
              ))} */}
              {stateCtx.otherStreams.map((stream, index) => (
                <StreamPlayer key={index} stream={stream} uid={stream.getId()} onDoubleClick={handleDoubleClick}>
                </StreamPlayer>
              ))}
            </div>
          </StreamPlayer> : null}
      </div>
    </div>
  )
};

// const MeetingPage = Meeting(props);

export default MeetingPage;