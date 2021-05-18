import AgoraRTC from 'agora-rtc-sdk-ng'
import EventEmitter from 'events'
const appID = process.env.REACT_APP_AGORA_APP_ID
console.log(
  'agora sdk version: ' +
  AgoraRTC.VERSION +
  ' compatible: ' +
  AgoraRTC.checkSystemRequirements()
)
export default class RTCClient {
  constructor () {
    this._client = null
    this._joined = false
    this._leave = false
    this.mLocalAudioTrack = null
    this.mLocalVideoTrack = null
    this._params = {}
    this._uid = 0
    this._eventBus = new EventEmitter()
    this._showProfile = false
    this._subscribed = false
    this._created = false
  }

  createClient (data) {
    if (this._client != null) {
      return this._client
    }

    const config = {
      mode: data?.mode ? data.mode : 'live',
      codec: data?.codec ? data.codec : 'vp8'
    }
    console.debug('createClient() mode: ' + config.mode + ' codec: ' + config.codec)
    this._client = AgoraRTC.createClient(config)
    this._created = true
    return this._client
  }

  closeStream () {
    // if (this._localStream.isPlaying()) {
    //   this._localStream.stop()
    // }
    // this._localStream.close()
  }

  destroy () {
    console.debug('destroy()')
    this._created = false
    this._client = null
  }

  on (evt, callback) {
    this._client.on(evt, callback)
  }

  setClientRole (role) {
    console.debug('setClientRole() role: ' + role)
    this._client.setClientRole(role)
  }

  startLive (microphoneId, cameraId) {
    return new Promise((resolve, reject) => {
      console.debug('startLive()')

      if (this.mLocalAudioTrack) {
        this.mLocalAudioTrack.stop()
        this.mLocalAudioTrack.close()
      }

      if (this.mLocalVideoTrack) {
        this.mLocalVideoTrack.stop()
        this.mLocalVideoTrack.close()
      }

      AgoraRTC.createMicrophoneAndCameraTracks({ microphoneId: microphoneId }, { cameraId: cameraId })
        .then((tracks) => {
          this.mLocalAudioTrack = tracks[0]
          this.mLocalVideoTrack = tracks[1]
          this._client.publish([this.mLocalAudioTrack, this.mLocalVideoTrack])

          resolve()
        })
        .catch(e => {
          reject(e)
        })
    })
  }

  stopLive () {
    console.debug('stopLive()')

    this._client.unpublish([this.mLocalAudioTrack, this.mLocalVideoTrack])

    if (this.mLocalAudioTrack) {
      this.mLocalAudioTrack.stop()
      this.mLocalAudioTrack.close()
      this.mLocalAudioTrack = null
    }

    if (this.mLocalVideoTrack) {
      this.mLocalVideoTrack.stop()
      this.mLocalVideoTrack.close()
      this.mLocalVideoTrack = null
    }
  }

  createRTCStream (data) {
    // return new Promise((resolve, reject) => {
    //   this._uid = this._localStream ? this._localStream.getId() : data.uid
    //   if (this._localStream) {
    //     this.unpublish()
    //     this.closeStream()
    //   }
    //   // create rtc stream
    //   const rtcStream = AgoraRTC.createStream({
    //     streamID: this._uid,
    //     audio: true,
    //     video: true,
    //     screen: false,
    //     microphoneId: data.microphoneId,
    //     cameraId: data.cameraId
    //   })

    //   if (data.resolution && data.resolution !== 'default') {
    //     rtcStream.setVideoProfile(data.resolution)
    //   }

    //   // init local stream
    //   rtcStream.init(
    //     () => {
    //       this._localStream = rtcStream
    //       this._eventBus.emit('localStream-added', {
    //         stream: this._localStream
    //       })
    //       if (data.muteVideo === false) {
    //         this._localStream.muteVideo()
    //       }
    //       if (data.muteAudio === false) {
    //         this._localStream.muteAudio()
    //       }
    //       // if (data.beauty === true) {
    //       //   this._localStream.setBeautyEffectOptions(true, {
    //       //     lighteningContrastLevel: 1,
    //       //     lighteningLevel: 0.7,
    //       //     smoothnessLevel: 0.5,
    //       //     rednessLevel: 0.1
    //       //   })
    //       //   this._enableBeauty = true;
    //       // }
    //       resolve()
    //     },
    //     (err) => {
    //       reject(err)
    //       // Toast.error("stream init failed, please open console see more detail");
    //       console.error('init local stream failed ', err)
    //     }
    //   )
    // })
  }

  createScreenSharingStream (data) {
    // return new Promise((resolve, reject) => {
    //   // create screen sharing stream
    //   this._uid = this._localStream ? this._localStream.getId() : data.uid
    //   if (this._localStream) {
    //     this._uid = this._localStream.getId()
    //     this.unpublish()
    //   }
    //   const screenSharingStream = AgoraRTC.createStream({
    //     streamID: this._uid,
    //     audio: true,
    //     video: false,
    //     screen: true,
    //     microphoneId: data.microphoneId,
    //     cameraId: data.cameraId
    //   })

    //   if (data.resolution && data.resolution !== 'default') {
    //     screenSharingStream.setScreenProfile(`${data.resolution}_1`)
    //   }

    //   screenSharingStream.on('stopScreenSharing', (evt) => {
    //     this._eventBus.emit('stopScreenSharing', evt)
    //     this.closeStream()
    //     this.unpublish()
    //   })

    //   // init local stream
    //   screenSharingStream.init(
    //     () => {
    //       this.closeStream()
    //       this._localStream = screenSharingStream

    //       // run callback
    //       this._eventBus.emit('localStream-added', {
    //         stream: this._localStream
    //       })
    //       resolve()
    //     },
    //     (err) => {
    //       this.publish()
    //       reject(err)
    //     }
    //   )
    // })
  }

  subscribe (stream) {
    return new Promise((resolve, reject) => {
      console.debug('subscribe()')

      this._client.subscribe(stream)
        .then(mRemoteTrack => {
          resolve(mRemoteTrack)
        })
    })
  }

  getDevices () {
    return new Promise((resolve, reject) => {
      if (!this._client) {
        this.createClient()
      }

      console.debug('getDevices()')

      if (this.mLocalAudioTrack) {
        this.mLocalAudioTrack.stop()
        this.mLocalAudioTrack.close()
      }

      if (this.mLocalVideoTrack) {
        this.mLocalVideoTrack.stop()
        this.mLocalVideoTrack.close()
      }

      AgoraRTC.createMicrophoneAndCameraTracks()
        .then((tracks) => {
          this.mLocalAudioTrack = tracks[0]
          this.mLocalVideoTrack = tracks[1]

          AgoraRTC.getDevices().then(it => {
            resolve(it)

            if (this.mLocalAudioTrack) {
              this.mLocalAudioTrack.stop()
              this.mLocalAudioTrack.close()
            }

            if (this.mLocalVideoTrack) {
              this.mLocalVideoTrack.stop()
              this.mLocalVideoTrack.close()
            }
          })
        })
        .catch(e => {
          reject(e)
        })
    })
  }

  setStreamFallbackOption (stream, type) {
    // this._client.setStreamFallbackOption(stream, type)
  }

  enableDualStream () {
    // return new Promise((resolve, reject) => {
    //   this._client.enableDualStream(resolve, reject)
    // })
  }

  setRemoteVideoStreamType (stream, streamType) {
    // this._client.setRemoteVideoStreamType(stream, streamType)
  }

  join (data) {
    return new Promise((resolve, reject) => {
      if (this._joined == true) {
        resolve(data.uid)
        return
      }

      this._joined = true
      this._leave = false
      this._uid = 0
      console.debug('join appID: ' + appID + ',channel: ' + data.channel)

      this._params = data
      this._client.join(appID, data.channel, data.token ? data.token : null).then(uid => {
        this._uid = uid

        console.debug(
          'join success, channel: ' + data.channel + ', uid: ' + uid
        )
        this._joined = true

        data.uid = uid
        resolve(data.uid)
      })
        .catch(e => {
          this._joined = false
          reject(e)
          console.error('join error: ' + e)
        })
    })
  }

  leave () {
    return new Promise((resolve) => {
      if (this._leave == true) {
        resolve()
        return
      }

      console.debug('leave()')
      this._leave = true
      if (!this._client) return resolve()

      this._client.leave()
        .then(() => {
          console.debug('leave() success')
          this._joined = false
          this._uid = null
          resolve()
        })
        .catch((e) => {
          console.error('leave failed: ' + e)
        })
    })
  }
}
