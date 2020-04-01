const readDefaultState = () => {
  try {
    return JSON.parse(window.sessionStorage.getItem('custom_storage'))
  } catch (err) {
    return {}
  }
}

const defaultState = {
  // loading effect
  loading: false,
  // media devices
  streams: [],
  localStream: null,
  currentStream: null,
  otherStreams: [],
  devicesList: [],
  // web sdk params
  config: {
    uid: 0,
    host: true,
    channelName: '',
    token: process.env.REACT_APP_AGORA_APP_TOKEN,
    resolution: '480p',
    ...readDefaultState(),
    microphoneId: '',
    cameraId: ''
  },
  agoraClient: null,
  mode: 'live',
  codec: 'vp8',
  muteVideo: true,
  muteAudio: true,
  screen: false,
  profile: false
  // beauty: false
}

const reducer = (state, action) => {
  switch (action.type) {
    case 'config': {
      return { ...state, config: action.payload }
    }
    case 'client': {
      return { ...state, client: action.payload }
    }
    case 'loading': {
      return { ...state, loading: action.payload }
    }
    case 'codec': {
      return { ...state, codec: action.payload }
    }
    case 'video': {
      return { ...state, muteVideo: action.payload }
    }
    case 'audio': {
      return { ...state, muteAudio: action.payload }
    }
    case 'screen': {
      return { ...state, screen: action.payload }
    }
    case 'devicesList': {
      return { ...state, devicesList: action.payload }
    }
    case 'localStream': {
      return { ...state, localStream: action.payload }
    }
    case 'profile': {
      return { ...state, profile: action.payload }
    }
    case 'currentStream': {
      const { streams } = state
      const newCurrentStream = action.payload
      const otherStreams = streams.filter(
        (it) => it.getId() !== newCurrentStream.getId()
      )
      return { ...state, currentStream: newCurrentStream, otherStreams }
    }
    case 'addStream': {
      const { streams, currentStream } = state
      const newStream = action.payload
      let newCurrentStream = currentStream
      if (!newCurrentStream) {
        newCurrentStream = newStream
      }
      if (streams.length === 4) return { ...state }
      const newStreams = [...streams, newStream]
      const otherStreams = newStreams.filter(
        (it) => it.getId() !== newCurrentStream.getId()
      )
      return {
        ...state,
        streams: newStreams,
        currentStream: newCurrentStream,
        otherStreams
      }
    }
    case 'removeStream': {
      const { streams, currentStream } = state
      const { stream, uid } = action
      const targetId = stream ? stream.getId() : uid
      let newCurrentStream = currentStream
      const newStreams = streams.filter(
        (stream) => stream.getId() !== targetId
      )
      if (targetId === currentStream.getId()) {
        if (newStreams.length === 0) {
          newCurrentStream = null
        } else {
          newCurrentStream = newStreams[0]
        }
      }
      const otherStreams = newCurrentStream
        ? newStreams.filter((it) => it.getId() !== newCurrentStream.getId())
        : []
      return {
        ...state,
        streams: newStreams,
        currentStream: newCurrentStream,
        otherStreams
      }
    }
    case 'clearAllStream': {
      // const {streams, localStream, currentStream, beauty} = state;
      const { streams, localStream, currentStream } = state
      streams.forEach((stream) => {
        if (stream.isPlaying()) {
          stream.stop()
        }
        // stream.close();
      })

      if (localStream) {
        localStream.isPlaying() && localStream.stop()
        localStream.close()
      }
      if (currentStream) {
        currentStream.isPlaying() && currentStream.stop()
        currentStream.close()
      }
      return { ...state, currentStream: null, localStream: null, streams: [] }
    }
    // case 'enableBeauty': {
    //   return {
    //     ...state,
    //     beauty: action.enable
    //   }
    // }
    default:
      throw new Error('mutation type not defined')
  }
}

export { reducer, defaultState }
