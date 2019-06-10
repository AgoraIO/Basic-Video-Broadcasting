# Open Live macOS for Objective-C

*English | [中文](README.zh.md)*

The Open Live macOS for Swift Sample App is an open-source demo that will help you get live video chat integrated directly into your macOS applications using the Agora Video SDK.

With this sample app, you can:

- Join / leave channel
- Set role as broadcaster or audience
- Mute / unmute audio
- Setup resolution, frame rate and bit rate

This demo is written in **Objective-C**, you can find **Swift** version here: [OpenLive-macOS](https://github.com/AgoraIO/Basic-Video-Broadcasting/tree/master/OpenLive-macOS)

A tutorial demo can be found here: [Agora-macOS-Tutorial-Swift-1to1](https://github.com/AgoraIO/Basic-Video-Call/tree/master/One-to-One-Video/Agora-macOS-Tutorial-Swift-1to1)

## Prerequisites

- Xcode 10.0+

## Quick Start

This section shows you how to prepare, build, and run the sample application.

### Obtain an App ID

To build and run the sample application, get an App ID:
1. Create a developer account at [agora.io](https://dashboard.agora.io/signin/). Once you finish the signup process, you will be redirected to the Dashboard.
2. Navigate in the Dashboard tree on the left to **Projects** > **Project List**.
3. Save the **App ID** from the Dashboard for later use.
4. Generate a temp **Access Token** (valid for 24 hours) from dashboard page with given channel name, save for later use.

5. Open `OpenLive.xcodeproj` and edit the `KeyCenter.m` file. Update `<#Your App Id#>` with your app ID, and assign the token variable with the temp Access Token generated from dashboard.

    ```
    + (NSString *)AppId {
        return <#Your App Id#>;
    }

    // assign token to nil if you have not enabled app certificate
    + (NSString *)Token {
        return <#Temp Access Token#>;
    }
    ```

### Integrate the Agora Video SDK

1. Download the [Agora Video SDK](https://www.agora.io/en/download/). Unzip the downloaded SDK package and copy the following files from the SDK `libs` folder into the sample application `OpenLive` folder.
    - `AograRtcEngineKit.framework`
    - `AgoraRtcCryptoLoader.framework`
    - `libcrypto.a`
  
2. Ensure a valid provisioning profile is applied or your project will not run.


## Resources

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/Basic-Video-Call/issues)

## License

The MIT License (MIT)
