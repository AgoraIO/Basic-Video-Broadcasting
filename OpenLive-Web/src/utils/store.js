const defaultState = {
  // loading effect
  loading: false,
  // media devices
  streams: [],
  localStream: null,
  devicesList: [],
  // web sdk property
  config: {
    uid: 0,
    channelName: '',
    token: null,
    microphoneId: '',
    cameraId: '',
    resolution: '360p'
  },
  agoraClient: null,
  mode: 'live',
  codec: 'h264',
  video: true,
  audio: true,
  screen: false
};

const mutations = (state, action) => {
  switch (action.type) {
    case 'config': {
      return {...state, config: action.payload};
    }
    case 'client': {
      return { ...state, client: action.payload };
    }
    case 'loading': {
      return { ...state, loading: action.payload };
    }
    case 'codec': {
      return { ...state, codec: action.payload };
    }
    case 'video': {
      return { ...state, video: action.payload };
    }
    case 'audio': {
      return { ...state, audio: action.payload };
    }
    case 'screen': {
      return { ...state, screen: action.payload };
    }
    case 'localStream': {
      return { ...state, localStream: action.payload };
    }
    case 'streamList': {
      return { ...state, streams: action.payload };
    }
    case 'devicesList': {
      return {...state, devicesList: action.payload};
    }
    default:
      throw new Error("mutation type not defined")
  }
};

export {
  mutations,
  defaultState,
};
