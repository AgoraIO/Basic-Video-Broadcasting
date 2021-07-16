# Open Live iOS for Swift

*English | [中文](README.zh.md)*

This tutorial describes how to add live video chat to your iOS applications using Swift and the Agora Video SDK.

With this sample app, you can:

- Join/leave a channel
- Set user role as a broadcaster or audience member
- Mute/unmute audio
- Enable/disable video
- Switch camera views
- Enable/disable image enhancement
- Set up the resolution, the frame rate display

## Problem
After users upgrade their iOS devices to iOS 14.0, and use an app that integrates the Agora RTC SDK for iOS for the first time, users see a prompt for finding local network devices. The following picture shows the pop-up prompt:

![](../pictures/ios_14_privacy.png)

[Solution](https://docs.agora.io/en/faq/local_network_privacy)

## Prerequisites

- Xcode 10.0+
- Physical iOS device (iPhone or iPad)
- iOS simulator is NOT supported

## Quick Start

This section shows you how to prepare, build, and run the sample application.

### Obtain an App Id

To build and run the sample application, get an App Id:

1. Create a developer account at [agora.io](https://dashboard.agora.io/signin/). Once you finish the signup process, you will be redirected to the Dashboard.
2. Navigate in the Dashboard tree on the left to **Projects** > **Project List**.
3. Save the **App Id** from the Dashboard for later use.
4. Generate a temp **Access Token** (valid for 24 hours) from dashboard page with given channel name, save for later use.


> To ensure communication security, Agora uses tokens (dynamic keys) to authenticate users joining a channel.
>
> Temporary tokens are for demonstration and testing purposes only and remain valid for 24 hours. In a production environment, you need to deploy your own server for generating tokens. See [Generate a Token](https://docs.agora.io/en/Interactive Broadcast/token_server)for details.

### Integrate the Agora Video SDK

1. Run following command to install project dependencies:

    ```shell
    $ pod install
    ```

2. Open the generated `Openlive.xcworkspace` file with Xcode.
3. Edit the `KeyCenter.swift` file.
   - Replace `YOUR APP ID` with your App ID.
   - Replace `YOUR ACCESS TOKEN` with the Access Token.

    ```swift
    struct KeyCenter {
    static let AppId: String = <#Your App Id#>

    // assign token to nil if you have not enabled app certificate
    static var Token: String? = <#Temp Access Token#>
    }
    ```

   > See [Set up Authentication](https://docs.agora.io/en/Agora%20Platform/token) to learn how to get an App ID and access token. You can get a temporary access token to quickly try out this sample project.
   >
   > The Channel name you used to generate the token must be the same as the channel name you use to join a channel.

   > To ensure communication security, Agora uses access tokens (dynamic keys) to authenticate users joining a channel.
   >
   > Temporary access tokens are for demonstration and testing purposes only and remain valid for 24 hours. In a production environment, you need to deploy your own server for generating access tokens. See [Generate a Token](https://docs.agora.io/en/Interactive%20Broadcast/token_server) for details.

4. Build and run the project in your iOS device.
## Contact Us

- For potential issues, take a look at our [FAQ](https://docs.agora.io/en/faq) first
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated real use case
- Repositories managed by developer communities can be found at [Agora Community](https://github.com/AgoraIO-Community)
- You can find full API documentation at [Document Center](https://docs.agora.io/en/)
- If you encounter problems during integration, you can ask question in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)
- You can file bugs about this sample at [issue](https://github.com/AgoraIO/Basic-Video-Broadcasting/issues)

## License

The MIT License (MIT)
