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
    this._uid = 0
    this._eventBus = new EventEmitter()
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

    if (this.mLocalAudioTrack) {
      this._client.unpublish(this.mLocalAudioTrack)

      this.mLocalAudioTrack.stop()
      this.mLocalAudioTrack.close()
      this.mLocalAudioTrack = null
    }

    if (this.mLocalVideoTrack) {
      this._client.unpublish(this.mLocalVideoTrack)

      this.mLocalVideoTrack.stop()
      this.mLocalVideoTrack.close()
      this.mLocalVideoTrack = null
    }
  }

  async startShareScrren () {
    [this.mLocalAudioTrack, this.mLocalVideoTrack] = await Promise.all([
      AgoraRTC.createMicrophoneAudioTrack(),
      AgoraRTC.createScreenVideoTrack()
    ])

    if (this.mLocalAudioTrack) {
      this._client.publish(this.mLocalAudioTrack)
    }

    if (this.mLocalVideoTrack) {
      this._client.publish(this.mLocalVideoTrack)
    }
  }

  stopShareScrren () {
    console.debug('stopShareScrren()')

    if (this.mLocalAudioTrack) {
      this._client.unpublish(this.mLocalAudioTrack)

      this.mLocalAudioTrack.stop()
      this.mLocalAudioTrack.close()
      this.mLocalAudioTrack = null
    }

    if (this.mLocalVideoTrack) {
      this._client.unpublish(this.mLocalVideoTrack)

      this.mLocalVideoTrack.stop()
      this.mLocalVideoTrack.close()
      this.mLocalVideoTrack = null
    }
  }

  subscribe (user, mediaType) {
    return new Promise((resolve, reject) => {
      this._client.subscribe(user, mediaType)
        .then(mRemoteTrack => {
          console.debug(`subscribe success user=${user.uid}, mediaType=${mediaType}`)
          resolve(mRemoteTrack)
        })
        .catch(e => {
          console.debug(`subscribe error user=${user.uid}, mediaType=${mediaType}`)
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
        this.mLocalAudioTrack = null
      }

      if (this.mLocalVideoTrack) {
        this.mLocalVideoTrack.stop()
        this.mLocalVideoTrack.close()
        this.mLocalVideoTrack = null
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
              this.mLocalAudioTrack = null
            }

            if (this.mLocalVideoTrack) {
              this.mLocalVideoTrack.stop()
              this.mLocalVideoTrack.close()
              this.mLocalVideoTrack = null
            }
          })
        })
        .catch(e => {
          reject(e)
        })
    })
  }

  join (channel, token) {
    return new Promise((resolve, reject) => {
      if (this._joined == true) {
        resolve(this._uid)
        return
      }

      this._joined = true
      this._leave = false
      this._uid = 0
      console.debug('join appID: ' + appID + ',channel: ' + channel)

      this._client.join(appID, channel, token || null).then(uid => {
        console.debug(
          'join success, channel: ' + channel + ', uid: ' + uid
        )

        this._uid = uid
        this._joined = true

        resolve(uid)
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
