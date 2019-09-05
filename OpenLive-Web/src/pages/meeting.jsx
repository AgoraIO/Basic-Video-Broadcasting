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

  const [localStream, streamList] = useStream(localClient);

  const config = useMemo(() => {
    return {
      token: null,
      channel: stateCtx.config.channelName,
      microphoneId: stateCtx.config.microphoneId,
      cameraId: stateCtx.config.cameraId,
      resolution: stateCtx.config.resolution,
      video: stateCtx.video,
      audio: stateCtx.audio,
      uid: 0,
    }
  }, [stateCtx]);

  const history = routerCtx.history;

  useEffect(() => {
    if (!config.channel) {
      history.push('/');
    }
  }, [config.channel, history]);

  useEffect(() => {
    if (config.channel && localClient._created && localClient._joined === false) {
      localClient.join(config).then(() => {
        localClient.publish();
        mutationCtx.stopLoading();
      });
    }
  }, [localClient, mutationCtx, config]);

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
              resolution: stateCtx.config.resolution,
              video: stateCtx.video,
              audio: stateCtx.audio,
            }).then(() => {
              localClient.publish();
              mutationCtx.setScreen(false)
            })
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
                <i onClick={handleClick('video')} className={clsx(classes.customBtn, stateCtx.video ? 'mute-video' : 'unmute-video')}/>
                <i onClick={handleClick('audio')} className={clsx(classes.customBtn, stateCtx.audio ? 'mute-audio' : 'unmute-audio')}/>
                {/* <i onClick={handleClick('screen')} className={clsx(classes.customBtn, stateCtx.screen ? 'start-screen-share' : 'stop-screen-share)}/> */}
                {/* <i onClick={handleClick('profile')} className={clsx(classes.customBtn, 'show-profile')}/> */}
              </div>
            </div>
            <div className="nav">
              <div className="avatar-container">
                <div className="default-avatar"></div>
                <div className="avatar-uid">{localStream.getId()}</div>
                <div className="like"></div>
              </div>
              <div className="quit" onClick={() => {
                localClient.leave().then(() => {
                  mutationCtx.resetStreamList();
                  routerCtx.history.push('/');
                })
              }}></div>
            </div>
            <div className="stream-container">
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