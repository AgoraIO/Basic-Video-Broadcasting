# Open Live macOS for Swift

*其他语言版本： [简体中文](README.zh.md)*

The Open Live macOS for Swift Sample App is an open-source demo that will help you get live video chat integrated directly into your macOS applications using the Agora Video SDK.

With this sample app, you can:

- Join / leave channel
- Set role as broadcaster or audience
- Mute / unmute audio
- Setup resolution, frame rate and bit rate

A tutorial demo can be found here: [Agora-macOS-Tutorial-Swift-1to1](https://github.com/AgoraIO/Agora-macOS-Tutorial-Swift-1to1)

Agora Video SDK supports iOS / Android / Windows / macOS etc. You can find demos of these platform here:

- [OpenLive-iOS](https://github.com/AgoraIO/OpenLive-iOS)
- [OpenLive-Android](https://github.com/AgoraIO/OpenLive-Android)
- [OpenLive-Windows](https://github.com/AgoraIO/OpenLive-Windows)

## Running the App
First, create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID. Update "KeyCenter.swift" with your App ID.

```
static let AppId: String = "Your App ID"
```

Next, download the **Agora Video SDK** from [Agora.io SDK](https://www.agora.io/en/download/). Unzip the downloaded SDK package and copy the **libs/AgoraRtcEngineKit.framework** to the "OpenLive" folder in project.

Finally, Open OpenLive.xcodeproj, setup your development signing and run.

## Developer Environment Requirements
* XCode 8.0 +

## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/Agora-macOS-Tutorial-Swift-1to1/issues)

## License

The MIT License (MIT).
