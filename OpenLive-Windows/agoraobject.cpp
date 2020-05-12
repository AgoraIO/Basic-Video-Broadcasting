#include "agoraobject.h"
#include <qmessagebox.h>
#include <cassert>
#include <qprocess>
#include "stdafx.h"
int videoDeviceWindowId;
class AgoraRtcEngineEvent : public agora::rtc::IRtcEngineEventHandler
{
    CAgoraObject& m_pInstance;
public:
    AgoraRtcEngineEvent(CAgoraObject& engine)
        :m_pInstance(engine)
    {}

    virtual void onVideoStopped() override
    {
        emit m_pInstance.sender_videoStopped();
    }
    virtual void onJoinChannelSuccess(const char* channel, uid_t uid, int elapsed) override
    {
       emit m_pInstance.sender_joinedChannelSuccess(QString(channel),uid,elapsed);
    }
    virtual void onUserJoined(uid_t uid, int elapsed) override
    {
        //qDebug("%s %u",__FUNCTION__,uid);
        emit m_pInstance.sender_userJoined(uid, elapsed);
    }
    virtual void onUserOffline(uid_t uid, USER_OFFLINE_REASON_TYPE reason) override
    {
        emit m_pInstance.sender_userOffline(uid, reason);
    }
    virtual void onFirstLocalVideoFrame(int width, int height, int elapsed) override
    {
        emit m_pInstance.sender_firstLocalVideoFrame(width, height, elapsed);
    }
    virtual void onFirstRemoteVideoDecoded(uid_t uid, int width, int height, int elapsed) override
    {
        emit m_pInstance.sender_firstRemoteVideoDecoded(uid, width, height, elapsed);
    }
    virtual void onFirstRemoteVideoFrame(uid_t uid, int width, int height, int elapsed) override
    {
       emit m_pInstance.sender_firstRemoteVideoFrameDrawn(uid, width, height, elapsed);
    }
    virtual void onLocalVideoStats(const LocalVideoStats &stats) override
    {
        emit m_pInstance.sender_localVideoStats(stats);
    }
    virtual void onRemoteVideoStats(const RemoteVideoStats &stats) override
    {
        emit m_pInstance.sender_remoteVideoStats(stats);
    }
    virtual void onRtcStats(const RtcStats &stats)
    {
        emit m_pInstance.sender_rtcStats(stats);
    }

    virtual void onVideoDeviceStateChanged(const char* deviceId, int deviceType, int deviceState)
    {
        QString id = QString::fromUtf8(deviceId);
        emit m_pInstance.update_videoDevices(id, deviceType, deviceState);
    }

    virtual void onAudioDeviceStateChanged(const char* deviceId, int deviceType, int deviceState)
    {
        QString id = QString::fromUtf8(deviceId);
        emit m_pInstance.update_audioDevices(id, deviceType, deviceState);
    }
};

CAgoraObject* CAgoraObject::getInstance(QObject *parent)
{
    std::lock_guard<std::mutex> autoLock(m_mutex);
    if(nullptr == m_pInstance)
        m_pInstance = new CAgoraObject(parent);

    return m_pInstance;
}

CAgoraObject* CAgoraObject::m_pInstance = nullptr;
std::mutex  CAgoraObject::m_mutex;

CAgoraObject::CAgoraObject(QObject *parent):
    QObject(parent),
    m_rtcEngine(createAgoraRtcEngine()),
    m_eventHandler(new AgoraRtcEngineEvent(*this))
{
    agora::rtc::RtcEngineContext context;
    context.eventHandler = m_eventHandler.get();
    QByteArray temp;
    if(strlen(APP_ID))
        context.appId = APP_ID;
    else {
        QString strAppId = gAgoraConfig.getAppId();
        if(strAppId.length()== 0){
            gAgoraConfig.setAppId(QString(""));
        }
        temp = strAppId.toUtf8();
        context.appId = const_cast<const char*>(temp.data());
    }
    if (*context.appId == '\0')
    {
        QMessageBox::critical(nullptr, ("AgoraOpenLive"),
                                       ("You must specify APP ID before using the demo"));
        QProcess process;
        process.startDetached("notepad.exe", {"AgoraConfigOpenLive.ini"}, "");
        ExitProcess(0);
    }

    context.context = (void*)videoDeviceWindowId;
    m_rtcEngine->initialize(context);

    videoDeviceManager  = new AVideoDeviceManager(m_rtcEngine);
    audioDeviceManager  = new AAudioDeviceManager(m_rtcEngine);

    connect(this, SIGNAL(update_audioDevices(QString,  int, int)),
            this, SLOT(UpdateAudioDevices(QString, int, int)));

    connect(this, SIGNAL(update_videoDevices(QString, int, int)),
            this, SLOT(UpdateVideoDevices(QString, int, int)));

}

CAgoraObject::~CAgoraObject()
{
    if(m_rtcEngine)
        m_rtcEngine->release();
}

int CAgoraObject::joinChannel(const QString& key, const QString& channel, uint uid)
{
    if (channel.isEmpty()) {
        QMessageBox::warning(nullptr,("AgoraHighSound"),("channelname is empty"));
        return -1;
    }
    m_rtcEngine->startPreview();
    int r = m_rtcEngine->joinChannel(key.toUtf8().data(), channel.toUtf8().data(), nullptr, uid);

    return r;
}

int CAgoraObject::leaveChannel()
{
    int r = m_rtcEngine->leaveChannel();
    return r;
}

int CAgoraObject::muteLocalAudioStream(bool muted)
{
    RtcEngineParameters rep(*m_rtcEngine);
    return rep.muteLocalAudioStream(muted);
}

BOOL CAgoraObject::LocalVideoPreview(HWND hVideoWnd, BOOL bPreviewOn, RENDER_MODE_TYPE renderType/* = RENDER_MODE_TYPE::RENDER_MODE_FIT*/)
{
    int nRet = 0;

    if (bPreviewOn) {
        VideoCanvas vc;

        vc.uid = 0;
        vc.view = hVideoWnd;
        vc.renderMode = renderType;

        m_rtcEngine->setupLocalVideo(vc);
        nRet = m_rtcEngine->startPreview();
    }
    else
        nRet = m_rtcEngine->stopPreview();

    return nRet == 0 ? TRUE : FALSE;
}

BOOL CAgoraObject::RemoteVideoRender(uid_t uid, HWND hVideoWnd, RENDER_MODE_TYPE renderType/* = RENDER_MODE_TYPE::RENDER_MODE_HIDDEN*/)
{
    int nRet = 0;

    VideoCanvas vc;

    vc.uid = uid;
    vc.view = hVideoWnd;
    vc.renderMode = renderType;

    m_rtcEngine->setupRemoteVideo(vc);

    return nRet == 0 ? TRUE : FALSE;
}

int CAgoraObject::enableVideo(bool enabled)
{
    return enabled ? m_rtcEngine->enableVideo() : m_rtcEngine->disableVideo();
}

int CAgoraObject::enableAudio(bool enabled)
{
    return enabled ? m_rtcEngine->enableAudio() : m_rtcEngine->disableAudio();
}

BOOL CAgoraObject::setLogPath(const QString &strDir)
{
    int ret = 0;

    RtcEngineParameters rep(*m_rtcEngine);
    ret = rep.setLogFile(strDir.toUtf8().data());

    return ret == 0 ? TRUE : FALSE;
}

BOOL CAgoraObject::SetChannelProfile(CHANNEL_PROFILE_TYPE channelType)
{
    int ret = 0;
    ret = m_rtcEngine->setChannelProfile(channelType);

    return ret == 0 ? TRUE : FALSE;
}

BOOL CAgoraObject::SetClientRole(CLIENT_ROLE_TYPE roleType)
{
    int ret = 0;

    ret = m_rtcEngine->setClientRole(roleType);

    return ret == 0 ? TRUE : FALSE;
}

BOOL CAgoraObject::EnableWebSdkInteroperability(BOOL bEnable)
{
    RtcEngineParameters rep(*m_rtcEngine);

    int	nRet = rep.enableWebSdkInteroperability(static_cast<bool>(bEnable));

    return nRet == 0 ? TRUE : FALSE;
}

qSSMap CAgoraObject::getRecordingDeviceList()
{
    qSSMap devices;

    if (!audioDeviceManager || !audioDeviceManager->get())
        return devices;

    agora::util::AutoPtr<IAudioDeviceCollection> spCollection((*audioDeviceManager)->enumerateRecordingDevices());
    if (!spCollection)
        return devices;
    char name[MAX_DEVICE_ID_LENGTH], guid[MAX_DEVICE_ID_LENGTH];
    int count = spCollection->getCount();
    if (count > 0)
    {
        for (int i = 0; i < count; i++)
        {
            if (!spCollection->getDevice(i, name, guid))
            {
                DeviceInfo info;
                info.id   = guid;
                info.name = name;
                devices.push_back(info);
            }
        }
    }
    return devices;
}

qSSMap CAgoraObject::getPlayoutDeviceList()
{
    qSSMap devices;

    if (!audioDeviceManager || !audioDeviceManager->get())
        return devices;

    agora::util::AutoPtr<IAudioDeviceCollection> spCollection((*audioDeviceManager)->enumeratePlaybackDevices());
    if (!spCollection)
        return devices;
    char name[MAX_DEVICE_ID_LENGTH], guid[MAX_DEVICE_ID_LENGTH];
    int count = spCollection->getCount();
    if (count > 0)
    {
        for (int i = 0; i < count; i++)
        {
            if (!spCollection->getDevice(i, name, guid))
            {
                DeviceInfo info;
                info.id   = guid;
                info.name = name;
                devices.push_back(info);
            }
        }
    }
    return devices;
}

qSSMap CAgoraObject::getVideoDeviceList()
{
	m_rtcEngine->enableVideo();
    qSSMap devices;

    if (!videoDeviceManager || !videoDeviceManager->get())
        return devices;

    agora::util::AutoPtr<IVideoDeviceCollection> spCollection((*videoDeviceManager)->enumerateVideoDevices());
    if (!spCollection)
        return devices;
    char name[MAX_DEVICE_ID_LENGTH], guid[MAX_DEVICE_ID_LENGTH];
    int count = spCollection->getCount();
    if (count > 0)
    {
        for (int i = 0; i < count; i++)
        {
            if (!spCollection->getDevice(i, name, guid))
            {
                DeviceInfo info;
                info.id   = guid;
                info.name = name;
                devices.push_back(info);
            }
        }
    }
    return devices;
}

QString CAgoraObject::getCurrentVideoDevice()
{
     if (!videoDeviceManager || !videoDeviceManager->get())
         return QString("");
     char deviceId[MAX_DEVICE_ID_LENGTH] = {0};
     QString strId("");
     if(0 == (*videoDeviceManager)->getDevice(deviceId)){
         strId = QString::fromUtf8(deviceId);
     }
     return strId;
}

QString CAgoraObject::getCurrentPlaybackDevice()
{
    if (!audioDeviceManager || !audioDeviceManager->get())
        return QString("");
    char deviceId[MAX_DEVICE_ID_LENGTH] = {0};
    QString strId("");
    if(0 == (*videoDeviceManager)->getDevice(deviceId)){
        strId = QString::fromUtf8(deviceId);
    }
    return strId;
}

QString CAgoraObject::getCurrentRecordingDevice()
{
    if (!audioDeviceManager || !audioDeviceManager->get())
        return QString("");
    char deviceId[MAX_DEVICE_ID_LENGTH] = {0};
    QString strId("");
    if(0 == (*audioDeviceManager)->getPlaybackDevice(deviceId)){
        strId = QString::fromUtf8(deviceId);
    }
    return strId;
}

int CAgoraObject::setRecordingDevice(const QString& guid)
{
    if (guid.isEmpty())
        return -1;

    if (!audioDeviceManager || !audioDeviceManager->get())
        return -1;
    return (*audioDeviceManager)->setRecordingDevice(guid.toUtf8().data());
}

int CAgoraObject::setPlayoutDevice(const QString& guid)
{
    if (guid.isEmpty())
        return -1;

    if (!audioDeviceManager || !audioDeviceManager->get())
        return -1;
    return (*audioDeviceManager)->setPlaybackDevice(guid.toUtf8().data());
}

int CAgoraObject::setVideoDevice(const QString& guid)
{
    if (guid.isEmpty())
        return -1;

    if (!videoDeviceManager || !videoDeviceManager->get())
        return -1;
    return (*videoDeviceManager)->setDevice(guid.toUtf8().data());
}

BOOL CAgoraObject::setVideoProfile(int nWidth,int nHeight, FRAME_RATE fps, int bitrate)
{
    int res = 0;
    VideoEncoderConfiguration vec;

    if(gAgoraConfig.isCustomFPS())
        fps = (FRAME_RATE)gAgoraConfig.getFPS();

    if(gAgoraConfig.isCustomBitrate())
        bitrate = gAgoraConfig.getBitrate();

     if(gAgoraConfig.isCustomResolution())
         gAgoraConfig.getVideoResolution(nWidth, nHeight);

    vec = VideoEncoderConfiguration(nWidth,nHeight,FRAME_RATE_FPS_15,bitrate,ORIENTATION_MODE_FIXED_LANDSCAPE);
    res = m_rtcEngine->setVideoEncoderConfiguration(vec);

    return res ==0 ? TRUE : FALSE;
}

BOOL CAgoraObject::setRecordingIndex(int nIndex)
{
    int res = 0;
   if (!audioDeviceManager || !audioDeviceManager->get())
        return FALSE;

    agora::util::AutoPtr<IAudioDeviceCollection> spCollection((*audioDeviceManager)->enumerateRecordingDevices());
    if (!spCollection)
        return FALSE;
    char name[MAX_DEVICE_ID_LENGTH], guid[MAX_DEVICE_ID_LENGTH];
    int count = spCollection->getCount();
    assert(res < count);
    spCollection->getDevice(nIndex,name,guid);
    res = spCollection->setDevice(guid);

    return res ==0 ? TRUE:FALSE;
}

BOOL CAgoraObject::setPlayoutIndex(int nIndex)
{
    int res = 0;
    if (!audioDeviceManager || !audioDeviceManager->get())
        return FALSE;

    agora::util::AutoPtr<IAudioDeviceCollection> spCollection((*audioDeviceManager)->enumeratePlaybackDevices());
    if (!spCollection)
        return FALSE;
    char name[MAX_DEVICE_ID_LENGTH], guid[MAX_DEVICE_ID_LENGTH];
    int count = spCollection->getCount();
    assert(res < count);
    spCollection->getDevice(nIndex,name,guid);
    res = spCollection->setDevice(guid);

    return res ==0 ? TRUE:FALSE;
}

BOOL CAgoraObject::setVideoIndex(int nIndex)
{
    int res = 0;

    if (!videoDeviceManager || !videoDeviceManager->get())
        return FALSE;

    agora::util::AutoPtr<IVideoDeviceCollection> spCollection((*videoDeviceManager)->enumerateVideoDevices());
    if (!spCollection)
        return FALSE;
    char name[MAX_DEVICE_ID_LENGTH], guid[MAX_DEVICE_ID_LENGTH];
    int count = spCollection->getCount();
    assert(nIndex < count);
    spCollection->getDevice(nIndex,name,guid);
    res = spCollection->setDevice(guid);

    return res == 0 ? TRUE : FALSE;
}

BOOL CAgoraObject::MuteLocalVideo(BOOL bMute)
{
     int nRet = 0;

    RtcEngineParameters rep(*m_rtcEngine);
    nRet = rep.enableLocalVideo(!bMute);

    return nRet == 0 ? TRUE : FALSE;
}

BOOL CAgoraObject::MuteLocalAudio(BOOL bMute)
{
    int nRet = 0;

    RtcEngineParameters rep(*m_rtcEngine);
    nRet = m_rtcEngine->enableLocalAudio(!bMute);

    return nRet == 0 ? TRUE : FALSE;
}

bool CAgoraObject::setBeautyEffectOptions(bool enabled, BeautyOptions& options)
{
	int nRet = 0;

	nRet = m_rtcEngine->setBeautyEffectOptions(enabled, options);
	return nRet == 0 ? true : false;
}

void CAgoraObject::CAgoraObject::SetDefaultParameters()
{
    std::map<std::string, std::string> mapStringParamsters;
    std::map<std::string, bool> mapBoolParameters;
    std::map<std::string, int> mapIntParameters;
    std::map<std::string, std::string> mapObjectParamsters;
    if(m_agoraJson.GetParameters(mapStringParamsters,
                                 mapBoolParameters,
                                 mapIntParameters,
                                 mapObjectParamsters)){
        AParameter apm(m_rtcEngine);
        for (auto iter = mapBoolParameters.begin();
            iter != mapBoolParameters.end(); ++iter) {
            apm->setBool(iter->first.c_str(), iter->second);
        }
        for (auto iter = mapStringParamsters.begin();
            iter != mapStringParamsters.end(); ++iter) {
            apm->setString(iter->first.c_str(), iter->second.c_str());
        }
        for (auto iter = mapIntParameters.begin();
            iter != mapIntParameters.end(); ++iter) {
            apm->setInt(iter->first.c_str(), iter->second);
        }

        for (auto iter = mapObjectParamsters.begin();
            iter != mapObjectParamsters.end(); ++iter) {
            apm->setObject(iter->first.c_str(), iter->second.c_str());
        }
    }
}

void CAgoraObject::UpdateVideoDevices( QString deviceId, int deviceType, int deviceState)
{
    if(deviceType == VIDEO_CAPTURE_DEVICE && videoDeviceManager){
        videoDeviceManager->release();
        videoDeviceManager = new AVideoDeviceManager(m_rtcEngine);
        QString cameraid = getCurrentVideoDevice();
        if(cameraid == deviceId && (deviceState == MEDIA_DEVICE_STATE_UNPLUGGED || deviceState == MEDIA_DEVICE_STATE_DISABLED)){
            qSSMap videoDeviceList = getVideoDeviceList();
            if(videoDeviceList.size() > 0){
                (*videoDeviceManager)->setDevice(deviceId.toUtf8());
            }
        }
    }
}
void CAgoraObject::UpdateAudioDevices( QString deviceId, int deviceType, int deviceState)
{
    if(audioDeviceManager){
        audioDeviceManager->release();
        audioDeviceManager = new AAudioDeviceManager(m_rtcEngine);

        QString audioId;


        if(deviceState == MEDIA_DEVICE_STATE_UNPLUGGED || deviceState == MEDIA_DEVICE_STATE_DISABLED){
            qSSMap audioDeviceList;
            if(deviceType == AUDIO_RECORDING_DEVICE){
                audioId = getCurrentRecordingDevice();
                audioDeviceList = getRecordingDeviceList();

                if(audioDeviceList.size() > 0 && audioId ==  deviceId){
                    (*audioDeviceManager)->setRecordingDevice(deviceId.toUtf8());
                }
            }
            else if(deviceType == AUDIO_PLAYOUT_DEVICE ){
                audioId = getCurrentPlaybackDevice();
                audioDeviceList = getPlayoutDeviceList();

                if(audioDeviceList.size() > 0 && audioId ==  deviceId){
                    (*audioDeviceManager)->setPlaybackDevice(deviceId.toUtf8());
                }
            }


        }
    }
}
