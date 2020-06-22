#ifndef AGORA_MEDIA_ENGINE_H
#define AGORA_MEDIA_ENGINE_H
#if defined _WIN32 || defined __CYGWIN__
typedef __int64 int64_t;
typedef unsigned __int64 uint64_t;
typedef __int32 int32_t;
typedef unsigned __int32 uint32_t;
#else
#include <stdint.h>
#endif

namespace agora {
namespace media {
/** **DEPRECATED** Type of audio device.
 */
enum MEDIA_SOURCE_TYPE {
  /** Audio playback device.
   */
  AUDIO_PLAYOUT_SOURCE = 0,
  /** Microphone.
   */
  AUDIO_RECORDING_SOURCE = 1,
};

class IAudioFrameObserver {
 public:
  enum AUDIO_FRAME_TYPE {
    FRAME_TYPE_PCM16 = 0,  // PCM 16bit little endian
  };
  /** Definition of AudioFrame */
  struct AudioFrame {
    AUDIO_FRAME_TYPE type;
    /** Number of samples in the audio frame: samples = (int)samplesPerCall = (int)(sampleRate &times; sampleInterval)
     */
    int samples;  //number of samples in this frame
    /** Number of bytes per audio sample. For example, each PCM audio sample usually takes up 16 bits (2 bytes).
     */
    int bytesPerSample;  //number of bytes per sample: 2 for PCM16
    /** Number of audio channels.
     - 1: Mono
     - 2: Stereo (the data is interleaved)
     */
    int channels;  //number of channels (data are interleaved if stereo)
    /** Audio frame sample rate: 8000, 16000, 32000, 44100, or 48000 Hz.
     * samplesPerCall = (int)(samplesPerSec &times; sampleInterval &times; numChannels), where sampleInterval &ge; 0.01 in seconds.
     */
    int samplesPerSec;  //sampling rate
    /** Audio frame data buffer. The valid data length is: samples &times; channels &times; bytesPerSample
     */
    void* buffer;  //data buffer
      /** The timestamp of the external audio frame. It is mandatory. You can use this parameter for the following purposes:
       - Restore the order of the captured audio frame.
       - Synchronize audio and video frames in video-related scenarios, including scenarios where external video sources are used.
       */
    int64_t renderTimeMs;
    int avsync_type;
  };

 public:
  /** Retrieves the recorded audio frame.

  The SDK triggers this callback once every 10 ms.

   @param audioFrame Pointer to AudioFrame.
   @return
   - true: Valid buffer in AudioFrame, and the recorded audio frame is sent out.
   - false: Invalid buffer in AudioFrame, and the recorded audio frame is discarded.
   */
  virtual bool onRecordAudioFrame(AudioFrame& audioFrame) = 0;
  /** Retrieves the audio playback frame every 10 ms for getting the audio.

   @param audioFrame Pointer to AudioFrame.
   @return
   - true: Valid buffer in AudioFrame, and the audio playback frame is sent out.
   - false: Invalid buffer in AudioFrame, and the audio playback frame is discarded.
   */
  virtual bool onPlaybackAudioFrame(AudioFrame& audioFrame) = 0;
  /** Retrieves the mixed recorded and playback audio frame.

  The SDK triggers this callback once every 10 ms.

   @note This callback only returns the single-channel data.

   @param audioFrame Pointer to AudioFrame.
   @return
   - true: Valid buffer in AudioFrame and the mixed recorded and playback audio frame is sent out.
   - false: Invalid buffer in AudioFrame and the mixed recorded and playback audio frame is discarded.
   */
  virtual bool onMixedAudioFrame(AudioFrame& audioFrame) = 0;
  /** Retrieves the audio frame of a specified user before mixing.

  The SDK triggers this callback once every 10 ms.

  @param uid The user ID
  @param audioFrame Pointer to AudioFrame.
  @return
  - true: Valid buffer in AudioFrame, and the mixed recorded and playback audio frame is sent out.
  - false: Invalid buffer in AudioFrame, and the mixed recorded and playback audio frame is discarded.
  */
  virtual bool onPlaybackAudioFrameBeforeMixing(unsigned int uid,
      AudioFrame& audioFrame) = 0;
  
  /** Determines whether to receive audio data from multiple channels.
   
   After you register the audio frame observer, the SDK triggers this callback every time it captures an audio frame.

   In the multi-channel scenario, if you want to get audio data from multiple channels,
   set the return value of this callback as true. After that, the SDK triggers the
   \ref IAudioFrameObserver::onPlaybackAudioFrameBeforeMixingEx "onPlaybackAudioFrameBeforeMixingEx" callback to send you the before-mixing
   audio data from various channels. You can also get the channel ID of each audio frame.
   
   @note
   - Once you set the return value of this callback as true, the SDK triggers
   only the \ref IAudioFrameObserver::onPlaybackAudioFrameBeforeMixingEx "onPlaybackAudioFrameBeforeMixingEx" callback
   to send the before-mixing audio frame. \ref IAudioFrameObserver::onPlaybackAudioFrameBeforeMixing "onPlaybackAudioFrameBeforeMixing" is not triggered.
   In the multi-channel scenario, Agora recommends setting the return value as true.
   - If you set the return value of this callback as false, the SDK triggers only the `onPlaybackAudioFrameBeforeMixing` callback to send the audio data.
   @return
   - `true`: Receive audio data from multiple channels.
   - `false`: Do not receive audio data from multiple channels.
   */
  virtual bool isMultipleChannelFrameWanted() { return false; }
  
  /** Gets the before-mixing playback audio frame from multiple channels.

  After you successfully register the audio frame observer, if you set the return
  value of isMultipleChannelFrameWanted as true, the SDK triggers this callback each
  time it receives a before-mixing audio frame from any of the channel.

  @param channelId The channel ID of this audio frame.
  @param uid The ID of the user sending this audio frame.
  @param audioFrame The pointer to AudioFrame.
  @return
  - `true`: The data in AudioFrame is valid, and send this audio frame.
  - `false`: The data in AudioFrame in invalid, and do not send this audio frame.
  */
  virtual bool onPlaybackAudioFrameBeforeMixingEx(const char *channelId,
      unsigned int uid, AudioFrame& audioFrame) { return true; }
};

class IVideoFrameObserver {
 public:
  enum VIDEO_FRAME_TYPE {
    FRAME_TYPE_YUV420 = 0,  // YUV 420 format
    FRAME_TYPE_RGBA = 2,    // RGBA format
  };
  enum VIDEO_OBSERVER_POSITION {
    POSITION_POST_CAPTURER = 1 << 0,
    POSITION_PRE_RENDERER = 1 << 1,
    POSITION_PRE_ENCODER = 1 << 2,
  };
  /** Video frame information. The video data format is YUV420. The buffer provides a pointer to a pointer. The interface cannot modify the pointer of the buffer, but can modify the content of the buffer only.
   */
  struct VideoFrame {
    VIDEO_FRAME_TYPE type;
    /** Video pixel width.
     */
    int width;  //width of video frame
    /** Video pixel height.
     */
    int height;  //height of video frame
    /** Line span of the Y buffer within the YUV data.
     */
    int yStride;  //stride of Y data buffer
    /** Line span of the U buffer within the YUV data.
     */
    int uStride;  //stride of U data buffer
    /** Line span of the V buffer within the YUV data.
     */
    int vStride;  //stride of V data buffer
    /** Pointer to the Y buffer pointer within the YUV data.
     */
    void* yBuffer;  //Y data buffer
    /** Pointer to the U buffer pointer within the YUV data.
     */
    void* uBuffer;  //U data buffer
    /** Pointer to the V buffer pointer within the YUV data.
     */
    void* vBuffer;  //V data buffer
    /** Set the rotation of this frame before rendering the video. Supports 0, 90, 180, 270 degrees clockwise.
     */
    int rotation; // rotation of this frame (0, 90, 180, 270)
      /** The timestamp of the external audio frame. It is mandatory. You can use this parameter for the following purposes:
       - Restore the order of the captured audio frame.
       - Synchronize audio and video frames in video-related scenarios, including scenarios where external video sources are used.
     @note This timestamp is for rendering the video stream, and not for capturing the video stream.
     */
    int64_t renderTimeMs;
    int avsync_type;
  };

 public:
  /** Retrieves the camera captured image.

   @param videoFrame VideoFrame
   */
  virtual bool onCaptureVideoFrame(VideoFrame& videoFrame) = 0;
  /** Retrieves the video data to be encoded.

   @param videoFrame VideoFrame
   */
  virtual bool onPreEncodeVideoFrame(VideoFrame& videoFrame) { return true; }
  /** Processes the received image of the specified user (post-processing).

   @param uid User ID of the specified user sending the image.
   @param videoFrame VideoFrame
   */
  virtual bool onRenderVideoFrame(unsigned int uid, VideoFrame& videoFrame) = 0;
  /** Occurs each time the SDK receives a video frame and prompts you whether or not to rotate the captured video according to the rotation member in the VideoFrame class. 
   *
   * The SDK does not rotate the captured video by default. If you want to rotate the captured video according to the rotation member in the VideoFrame class, register this callback in the IVideoFrameObserver class.
   *
   * After you successfully register the video frame observer, the SDK triggers this callback each time it receives a video frame. You need to set whether or not to rotate the video frame in the return value of this callback.
   *
   * @return Sets whether or not to rotate the captured video:
   * - true: Rotate.
   * - false: （Default) Do not rotate.
   */
  virtual bool getRotationApplied() { return false; }
  /** Prompts the SDK which position you want to observe. 
   *
   * The SDK provides 3 positions for observer. Each position corresponds to a callback function:
   * - POSITION_POST_CAPTURER: onCaptureVideoFrame
   * - POSITION_PRE_RENDERER: onRenderVideoFrame
   * - POSITION_PRE_ENCODER: onPreEncodeVideoFrame
   *
   * You can observe more than one position by using '|' operator.
   * 
   * After you successfully register the video frame observer, the SDK triggers this callback each time it receives a video frame. You have to set which position you need to observe.
   *
   * @return Bit code of video frame positions to be observed.
   * 
   */
  virtual uint32_t getObservedFramePosition() { return POSITION_POST_CAPTURER | POSITION_PRE_RENDERER; }
  
  /** Determines whether to receive video data from multiple channels.

   After you register the video frame observer, the SDK triggers this callback
   every time it captures a video frame.

   In the multi-channel scenario, if you want to get video data from multiple channels,
   set the return value of this callback as true. After that, the SDK triggers the
   onRenderVideoFrameEx callback to send you
   the video data from various channels. You can also get the channel ID of each video frame.

   @note
   - Once you set the return value of this callback as true, the SDK triggers only the `onRenderVideoFrameEx` callback to
   send the video frame. onRenderVideoFrame will not be triggered. In the multi-channel scenario, Agora recommends setting the return value as true.
   - If you set the return value of this callback as false, the SDK triggers only the `onRenderVideoFrame` callback to send the video data.
   @return
   - `true`: Receive video data from multiple channels.
   - `false`: Do not receive video data from multiple channels.
   */
  virtual bool isMultipleChannelFrameWanted() { return false; }

  /** Gets the video frame from multiple channels.
   
   After you successfully register the video frame observer, if you set the return value of
   isMultipleChannelFrameWanted as true, the SDK triggers this callback each time it receives a video frame
   from any of the channel.

   You can process the video data retrieved from this callback according to your scenario, and send the
   processed data back to the SDK using the `videoFrame` parameter in this callback.

   @note This callback does not support sending RGBA video data back to the SDK.

   @param channelId The channel ID of this video frame.
   @param uid The ID of the user sending this video frame.
   @param videoFrame The pointer to VideoFrame.
   @return Whether to send this video frame to the SDK if post-processing fails:
   - `true`: Send this video frame.
   - `false`: Do not send this video frame.
   */
  virtual bool onRenderVideoFrameEx(const char *channelId, unsigned int uid, VideoFrame& videoFrame) { return true; }

  /** Occurs each time the SDK receives a video frame and prompts you to set the video format. 
   *
   * YUV420 is the default video format. If you want to receive other video formats, register this callback in the IVideoFrameObserver class.
   *
   * After you successfully register the video frame observer, the SDK triggers this callback each time it receives a video frame. 
   * You need to set your preferred video data in the return value of this callback.
   *
   * @return Sets the video format: #VIDEO_FRAME_TYPE
   * - #FRAME_TYPE_YUV420 (0): (Default) YUV420.
   * - #FRAME_TYPE_RGBA (2): RGBA
   */
  virtual VIDEO_FRAME_TYPE getVideoFormatPreference() { return FRAME_TYPE_YUV420; }
};

class IVideoFrame {
 public:
  enum PLANE_TYPE { Y_PLANE = 0, U_PLANE = 1, V_PLANE = 2, NUM_OF_PLANES = 3 };
  enum VIDEO_TYPE {
    VIDEO_TYPE_UNKNOWN = 0,
    VIDEO_TYPE_I420 = 1,
    VIDEO_TYPE_IYUV = 2,
    VIDEO_TYPE_RGB24 = 3,
    VIDEO_TYPE_ABGR = 4,
    VIDEO_TYPE_ARGB = 5,
    VIDEO_TYPE_ARGB4444 = 6,
    VIDEO_TYPE_RGB565 = 7,
    VIDEO_TYPE_ARGB1555 = 8,
    VIDEO_TYPE_YUY2 = 9,
    VIDEO_TYPE_YV12 = 10,
    VIDEO_TYPE_UYVY = 11,
    VIDEO_TYPE_MJPG = 12,
    VIDEO_TYPE_NV21 = 13,
    VIDEO_TYPE_NV12 = 14,
    VIDEO_TYPE_BGRA = 15,
    VIDEO_TYPE_RGBA = 16,
  };
  virtual void release() = 0;
  virtual const unsigned char* buffer(PLANE_TYPE type) const = 0;

  /** Copies the frame.

   If the required size is larger than the allocated size, new buffers of the adequate size will be allocated.

   @param dest_frame Address of the returned destination frame. See IVideoFrame.
   @return
   - 0: Success.
   - -1: Failure.
   */
  virtual int copyFrame(IVideoFrame** dest_frame) const = 0;
  /** Converts the frame.

   @note The source and destination frames have equal heights.

   @param dst_video_type Type of the output video.
   @param dst_sample_size Required only for the parsing of M-JPEG.
   @param dst_frame Pointer to a destination frame. See IVideoFrame.
   @return
   - 0: Success.
   - < 0: Failure.
   */
  virtual int convertFrame(VIDEO_TYPE dst_video_type, int dst_sample_size,
                           unsigned char* dst_frame) const = 0;
  /** Retrieves the specified component in the YUV space.

   @param type Component type: #PLANE_TYPE
   */
  virtual int allocated_size(PLANE_TYPE type) const = 0;
  /** Retrieves the stride of the specified component in the YUV space.

   @param type Component type: #PLANE_TYPE
   */
  virtual int stride(PLANE_TYPE type) const = 0;
  /** Retrieves the width of the frame.
   */
  virtual int width() const = 0;
  /** Retrieves the height of the frame.
   */
  virtual int height() const = 0;
  /** Retrieves the timestamp (90 ms) of the frame.
   */
  virtual unsigned int timestamp() const = 0;
  /** Retrieves the render time (ms).
   */
  virtual int64_t render_time_ms() const = 0;
  /** Checks if a plane is of zero size.

   @return
   - true: The plane is of zero size.
   - false: The plane is not of zero size.
   */
  virtual bool IsZeroSize() const = 0;
};
/** **DEPRECATED** */
class IExternalVideoRenderCallback {
 public:
  /** Occurs when the video view size has changed.
  */
  virtual void onViewSizeChanged(int width, int height) = 0;
  /** Occurs when the video view is destroyed.
  */
  virtual void onViewDestroyed() = 0;
};
/** **DEPRECATED** */
struct ExternalVideoRenerContext {
  IExternalVideoRenderCallback* renderCallback;
  /** Video display window.
   */
  void* view;
  /** Video display mode: \ref agora::rtc::RENDER_MODE_TYPE "RENDER_MODE_TYPE" */
  int renderMode;
  /** The image layer location.

   - 0: (Default) The image is at the bottom of the stack
   - 100: The image is at the top of the stack.

   @note If the value is set to below 0 or above 100, the #ERR_INVALID_ARGUMENT error occurs.
   */
  int zOrder;
  /** Video layout distance from the left.
   */
  float left;
  /** Video layout distance from the top.
   */
  float top;
  /** Video layout distance from the right.
   */
  float right;
  /** Video layout distance from the bottom.
   */
  float bottom;
};

class IExternalVideoRender {
 public:
  virtual void release() = 0;
  virtual int initialize() = 0;
  virtual int deliverFrame(const IVideoFrame& videoFrame, int rotation,
                           bool mirrored) = 0;
};

class IExternalVideoRenderFactory {
 public:
  virtual IExternalVideoRender* createRenderInstance(
      const ExternalVideoRenerContext& context) = 0;
};

/** The external video frame.
 */
struct ExternalVideoFrame
{
    /** The video buffer type.
     */
    enum VIDEO_BUFFER_TYPE
    {
        /** 1: The video buffer in the format of raw data.
         */
        VIDEO_BUFFER_RAW_DATA = 1,
    };

    /** The video pixel format.
     */
    enum VIDEO_PIXEL_FORMAT
    {
        /** 0: The video pixel format is unknown.
         */
        VIDEO_PIXEL_UNKNOWN = 0,
        /** 1: The video pixel format is I420.
         */
        VIDEO_PIXEL_I420 = 1,
        /** 2: The video pixel format is BGRA.
         */
        VIDEO_PIXEL_BGRA = 2,
        /** 8: The video pixel format is NV12.
         */
        VIDEO_PIXEL_NV12 = 8,
    };

    /** The buffer type. See #VIDEO_BUFFER_TYPE
     */
    VIDEO_BUFFER_TYPE type;
    /** The pixel format. See #VIDEO_PIXEL_FORMAT
     */
    VIDEO_PIXEL_FORMAT format;
    /** The video buffer.
     */
    void* buffer;
    /** Line spacing of the incoming video frame, which must be in pixels instead of bytes. For textures, it is the width of the texture.
     */
    int stride;
    /** Height of the incoming video frame.
     */
    int height;
    /** [Raw data related parameter] The number of pixels trimmed from the left. The default value is 0.
     */
    int cropLeft;
    /** [Raw data related parameter] The number of pixels trimmed from the top. The default value is 0.
     */
    int cropTop;
    /** [Raw data related parameter] The number of pixels trimmed from the right. The default value is 0.
     */
    int cropRight;
    /** [Raw data related parameter] The number of pixels trimmed from the bottom. The default value is 0.
     */
    int cropBottom;
    /** [Raw data related parameter] The clockwise rotation of the video frame. You can set the rotation angle as 0, 90, 180, or 270. The default value is 0.
     */
    int rotation;
    /** Timestamp of the incoming video frame (ms). An incorrect timestamp results in frame loss or unsynchronized audio and video.
     */
    long long timestamp;
};

class IMediaEngine {
 public:
  virtual void release() = 0;
  /** Registers an audio frame observer object.

   This method is used to register an audio frame observer object (register a callback). This method is required to register callbacks when the engine is required to provide an \ref IAudioFrameObserver::onRecordAudioFrame "onRecordAudioFrame" or \ref IAudioFrameObserver::onPlaybackAudioFrame "onPlaybackAudioFrame" callback.

   @param observer Audio frame observer object instance. If NULL is passed in, the registration is canceled.
   @return
   - 0: Success.
   - < 0: Failure.
   */
  virtual int registerAudioFrameObserver(IAudioFrameObserver* observer) = 0;
  /** Registers a video frame observer object.

   This method is required to register callbacks when the engine is required to provide an \ref IVideoFrameObserver::onCaptureVideoFrame "onCaptureVideoFrame" or \ref IVideoFrameObserver::onRenderVideoFrame "onRenderVideoFrame" callback.

   @param observer Video frame observer object instance. If NULL is passed in, the registration is canceled.
   @return
   - 0: Success.
   - < 0: Failure.
   */
  virtual int registerVideoFrameObserver(IVideoFrameObserver* observer) = 0;
  /** **DEPRECATED** */
  virtual int registerVideoRenderFactory(IExternalVideoRenderFactory* factory) = 0;
  /** **DEPRECATED** Use \ref agora::media::IMediaEngine::pushAudioFrame(IAudioFrameObserver::AudioFrame* frame) "pushAudioFrame(IAudioFrameObserver::AudioFrame* frame)" instead.

   Pushes the external audio frame.

   @param type Type of audio capture device: #MEDIA_SOURCE_TYPE.
   @param frame Audio frame pointer: \ref IAudioFrameObserver::AudioFrame "AudioFrame".
   @param wrap Whether to use the placeholder. We recommend setting the default value.
   - true: Use.
   - false: (Default) Not use.

   @return
   - 0: Success.
   - < 0: Failure.
   */
  virtual int pushAudioFrame(MEDIA_SOURCE_TYPE type,
                             IAudioFrameObserver::AudioFrame* frame,
                             bool wrap) = 0;
  /** Pushes the external audio frame.

   @param frame Pointer to the audio frame: \ref IAudioFrameObserver::AudioFrame "AudioFrame".

   @return
   - 0: Success.
   - < 0: Failure.
   */
  virtual int pushAudioFrame(IAudioFrameObserver::AudioFrame* frame) = 0;
  /** Pulls the remote audio data.
   * 
   * Before calling this method, call the 
   * \ref agora::rtc::IRtcEngine::setExternalAudioSink 
   * "setExternalAudioSink(enabled: true)" method to enable and set the 
   * external audio sink.
   * 
   * After a successful method call, the app pulls the decoded and mixed 
   * audio data for playback.
   * 
   * @note
   * - Once you call the \ref agora::rtc::IRtcEngine::pullAudioFrame 
   * "pullAudioFrame" method successfully, the app will not retrieve any audio 
   * data from the 
   * \ref agora::rtc::IRtcEngineEventHandler::onPlaybackAudioFrame 
   * "onPlaybackAudioFrame" callback.
   * - The difference between the 
   * \ref agora::rtc::IRtcEngineEventHandler::onPlaybackAudioFrame 
   * "onPlaybackAudioFrame" callback and the 
   * \ref agora::rtc::IRtcEngine::pullAudioFrame "pullAudioFrame" method is as 
   * follows:
   *  - onPlaybackAudioFrame: The SDK sends the audio data to the app once 
   * every 10 ms. Any delay in processing the audio frames may result in audio 
   * jitter.
   *  - pullAudioFrame: The app pulls the remote audio data. After setting the 
   * audio data parameters, the SDK adjusts the frame buffer and avoids 
   * problems caused by jitter in the external audio playback.
   * 
   * @param frame Pointers to the audio frame. 
   * See: \ref IAudioFrameObserver::AudioFrame "AudioFrame".
   * 
   * @return
   * - 0: Success.
   * - < 0: Failure.
   */
  virtual int pullAudioFrame(IAudioFrameObserver::AudioFrame* frame) = 0;
    /** Configures the external video source.

    @param enable Sets whether to use the external video source:
    - true: Use the external video source.
    - false: (Default) Do not use the external video source.

    @param useTexture Sets whether to use texture as an input:
    - true: Use texture as an input.
    - false: (Default) Do not use texture as an input.

    @return
    - 0: Success.
    - < 0: Failure.
    */
    virtual int setExternalVideoSource(bool enable, bool useTexture) = 0;
    /** Pushes the video frame using the \ref ExternalVideoFrame "ExternalVideoFrame" and passes the video frame to the Agora SDK.

     @param frame Video frame to be pushed. See \ref ExternalVideoFrame "ExternalVideoFrame".

     @note In the Communication profile, this method does not support video frames in the Texture format.

     @return
     - 0: Success.
     - < 0: Failure.
     */
    virtual int pushVideoFrame(ExternalVideoFrame *frame) = 0;
};

}  // namespace media

}  // namespace agora

#endif  // AGORA_MEDIA_ENGINE_H
